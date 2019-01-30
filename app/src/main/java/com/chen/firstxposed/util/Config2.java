package com.chen.firstxposed.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XSharedPreferences;

public class Config2 {
    private static String settingFile = null;
    public static SetBean bean;
    private static String path = Environment.getExternalStorageDirectory().getPath() + "/ACEnergy";

    public static void init() {
        try {
            settingFile = RecordUtil.readFileSdcardFile(path + "/setting.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingFile2Bean();
    }

    //获取bean
    private static void settingFile2Bean() {
        bean = new SetBean(settingFile);
        System.out.println(bean.toJson());
    }

    public static void save() {
        try {
            RecordUtil.writeFileSdcardFile(path + "/setting.txt", bean.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(bean.toJson());
    }


    public static class SetBean {
        public boolean steal = true;
        public boolean help = true;
        public boolean stealWhite = false;
        public boolean recEnergy = true;
        public long openTime = 0;
        public long runTime = 0;
        public List<String> whiteList = new ArrayList<>();

        SetBean() {
        }

        SetBean(String json) {
            try {
                JSONObject object = new JSONObject(json);
                steal = object.optBoolean("steal");
                help = object.optBoolean("help");
                stealWhite = object.optBoolean("stealWhite");
                recEnergy = object.optBoolean("recEnergy");
                openTime = object.getLong("openTime");
                runTime = object.getLong("runTime");
                if (!object.getString("whiteList").equals("[]")) {
                    JSONArray array = object.getJSONArray("whiteList");
                    for (int i = 0; i < array.length(); i++) {
                        whiteList.add(array.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String toJson() {
            JSONObject object = new JSONObject();
            try {
                object.put("steal", steal);
                object.put("help", help);
                object.put("stealWhite", stealWhite);
                object.put("recEnergy", recEnergy);
                object.put("whiteList", whiteList);
                object.put("openTime", openTime);
                object.put("runTime", runTime);
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
