package org.FastData.Base;

import com.googlecode.aviator.AviatorEvaluator;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.sun.org.apache.xerces.internal.dom.DeferredTextImpl;
import org.FastData.Model.MapResult;
import org.FastData.Model.XmlModel;
import org.FastData.Util.CacheUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public final class MapXml {
    public static List<String> readXml(String xml) {
        Map map = CacheUtil.getMap("FastMap.Api");
        XmlModel result = getXmlList(xml, "sqlMap");
        for (int i = 0; i < result.key.size(); i++) {
            CacheUtil.set(result.key.get(i).toLowerCase(), result.sql.get(i));
        }

        result.db.forEach((k, v) -> {
            CacheUtil.set(String.format("%s.db", k.toLowerCase()), v.toString());
        });

        result.param.forEach((k, v) -> {
            CacheUtil.setModel(String.format("%s.param", k.toLowerCase()), v);
            result.key.add(String.format("%s.param", k.toLowerCase()));
        });

        result.name.forEach((k, v) -> {
            CacheUtil.set(k.toLowerCase(), v.toString());
            result.key.add(k.toLowerCase());
        });

        result.check.forEach((k, v) -> {
            CacheUtil.set(k.toLowerCase(), v.toString());
            result.key.add(k.toLowerCase());
        });

        return result.key;
    }

    public static MapResult getMapSql(String name, Map<String, Object> param) {
        MapResult result = new MapResult();
        StringBuilder sql = new StringBuilder();
        Map tempParam = new HashMap<String, Object>();
        name = name.toLowerCase();

        for (int i = 0; i <= Integer.parseInt(CacheUtil.get(name)); i++) {
            String txtKey = String.format("%s.%s", name, i);
            if (CacheUtil.exists(txtKey))
                sql.append(CacheUtil.get(txtKey));

            String dynKey = String.format("%S.format.%S", name, i).toLowerCase();
            String dynLtrimKey = String.format("%S.ltrim.%S", name, i).toLowerCase();
            String dynRtrimKey = String.format("%S.rtrim.%S", name, i).toLowerCase();

            if (CacheUtil.exists(dynKey) && param != null) {
                StringBuilder condtionSql = new StringBuilder();

                for (String item : CacheUtil.getList(String.format("%s.param", name), String.class)) {
                    if (param.keySet().stream().noneMatch(a -> a.equals(item)))
                        continue;
                    String tempName = item.toLowerCase();
                    if (result.name.stream().noneMatch(a -> a.equals(item))) {
                        tempParam.put(item, param.get(item));
                        result.name.add(item);
                    }

                    String key = String.format("%s.%s.%s", name, tempName, i).toLowerCase();
                    String conditionKey = String.format("%s.%s.condition.%s", name, tempName, i).toLowerCase();
                    String conditionValueKey = String.format("%s.%s.condition.value.%s", name, tempName, i).toLowerCase();

                    if (CacheUtil.exists(key)) {
                        String flagParam = String.format("?%s", tempName);
                        String tempKey = String.format("#%s#", tempName);
                        String paramSql = CacheUtil.get(key).toLowerCase();
                        String condition = CacheUtil.get(conditionKey).toLowerCase();
                        String conditionValue = CacheUtil.get(conditionValueKey);
                        switch (condition) {
                            case "isnotnullorempty": {
                                if (param.get(item) == null || param.get(item).equals("")) {
                                    tempParam.remove(item);
                                    result.name.remove(item);
                                } else {
                                    if (paramSql.contains(tempKey)) {
                                        tempParam.remove(item);
                                        result.name.remove(item);
                                        condtionSql.append(paramSql.replace(tempKey, param.get(item).toString()));
                                    } else if (paramSql.contains(flagParam))
                                        condtionSql.append(paramSql.replace(flagParam, "?"));
                                    else {
                                        tempParam.remove(item);
                                        result.name.remove(item);
                                        condtionSql.append(condtionSql);
                                    }
                                }
                                break;
                            }
                            case "if": {
                                Boolean ifSuccess = (Boolean) AviatorEvaluator.execute(CacheUtil.get(conditionValueKey).replace(item, param.get(item).toString()), null);
                                if (ifSuccess) {
                                    if (paramSql.contains(tempKey)) {
                                        tempParam.remove(key);
                                        result.name.remove(item);
                                        condtionSql.append(paramSql.replace(tempKey, param.get(item).toString()));
                                    } else if (paramSql.contains(flagParam))
                                        condtionSql.append(paramSql.replace(flagParam, "?"));
                                    else {
                                        tempParam.remove(item);
                                        result.name.remove(item);
                                        condtionSql.append(paramSql);
                                    }
                                } else {
                                    tempParam.remove(item);
                                    result.name.remove(item);
                                }
                            }
                            case "choose": {
                                String conditionOtherSql = "";
                                Boolean isSuccess = false;
                                for (int j = 0; j < Integer.parseInt(CacheUtil.get(key)); j++) {
                                    String conditionOtherKey = String.format("%s.choose.other.%s", key, j);
                                    conditionValueKey = String.format("%s.choose.condition.%s", key, j);
                                    if (CacheUtil.get(conditionOtherKey) != null)
                                        conditionOtherSql = CacheUtil.get(conditionOtherKey).toLowerCase();
                                    else {
                                        conditionKey = String.format("%s.choose.%s", key, j);
                                        String chooseSql = CacheUtil.get(conditionKey).toLowerCase();
                                        isSuccess = (Boolean) AviatorEvaluator.execute(CacheUtil.get(conditionValueKey).replace(item, param.get(item).toString()), null);

                                        if (isSuccess) {
                                            if (chooseSql.contains(tempKey)) {
                                                tempParam.remove(item);
                                                result.name.remove(item);
                                                condtionSql.append(chooseSql.replace(tempKey, param.get(item).toString()));
                                            } else if (chooseSql.contains(flagParam))
                                                condtionSql.append(chooseSql.replace(flagParam, "?"));
                                            else {
                                                tempParam.remove(item);
                                                result.name.remove(item);
                                                condtionSql.append(chooseSql);
                                            }
                                            break;
                                        }
                                    }
                                }

                                if (!isSuccess) {
                                    if (conditionOtherSql.equals("")) {
                                        tempParam.remove(item);
                                        result.name.remove(item);
                                    } else if (conditionOtherSql.contains(tempKey)) {
                                        tempParam.remove(item);
                                        result.name.remove(item);
                                        condtionSql.append(conditionOtherSql.replace(tempKey, param.get(item).toString()));
                                    } else if (conditionOtherSql.contains(flagParam))
                                        condtionSql.append(conditionOtherSql.replace(flagParam, "?"));
                                    else {
                                        tempParam.remove(item);
                                        result.name.remove(item);
                                        condtionSql.append(conditionOtherSql);
                                    }
                                }
                                break;
                            }
                            default: {
                                if (paramSql.contains(tempKey)) {
                                    tempParam.remove(item);
                                    result.name.remove(item);
                                    condtionSql.append(paramSql.replace(tempKey, param.get(item).toString()));
                                } else if (paramSql.contains(flagParam))
                                    condtionSql.append(paramSql.replace(flagParam, "?"));
                                else {
                                    tempParam.remove(item);
                                    result.name.remove(item);
                                    condtionSql.append(condtionSql);
                                }
                                break;
                            }
                        }
                    }
                }

                String rTrimValue = CacheUtil.get(dynRtrimKey);
                String lTrimValue = CacheUtil.get(dynLtrimKey);
                int rLen = condtionSql.toString().trim().length();
                if (CacheUtil.exists(dynRtrimKey) && condtionSql.toString().trim().substring(rLen - rTrimValue.length(), rLen).equals(rTrimValue)) {
                    condtionSql = new StringBuilder(condtionSql.substring(0, condtionSql.length() - rTrimValue.length()));
                }

                if (CacheUtil.exists(dynLtrimKey) && condtionSql.toString().trim().substring(0, lTrimValue.length()).equals(CacheUtil.get(dynLtrimKey))) {
                    condtionSql = new StringBuilder(condtionSql.toString().trim().substring(lTrimValue.length(), condtionSql.length() - lTrimValue.length()));
                }

                if (condtionSql.toString().length() != 0) {
                    sql.append(CacheUtil.get(dynKey));
                    sql.append(condtionSql);
                }
            }
        }

        if (CacheUtil.getList(String.format("%s.param", name), String.class).size() > 0)
            result.param = tempParam;
        else {
            assert param != null;
            for (Map.Entry<String, Object> item : param.entrySet()) {
                String tempKey = String.format("#%s#", item.getKey()).toLowerCase();
                String flagParam = String.format("?%s", item.getKey()).toLowerCase();
                if (sql.toString().toLowerCase().contains(tempKey)) {
                    String tempSql = sql.toString().toLowerCase().replace(tempKey, item.getKey());
                    sql = new StringBuilder();
                    sql.append(tempSql);
                } else if (sql.toString().toLowerCase().contains(flagParam))
                    tempParam.put(item.getKey(), item.getValue());
            }
            result.param = tempParam;
        }

        result.sql = sql.toString();

        return result;
    }

    private static XmlModel getXmlList(String xml, String xmlNode) {
        XmlModel result = new XmlModel();
        result.isSuccess = true;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader reader = new StringReader(xml);
            InputSource source = new InputSource(reader);
            Document doc = builder.parse(source);

            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                Node node = nodeList.item(j);
                if (!node.getParentNode().getNodeName().equals(xmlNode)) {
                    result.isSuccess = false;
                    throw new Exception(String.format("%s节点不存在", xmlNode));
                }

                if (node.getNodeName().equalsIgnoreCase("select")
                        || node.getNodeName().equalsIgnoreCase("update")
                        || node.getNodeName().equalsIgnoreCase("delete")) {
                    int i = 0;
                    List<String> param = new ArrayList<String>();
                    String key = node.getAttributes().getNamedItem("id").getNodeValue().toLowerCase();

                    if (result.key.stream().anyMatch(t -> t.equals(key))) {
                        result.isSuccess = false;
                        throw new Exception(String.format("key:%s已经存在", key));
                    }

                    result.key.add(key);
                    result.sql.add(String.valueOf(node.getChildNodes().getLength()));

                    if (node.getAttributes().getNamedItem("log") != null)
                        result.name.put(String.format("%s.log", key), node.getAttributes().getNamedItem("log").getNodeValue());

                    if (node.getAttributes().getNamedItem("db") != null)
                        result.db.put(key, node.getAttributes().getNamedItem("db").getNodeValue());

                    for (int child = 0; child < node.getChildNodes().getLength(); child++) {
                        Node childNode = node.getChildNodes().item(child);

                        if (childNode instanceof DeferredTextImpl) {
                            result.key.add(String.format("%s.%s", key, i));
                            result.sql.add(((DeferredTextImpl) childNode).getNodeValue());
                        }

                        if (childNode instanceof DeferredElementImpl) {
                            NodeList tempNode = childNode.getChildNodes();

                            if (childNode.getAttributes().getNamedItem("prepend") != null) {
                                result.key.add(String.format("%s.format.%s", key, i));
                                result.sql.add(childNode.getAttributes().getNamedItem("prepend").getNodeValue());
                            }
                            else
                            {
                                result.key.add(String.format("%s.format.%s", key, i));
                                result.sql.add("");
                            }

                            if (childNode.getAttributes().getNamedItem("rtrim") != null) {
                                result.key.add(String.format("%s.rtrim.%s", key, i));
                                result.sql.add(childNode.getAttributes().getNamedItem("rtrim").getNodeValue());
                            }

                            if (childNode.getAttributes().getNamedItem("ltrim") != null) {
                                result.key.add(String.format("%s.ltrim.%s", key, i));
                                result.sql.add(childNode.getAttributes().getNamedItem("ltrim").getNodeValue());
                            }

                            for (int condtion = 0; condtion < tempNode.getLength(); condtion++) {
                                Node condtionNode = tempNode.item(condtion);

                                if (condtionNode instanceof DeferredTextImpl)
                                    continue;

                                NamedNodeMap attribute = condtionNode.getAttributes();
                                result.key.add(String.format("%s.%s.condition.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                result.sql.add(condtionNode.getNodeName());

                                if (attribute.getNamedItem("required") != null) {
                                    String checkkey = String.format("%s.%s.required", key, attribute.getNamedItem("property").getNodeValue().toLowerCase());
                                    result.check.put(checkkey, attribute.getNamedItem("required").getNodeValue());
                                }

                                if (attribute.getNamedItem("property") != null)
                                    param.add(attribute.getNamedItem("property").getNodeValue());

                                if (condtionNode.getNodeName().equalsIgnoreCase("ispropertyavailable")) {
                                    result.key.add(String.format("%s.%s.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                    result.sql.add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue(), condtionNode.getTextContent()));
                                } else if (!condtionNode.getNodeName().equalsIgnoreCase("choose")) {

                                    result.key.add(String.format("%s.%s.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                    result.sql.add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue(), condtionNode.getTextContent()));

                                    if (attribute.getNamedItem("condition") != null) {
                                        result.key.add(String.format("%s.%s.condition.value.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                        result.sql.add(attribute.getNamedItem("condition").getNodeValue());
                                    }

                                    if (attribute.getNamedItem("compareValue") != null) {
                                        result.key.add(String.format("%s.%s.condition.value.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                        result.sql.add(attribute.getNamedItem("compareValue").getNodeValue());
                                    }
                                } else {
                                    int count = 0;
                                    result.key.add(String.format("%s.%s.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                    result.sql.add(String.valueOf(node.getChildNodes().getLength()));
                                    NodeList chooseNodeList = condtionNode.getChildNodes();
                                    String chooseKey = attribute.getNamedItem("property").getNodeValue().toLowerCase();
                                    for (int choose = 0; choose < chooseNodeList.getLength(); choose++) {
                                        Node chooseNode = chooseNodeList.item(choose);
                                        attribute = chooseNode.getAttributes();
                                        if (attribute == null)
                                            continue;
                                        if (chooseNode.getNodeName().equalsIgnoreCase("other")) {
                                            result.key.add(String.format("%s.%s.%s.choose.other.%s", key, chooseKey, i, count));
                                            result.sql.add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue(), chooseNode.getTextContent()));
                                        } else {
                                            if (attribute.getNamedItem("property") != null) {
                                                result.key.add(String.format("%s.%s.%s.choose.condition.%s", key, chooseKey, i, count));
                                                result.sql.add(attribute.getNamedItem("property").getNodeValue());
                                            }

                                            result.key.add(String.format("%s.%s.%s.choose.%s", key, chooseKey, i, count));
                                            result.sql.add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue().toLowerCase(), chooseNode.getTextContent()));
                                        }
                                        count++;
                                    }
                                }
                            }
                        }

                        i++;
                    }
                    result.param.put(key, param);

                    result.sql.forEach(a -> {
                        a = a.replace("&lt;", "<").replace("&gt", ">");
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.isSuccess = false;
        }
        return result;
    }
}
