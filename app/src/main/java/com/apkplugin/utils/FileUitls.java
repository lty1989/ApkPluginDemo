package com.apkplugin.utils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017-12-15.
 */

public final class FileUitls {

    private FileUitls() {
    }

    public static void copyFile(String from, String to) {

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);

            byte[] buff = new byte[1024];
            int read;
            while ((read = fis.read(buff)) != -1) {
                fos.write(buff, 0, read);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fis);
            close(fos);
        }


    }


    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
