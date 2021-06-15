package org.FastData.Spring.Base;

import com.googlecode.aviator.AviatorEvaluator;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.sun.org.apache.xerces.internal.dom.DeferredTextImpl;
import org.FastData.Spring.Model.MapResult;
import org.FastData.Spring.Model.XmlModel;
import org.FastData.Spring.Util.CacheUtil;
import org.FastData.Spring.Util.FastUtil;
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
        XmlModel result = getXmlList(xml, "sqlMap");
        for (int i = 0; i < result.getKey().size(); i++) {
            CacheUtil.set(result.getKey().get(i).toLowerCase(), result.getSql().get(i));
        }

        result.getDb().forEach((k, v) -> {
            CacheUtil.set(k.toLowerCase(), v.toString());
        });

        result.getParam().forEach((k, v) -> {
            CacheUtil.setModel(String.format("%s.param", k.toLowerCase()), v);
            result.getKey().add(String.format("%s.param", k.toLowerCase()));
        });

        result.getName().forEach((k, v) -> {
            CacheUtil.set(k.toLowerCase(), v.toString());
            result.getKey().add(k.toLowerCase());
        });

        result.getCheck().forEach((k, v) -> {
            CacheUtil.set(k.toLowerCase(), v.toString());
            result.getKey().add(k.toLowerCase());
        });

        return result.getKey();
    }

    public static MapResult getMapSql(String name, Map<String, Object> param) {
        MapResult result = new MapResult();
        StringBuilder sql = new StringBuilder();
        LinkedHashMap<String, Object> tempParam = new LinkedHashMap<String, Object>();
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
                   if(result.getParam().keySet().stream().noneMatch(a->a.equalsIgnoreCase(item))) {
                       tempParam.put(item, param.get(item));
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
                                if (FastUtil.isNullOrEmpty(param.get(item))) {
                                    tempParam.remove(item);
                                } else {
                                    if (paramSql.contains(tempKey)) {
                                        tempParam.remove(item);
                                        condtionSql.append(paramSql.replace(tempKey, param.get(item).toString()));
                                    } else if (paramSql.contains(flagParam))
                                        condtionSql.append(paramSql.replace(flagParam, "?"));
                                    else {
                                        tempParam.remove(item);
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
                                        condtionSql.append(paramSql.replace(tempKey, param.get(item).toString()));
                                    } else if (paramSql.contains(flagParam))
                                        condtionSql.append(paramSql.replace(flagParam, "?"));
                                    else {
                                        tempParam.remove(item);
                                        condtionSql.append(paramSql);
                                    }
                                } else {
                                    tempParam.remove(item);
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
                                                condtionSql.append(chooseSql.replace(tempKey, param.get(item).toString()));
                                            } else if (chooseSql.contains(flagParam))
                                                condtionSql.append(chooseSql.replace(flagParam, "?"));
                                            else {
                                                tempParam.remove(item);
                                                condtionSql.append(chooseSql);
                                            }
                                            break;
                                        }
                                    }
                                }

                                if (!isSuccess) {
                                    if (conditionOtherSql.equals("")) {
                                        tempParam.remove(item);
                                    } else if (conditionOtherSql.contains(tempKey)) {
                                        tempParam.remove(item);
                                        condtionSql.append(conditionOtherSql.replace(tempKey, param.get(item).toString()));
                                    } else if (conditionOtherSql.contains(flagParam))
                                        condtionSql.append(conditionOtherSql.replace(flagParam, "?"));
                                    else {
                                        tempParam.remove(item);
                                        condtionSql.append(conditionOtherSql);
                                    }
                                }
                                break;
                            }
                            default: {
                                if (paramSql.contains(tempKey)) {
                                    tempParam.remove(item);
                                    condtionSql.append(paramSql.replace(tempKey, param.get(item).toString()));
                                } else if (paramSql.contains(flagParam))
                                    condtionSql.append(paramSql.replace(flagParam, "?"));
                                else {
                                    tempParam.remove(item);
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
            result.setParam(tempParam);
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
            result.setParam(tempParam);
        }

        result.setSql(sql.toString());

        return result;
    }

    private static XmlModel getXmlList(String xml, String xmlNode) {
        XmlModel result = new XmlModel();
        result.setSuccess(true);
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
                    result.setSuccess(false);
                    throw new Exception(String.format("%s节点不存在", xmlNode));
                }

                if (node.getNodeName().equalsIgnoreCase("select")
                        || node.getNodeName().equalsIgnoreCase("update")
                        || node.getNodeName().equalsIgnoreCase("delete")) {
                    int i = 0;
                    List<String> param = new ArrayList<String>();
                    String key = node.getAttributes().getNamedItem("id").getNodeValue().toLowerCase();

                    if (result.getKey().stream().anyMatch(t -> t.equals(key))) {
                        result.setSuccess(false);
                        throw new Exception(String.format("key:%s已经存在", key));
                    }

                    result.getKey().add(key);
                    result.getSql().add(String.valueOf(node.getChildNodes().getLength()));

                    if (node.getAttributes().getNamedItem("log") != null)
                        result.getName().put(String.format("%s.log", key), node.getAttributes().getNamedItem("log").getNodeValue());

                    if (node.getAttributes().getNamedItem("db") != null)
                        result.getDb().put(String.format("%s.db", key), node.getAttributes().getNamedItem("db").getNodeValue());

                    if (node.getAttributes().getNamedItem("type") != null)
                        result.getCheck().put(String.format("%s.type", key), node.getAttributes().getNamedItem("type").getNodeValue());

                    for (int child = 0; child < node.getChildNodes().getLength(); child++) {
                        Node childNode = node.getChildNodes().item(child);

                        if (childNode instanceof DeferredTextImpl) {
                            result.getKey().add(String.format("%s.%s", key, i));
                            result.getSql().add(((DeferredTextImpl) childNode).getNodeValue());
                        }

                        if (childNode instanceof DeferredElementImpl) {
                            NodeList tempNode = childNode.getChildNodes();

                            if (childNode.getAttributes().getNamedItem("prepend") != null) {
                                result.getKey().add(String.format("%s.format.%s", key, i));
                                result.getSql().add(childNode.getAttributes().getNamedItem("prepend").getNodeValue());
                            } else {
                                result.getKey().add(String.format("%s.format.%s", key, i));
                                result.getSql().add("");
                            }

                            if (childNode.getAttributes().getNamedItem("rtrim") != null) {
                                result.getKey().add(String.format("%s.rtrim.%s", key, i));
                                result.getSql().add(childNode.getAttributes().getNamedItem("rtrim").getNodeValue());
                            }

                            if (childNode.getAttributes().getNamedItem("ltrim") != null) {
                                result.getKey().add(String.format("%s.ltrim.%s", key, i));
                                result.getSql().add(childNode.getAttributes().getNamedItem("ltrim").getNodeValue());
                            }

                            for (int condtion = 0; condtion < tempNode.getLength(); condtion++) {
                                Node condtionNode = tempNode.item(condtion);

                                if (condtionNode instanceof DeferredTextImpl)
                                    continue;

                                NamedNodeMap attribute = condtionNode.getAttributes();
                                result.getKey().add(String.format("%s.%s.condition.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                result.getSql().add(condtionNode.getNodeName());

                                if (attribute.getNamedItem("required") != null) {
                                    String checkkey = String.format("%s.%s.required", key, attribute.getNamedItem("property").getNodeValue().toLowerCase());
                                    result.getCheck().put(checkkey, attribute.getNamedItem("required").getNodeValue());
                                }

                                if (attribute.getNamedItem("maxlength") != null) {
                                    String checkkey = String.format("%s.%s.maxlength", key, attribute.getNamedItem("property").getNodeValue().toLowerCase());
                                    result.getCheck().put(checkkey, attribute.getNamedItem("maxlength").getNodeValue());
                                }

                                if (attribute.getNamedItem("property") != null)
                                    param.add(attribute.getNamedItem("property").getNodeValue());

                                if (condtionNode.getNodeName().equalsIgnoreCase("ispropertyavailable")) {
                                    result.getKey().add(String.format("%s.%s.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                    result.getSql().add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue(), condtionNode.getTextContent()));
                                } else if (!condtionNode.getNodeName().equalsIgnoreCase("choose")) {

                                    result.getKey().add(String.format("%s.%s.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                    result.getSql().add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue(), condtionNode.getTextContent()));

                                    if (attribute.getNamedItem("condition") != null) {
                                        result.getKey().add(String.format("%s.%s.condition.value.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                        result.getSql().add(attribute.getNamedItem("condition").getNodeValue());
                                    }

                                    if (attribute.getNamedItem("compareValue") != null) {
                                        result.getKey().add(String.format("%s.%s.condition.value.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i));
                                        result.getSql().add(attribute.getNamedItem("compareValue").getNodeValue());
                                    }
                                } else {
                                    int count = 0;
                                    String chooseCountKey=String.format("%s.%s.%s", key, attribute.getNamedItem("property").getNodeValue().toLowerCase(), i);
                                    NodeList chooseNodeList = condtionNode.getChildNodes();
                                    String chooseKey = attribute.getNamedItem("property").getNodeValue().toLowerCase();
                                    for (int choose = 0; choose < chooseNodeList.getLength(); choose++) {
                                        Node chooseNode = chooseNodeList.item(choose);
                                        attribute = chooseNode.getAttributes();
                                        if (attribute == null)
                                            continue;
                                        if (chooseNode.getNodeName().equalsIgnoreCase("other")) {
                                            result.getKey().add(String.format("%s.%s.%s.choose.other.%s", key, chooseKey, i, count));
                                            result.getSql().add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue(), chooseNode.getTextContent()));
                                        } else {
                                            if (attribute.getNamedItem("property") != null) {
                                                result.getKey().add(String.format("%s.%s.%s.choose.condition.%s", key, chooseKey, i, count));
                                                result.getSql().add(attribute.getNamedItem("property").getNodeValue());
                                            }

                                            result.getKey().add(String.format("%s.%s.%s.choose.%s", key, chooseKey, i, count));
                                            result.getSql().add(String.format("%s%s", attribute.getNamedItem("prepend").getNodeValue().toLowerCase(), chooseNode.getTextContent()));
                                        }
                                        count++;
                                    }
                                    result.getKey().add(chooseCountKey);
                                    result.getSql().add(String.valueOf(count));
                                }
                            }
                        }

                        i++;
                    }
                    result.getParam().put(key, param);

                    result.getSql().forEach(a -> {
                        a = a.replace("&lt;", "<").replace("&gt", ">");
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }
}