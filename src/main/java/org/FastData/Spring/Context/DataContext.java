package org.FastData.Spring.Context;

import org.FastData.Spring.Base.BaseModel;
import org.FastData.Spring.Base.DataConfig;
import org.FastData.Spring.CacheModel.DbConfig;
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

    public DataContext(String key) {
        try {
            config = DataConfig.db(key);
            Class.forName(config.providerName);
            conn = DriverManager.getConnection(config.connStr, config.user, config.passWord);
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
            if (map.param.size() != 0)
                result.sql = getSql(map);
            else
                result.sql = map.sql;

            if (map.param.size() != 0) {
                preparedStatement = conn.prepareStatement(map.sql);
                for (int i = 0; i < map.name.size(); i++) {
                    preparedStatement.setObject(i + 1, map.param.get(map.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.sql);
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                T model = type.newInstance();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    if (property.stream().anyMatch(a -> a.name.equalsIgnoreCase(name))) {
                        PropertyModel pInfo = property.stream().filter(a -> a.name.equalsIgnoreCase(name)).findFirst().get();
                        ReflectUtil.set(model, resultSet.getObject(name), pInfo.name, pInfo.type);
                    } else
                        continue;
                }
                result.list.add(model);
            }
            resultSet.close();
            if (config.isOutSql)
                System.out.println("\033[35;4m" + result.sql + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
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
            if (map.param.size() != 0)
                result.sql = getSql(map);
            else
                result.sql = map.sql;

            if (map.param.size() != 0) {
                preparedStatement = conn.prepareStatement(map.sql);
                for (int i = 0; i < map.name.size(); i++) {
                    preparedStatement.setObject(i + 1, map.param.get(map.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.sql);
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                Map model = new HashMap<String, Object>();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    model.put(name, resultSet.getObject(name));
                }
                result.list.add(model);
            }
            resultSet.close();
            if (config.isOutSql)
                System.out.println("\033[35;4m" + result.sql + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
        }

        return result;
    }

    private int pageCount(MapResult map) {
        try {
            ResultSet resultSet;
            String sql = String.format("select count(0) count from (%s)t", map.sql);
            if (map.param.size() != 0) {
                preparedStatement = conn.prepareStatement(sql);
                for (int i = 0; i < map.name.size(); i++) {
                    preparedStatement.setObject(i + 1, map.param.get(map.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.sql);
            }
            while (resultSet.next()) {
                return resultSet.getInt("count");
            }
            if (config.isOutSql) {
                map.sql = sql;
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
            }
        } catch (Exception ex) {
            if (config.isOutError)
                ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
        }
        return 0;
    }

    private ResultSet pageResult(PageModel pModel, MapResult map) {
        ResultSet resultSet = null;
        try {
            if (config.dbType.equalsIgnoreCase(DataDbType.Oracle))
                map.sql = String.format("select * from(select field.*,ROWNUM RN from(%s) field where rownum<=%s) where rn>=%s"
                        , map.sql, pModel.endId, pModel.starId);

            if (config.dbType.equalsIgnoreCase(DataDbType.SqlServer))
                map.sql = String.format("select top %s * from (select row_number()over(order by tempcolumn)temprownumber,* from(select tempcolumn = 0, * from (%s)t)tt)ttt where temprownumber >= %s"
                        , map.sql, pModel.pageSize, pModel.starId - 1);

            if (config.dbType.equalsIgnoreCase(DataDbType.MySql))
                map.sql = String.format("%s limit %s,%s", map.sql, pModel.starId, pModel.pageSize);

            if (config.dbType.equalsIgnoreCase(DataDbType.DB2))
                map.sql = String.format("select * from(select field.*,ROWNUM RN from(%s) field where rownum<=%s) where rn>=%s"
                        , map.sql, pModel.endId, pModel.starId);

            if (config.dbType.equalsIgnoreCase(DataDbType.PostgreSql))
                map.sql = String.format("%s limit %s offset %s", map.sql, pModel.pageSize, pModel.starId);

            if (config.dbType.equalsIgnoreCase(DataDbType.SQLite))
                map.sql = String.format("%s limit %s offset %s", map.sql, pModel.pageSize, pModel.starId);

            if (map.param.size() != 0) {
                preparedStatement = conn.prepareStatement(map.sql);
                for (int i = 0; i < map.name.size(); i++) {
                    preparedStatement.setObject(i + 1, map.param.get(map.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.sql);
            }

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
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
            pModel.starId = (pModel.pageId - 1) * pModel.pageSize + 1;
            pModel.endId = pModel.pageId * pModel.pageSize;
            pModel.totalRecord = pageCount(map);
            if (pModel.totalRecord > 0) {
                if ((pModel.totalRecord % pModel.pageSize) == 0)
                    pModel.totalPage = pModel.totalRecord / pModel.pageSize;
                else
                    pModel.totalPage = (pModel.totalRecord / pModel.pageSize) + 1;

                if (pModel.pageId > pModel.totalPage)
                    pModel.pageId = pModel.totalPage;

                ResultSet resultSet = pageResult(pModel, map);
                if (resultSet != null) {
                    ResultSetMetaData col = resultSet.getMetaData();
                    while (resultSet.next()) {
                        T model = type.newInstance();
                        for (int i = 1; i <= col.getColumnCount(); i++) {
                            String name = col.getColumnName(i);
                            if (property.stream().anyMatch(a -> a.name.equalsIgnoreCase(name))) {
                                PropertyModel pInfo = property.stream().filter(a -> a.name.equalsIgnoreCase(name)).findFirst().get();
                                ReflectUtil.set(model, resultSet.getObject(name), pInfo.name, pInfo.type);
                            } else
                                continue;
                        }
                        result.list.add(model);
                    }
                    resultSet.close();
                }
                result.pModel = pModel;
            } else
                return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
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
            pModel.starId = (pModel.pageId - 1) * pModel.pageSize + 1;
            pModel.endId = pModel.pageId * pModel.pageSize;
            pModel.totalRecord = pageCount(map);
            if (pModel.totalRecord > 0) {
                if ((pModel.totalRecord % pModel.pageSize) == 0)
                    pModel.totalPage = pModel.totalRecord / pModel.pageSize;
                else
                    pModel.totalPage = (pModel.totalRecord / pModel.pageSize) + 1;

                if (pModel.pageId > pModel.totalPage)
                    pModel.pageId = pModel.totalPage;

                ResultSet resultSet = pageResult(pModel, map);
                if (resultSet != null) {
                    ResultSetMetaData col = resultSet.getMetaData();
                    while (resultSet.next()) {
                        Map model = new HashMap<String, Object>();
                        for (int i = 1; i <= col.getColumnCount(); i++) {
                            String name = col.getColumnName(i);
                            model.put(name, resultSet.getObject(name));
                        }
                        result.list.add(model);
                    }
                    resultSet.close();
                }
                result.pModel = pModel;
            } else
                return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
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

            if (insert.param.size() != 0) {
                preparedStatement = conn.prepareStatement(insert.sql);
                for (int i = 0; i < insert.name.size(); i++) {
                    preparedStatement.setObject(i + 1, insert.param.get(insert.name.get(i)));
                }
                result.writeReturn.isSuccess = preparedStatement.execute();
            }

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(insert) + "\033[0m");

        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
            result.writeReturn.isSuccess = false;
            result.writeReturn.message = ex.getMessage();
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

            if (!delete.isSuccess) {
                result.writeReturn.isSuccess = false;
                result.writeReturn.message = delete.message;
            } else {
                preparedStatement = conn.prepareStatement(delete.sql);
                for (int i = 0; i < delete.name.size(); i++) {
                    preparedStatement.setObject(i + 1, delete.param.get(delete.name.get(i)));
                }
                result.writeReturn.isSuccess = preparedStatement.executeUpdate() > 0;
            }

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(delete) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
            result.writeReturn.isSuccess = false;
            result.writeReturn.message = ex.getMessage();
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

            if (!update.isSuccess) {
                result.writeReturn.isSuccess = false;
                result.writeReturn.message = update.message;
            } else {
                preparedStatement = conn.prepareStatement(update.sql);
                for (int i = 0; i < update.name.size(); i++) {
                    preparedStatement.setObject(i + 1, update.param.get(update.name.get(i)));
                }
                result.writeReturn.isSuccess = preparedStatement.executeUpdate() > 0;
            }

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
            result.writeReturn.isSuccess = false;
            result.writeReturn.message = ex.getMessage();
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

            if (!update.isSuccess) {
                result.writeReturn.isSuccess = false;
                result.writeReturn.message = update.message;
            } else {
                preparedStatement = conn.prepareStatement(update.sql);
                for (int i = 0; i < update.name.size(); i++) {
                    preparedStatement.setObject(i + 1, update.param.get(update.name.get(i)));
                }
                result.writeReturn.isSuccess = preparedStatement.executeUpdate() > 0;
            }

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(update) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
            result.writeReturn.isSuccess = false;
            result.writeReturn.message = ex.getMessage();
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
            if (!exists.isSuccess) {
                result.writeReturn.isSuccess = false;
                result.writeReturn.message = exists.message;
            } else {
                preparedStatement = conn.prepareStatement(exists.sql);
                for (int i = 0; i < exists.name.size(); i++) {
                    preparedStatement.setObject(i + 1, exists.param.get(exists.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    result.count = Integer.parseInt(resultSet.getObject(1).toString());
                }
            }

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(exists) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
            result.writeReturn.isSuccess = false;
            result.writeReturn.message = ex.getMessage();
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
            if (query.param.size() != 0)
                result.sql = getSql(query);
            else
                result.sql = query.sql;

            if (query.param.size() != 0) {
                preparedStatement = conn.prepareStatement(query.sql);
                for (int i = 0; i < query.name.size(); i++) {
                    preparedStatement.setObject(i + 1, query.param.get(query.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(query.sql);
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                Map map = new HashMap<String, Object>();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    map.put(name, resultSet.getObject(name));
                }
                result.item = map;
            }
            resultSet.close();

            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(query) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
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
            if (query.param.size() != 0)
                result.sql = getSql(query);
            else
                result.sql = query.sql;

            if (query.param.size() != 0) {
                preparedStatement = conn.prepareStatement(query.sql);
                for (int i = 0; i < query.name.size(); i++) {
                    preparedStatement.setObject(i + 1, query.param.get(query.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(query.sql);
            }
            ResultSetMetaData col = resultSet.getMetaData();
            while (resultSet.next()) {
                T item = (T) type.newInstance();
                for (int i = 1; i <= col.getColumnCount(); i++) {
                    String name = col.getColumnName(i);
                    if (property.stream().anyMatch(a -> a.name.equalsIgnoreCase(name))) {
                        PropertyModel pInfo = property.stream().filter(a -> a.name.equalsIgnoreCase(name)).findFirst().get();
                        ReflectUtil.set(item, resultSet.getObject(name), pInfo.name, pInfo.type);
                    } else
                        continue;
                }
                result.item = item;
            }
            resultSet.close();
            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(query) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
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
            if (map.param.size() != 0) {
                preparedStatement = conn.prepareStatement(map.sql);
                for (int i = 0; i < map.name.size(); i++) {
                    preparedStatement.setObject(i + 1, map.param.get(map.name.get(i)));
                }
                resultSet = preparedStatement.executeQuery();
            } else {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(map.sql);
            }
            while (resultSet.next()) {
                count = Integer.parseInt(resultSet.getObject(1).toString());
            }
            resultSet.close();
            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            if (config.isOutError)
                ex.printStackTrace();
            if (config.isOutError)
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
            if (map.param.size() != 0) {
                preparedStatement = conn.prepareStatement(map.sql);
                for (int i = 0; i < map.name.size(); i++) {
                    preparedStatement.setObject(i + 1, map.param.get(map.name.get(i)));
                }
                result.writeReturn.isSuccess = preparedStatement.executeUpdate() > 0;
            } else {
                statement = conn.createStatement();
                result.writeReturn.isSuccess = statement.execute(map.sql);
            }
            if (config.isOutSql)
                System.out.println("\033[35;4m" + getSql(map) + "\033[0m");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (config.isOutError)
                LogUtil.error(ex);
        }
        return result;
    }

    private String getSql(MapResult map) {
        if (map.param == null || map.param.size() == 0)
            return map.sql;

        StringBuilder result = new StringBuilder();
        result.append(map.sql);
        map.param.forEach((k, v) -> {
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

    public void close() {
        try {
            if (statement != null)
                statement.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}