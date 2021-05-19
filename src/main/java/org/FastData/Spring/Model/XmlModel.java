package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlModel {
    public List<String> key = new ArrayList<String>();

    public List<String> sql = new ArrayList<String>();

    public Map<String, Object> db = new HashMap<>();

    public Map<String, Object> param = new HashMap<>();

    public Map<String, Object> name = new HashMap<>();

    public Map<String, Object> check = new HashMap<>();

    public boolean isSuccess;
}
