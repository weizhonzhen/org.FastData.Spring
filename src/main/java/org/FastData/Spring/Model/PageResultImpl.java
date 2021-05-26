package org.FastData.Spring.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PageResultImpl<T> {
    private PageModel pModel = new PageModel();

    private List<T> list = new ArrayList<T>();
}
