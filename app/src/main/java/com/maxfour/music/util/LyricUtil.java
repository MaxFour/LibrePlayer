package com.maxfour.music.util;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

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

    @NonNull
    public static File writeLrcToLoc(@NonNull String title, @NonNull String artist, @NonNull String lrcContext) {
        FileWriter writer = null;
        try {
            File file = new File(getLrcPath2(title, artist));
            if (!file.exists()) {
                file.mkdirs();
            }
            writer = new FileWriter(getLrcPath2(title, artist));
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

    public static boolean deleteLrcFile(@NonNull String title, @NonNull String artist) {
        File file = new File(getLrcPath(title, artist));
        return file.delete();
    }

    public static boolean isLrcFileExist(@NonNull String title, @NonNull String artist) {
        File file = new File(getLrcPath(title, artist));
        return file.exists();
    }

    public static boolean isLrcFile2Exist(@NonNull String title, @NonNull String artist) {
        File file = new File(getLrcPath2(title, artist));

        return file.exists();
    }

    @NonNull
    public static File getLocalLyricFile(@NonNull String title, @NonNull String artist) {
        try {
            File file = new File(getLrcPath(title, artist));
            File file2 = new File(getLrcPath2(title, artist));
            if (file.exists()) {

                return file;
            } else if (file2.exists()) {

                return file2;
            } else {

                return new File("lyric file not exist");
            }
        } catch (Exception dfs) {
            dfs.printStackTrace();
            return new File("lyric file not exist");

        }
    }

    public static String getLrcPath2(String title, String artist) {
        if (!TextUtils.isEmpty(title)) {
            String x2;
            if (title.endsWith(".flac") || title.endsWith(".mogg") || title.endsWith(".alac") || title.endsWith(".aiff") || title.endsWith(".webv")) {
                x2 = title.substring(0, title.length() - 5) + ".lrc";
            } else {
                x2 = title.substring(0, title.length() - 4) + ".lrc";
            }
            return x2;
        }
        return "";
    }

    public static String getLrcPath(String title, String artist) {
        return lrcRootPath + title + " - " + artist + ".lrc";
    }

    @NonNull
    public static String decryptBASE64(@NonNull String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 解密
            return new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @NonNull
    public static String getStringFromFile(@NonNull String title, @NonNull String artist) throws Exception {
        File file;
        File file2 = new File(getLrcPath(title, artist));
        File file3 = new File(getLrcPath2(title, artist));
        if (file2.exists()) {
            file = file2;
        } else {
            file = file3;
        }
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        fin.close();
        //  Log.d("damn2",ret);
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
