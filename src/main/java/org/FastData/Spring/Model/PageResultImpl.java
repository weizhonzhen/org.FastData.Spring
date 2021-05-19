package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.List;

public class PageResultImpl<T> {
    public PageModel pModel = new PageModel();

    public List<T> list = new ArrayList<T>();
}
