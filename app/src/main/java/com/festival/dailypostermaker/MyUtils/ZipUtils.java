package com.festival.dailypostermaker.MyUtils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    private static final String TAG = ZipUtils.class.getSimpleName();

    private static final int BUFFER_SIZE = 2048;

    public static void unzip(File sourceFile, File destinationFolder) {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
            ZipEntry zipEntry;
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                Log.e(TAG, "unzip: " + fileName);
                if (!fileName.contains(".zip")) {
                    /*fileName = fileName.substring(fileName.indexOf("/") + 1);*/
                    Log.e(TAG, "unzip2: " + fileName);
                    File file = new File(destinationFolder, fileName);
                    File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs()) {
                        dir.mkdirs();
                    }
                    if (zipEntry.isDirectory()) {
                        continue;
                    }
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        while ((count = zipInputStream.read(buffer)) != -1)
                            fileOutputStream.write(buffer, 0, count);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null)
                try {
                    zipInputStream.close();
                    if (sourceFile.exists()) {
                        sourceFile.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
