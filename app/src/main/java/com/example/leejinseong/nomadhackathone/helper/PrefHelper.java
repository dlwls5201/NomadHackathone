package com.example.leejinseong.nomadhackathone.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iwedding on 2018. 4. 2..
 */

public class PrefHelper {

    // 몇 일 차 (int)
    public static final String PER_DAY                      = "per_day";

    // 하루 남은 금액 (int)
    public static final String PER_DAY_MONEY                = "per_day_money";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static PrefHelper pfc = null;

    private Context mContext;
    private SharedPreferences pref;

    public static PrefHelper getInstanceOf(Context c){
        if(pfc==null){
            pfc = new PrefHelper(c);
        }

        return pfc;
    }


    public PrefHelper(Context c) {
        final String PREF_NAME = c.getPackageName();

        mContext = c;
        pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }

    /**
     * 키에 해당하는 데이터(문자열)를 삽입한다.
     * @param key		키
     * @param value 	문자열
     */
    public void put(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, long value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void put(String key, Boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }




    /**
     * 키에 해당하는 데이터를 가저온다.
     * @param key				키
     * @param defaultValue		기본 값
     * @return
     */
    public String getValue(String key, String defaultValue) {

        try {
            return pref.getString(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Boolean getValue(String key, Boolean defaultValue) {

        try {
            return pref.getBoolean(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Long getValue(String key, Long defaultValue) {

        try {
            return pref.getLong(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getValue(String key, int defaultValue) {

        try {
            return pref.getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }



    /**
     * 키에 해당하는 데이터를 삭제한다.
     * @param key 키
     */
    public void removePreferences(String key){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    public void deleteData() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}
