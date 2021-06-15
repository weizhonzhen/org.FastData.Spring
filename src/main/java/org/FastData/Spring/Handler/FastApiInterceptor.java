package org.FastData.Spring.Handler;

import com.alibaba.fastjson.JSON;
import org.FastData.Spring.Base.MapXml;
import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Model.*;
import org.FastData.Spring.Repository.IFastRepository;
import org.FastData.Spring.Util.CacheUtil;
import org.FastData.Spring.Util.FastUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

@Component
public class FastApiInterceptor implements HandlerInterceptor {
    private final static String page = "page";
    private final static String pageAll = "pageall";
    private final static String param = "param";
    private final static String all = "all";
    private final static String write = "write";

    @Resource
    IFastRepository ifast;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map param = request.getParameterMap();
        String name = request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping").toString().toLowerCase();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();

        if (name != null && CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            for (String item : mapParam(name)) {
                if (param.keySet().stream().anyMatch(a -> a.toString().equalsIgnoreCase(item))) {
                    Object key = param.keySet().stream().filter(k -> k.toString().equalsIgnoreCase(item)).findFirst().get();
                    String value = ((String[]) param.get(key))[0];

                    if (value == null)
                        continue;

                    if (mapRequired(name, item)) {
                        if (!value.equals(""))
                            map.put(item, value);
                        else {
                            data.put("error", String.format("%s:不能为空", item));
                            data.put("success", false);
                            return getResult(response, data);
                        }
                    }

                    int len = mapMaxlength(name, item);
                    if (len != 0 && value.length() > len) {
                        data.put("error", String.format("%s:最大长度%s", item, len));
                        data.put("success", false);
                        return getResult(response, data);
                    } else
                        map.put(item, value);
                }
            }

            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, map);
                if (mapType(name).equalsIgnoreCase(page) || mapType(name).equalsIgnoreCase(pageAll)) {
                    PageModel page = new PageModel();
                    Optional pageSizeKey = param.keySet().stream().filter(k -> k.toString().equalsIgnoreCase("pagesize")).findFirst();
                    if (pageSizeKey == Optional.empty())
                        page.setPageSize(10);
                    else {
                        Object pageSize = ((String[])param.get(pageSizeKey.get()))[0];
                        page.setPageSize( pageSize == null ? 10 : Integer.parseInt(pageSize.toString()));
                    }

                    Optional pageIdKey = param.keySet().stream().filter(k -> k.toString().equalsIgnoreCase("pageid")).findFirst();
                    if (pageIdKey == Optional.empty())
                        page.setPageId(1);
                    else {
                        Object pageId = ((String[])param.get((pageIdKey).get()))[0];
                        page.setPageId(pageId == null ? 1 : Integer.parseInt(pageId.toString()));
                    }

                    PageResult pageInfo = db.page(page, result);

                    data.put("success", true);
                    map.put("data", pageInfo.getList());
                    map.put("page", pageInfo.getpModel());
                    return getResult(response, map);
                }

                if (mapType(name).equalsIgnoreCase(all)) {
                    data.put("success", true);
                    map.put("data", db.query(result).getList());
                    return getResult(response, map);
                }

                if (mapType(name).equalsIgnoreCase(write) && map.size() > 0) {
                    DataReturn info = db.execute(result);
                    data.put("success", info.getWriteReturn().getSuccess());
                    if (!info.getWriteReturn().getSuccess())
                        data.put("error", info.getWriteReturn().getMessage());
                    return getResult(response, map);
                }

                if (map.size() > 0) {
                    data.put("success", true);
                    map.put("data", db.query(result).getList());
                    return getResult(response, map);
                }
            }
        }
        return true;
    }

    private int mapMaxlength(String name, String param) {
        try {
            String len = CacheUtil.get(String.format("%s.%s.maxlength", name.toLowerCase(), param.toLowerCase()));
            if (FastUtil.isNullOrEmpty(len))
                return 0;
            else
                return Integer.parseInt(len);
        } catch (Exception ex) {
            return 0;
        }
    }

    private boolean mapRequired(String name, String param) {
        String value = CacheUtil.get(String.format("%s.%s.required", name.toLowerCase(), param.toLowerCase()));
        if (FastUtil.isNullOrEmpty(value))
            return false;
        else
            return value.equalsIgnoreCase("true");
    }

    private String mapType(String name) {
        String value = CacheUtil.get(String.format("%s.type", name.toLowerCase()));
        if (FastUtil.isNullOrEmpty(value))
            return "";
        else
            return value;
    }

    private String mapDb(String name) {
        return CacheUtil.get(String.format("%s.db", name.toLowerCase()));
    }

    private List<String> mapParam(String name) {
        List<String> value = CacheUtil.getModel(String.format("%s.param", name.toLowerCase()), ArrayList.class);
        if (value == null)
            return new ArrayList<>();
        else
            return value;
    }

    private boolean getResult(HttpServletResponse response, Map msg) {
        try {
            response.setContentType("application/Json;charset=utf-8");
            PrintWriter pWriter = response.getWriter();
            pWriter.write(JSON.toJSONString(msg));
            pWriter.flush();
            pWriter.close();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }
}