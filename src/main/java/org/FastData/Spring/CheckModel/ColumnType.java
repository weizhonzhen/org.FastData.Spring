package org.FastData.Spring.CheckModel;

import lombok.Data;

@Data
public class ColumnType {
    private String name;

    private String type ;

    private int length ;

    private int precision ;

    private int scale ;
}
