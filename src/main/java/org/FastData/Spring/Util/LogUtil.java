package org.FastData.Spring.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LogUtil {
    public static void error(Exception ex) {
        StringBuilder sb = new StringBuilder();
        try {
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
            FileOutputStream stream = null;
            if (!file.exists()) {
                file.createNewFile();
                stream = new FileOutputStream(file);
            } else
                stream = new FileOutputStream(file, true);

            OutputStreamWriter write = new OutputStreamWriter(stream, "UTF-8");

            if (ex.getMessage() != null)
                write.write(String.format("%s:%s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ex.getMessage()));

            for (StackTraceElement item : ex.getStackTrace()) {
                write.write(String.format("%s:%s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), item.toString()));
            }
            write.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(sb.toString());
        }
    }
}
