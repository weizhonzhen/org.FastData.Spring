package org.FastData.Spring.Aop;

public class ExceptionContext {
    private String dbType ;

    private int aopType ;

    private String name ;

    private Exception ex;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public int getAopType() {
        return aopType;
    }

    public void setAopType(int aopType) {
        this.aopType = aopType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }
}
