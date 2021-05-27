package org.FastData.Spring.Context;

import org.FastData.Spring.Base.BaseModel;
import org.FastData.Spring.Base.DataConfig;
import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.CacheModel.PoolModel;
import org.FastData.Spring.CacheModel.PropertyModel;
import org.FastData.Spring.Model.*;
import org.FastData.Spring.Config.DataDbType;
import org.FastData.Spring.Util.CacheUtil;
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
        PoolModel model = getConnection(config, cacheKey);
        conn = model.getConn();
        id = model.getId();
    }

    public synchronized void close() {
        try {
            if (statement != null)
                statement.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null) {
                List<PoolModel> pool = CacheUtil.getList(cacheKey, PoolModel.class);
                PoolModel model = pool.stream().filter(a -> a.getId() == id).findFirst().get();
                pool.remove(model);

                if (pool.size() > config.getPoolSize())
                    model.getConn().close();
                else {
                    model.setUse(false);
                    pool.add(model);
                }
                CacheUtil.setModel(cacheKey, pool);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
       query by list
    */
    public <T> DataReturnImpl<T> query(MapResult map, Class<T> type) {
        DataReturnImpl<T> result = new DataReturnImpl<T>();
        String cacheKey = type.getName();
        List<PropertyModel> property = CacheUtil.getList(cacheKey, PropertyModel.class);
        try {
            ResultSet resultSet;
            if (map.getParam().size() != 0)
                result.setSql(getSql(map));
            else
                result.setSql(map.getSql());

            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                for (int i = 0; i < map.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(map.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                T model = type.newInstance();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    if (property.stream().anyMatch(a -> a.getName().equalsIgnoreCase(name))) {
                        PropertyModel pInfo = property.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().get();
                        ReflectUtil.set(model, resultSet.getObject(name), pInfo.getName(), pInfo.getType());
                    } else
                        continue;
                }
                result.getList().add(model);
            }
            resultSet.close();
            if (config.isOutSql())
                System.out.println("\033[35;4m" + result.getSql() + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return result;
    }

    /*
       query by list
    */
    public DataReturn query(MapResult map) {
        DataReturn result = new DataReturn();
        try {
            ResultSet resultSet;
            if (map.getParam().size() != 0)
                result.setSql(getSql(map));
            else
                result.setSql(map.getSql());

            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                for (int i = 0; i < map.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(map.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                Map model = new HashMap<String, Object>();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    model.put(name, resultSet.getObject(name));
                }
                result.getList().add(model);
            }
            resultSet.close();
            if (config.isOutSql())
                System.out.println("\033[35;4m" + result.getSql() + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }

        return result;
    }

    private int pageCount(MapResult map) {
        try {
            ResultSet resultSet;
            String sql = String.format("select count(0) count from (%s)t", map.getSql());
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(sql);
                for (int i = 0; i < map.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(map.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(sql);
            }
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
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
                for (int i = 0; i < map.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(map.getName().get(i)));
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

    /*
       query by list
    */
    public <T> PageResultImpl<T> page(PageModel pModel, MapResult map, Class<T> type) {
        PageResultImpl<T> result = new PageResultImpl<T>();
        String cacheKey = type.getName();
        List<PropertyModel> property = CacheUtil.getList(cacheKey, PropertyModel.class);
        try {
            pModel.setStarId((pModel.getPageId() - 1) * pModel.getPageSize() + 1);
            pModel.setEndId(pModel.getPageId() * pModel.getPageSize());
            pModel.setTotalRecord( pageCount(map));
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
                        T model = type.newInstance();
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
                        result.getList().add(model);
                    }
                    resultSet.close();
                }
                result.setPModel( pModel);
            } else
                return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return result;
    }

    /*
      query by page
    */
    public PageResult page(PageModel pModel, MapResult map) {
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

                ResultSet resultSet = pageResult(pModel, map);
                if (resultSet != null) {
                    ResultSetMetaData col = resultSet.getMetaData();
                    while (resultSet.next()) {
                        Map model = new HashMap<String, Object>();
                        for (int i = 1; i <= col.getColumnCount(); i++) {
                            String name = col.getColumnName(i);
                            model.put(name, resultSet.getObject(name));
                        }
                        result.getList().add(model);
                    }
                    resultSet.close();
                }
                result.setPModel( pModel);
            } else
                return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return result;
    }

    /*
    insert table
     model: database table
   */
    public DataReturn add(Object model) {
        DataReturn result = new DataReturn();
        try {
            MapResult insert = BaseModel.insert(model);

            if (insert.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(insert.getSql());
                for (int i = 0; i < insert.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, insert.getParam().get(insert.getName().get(i)));
                }
                result.getWriteReturn().setIsSuccess( preparedStatement.execute());
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(insert) + "\033[0m");

        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setIsSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }

        return result;
    }

    /*
     delete table by primary Key
     model: database table
    */
    public DataReturn delete(Object model) {
        DataReturn result = new DataReturn();
        try {
            MapResult delete = BaseModel.delete(model, config, conn);

            if (!delete.isSuccess()) {
                result.getWriteReturn().setIsSuccess(false);
                result.getWriteReturn().setMessage(delete.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(delete.getSql());
                for (int i = 0; i < delete.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, delete.getParam().get(delete.getName().get(i)));
                }
                result.getWriteReturn().setIsSuccess( preparedStatement.executeUpdate() > 0);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(delete) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setIsSuccess(false);
            result.getWriteReturn().setMessage( ex.getMessage());
        }
        return result;
    }

    /*
     update table by primary Key
     model: database table
     field: update field
    */
    public DataReturn update(Object model, List<String> field) {
        DataReturn result = new DataReturn();

        try {
            MapResult update = BaseModel.update(model, field, config, conn);

            if (!update.isSuccess()) {
                result.getWriteReturn().setIsSuccess (false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(update.getSql());
                for (int i = 0; i < update.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, update.getParam().get(update.getName().get(i)));
                }
                result.getWriteReturn().setIsSuccess(preparedStatement.executeUpdate() > 0);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setIsSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        return result;
    }

    /*
     update table by primary Key
     model: database table
    */
    public DataReturn update(Object model) {
        DataReturn result = new DataReturn();

        try {
            MapResult update = BaseModel.update(model, null, config, conn);

            if (!update.isSuccess()) {
                result.getWriteReturn().setIsSuccess(false);
                result.getWriteReturn().setMessage(update.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(update.getSql());
                for (int i = 0; i < update.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, update.getParam().get(update.getName().get(i)));
                }
                result.getWriteReturn().setIsSuccess(preparedStatement.executeUpdate() > 0);
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setIsSuccess(false);
            result.getWriteReturn().setMessage( ex.getMessage());
        }
        return result;
    }

    /*
     query exists by primary Key
     model: database table
    */
    public DataReturn exists(Object model) {
        DataReturn result = new DataReturn();
        try {
            ResultSet resultSet;
            MapResult exists = BaseModel.exists(model, config, conn);
            if (!exists.isSuccess()) {
                result.getWriteReturn().setIsSuccess(false);
                result.getWriteReturn().setMessage( exists.getMessage());
            } else {
                preparedStatement = conn.prepareStatement(exists.getSql());
                for (int i = 0; i < exists.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, exists.getParam().get(exists.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result.setCount(Integer.parseInt(resultSet.getObject(1).toString()));
                }
            }

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(exists) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
            result.getWriteReturn().setIsSuccess(false);
            result.getWriteReturn().setMessage(ex.getMessage());
        }
        return result;
    }

    /*
      query model by primary Key
      model: database table
      key: database key
    */
    public DataReturn query(Object model) {
        DataReturn result = new DataReturn();
        String cacheKey = model.getClass().getName();
        List<PropertyModel> property = CacheUtil.getList(cacheKey, PropertyModel.class);
        try {
            ResultSet resultSet;
            MapResult query = BaseModel.query(model, config, conn);
            if (query.getParam().size() != 0)
                result.setSql(getSql(query));
            else
                result.setSql(query.getSql());

            if (query.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(query.getSql());
                for (int i = 0; i < query.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, query.getParam().get(query.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(query.getSql());
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                Map map = new HashMap<String, Object>();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    map.put(name, resultSet.getObject(name));
                }
                result.setItem(map);
            }
            resultSet.close();

            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(query) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return result;
    }

    /*
      query model by primary Key
      model: database table
      key: database key
    */
    public <T> DataReturnImpl<T> query(T model, Class<?> type) {
        DataReturnImpl<T> result = new DataReturnImpl<T>();
        String cacheKey = model.getClass().getName();
        List<PropertyModel> property = CacheUtil.getList(cacheKey, PropertyModel.class);
        try {
            ResultSet resultSet;
            MapResult query = BaseModel.query(model, config, conn);
            if (query.getParam().size() != 0)
                result.setSql(getSql(query));
            else
                result.setSql(query.getSql());

            if (query.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(query.getSql());
                for (int i = 0; i < query.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, query.getParam().get(query.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(query.getSql());
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                T item = (T) type.newInstance();
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
                result.setItem(item);
            }
            resultSet.close();
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(query) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return result;
    }

    /*
        query count
    */
    public int count(MapResult map) {
        int count = 0;
        try {
            ResultSet resultSet;
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                for (int i = 0; i < map.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(map.getName().get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.getSql());
            }
            while (resultSet.next()) {
                count = Integer.parseInt(resultSet.getObject(1).toString());
            }
            resultSet.close();
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
                ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
        return count;
    }

    /*
       execute sql
    */
    public DataReturn execute(MapResult map) {
        DataReturn result = new DataReturn();
        try {
            if (map.getParam().size() != 0) {
                preparedStatement = conn.prepareStatement(map.getSql());
                for (int i = 0; i < map.getName().size(); i++) {
                    preparedStatement.setObject(i + 1, map.getParam().get(map.getName().get(i)));
                }
                result.getWriteReturn().setIsSuccess(preparedStatement.executeUpdate() > 0);
            } else {
                statement = conn.createStatement();
                result.getWriteReturn().setIsSuccess(statement.execute(map.getSql()));
            }
            if (config.isOutSql())
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError())
                LogUtil.error(ex);
        }
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
            this.conn.setAutoCommit(true);
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

    private synchronized PoolModel getConnection(DbConfig dbconfig,String cacheKey) {
        PoolModel model = new PoolModel();
        try {
            dbconfig = DataConfig.db(dbconfig.getKey());
            Class.forName(dbconfig.getProviderName());
            List<PoolModel> pool = CacheUtil.getList(cacheKey, PoolModel.class);
            pool = pool == null ? new ArrayList<PoolModel>() : pool;

            if (pool.stream().filter(a -> !a.isUse()).count() < 1) {
                conn = DriverManager.getConnection(dbconfig.getConnStr(), dbconfig.getUser(), dbconfig.getPassWord());
                model.setUse(true);
                model.setConn(conn);
                model.setId(UUID.randomUUID().toString());
                id = model.getId();
                pool.add(model);

            } else if (pool.stream().anyMatch(a -> !a.isUse())) {
                model = pool.stream().filter(a -> !a.isUse()).findFirst().get();
                model.setUse(true);
                conn = model.getConn();
                id = model.getId();
            }
            CacheUtil.setModel(cacheKey, pool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
}