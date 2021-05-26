package org.FastData.Spring.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class XmlModel {
    private List<String> key = new ArrayList<String>();

    private List<String> sql = new ArrayList<String>();

    private Map<String, Object> db = new HashMap<>();

    private Map<String, Object> param = new HashMap<>();

    private Map<String, Object> name = new HashMap<>();

    private Map<String, Object> check = new HashMap<>();

    private boolean isSuccess;
}
