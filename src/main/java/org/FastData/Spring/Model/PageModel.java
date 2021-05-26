package org.FastData.Spring.Model;

import lombok.Data;

@Data
public class PageModel {

    private int starId;

    private int endId;

    private int totalRecord;

    private int totalPage;

    private int pageId = 1;

    private int pageSize = 10;
}
