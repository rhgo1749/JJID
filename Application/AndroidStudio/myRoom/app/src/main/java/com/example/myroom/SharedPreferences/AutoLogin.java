/*
 * 자동로그인 SharedPreferences 관련 클래스이다
 *
 *
 *
 *
 *
 *
 */
package com.example.myroom.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AutoLogin {
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_PW = "userPw";
    static final String PREF_FLAG_AL = "autologin";
    static final String PREF_FLAG_L = "login";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setAutoLoginName(Context ctx, String userName) {
        SharedPreferences.Editor autoLoginNAMEEditor = getSharedPreferences(ctx).edit();
        autoLoginNAMEEditor.putString(PREF_USER_NAME, userName);
        autoLoginNAMEEditor.commit();
    }
    public static void setAutoLoginPW(Context ctx, String userPW) {
        SharedPreferences.Editor autoLoginPWEditor = getSharedPreferences(ctx).edit();
        autoLoginPWEditor.putString(PREF_USER_PW, userPW);
        autoLoginPWEditor.commit();
    }
    public static void setALFLAG(Context ctx, Boolean ALFLAG) {
        SharedPreferences.Editor ALFLAGEditor = getSharedPreferences(ctx).edit();
        ALFLAGEditor.putBoolean(PREF_FLAG_AL, ALFLAG);
        ALFLAGEditor.commit();
    }
    public static void setLFLAG(Context ctx, Boolean LFLAG) {
        SharedPreferences.Editor LFLAGEditor = getSharedPreferences(ctx).edit();
        LFLAGEditor.putBoolean(PREF_FLAG_L, LFLAG);
        LFLAGEditor.commit();
    }

    // 저장된 정보 가져오기
    public static String getAutoLoginName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static String getAutoLoginPW(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_PW, "");
    }

    public static Boolean getALFLAG(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_FLAG_AL,false);
    }
    public static Boolean getLFlag(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_FLAG_L, false);
    }

    // 로그아웃
    public static void clearAutoLogin(Context ctx) {
        SharedPreferences.Editor autoLoginNAMEEditor = getSharedPreferences(ctx).edit();
        autoLoginNAMEEditor.remove(PREF_USER_NAME);
        autoLoginNAMEEditor.commit();
    }
    public static void clearAutoLoginPW(Context ctx) {
        SharedPreferences.Editor autoLoginPWEditor = getSharedPreferences(ctx).edit();
        autoLoginPWEditor.remove(PREF_USER_PW);
        autoLoginPWEditor.commit();
    }
    public static void clearALLFLAG(Context ctx) {
        SharedPreferences.Editor ALLFLAGEditor = getSharedPreferences(ctx).edit();
        ALLFLAGEditor.remove(PREF_FLAG_AL);
        ALLFLAGEditor.remove(PREF_FLAG_L);
        ALLFLAGEditor.commit();
    }
}
