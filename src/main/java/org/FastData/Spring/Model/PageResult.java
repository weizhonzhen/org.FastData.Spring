package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageResult {
    private PageModel pModel = new PageModel();

    private List<Map<String, Object>> list = new ArrayList<>();

    public PageModel getpModel() {
        return pModel;
    }

    public void setpModel(PageModel pModel) {
        this.pModel = pModel;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }
}
