package com.maxfour.libreplayer.util;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class LyricUtil {

    private static final String lrcRootPath = android.os.Environment
            .getExternalStorageDirectory().toString() + "/Music/lyrics/";
    private static final String TAG = "LyricUtil";

    @Nullable
    public static File writeLrcToLoc(@NonNull String artist, @NonNull String title, @NonNull String lrcContext) {
        FileWriter writer = null;
        try {
            File file = new File(getLrcPath(artist, title));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            writer = new FileWriter(getLrcPath(artist, title));
            writer.write(lrcContext);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteLrcFile(@NonNull String artist, @NonNull String title) {
        File file = new File(getLrcPath(artist, title));
        return file.delete();
    }

    public static boolean isLrcFileExist(@NonNull String artist, @NonNull String title) {
        File file = new File(getLrcPath(artist, title));
        return file.exists();
    }

    @Nullable
    public static File getLocalLyricFile(@NonNull String artist, @NonNull String title) {
        File file = new File(getLrcPath(artist, title));
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private static String getLrcPath(String artist, String title) {
        return lrcRootPath + title + " - " + artist + ".lrc";
    }

    @NonNull
    public static String decryptBASE64(@NonNull String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            return new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @NonNull
    public static String getStringFromFile(@NonNull String artist, @NonNull String title) throws Exception {
        File file = new File(getLrcPath(artist, title));
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
