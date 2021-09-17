package org.FastData.Spring.Aop;

public class ExceptionContext {
    private String dbType ;

    private int aopType ;

    private String name ;

    private Exception ex;

    public String getDbType() {
        return dbType;
    }

    void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public int getAopType() {
        return aopType;
    }

    void setAopType(int aopType) {
        this.aopType = aopType;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Exception getEx() {
        return ex;
    }

    void setEx(Exception ex) {
        this.ex = ex;
    }
}
