package org.FastData.Spring.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.cert.TrustAnchor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.stream.Stream;

public final class LogUtil {
    public static void error(Exception ex) {
        try {
            FileOutputStream stream = init();
            if (stream == null)
                return;
            OutputStreamWriter write = new OutputStreamWriter(stream, "UTF-8");

            if (ex.getMessage() != null)
                write.write(String.format("%s:%s\r\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ex.getMessage()));

            for (StackTraceElement item : ex.getStackTrace()) {
                write.write(String.format("%s:%s\r\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), item.toString()));
            }
            write.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileOutputStream init() {
        FileOutputStream stream = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(System.getProperty("user.dir"));
            sb.append("\\log\\");
            sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
            sb.append("\\");
            sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            File file = new File(sb.toString());
            if (!file.exists())
                file.mkdirs();

            sb.append("\\");
            sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");

            file = new File(sb.toString());
            if (!file.exists()) {
                file.createNewFile();
                stream = new FileOutputStream(file);
            } else
                stream = new FileOutputStream(file, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static void error(HashMap<String,Object> param){
        try {
            FileOutputStream stream = init();
            if (stream == null)
                return;

            OutputStreamWriter write = new OutputStreamWriter(stream, "UTF-8");

            StringBuilder sb =new StringBuilder();
            for(String key : param.keySet())
            {
                sb.append(key);
                sb.append(":");
                sb.append(param.get(key));
                sb.append("\r\n");
            }

            write.write(sb.toString());
            write.write("\r\n");
            write.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
