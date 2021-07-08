package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.List;

public class PageResult {
    private PageModel pModel = new PageModel();

    private List<FastMap<String, Object>> list = new ArrayList<>();

    public PageModel getpModel() {
        return pModel;
    }

    public void setpModel(PageModel pModel) {
        this.pModel = pModel;
    }

    public List<FastMap<String, Object>> getList() {
        return list;
    }

    public void setList(List<FastMap<String, Object>> list) {
        this.list = list;
    }
}
