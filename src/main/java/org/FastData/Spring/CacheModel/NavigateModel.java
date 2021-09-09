package org.FastData.Spring.CacheModel;

import java.io.Serializable;

public class NavigateModel implements Serializable {
    private Class<?> propertyType;
    private String name;
    private Class<?> type;
    private boolean isList;
    private String appand;
    private String memberName;
    private Class<?> memberType;

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public String getAppand() {
        return appand;
    }

    public void setAppand(String appand) {
        this.appand = appand;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Class<?> getMemberType() {
        return memberType;
    }

    public void setMemberType(Class<?> memberType) {
        this.memberType = memberType;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
