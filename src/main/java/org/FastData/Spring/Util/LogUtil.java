package org.FastData.Spring.Util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
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
            if (!file.exists())
                file.createNewFile();

            RandomAccessFile write = new RandomAccessFile(file, "rw");
            write.write(ex.getMessage().getBytes(StandardCharsets.UTF_8));
            write.write("\r\n".getBytes(StandardCharsets.UTF_8));
            write.write(ex.getStackTrace().toString().getBytes(StandardCharsets.UTF_8));
            write.write("\r\n".getBytes(StandardCharsets.UTF_8));
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(sb.toString());
        }
    }
}
