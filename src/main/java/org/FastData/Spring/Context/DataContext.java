package org.FastData.Spring.Context;

import org.FastData.Spring.FastDataAop.*;
import org.FastData.Spring.Base.BaseModel;
import org.FastData.Spring.Base.DataConfig;
import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.CacheModel.NavigateModel;
import org.FastData.Spring.CacheModel.PoolModel;
import org.FastData.Spring.CacheModel.PropertyModel;
import org.FastData.Spring.Model.*;
import org.FastData.Spring.Config.DataDbType;
import org.FastData.Spring.Util.CacheUtil;
import org.FastData.Spring.Util.FastUtil;
import org.FastData.Spring.Util.LogUtil;
import org.FastData.Spring.Util.ReflectUtil;
import java.io.Closeable;
import java.sql.*;
import java.util.*;

public class DataContext implements Closeable {
    public DbConfig config;
    private Connection conn;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private String cacheKey;
    private String id;

    public DataContext(String key) {
        config = DataConfig.db(key);
        cacheKey = String.format("pool.DataContext.%s", config.getKey().toLowerCase());
        PoolModel model = PoolUtil.getConnection(config, cacheKey);
        conn = model.getConn();
        id = model.getId();
    }

    public void close() {
        PoolUtil.close(config, conn, cacheKey, id);
    }

    public <T> DataReturnImpl<T> query(MapResult map, Class<T> type) {
        return query(map, type, true);
    }

    /*
       query by list
    */
    public <T> DataReturnImpl<T> query(MapResult map, Class<T> type, boolean isAop) {
        DataReturnImpl<T> result = new DataReturnImpl<T>();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        try {
            ResultSet resultSet;
            if (map.getParam().size() != 0)
                result.setSql(getSql(map));
            else
                result.setSql(map.getSql());

            if (isAop)
                BaseAop.aopBefore(type.getName(), map, config, true, AopEnum.Query_List,null);
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                Object[] param = map.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(param[i]));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                result.getList().add(setModel(type,col,resultSet,property));
            }
            close(resultSet, map);
            navigate(result.getList());
            if (config.isOutSql())
                System.out.println("\033[35;4m" + result.getSql() + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "to list tableName:" + type.getName(), AopEnum.Query_List, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            BaseAop.aopAfter(type.getName(), map, config, true, AopEnum.Query_List, result,null);
        return result;
    }

    public DataReturn query(MapResult map) {
        return query(map, true);
    }

    /*
       query by list
    */
    public DataReturn query(MapResult map, boolean isAop) {
        DataReturn result = new DataReturn();
        try {
            ResultSet resultSet;
            if (map.getParam().size() != 0)
                result.setSql(getSql(map));
            else
                result.setSql(map.getSql());
            if (isAop)
                BaseAop.aopBefore(null, map, config, true, AopEnum.Query_Dic,null);
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                Object[] param = map.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(param[i]));
                }
                resultSet = preparedStatement.executeQuery();

            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                FastMap<String, Object> model = read(col, resultSet);
                if (model != null)
                    result.getList().add(model);
            }
            close(resultSet, map);
            if (config.isOutSql())
                System.out.println("\033[35;4m" + result.getSql() + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "to List<map>", AopEnum.Query_Dic, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            BaseAop.aopAfter(null, map, config, true, AopEnum.Query_Dic, result.getList(),null);
        return result;
    }

    private int pageCount(MapResult map) {
        try {
            int count = 0;
            ResultSet resultSet;
            String sql = String.format("select count(0) count from (%s)t", map.getSql());
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(sql);
                Object[] param = map.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(param[i]));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(sql);
            }
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            close(resultSet, map);
            return count;
        } catch (Exception ex) {
            if (config.isOutError())
                ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return 0;
    }

    private ResultSet pageResult(PageModel pModel, MapResult map) {
        ResultSet resultSet = null;
        try {
            if (config.getDbType().equalsIgnoreCase(DataDbType.Oracle))
                map.setSql(String.format("select * from(select field.*,ROWNUM RN from(%s) field where rownum<=%s) where rn>=%s"
                        , map.getSql(), pModel.getEndId(), pModel.getStarId()));

            if (config.getDbType().equalsIgnoreCase(DataDbType.SqlServer))
                map.setSql(String.format("select top %s * from (select row_number()over(order by tempcolumn)temprownumber,* from(select tempcolumn = 0, * from (%s)t)tt)ttt where temprownumber >= %s"
                        , map.getSql(), pModel.getPageSize(), pModel.getStarId() - 1));

            if (config.getDbType().equalsIgnoreCase(DataDbType.MySql))
                map.setSql(String.format("%s limit %s,%s", map.getSql(), pModel.getStarId(), pModel.getPageSize()));

            if (config.getDbType().equalsIgnoreCase(DataDbType.DB2))
                map.setSql(String.format("select * from(select field.*,ROWNUM RN from(%s) field where rownum<=%s) where rn>=%s"
                        , map.getSql(), pModel.getEndId(), pModel.getStarId()));

            if (config.getDbType().equalsIgnoreCase(DataDbType.PostgreSql))
                map.setSql(String.format("%s limit %s offset %s", map.getSql(), pModel.getPageSize(), pModel.getStarId()));

            if (config.getDbType().equalsIgnoreCase(DataDbType.SQLite))
                map.setSql(String.format("%s limit %s offset %s", map.getSql(), pModel.getPageSize(), pModel.getStarId()));

            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                Object[] param = map.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(param[i]));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }

        return resultSet;
    }

    public <T> PageResultImpl<T> page(PageModel pModel, MapResult map, Class<T> type) {
        return page(pModel, map, type, true);
    }

    /*
       query by list
    */
    public <T> PageResultImpl<T> page(PageModel pModel, MapResult map, Class<T> type, boolean isAop) {
        PageResultImpl<T> result = new PageResultImpl<T>();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        try {
            pModel.setStarId((pModel.getPageId() - 1) * pModel.getPageSize() + 1);
            pModel.setEndId(pModel.getPageId() * pModel.getPageSize());
            pModel.setTotalRecord(pageCount(map));

            if (isAop)
                BaseAop.aopBefore(type.getName(), map, config, true, AopEnum.Map_Page_Model,null);

            if (pModel.getTotalRecord() > 0) {
                if ((pModel.getTotalRecord() % pModel.getPageSize()) == 0)
                    pModel.setTotalPage(pModel.getTotalRecord() / pModel.getPageSize());
                else
                    pModel.setTotalPage((pModel.getTotalRecord() / pModel.getPageSize()) + 1);

                if (pModel.getPageId() > pModel.getTotalPage())
                    pModel.setPageId(pModel.getTotalPage());

                ResultSet resultSet = pageResult(pModel, map);
                if (resultSet != null) {
                    ResultSetMetaData col = resultSet.getMetaData();
                    while (resultSet.next()) {
                        result.getList().add(setModel(type,col,resultSet,property));
                    }
                    resultSet.close();
                }
                result.setpModel(pModel);
                navigate(result.getList());
            } else
                return result;
        } catch (Exception ex) {
            BaseAop.aopException(ex, "tp page tableName:" + type.getName(), AopEnum.Map_Page_Model, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            BaseAop.aopAfter(type.getName(), map, config, true, AopEnum.Map_Page_Model, result,null);
        return result;
    }

    public PageResult page(PageModel pModel, MapResult map) {
        return page(pModel, map, true);
    }

    /*
      query by page
    */
    public PageResult page(PageModel pModel, MapResult map, boolean isAop) {
        PageResult result = new PageResult();
        try {
            pModel.setStarId((pModel.getPageId() - 1) * pModel.getPageSize() + 1);
            pModel.setEndId(pModel.getPageId() * pModel.getPageSize());
            pModel.setTotalRecord(pageCount(map));
            if (pModel.getTotalRecord() > 0) {
                if ((pModel.getTotalRecord() % pModel.getPageSize()) == 0)
                    pModel.setTotalPage(pModel.getTotalRecord() / pModel.getPageSize());
                else
                    pModel.setTotalPage((pModel.getTotalRecord() / pModel.getPageSize()) + 1);

                if (pModel.getPageId() > pModel.getTotalPage())
                    pModel.setPageId(pModel.getTotalPage());

                if (isAop)
                    BaseAop.aopBefore(null, map, config, true, AopEnum.Query_Page_Dic,null);

                ResultSet resultSet = pageResult(pModel, map);
                if (resultSet != null) {
                    ResultSetMetaData col = resultSet.getMetaData();
                    while (resultSet.next()) {
                        FastMap<String, Object> model = read(col, resultSet);
                        if (model != null)
                            result.getList().add(model);
                    }
                    resultSet.close();
                }
                result.setpModel(pModel);
            } else
                return result;
        } catch (Exception ex) {
            BaseAop.aopException(ex, "to page", AopEnum.Query_Page_Dic, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            BaseAop.aopAfter(null, map, config, true, AopEnum.Query_Page_Dic, result,null);
        return result;
    }

    /*
    insert table
     model: database table
   */
    public DataReturn add(Object model) {
        boolean isTrans = false;
        DataReturn result = new DataReturn();
        MapResult insert = new MapResult();
        try {
            insert = BaseModel.insert(model);
            BaseAop.aopBefore(model.getClass().getName(), insert, config, false, AopEnum.Add, model);
            if (!insert.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(insert.getMessage());
            } else {
                List<Map<String, Object>> navigate = checkNavigate(model, AopEnum.Add_Navigate);
                List<DataReturn> dataReturns = new ArrayList<>();
                if (navigate.size() > 0) {
                    isTrans = true;
                    beginTrans();
                    dataReturns = addNavigate(model, navigate);
                    if (dataReturns.stream().anyMatch(a -> !a.getWriteReturn().getSuccess())) {
                        rollbackTrans();
                        result.getWriteReturn().setSuccess(false);
                        result.getWriteReturn().setMessage(dataReturns.stream().filter(a -> !a.getWriteReturn().getSuccess()).findFirst().get().getWriteReturn().getMessage());
                        BaseAop.aopAfter(model.getClass().getName(), insert, config, false, AopEnum.Add, result.getWriteReturn(), model);
                        close(null, insert);
                        return result;
                    }
                }
                if (insert.getParam().size() != 0) {
                    preparedStatement = conn.prepareStatement(insert.getSql());
                    Object[] param = insert.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, insert.getParam().get(param[i]));
                    }

                    result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);

                    if (isTrans&& result.getWriteReturn().getSuccess())
                        submitTrans();
                }
            }
            close(null, insert);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(insert) + "\033[0m");
        } catch (Exception ex) {
            if (isTrans)
                rollbackTrans();
            BaseAop.aopException(ex, "add tableName:" + model.getClass().getName(), AopEnum.Add, config, model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }

        BaseAop.aopAfter(model.getClass().getName(), insert, config, false, AopEnum.Add, result.getWriteReturn(), model);
        return result;
    }

    /*
     delete table by primary Key
     model: database table
    */
    public DataReturn delete(Object model) {
        boolean isTrans = false;
        DataReturn result = new DataReturn();
        MapResult delete = new MapResult();
        try {
            delete = BaseModel.delete(model, config, conn);
            BaseAop.aopBefore(model.getClass().getName(), delete, config, false, AopEnum.Delete_PrimaryKey,model);

            if (!delete.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(delete.getMessage());
            } else {
                List<Map<String, Object>> navigate = checkNavigate(model, AopEnum.Delete_Navigate);
                List<DataReturn> dataReturns = new ArrayList<>();
                if (navigate.size() > 0) {
                    isTrans = true;
                    beginTrans();
                    dataReturns = deleteNavigate(model, navigate);
                    if (dataReturns.stream().anyMatch(a -> !a.getWriteReturn().getSuccess())) {
                        rollbackTrans();
                        result.getWriteReturn().setSuccess(false);
                        result.getWriteReturn().setMessage(dataReturns.stream().filter(a -> !a.getWriteReturn().getSuccess()).findFirst().get().getWriteReturn().getMessage());
                        BaseAop.aopAfter(model.getClass().getName(), delete, config, false, AopEnum.Delete_PrimaryKey, result.getWriteReturn(), model);
                        close(null, delete);
                        return result;
                    }
                }

                if(delete.getParam().size()>0) {
                    preparedStatement = conn.prepareStatement(delete.getSql());
                    Object[] param = delete.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, delete.getParam().get(param[i]));
                    }
                    result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
                    if (isTrans&& result.getWriteReturn().getSuccess())
                        submitTrans();
                }
            }
            close(null, delete);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(delete) + "\033[0m");
        } catch (Exception ex) {
            if(isTrans)
                rollbackTrans();
            BaseAop.aopException(ex, "delete by Primary Key tableName:" + model.getClass().getName(), AopEnum.Delete_PrimaryKey, config,model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        BaseAop.aopAfter(model.getClass().getName(), delete, config, false, AopEnum.Delete_PrimaryKey, result.getWriteReturn(),model);
        return result;
    }

    /*
         delete table by primary Key
         model: database table
        */
    public DataReturn delete(Map<String, Object> map, Class<?> type) {
        MapResult param = new MapResult();
        LinkedHashMap<String, Object> linkMap = new LinkedHashMap<>();
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        param.setSql(String.format("delete from %s where 1=1 ", tableName));
        map.keySet().forEach(a -> {
            linkMap.put(a, map.get(a));
        });

        linkMap.keySet().forEach(a -> {
            param.setSql(String.format("%s and %s=?", param.getSql(), a));
        });

        param.setParam(linkMap);

        //BaseAop.aopBefore(type.getName(), param, config, false,AopEnum.Delete);
        DataReturn result = execute(param);
        //BaseAop.aopAfter(type.getName(), param, config, false,AopEnum.Delete, result);
        return result;
    }

    /*
     update table by primary Key
     model: database table
     field: update field
    */
    public DataReturn update(Object model, List<String> field) {
        boolean isTrans =false;
        DataReturn result = new DataReturn();
        MapResult update = new MapResult();
        try {
            update = BaseModel.update(model, field, config, conn);
            BaseAop.aopBefore(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey,model);

            if (!update.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else {
                List<Map<String, Object>> navigate = checkNavigate(model, AopEnum.Update_Navigate);
                List<DataReturn> dataReturns = new ArrayList<>();
                if (navigate.size() > 0) {
                    isTrans = true;
                    beginTrans();
                    dataReturns = updateNavigate(model, navigate);
                    if (dataReturns.stream().anyMatch(a -> !a.getWriteReturn().getSuccess())) {
                        rollbackTrans();
                        result.getWriteReturn().setSuccess(false);
                        result.getWriteReturn().setMessage(dataReturns.stream().filter(a -> !a.getWriteReturn().getSuccess()).findFirst().get().getWriteReturn().getMessage());
                        BaseAop.aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey, result.getWriteReturn(), model);
                        close(null, update);
                        return result;
                    }
                }

                if(update.getParam().size()>0) {
                    preparedStatement = conn.prepareStatement(update.getSql());
                    Object[] param = update.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, update.getParam().get(param[i]));
                    }
                    result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
                    if (isTrans&& result.getWriteReturn().getSuccess())
                        submitTrans();
                }
            }
            close(null, update);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "update by Primary Key tableName " + model.getClass().getName(), AopEnum.Update_PrimaryKey, config,model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        BaseAop.aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey, result.getWriteReturn(),model);
        return result;
    }

    /*
     update table by primary Key
     model: database table
    */
    public DataReturn update(Object model) {
        boolean isTrans =false;
        DataReturn result = new DataReturn();
        MapResult update = new MapResult();
        try {
            update = BaseModel.update(model, config, conn);
            BaseAop.aopBefore(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey,model);

            if (!update.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else {
                List<Map<String, Object>> navigate = checkNavigate(model, AopEnum.Update_Navigate);
                List<DataReturn> dataReturns = new ArrayList<>();
                if (navigate.size() > 0) {
                    isTrans = true;
                    beginTrans();
                    dataReturns = updateNavigate(model, navigate);
                    if (dataReturns.stream().anyMatch(a -> !a.getWriteReturn().getSuccess())) {
                        rollbackTrans();
                        result.getWriteReturn().setSuccess(false);
                        result.getWriteReturn().setMessage(dataReturns.stream().filter(a -> !a.getWriteReturn().getSuccess()).findFirst().get().getWriteReturn().getMessage());
                        BaseAop.aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey, result.getWriteReturn(), model);
                        close(null, update);
                        return result;
                    }
                }

                if(update.getParam().size()>0) {
                    preparedStatement = conn.prepareStatement(update.getSql());
                    Object[] param = update.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, update.getParam().get(param[i]));
                    }
                    result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
                    if (isTrans&& result.getWriteReturn().getSuccess())
                        submitTrans();
                }
            }
            close(null, update);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "update by Primary Key tableName " + model.getClass().getName(), AopEnum.Update_PrimaryKey, config,model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        BaseAop.aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey, result.getWriteReturn(),model);
        return result;
    }

    /*
     query exists by primary Key
     model: database table
    */
    public DataReturn exists(Object model) {
        DataReturn result = new DataReturn();
        MapResult exists = new MapResult();
        try {
            ResultSet resultSet;
            exists = BaseModel.exists(model, config, conn);
            BaseAop.aopBefore(model.getClass().getName(), exists, config, true, AopEnum.Exists_PrimaryKey,model);

            if (!exists.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(exists.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(exists.getSql());
                Object[] param = exists.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, exists.getParam().get(param[i]));
                }
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    result.setCount(Integer.parseInt(resultSet.getObject(1).toString()));
                }
                close(resultSet, exists);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(exists) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "exists tableName by Primary Key" + model.getClass().getName(), AopEnum.Exists_PrimaryKey, config,model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        BaseAop.aopAfter(model.getClass().getName(), exists, config, true, AopEnum.Exists_PrimaryKey, result.getWriteReturn(),model);
        return result;
    }

    /*
      query model by primary Key
      model: database table
      key: database key
    */
    public DataReturn query(Object model) {
        DataReturn result = new DataReturn();
        List<PropertyModel> property = CacheUtil.getList(model.getClass().getName(), PropertyModel.class);
        MapResult query = new MapResult();
        try {
            ResultSet resultSet;
            query = BaseModel.query(model, config, conn);
            BaseAop.aopBefore(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey_Dic,model);
            if (!query.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(query.getMessage());
            } else {
                if (query.getParam().size() != 0)
                    result.setSql(getSql(query));
                else
                    result.setSql(query.getSql());

                if (query.getParam().size() != 0) {
                    preparedStatement = conn.prepareStatement(query.getSql());
                    Object[] param = query.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, query.getParam().get(param[i]));
                    }
                    resultSet = preparedStatement.executeQuery();

                } else {
                    statement = conn.createStatement();
                    resultSet = statement.executeQuery(query.getSql());

                }
                ResultSetMetaData col = resultSet.getMetaData();
                while (resultSet.next()) {
                    FastMap<String, Object> map = read(col, resultSet);
                    if (model != null)
                        result.setItem(map);
                }
                close(resultSet, query);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(query) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "query by Primary Key tableName " + model.getClass().getName(), AopEnum.Query_PrimaryKey_Dic, config,model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        BaseAop.aopAfter(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey_Dic, result.getItem(),model);
        return result;
    }

    /*
      query model by primary Key
      model: database table
      key: database key
    */
    public <T> DataReturnImpl<T> query(T model, Class<?> type) {
        DataReturnImpl<T> result = new DataReturnImpl<T>();
        List<PropertyModel> property = CacheUtil.getList(model.getClass().getName(), PropertyModel.class);
        MapResult query = new MapResult();
        try {
            ResultSet resultSet;
            query = BaseModel.query(model, config, conn);
            BaseAop.aopBefore(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey,model);

            if (!query.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(query.getMessage());
            } else {
                if (query.getParam().size() != 0)
                    result.setSql(getSql(query));
                else
                    result.setSql(query.getSql());

                if (query.getParam().size() != 0) {
                    preparedStatement = conn.prepareStatement(query.getSql());
                    Object[] param = query.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, query.getParam().get(param[i]));
                    }
                    resultSet = preparedStatement.executeQuery();

                } else {
                    statement = conn.createStatement();
                    resultSet = statement.executeQuery(query.getSql());

                }
                ResultSetMetaData col = resultSet.getMetaData();
                while (resultSet.next()) {
                    result.setItem(setModel(type,col,resultSet,property));
                    navigate(result.getItem());
                }
                close(resultSet, query);
            }
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(query) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "query by Primary Key tableName " + model.getClass().getName(), AopEnum.Query_PrimaryKey, config,model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        BaseAop.aopAfter(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey, result.getItem(),model);
        return result;
    }

    public int count(MapResult map) {
        return count(map, true);
    }

    /*
        query count
    */
    public int count(MapResult map, boolean isAop) {
        if (isAop)
            BaseAop.aopBefore(null, map, config, true, AopEnum.Query_Count,null);
        int count = 0;
        try {
            ResultSet resultSet;
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                Object[] param = map.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(param[i]));
                }
                resultSet = preparedStatement.executeQuery();

            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());

            }
            while (resultSet.next()) {
                count = Integer.parseInt(resultSet.getObject(1).toString());
            }
            close(resultSet, map);
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "query count ", AopEnum.Query_Count, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            BaseAop.aopAfter(null, map, config, true, AopEnum.Query_Count, count,null);
        return count;
    }

    public int count(Map<String, Object> map, Class<?> type) {
        MapResult param = new MapResult();
        LinkedHashMap<String, Object> linkMap = new LinkedHashMap<>();
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        param.setSql(String.format("select count(0) from %S where 1=1 ", tableName));
        map.keySet().forEach(a -> {
            linkMap.put(a, map.get(a));
        });

        linkMap.keySet().forEach(a -> {
            param.setSql(String.format("%s and %s=?", param.getSql(), a));
        });

        param.setParam(linkMap);
        BaseAop.aopBefore(null, param, config, true, AopEnum.Query_Count,null);
        int result = count(param);
        BaseAop.aopAfter(null, param, config, true, AopEnum.Query_Count, result,null);
        return result;
    }

    public DataReturn execute(MapResult map) {
        return execute(map, true);
    }

    /*
       execute sql
    */
    public DataReturn execute(MapResult map, boolean isAop) {
        DataReturn result = new DataReturn();
        if (isAop)
            BaseAop.aopBefore(null, map, config, false, AopEnum.Execute_Sql_Bool,null);
        try {
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                Object[] param = map.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(param[i]));
                }
                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
            } else {
                statement = conn.createStatement();
                result.getWriteReturn().setSuccess(statement.execute(map.getSql()));
            }
            close(null, map);
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "execute sql", AopEnum.Execute_Sql_Bool, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            BaseAop.aopAfter(null, map, config, false, AopEnum.Execute_Sql_Bool, result.getWriteReturn(),null);
        return result;
    }

    public WriteReturn executeParam(MapResult map) {
        WriteReturn result = new WriteReturn();
        BaseAop.aopBefore(null, map, config, false, AopEnum.Execute_Sql_Bool,null);
        try {
            if (map.getParam().size() != 0) {
                Object[] param = map.getParam().keySet().toArray();
                LinkedHashMap<Integer, String> item = new LinkedHashMap<>();
                int[] sort = new int[param.length];
                for (int i = 0; i < param.length; i++) {
                    String temp = String.format("?%s", param[i].toString().toLowerCase());
                    int count = map.getSql().toLowerCase().indexOf(temp);
                    map.setSql(map.getSql().toLowerCase().replace(temp, "?"));
                    if (count > 0) {
                        item.put(count, param[i].toString());
                        sort[i] = count;
                    }
                }

                Arrays.sort(sort);
                preparedStatement = conn.prepareStatement(map.getSql());
                for (int i = 0; i < sort.length; i++) {
                    if (sort[i] != 0)
                        preparedStatement.setObject(i + 1, map.getParam().get(item.get(sort[i])));
                }

                result.setSuccess(preparedStatement.executeUpdate() > 0);
            } else {
                statement = conn.createStatement();
                result.setSuccess(statement.execute(map.getSql()));
            }
            close(null, map);
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, "execute sql", AopEnum.Execute_Sql_Bool, config,null);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        BaseAop.aopAfter(null, map, config, false, AopEnum.Execute_Sql_Bool, result,null);
        return result;
    }

    private String getSql(MapResult map) {
        if (map.getParam() == null || map.getParam().size() == 0)
            return map.getSql();

        StringBuilder result = new StringBuilder();
        result.append(map.getSql());
        map.getParam().forEach((k, v) -> {
            result.append(String.format(",%s:%s,", k, v));
        });

        return result.toString();
    }

    public void beginTrans() {
        try {
            this.conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitTrans() {
        try {
            this.conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rollbackTrans() {
        try {
            this.conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(ResultSet resultSet, MapResult map) {
        try {
            if (resultSet != null)
                resultSet.close();
            if (map.getParam().size() != 0) {
                preparedStatement.clearWarnings();
                preparedStatement.clearBatch();
                preparedStatement.clearParameters();
                preparedStatement.close();
            }
            else {
                statement.clearWarnings();
                statement.clearBatch();
                statement.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private FastMap<String, Object> read(ResultSetMetaData col, ResultSet resultSet) {
        FastMap<String, Object> model = new FastMap<>();
        try {
            for (int i = 1; i <= col.getColumnCount(); i++) {
                String name = col.getColumnName(i);
                if (col.getColumnClassName(i).contains("oracle.jdbc.OracleNClob")) {
                    NClob nClob = resultSet.getNClob(name);
                    model.put(name, nClob.getSubString(1, (int) nClob.length()));
                } else if (col.getColumnClassName(i).contains("oracle.jdbc.OracleClob")) {
                    Clob Clob = resultSet.getClob(name);
                    model.put(name, Clob.getSubString(1, (int) Clob.length()));
                } else if (col.getColumnClassName(i).contains("oracle.jdbc.OracleBlob")) {
                    Blob blob = resultSet.getBlob(name);
                    model.put(name, new String(blob.getBytes(1, (int) blob.length())));
                } else
                    model.put(name, resultSet.getObject(name));
            }
            return model;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private <T> void navigate(List<T> data) {
        String key = String.format("%s.Navigate", data.getClass().getName());
        if (CacheUtil.exists(key)) {
            List<NavigateModel> list = CacheUtil.getList(key, NavigateModel.class);
            list.forEach(this::navigate);
        }
    }

    private <T> void navigate(T data) {
        String key = String.format("%s.Navigate", data.getClass().getName());
        LinkedHashMap param = new LinkedHashMap();
        if (CacheUtil.exists(key)) {
            List<NavigateModel> list = CacheUtil.getList(key, NavigateModel.class);
            list.forEach(a -> {
                MapResult mapResult = new MapResult();
                List<PropertyModel> property = CacheUtil.getList(a.getPropertyType().getName(), PropertyModel.class);
                String tableName = a.getPropertyType().getName().replace(a.getPropertyType().getPackage().getName(), "").replace(".", "");
                StringBuilder sql = new StringBuilder();
                sql.append(String.format("select * from %s where 1=1 ", tableName));
                try {
                    for (int i = 0; i < a.getName().size(); i++) {
                        sql.append(String.format(" and %s=? ", a.getName().get(i)));
                        if (!FastUtil.isNullOrEmpty(a.getAppand().get(i)) && a.getAppand().get(i).toLowerCase().trim().startsWith("and"))
                            sql.append(a.getAppand().get(i));
                        if (!FastUtil.isNullOrEmpty(a.getAppand().get(i)) && !a.getAppand().get(i).toLowerCase().trim().startsWith("and"))
                            sql.append(String.format(" and %s ", a.getAppand().get(i)));
                        param.put(a.getName().get(i), ReflectUtil.get(data, a.getName().get(i), a.getType()));
                        mapResult.setParam(param);
                    }

                    mapResult.setSql(sql.toString());
                    BaseAop.aopBefore(tableName, mapResult, config, true, AopEnum.Query_Navigate,null);

                    preparedStatement = conn.prepareStatement(sql.toString());
                    for (int i = 0; i < a.getName().size(); i++) {
                        preparedStatement.setObject(i + 1, param.get(a.getName().get(i)));
                    }
                    ResultSet resultSet = preparedStatement.executeQuery();

                    List result = new ArrayList();
                    ResultSetMetaData col = resultSet.getMetaData();
                    while (resultSet.next()) {
                        result.add(setModel(a.getPropertyType(), col, resultSet, property));
                    }

                    if (a.isList())
                        ReflectUtil.set(data, result, a.getMemberName(), a.getMemberType());
                    else if(result.size()>0)
                        ReflectUtil.set(data, result.get(0), a.getMemberName(), a.getMemberType());

                    close(resultSet, mapResult);
                    BaseAop.aopAfter(tableName, mapResult, config, true, AopEnum.Query_Navigate, data,null);
                } catch (Exception ex) {
                    BaseAop.aopException(ex, "navigate tableName:" + tableName, AopEnum.Query_Navigate, config,null);
                    ex.printStackTrace();
                    if (config.isOutError())
                        LogUtil.error(ex);
                }
            });
        }
    }

    private List<Map<String,Object>> checkNavigate(Object model,int type) {
        List<Map<String, Object>> result = new ArrayList<>();
        String navigateKey = String.format("%s.Navigate", model.getClass().getName());
        if (CacheUtil.exists(navigateKey)) {
            List<NavigateModel> list = CacheUtil.getList(navigateKey, NavigateModel.class);
            list.forEach(a -> {
                try {
                    boolean check = false;
                    if (type == AopEnum.Add_Navigate) check = a.isAdd();
                    if (type == AopEnum.Delete_Navigate) check = a.isDelete();
                    if (type == AopEnum.Update_Navigate) check = a.isUpdate();
                    if (check && a.getMemberType() != Map.class && !a.isList()) {
                        Object item = ReflectUtil.get(model,a.getMemberName());
                        if (item != null) {
                            Map<String, Object> dic = new HashMap<>();
                            dic.put("model", item);
                            dic.put("navigate", a);
                            result.add(dic);
                        }
                    }

                    if (check && a.getMemberType() == List.class && a.isList()) {
                        Object item = ReflectUtil.get(model,a.getMemberName());
                        if (item != null) {
                            Map<String, Object> dic = new HashMap<>();
                            dic.put("model", item);
                            dic.put("navigate", a);
                            result.add(dic);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    private List<DataReturn> addNavigate(Object model,List<Map<String,Object>> list) {
        List<DataReturn> result = new ArrayList<>();
        list.forEach(a -> {
            Object item = a.get("model");
            NavigateModel navigateModel = (NavigateModel) a.get("navigate");
            if (navigateModel.isAdd() && navigateModel.isList())
                ((ArrayList) item).forEach(i -> {
                    result.add(add(i,navigateModel));
                });

            if (navigateModel.isAdd() && !navigateModel.isList())
                result.add(add(item,navigateModel));
        });
        return result;
    }

    private List<DataReturn> deleteNavigate(Object model,List<Map<String,Object>> list){
        List<DataReturn> result = new ArrayList<>();
        list.forEach(a -> {
            Object item = a.get("model");
            NavigateModel navigateModel = (NavigateModel) a.get("navigate");
            if (navigateModel.isAdd() && navigateModel.isList())
                ((ArrayList) item).forEach(i -> {
                    result.add(delete(i,navigateModel));
                });

            if (navigateModel.isAdd() && !navigateModel.isList())
                result.add(delete(item,navigateModel));
        });
        return result;
    }

    private List<DataReturn> updateNavigate(Object model,List<Map<String,Object>> list){
        List<DataReturn> result = new ArrayList<>();
        list.forEach(a -> {
            Object item = a.get("model");
            NavigateModel navigateModel = (NavigateModel) a.get("navigate");
            if (navigateModel.isAdd() && navigateModel.isList())
                ((ArrayList) item).forEach(i -> {
                    result.add(update(i,navigateModel));
                });

            if (navigateModel.isAdd() && !navigateModel.isList())
                result.add(update(item,navigateModel));
        });
        return result;
    }

    private DataReturn add(Object model,NavigateModel navigateModel) {
        DataReturn result = new DataReturn();
        MapResult insert = new MapResult();
        try {
            insert = BaseModel.insert(model);
            BaseAop.aopBefore(model.getClass().getName(), insert, config, false, AopEnum.Add_Navigate, model);
            if (!insert.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(insert.getMessage());
            } else if (insert.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(insert.getSql());
                Object[] param = insert.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, insert.getParam().get(param[i]));
                }

                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate()>0);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(insert) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, String.format("add tableName:%s,NavigateAdd:%s,MemberName:%s", model.getClass().getName(),ex.getMessage(),navigateModel.getMemberName()), AopEnum.Add_Navigate, config, model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(String.format("add tableName:%s,NavigateAdd:%s,MemberName:%s", model.getClass().getName(),ex.getMessage(),navigateModel.getMemberName()));
        }

        close(null, insert);
        BaseAop.aopAfter(model.getClass().getName(), insert, config, false, AopEnum.Add_Navigate, result.getWriteReturn(), model);
        return result;
    }

    private DataReturn delete(Object model,NavigateModel navigateModel) {
        DataReturn result = new DataReturn();
        MapResult delete = new MapResult();

        try {
            delete = BaseModel.delete(model,config,conn);
            BaseAop.aopBefore(model.getClass().getName(), delete, config, false, AopEnum.Delete_Navigate, model);
            if (!delete.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(delete.getMessage());
            } else if (delete.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(delete.getSql());
                Object[] param = delete.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, delete.getParam().get(param[i]));
                }

                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate()>0);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(delete) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, String.format("delete tableName:%s,NavigateDelete:%s,MemberName:%s", model.getClass().getName(),ex.getMessage(),navigateModel.getMemberName()), AopEnum.Delete_Navigate, config, model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(String.format("delete tableName:%s,NavigateDelete:%s,MemberName:%s", model.getClass().getName(),ex.getMessage(),navigateModel.getMemberName()));
        }

        close(null, delete);
        BaseAop.aopAfter(model.getClass().getName(), delete, config, false, AopEnum.Delete_Navigate, result.getWriteReturn(), model);
        return result;
    }

    private DataReturn update(Object model,NavigateModel navigateModel) {
        DataReturn result = new DataReturn();
        MapResult update = new MapResult();

        try {
            update = BaseModel.update(model,config,conn);
            BaseAop.aopBefore(model.getClass().getName(), update, config, false, AopEnum.Update_Navigate, model);
            if (!update.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else if (update.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(update.getSql());
                Object[] param = update.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, update.getParam().get(param[i]));
                }

                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate()>0);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            BaseAop.aopException(ex, String.format("update tableName:%s,NavigateUpdate:%s,MemberName:%s", model.getClass().getName(),ex.getMessage(),navigateModel.getMemberName()), AopEnum.Update_Navigate, config, model);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(String.format("update tableName:%s,NavigateUpdate:%s,MemberName:%s", model.getClass().getName(),ex.getMessage(),navigateModel.getMemberName()));
        }

        close(null, update);
        BaseAop.aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_Navigate, result.getWriteReturn(), model);
        return result;
    }

    private <T> T setModel(Class<?> type,ResultSetMetaData col,ResultSet resultSet, List<PropertyModel> property ) throws InstantiationException, IllegalAccessException, SQLException {
        T model = (T) type.newInstance();
        for (int i = 1; i <= col.getColumnCount(); i++) {
            String name = col.getColumnName(i);
            if (property.stream().anyMatch(a -> a.getName().equalsIgnoreCase(name))) {
                PropertyModel pInfo = property.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().get();
                Object value = resultSet.getObject(name);
                if (!resultSet.wasNull())
                    ReflectUtil.set(model, value, pInfo.getName(), pInfo.getType());
            } else
                continue;
        }
        return model;
    }
}

class PoolUtil {
    public static synchronized PoolModel getConnection(DbConfig dbconfig, String cacheKey) {
        PoolModel model = new PoolModel();
        try {
            dbconfig = DataConfig.db(dbconfig.getKey());
            Class.forName(dbconfig.getProviderName());
            List<PoolModel> pool = CacheUtil.getList(cacheKey, PoolModel.class);
            pool = pool == null ? new ArrayList<PoolModel>() : pool;

            if (pool.stream().filter(a -> !a.isUse()).count() < 1) {
                Connection conn = DriverManager.getConnection(dbconfig.getConnStr(), dbconfig.getUser(), dbconfig.getPassWord());
                model.setUse(true);
                model.setConn(conn);
                model.setId(UUID.randomUUID().toString());
                model.setKey(dbconfig.getKey());
                pool.add(model);

            } else if (pool.stream().anyMatch(a -> !a.isUse())) {
                model = pool.stream().filter(a -> !a.isUse()).findFirst().get();
                model.setUse(true);
            }
            CacheUtil.setModel(cacheKey, pool);
        } catch (Exception ex) {
            IFastDataAop aop =  CacheUtil.getModel("FastAop", IFastDataAop.class);
            if (aop != null)
                BaseAop.aopException(ex,"DataContext open key :" + dbconfig.getKey(),AopEnum.Pool_Get,dbconfig,null);

            if (dbconfig.isOutError())
                ex.printStackTrace();
            if (dbconfig.isOutError())
                LogUtil.error(ex);
        }
        return model;
    }

    public static synchronized void close(DbConfig config, Connection conn, String cacheKey, String id) {
        try {
            if (conn != null) {
                List<PoolModel> pool = CacheUtil.getList(cacheKey, PoolModel.class);
                Optional<PoolModel> temp = pool.stream().filter(a -> a.getId().equals(id)).findFirst();
                if (temp.isPresent()) {
                    PoolModel model = temp.get();
                    pool.remove(model);
                    if (pool.size() > config.getPoolSize())
                        model.getConn().close();
                    else {
                        model.setUse(false);
                        pool.add(model);
                    }
                    CacheUtil.setModel(cacheKey, pool);
                } else
                    conn.close();
            }
        } catch (Exception ex) {
            IFastDataAop aop =  CacheUtil.getModel("FastAop", IFastDataAop.class);
            if (aop != null)
                BaseAop.aopException(ex,"DataContext close key :" + config.getKey(),AopEnum.Pool_Close,config,null);

            if (config.isOutError())
                ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
    }
}