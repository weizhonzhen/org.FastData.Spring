package org.FastData.Spring.Context;

import org.FastData.Spring.Aop.*;
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
                aopBefore(type.getName(), map, config, true, AopEnum.Query_List);
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
            aopException(ex, "to list tableName:" + type.getName(), AopEnum.Query_List, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            aopAfter(type.getName(), map, config, true, AopEnum.Query_List, result);
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
                aopBefore(null, map, config, true, AopEnum.Query_Dic);
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
            aopException(ex, "to List<map>", AopEnum.Query_Dic, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            aopAfter(null, map, config, true, AopEnum.Query_Dic, result.getList());
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
                aopBefore(type.getName(), map, config, true, AopEnum.Map_Page_Model);

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
            aopException(ex, "tp page tableName:" + type.getName(), AopEnum.Map_Page_Model, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            aopAfter(type.getName(), map, config, true, AopEnum.Map_Page_Model, result);
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
                    aopBefore(null, map, config, true, AopEnum.Query_Page_Dic);

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
            aopException(ex, "to page", AopEnum.Query_Page_Dic, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            aopAfter(null, map, config, true, AopEnum.Query_Page_Dic, result);
        return result;
    }

    /*
    insert table
     model: database table
   */
    public DataReturn add(Object model) {
        DataReturn result = new DataReturn();
        MapResult insert = new MapResult();
        try {
            insert = BaseModel.insert(model);
            aopBefore(model.getClass().getName(), insert, config, false, AopEnum.Add);
            if (!insert.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(insert.getMessage());
            } else {
                if (insert.getParam().size() != 0) {
                    preparedStatement = conn.prepareStatement(insert.getSql());
                    Object[] param = insert.getParam().keySet().toArray();
                    for (int i = 0; i < param.length; i++) {
                        preparedStatement.setObject(i + 1, insert.getParam().get(param[i]));
                    }
                    preparedStatement.execute();
                    result.getWriteReturn().setSuccess(true);
                }
            }
            close(null, insert);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(insert) + "\033[0m");
        } catch (Exception ex) {
            aopException(ex, "add tableName:" + model.getClass().getName(), AopEnum.Add, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }

        aopAfter(model.getClass().getName(), insert, config, false, AopEnum.Add, result.getWriteReturn().getSuccess());
        return result;
    }

    /*
     delete table by primary Key
     model: database table
    */
    public DataReturn delete(Object model) {
        DataReturn result = new DataReturn();
        MapResult delete = new MapResult();
        try {
            delete = BaseModel.delete(model, config, conn);
            aopBefore(model.getClass().getName(), delete, config, false, AopEnum.Delete_PrimaryKey);

            if (!delete.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(delete.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(delete.getSql());
                Object[] param = delete.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, delete.getParam().get(param[i]));
                }

                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
            }
            close(null, delete);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(delete) + "\033[0m");
        } catch (Exception ex) {
            aopException(ex, "delete by Primary Key tableName:" + model.getClass().getName(), AopEnum.Delete_PrimaryKey, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        aopAfter(model.getClass().getName(), delete, config, false, AopEnum.Delete_PrimaryKey, result.getWriteReturn().getSuccess());
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

        //aopBefore(type.getName(), param, config, false,AopEnum.Delete);
        DataReturn result = execute(param);
        //aopAfter(type.getName(), param, config, false,AopEnum.Delete, result);
        return result;
    }

    /*
     update table by primary Key
     model: database table
     field: update field
    */
    public DataReturn update(Object model, List<String> field) {
        DataReturn result = new DataReturn();
        MapResult update = new MapResult();
        try {
            update = BaseModel.update(model, field, config, conn);
            aopBefore(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey);

            if (!update.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(update.getSql());
                Object[] param = update.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, update.getParam().get(param[i]));
                }
                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
            }
            close(null, update);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            aopException(ex, "update by Primary Key tableName " + model.getClass().getName(), AopEnum.Update_PrimaryKey, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey, result.getWriteReturn().getSuccess());
        return result;
    }

    /*
     update table by primary Key
     model: database table
    */
    public DataReturn update(Object model) {
        DataReturn result = new DataReturn();
        MapResult update = new MapResult();
        try {
            update = BaseModel.update(model, null, config, conn);
            aopBefore(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey);

            if (!update.isSuccess()) {
                result.getWriteReturn().setSuccess(false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(update.getSql());
                Object[] param = update.getParam().keySet().toArray();
                for (int i = 0; i < param.length; i++) {
                    preparedStatement.setObject(i + 1, update.getParam().get(param[i]));
                }
                result.getWriteReturn().setSuccess(preparedStatement.executeUpdate() > 0);
            }
            close(null, update);

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            aopException(ex, "update by Primary Key tableName " + model.getClass().getName(), AopEnum.Update_PrimaryKey, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        aopAfter(model.getClass().getName(), update, config, false, AopEnum.Update_PrimaryKey, result.getWriteReturn().getSuccess());
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
            aopBefore(model.getClass().getName(), exists, config, true, AopEnum.Exists_PrimaryKey);

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
            aopException(ex, "exists tableName by Primary Key" + model.getClass().getName(), AopEnum.Exists_PrimaryKey, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        aopAfter(model.getClass().getName(), exists, config, true, AopEnum.Exists_PrimaryKey, result.getWriteReturn().getSuccess());
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
            aopBefore(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey_Dic);
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
            aopException(ex, "query by Primary Key tableName " + model.getClass().getName(), AopEnum.Query_PrimaryKey_Dic, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        aopAfter(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey_Dic, result.getItem());
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
            aopBefore(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey);

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
            aopException(ex, "query by Primary Key tableName " + model.getClass().getName(), AopEnum.Query_PrimaryKey, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        aopAfter(model.getClass().getName(), query, config, true, AopEnum.Query_PrimaryKey, result.getItem());
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
            aopBefore(null, map, config, true, AopEnum.Query_Count);
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
            aopException(ex, "query count ", AopEnum.Query_Count, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            aopAfter(null, map, config, true, AopEnum.Query_Count, count);
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
        aopBefore(null, param, config, true, AopEnum.Query_Count);
        int result = count(param);
        aopAfter(null, param, config, true, AopEnum.Query_Count, result);
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
            aopBefore(null, map, config, false, AopEnum.Execute_Sql_Bool);
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
            aopException(ex, "execute sql", AopEnum.Execute_Sql_Bool, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        if (isAop)
            aopAfter(null, map, config, false, AopEnum.Execute_Sql_Bool, result.getWriteReturn().getSuccess());
        return result;
    }

    public WriteReturn executeParam(MapResult map) {
        WriteReturn result = new WriteReturn();
        aopBefore(null, map, config, false, AopEnum.Execute_Sql_Bool);
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
            aopException(ex, "execute sql", AopEnum.Execute_Sql_Bool, config);
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        aopAfter(null, map, config, false, AopEnum.Execute_Sql_Bool, result.getSuccess());
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
            if (map.getParam().size() != 0)
                preparedStatement.close();
            else
                statement.close();
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

    private void aopBefore(String tableName, MapResult result, DbConfig config, boolean isRead, int aopType) {
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            BeforeContext context = new BeforeContext();

            if (!FastUtil.isNullOrEmpty(tableName))
                context.setTableName(tableName);

            if (result != null) {
                context.setSql(result.getSql());

                if (result.getParam() != null)
                    context.setParam(result.getParam());
            } else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            context.setDbType(config.getDbType());
            context.setIsRead(isRead);
            context.setIsWrite(!isRead);
            context.setAopType(aopType);

            aop.before(context);
        }
    }

    private void aopAfter(String tableName, MapResult result, DbConfig config, boolean isRead, int aopType, Object data) {
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            AfterContext context = new AfterContext();

            if (!FastUtil.isNullOrEmpty(tableName))
                context.setTableName(tableName);

            if (result != null) {
                context.setSql(result.getSql());
                if (result.getParam() != null)
                    context.setParam(result.getParam());
            } else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            context.setAopType(aopType);
            context.setDbType(config.getDbType());
            context.setRead(isRead);
            context.setWrite(!isRead);
            context.setResult(data);

            aop.after(context);
        }
    }

    private void aopException(Exception ex, String name, int aopType, DbConfig config) {
        if (FastDataConfig.getAop() != null) {
            ExceptionContext context = new ExceptionContext();
            context.setAopType(aopType);
            context.setEx(ex);
            context.setName(name);
            if (config != null)
                context.setDbType(config.getDbType());
            FastDataConfig.getAop().exception(context);
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
                        sql.append(String.format("and %s=? ", a.getName().get(i)));
                        param.put(a.getName().get(i), ReflectUtil.get(data, a.getName().get(i), a.getType()));
                        mapResult.setParam(param);
                    }

                    mapResult.setSql(sql.toString());
                    aopBefore(tableName, mapResult, config, true, AopEnum.Query_Navigate);

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
                    else
                        ReflectUtil.set(data, result.get(0), a.getMemberName(), a.getMemberType());

                    close(resultSet, mapResult);
                    aopAfter(tableName, mapResult, config, true, AopEnum.Query_Navigate, data);
                } catch (Exception ex) {
                    aopException(ex, "navigate tableName:" + tableName, AopEnum.Query_Navigate, config);
                    ex.printStackTrace();
                    if (config.isOutError())
                        LogUtil.error(ex);
                }
            });
        }
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
            if (FastDataConfig.getAop() != null) {
                ExceptionContext context = new ExceptionContext();
                context.setEx(ex);
                if (dbconfig != null) {
                    context.setDbType(dbconfig.getDbType());
                    context.setName("DataContext open key :" + dbconfig.getKey());
                }
                context.setAopType(AopEnum.Pool_Get);
                FastDataConfig.getAop().exception(context);
            }
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
            if (FastDataConfig.getAop() != null) {
                ExceptionContext context = new ExceptionContext();
                context.setEx(ex);
                if (config != null) {
                    context.setDbType(config.getDbType());
                    context.setName("DataContext close key :" + config.getKey());
                }
                context.setAopType(AopEnum.Pool_Close);
                FastDataConfig.getAop().exception(context);
            }
            if (config.isOutError())
                ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
    }
}