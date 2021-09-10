package org.FastData.Spring.CacheModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NavigateModel implements Serializable {
    private Class<?> propertyType;
    private List<String> name =new ArrayList<>();
    private Class<?> type;
    private boolean isList;
    private List<String> appand = new ArrayList<>();
    private String memberName;
    private Class<?> memberType;

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public List<String> getAppand() {
        return appand;
    }

    public void setAppand(List<String> appand) {
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
