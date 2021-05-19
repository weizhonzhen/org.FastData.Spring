package org.FastData.Spring.Util;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class ScanPackage {
    public static List<Class> get(String packageName) {
        packageName = packageName.replace(".", "/");
        List<Class> result = new ArrayList<Class>();
        try {
            Enumeration<URL> list = Thread.currentThread().getContextClassLoader().getResources(packageName);
            while (list.hasMoreElements()) {
                URL url = list.nextElement();
                if ("file".equals(url.getProtocol())) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    File file = new File(filePath);
                    if (file.exists() && file.isDirectory()) {
                        File[] files = file.listFiles(new FileFilter() {
                            public boolean accept(File file) {
                                return (file.isDirectory()) || (file.getName().endsWith(".class"));
                            }
                        });
                        assert files != null;
                        for (File info : files) {
                            if (info.isDirectory())
                                result.addAll(ScanPackage.get(String.format("%s.%s", packageName, info.getName())));
                            else {
                                String className = info.getName().substring(0, info.getName().length() - 6);
                                className = String.format("%s.%s", packageName.replace("/", "."), className);
                                result.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
