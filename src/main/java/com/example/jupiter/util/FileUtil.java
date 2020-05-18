package com.example.jupiter.util;

import org.springframework.util.ResourceUtils;

import java.io.*;

public class FileUtil {

    private static String path = "file";

    public static boolean createFile(String fileName, String fileContent) {
        if (StringUtil.isOneEmpty(new String[]{fileName, fileContent})) {
            return false;
        }
        File text = new File(String.format("%s%s%s.txt", path, File.separator, fileName));
        FileOutputStream fos = null;
        try {
            if (!text.exists()) {
                text.createNewFile();
            }
            fos = new FileOutputStream(text);
            fos.write(fileContent.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void downloadFile(String fileName, OutputStream os) {
        if (StringUtil.isEmpty(fileName) || os == null) {
            return;
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            File text = new File(String.format("%s%s%s.txt", path, File.separator, fileName));
            fis = new FileInputStream(text);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
