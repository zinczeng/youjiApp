package com.example.zx.youjiandroid.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存的配置信息
 */
public final class Config {
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private static final String USER_FILENAME = "userid_config";    //保存的userid的文件名

    public static final String KEY_USERID = "userid";    //保存的key值 userid

    public static String userid = "";      //保存的userid

    public static String getConfig(String key) {
        sp = FlyContext.app.getSharedPreferences(USER_FILENAME, Context.MODE_PRIVATE);
        userid = sp.getString(key, null);
        return userid == null ? "" : userid;
    }

    public static void saveConfig(String key, String value) {
        sp = FlyContext.app.getSharedPreferences(USER_FILENAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
