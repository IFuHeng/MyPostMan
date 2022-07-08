package com.changhong.chpostman.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FileUtils {

    public static String readFile(InputStream is) throws IOException {
        if (is == null)
            return null;

        return new String(readFromIputStream(is));
    }

    public static String readFile(String filePath) throws IOException {
        if (filePath == null)
            return null;

        String result;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            result = readFile(fis);
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }

        return result;
    }

    public static byte[] readFromIputStream(InputStream is) throws IOException {
        if (is == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[128];
        do {
            int length = is.read(buf);
            if (length == -1)
                break;
            baos.write(buf, 0, length);
        } while (true);
        return baos.toByteArray();
    }
}
