package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.List;

public class PageResultImpl<T> {
    private PageModel pModel = new PageModel();

    private List<T> list = new ArrayList<T>();

    public PageModel getpModel() {
        return pModel;
    }

    public void setpModel(PageModel pModel) {
        this.pModel = pModel;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
