package org.FastData.Spring.CheckModel;

import lombok.Data;

@Data
public class ColumnModel {

    private boolean isKey = false;

    private String name;

    private String dataType;

    private int length;

    private int precision;

    private int scale;

    private boolean isNull = true;

    private String comments = "";
}