/*
 * 알람설정을 SharedPreferences에 저장해 스위치 상태 및 앱을 백그라운드에서 실행시에도 알람이
 * 작동하게끔 설정정보를 가지고 있게끔하려했는데 자동로그인만큼 쉽지가 않아 버그가 있다.
 *
 *
 *
 *
 */
package com.example.myroom.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.myroom.Items.AlarmSettingItem;

public class SettingSave {
    static final String SETTING_SAVE = "username";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setSettings(Context ctx, AlarmSettingItem alarmSettings) {
        SharedPreferences.Editor settingEditor = getSharedPreferences(ctx).edit();
        settingEditor.putBoolean("Settings"+alarmSettings.textStr,alarmSettings.listSwitch.isChecked());
        settingEditor.commit();
    }

    // 저장된 정보 가져오기
    public static boolean getSettings(Context ctx, String alarmSettings) {
        return getSharedPreferences(ctx).getBoolean("Settings"+alarmSettings,true);//스위치 초기값은 켜져있게
    }

        // 로그아웃
       public static void clearCurrentSettings(Context ctx, String alarmSettings) {
        SharedPreferences.Editor settingEditor = getSharedPreferences(ctx).edit();
           settingEditor.remove("Settings"+alarmSettings);
           settingEditor.commit();
    }

}
