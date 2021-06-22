package org.FastData.Spring.Util;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ScanPackage {
    public static List<Class> get(String packageName) {
        packageName = packageName.replace(".", "/");
        List<Class> result = new ArrayList<Class>();
        try {
            Enumeration<URL> list = Thread.currentThread().getContextClassLoader().getResources(packageName);
            while (list.hasMoreElements()) {
                URL url = list.nextElement();
                if ("file".equals(url.getProtocol()))
                    result.addAll(scanFile(url, packageName));

                if ("jar".equals(url.getProtocol()))
                    result.addAll(scanJar(url, packageName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static List<Class> scanFile(URL url, String packageName) {
        List<Class> result = new ArrayList<Class>();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static List<Class> scanJar(URL url, String packageName) {
        List<Class> result = new ArrayList<Class>();
        try {
            JarFile file = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> list = file.entries();
            while (list.hasMoreElements()) {
                JarEntry entry = list.nextElement();
                String name = entry.getName();
                if (name.charAt(0) == '/')
                    name = name.substring(1);

                if (name.contains(packageName) && !name.equals(String.format("%s/", packageName))) {
                    if (entry.isDirectory()) {
                        String clazzName = entry.getName().replace("/", ".");
                        int endIndex = clazzName.lastIndexOf(".");
                        if (endIndex > 0)
                            ScanPackage.get(clazzName.substring(0, endIndex));
                    }
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
                        result.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                    }
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}