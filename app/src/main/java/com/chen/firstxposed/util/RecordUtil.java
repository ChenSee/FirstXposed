package com.chen.firstxposed.util;

import android.annotation.SuppressLint;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordUtil {
    private static String path = Environment.getExternalStorageDirectory().getPath() + "/ACEnergy";
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    private static File file = new File(path + "/" + format.format(new Date()) + ".txt");

    public static synchronized void log(String content) {
        File parf = new File(path);
        if (!parf.exists()) {
            parf.mkdirs();
        }
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(content);
            printWriter.flush();
            fileWriter.close();
            printWriter.close();
        } catch (IOException ignored) {
        }
    }

    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static List<String> show() {
        List<String> array = new ArrayList<>();
        try {
            String str = readFileSdcardFile(path + "/" + format.format(new Date()) + ".txt");
            for (String x : str.split("\n")) {
                JSONObject object = null;
                try {
                    StringBuilder text = new StringBuilder();
                    object = new JSONObject(x);
                    text.append(getDateToString(object.getLong("time"), "HH时mm分"));
                    text.append("收取了");
                    text.append(object.getString("user"));
                    text.append("的");
                    text.append(object.getString("energy"));
                    text.append("个能量");
                    array.add(text.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Collections.reverse(array);
            return array;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static File create(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        create(fileName);
        FileInputStream fin = new FileInputStream(new File(fileName));
        int length = fin.available();
        byte[] buffer = new byte[length];
        fin.read(buffer);
        res = new String(buffer, "UTF-8");
        fin.close();
        return res;
    }

    public static void writeFileSdcardFile(String fileName, String write_str) throws IOException {
        create(fileName);
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        byte[] bytes = write_str.getBytes();
        fos.write(bytes);
        fos.close();
    }
}
