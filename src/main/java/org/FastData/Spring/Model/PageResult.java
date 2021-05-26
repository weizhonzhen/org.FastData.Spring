package org.FastData.Spring.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Data
public class PageResult {
    private PageModel pModel = new PageModel();
    private List<Map<String, Object>> list = new ArrayList<>();
}
