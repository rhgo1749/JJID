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

import com.example.myroom.Items.ReservationData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationStatusSave {
    static final String PREF_STD_NUM = "username";
    static final String PREF_ROOM_NUM = "roomnum";
    static final String PREF_R_DATE = "rdate";
    static final String PREF_START_TIME = "starttime";
    static final String PREF_END_TIME = "endtime";
    static final String PREF_DELEGATOR = "delegator";
    static final String PREF_AT_CHECK = "atcheck";
    static final String PREF_LENGTH = "length";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 예약 정보 저장
    public static void setMyReservation(Context ctx, ReservationData tempReservationData, int position) {
        SharedPreferences.Editor tempData_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor STD_NUM_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor ROOM_NUM_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor DR_DATE_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor START_TIME_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor END_TIME_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor DELEGATOR_Editor = getSharedPreferences(ctx).edit();
        //SharedPreferences.Editor AT_CHECK_Editor = getSharedPreferences(ctx).edit();
        tempData_Editor.putString(PREF_STD_NUM+"흠"+position, tempReservationData.getStu_num());
        tempData_Editor.putInt(PREF_ROOM_NUM+"흠"+position, tempReservationData.getRoom_num());
        //Date는 스트링말고 Long으로 변환
        tempData_Editor.putLong(PREF_R_DATE+"흠"+position,tempReservationData.getR_date().getTime());
        tempData_Editor.putInt(PREF_START_TIME+"흠"+position, tempReservationData.getStart_time());
        tempData_Editor.putInt(PREF_END_TIME+"흠"+position, tempReservationData.getEnd_time());
        tempData_Editor.putString(PREF_DELEGATOR+"흠"+position, tempReservationData.getDelegator());
        tempData_Editor.putInt(PREF_AT_CHECK+"흠"+position, tempReservationData.getAt_check());
        tempData_Editor.putInt(PREF_LENGTH,position);
        tempData_Editor.commit();
    }
    // 저장된 dnlcl정보 가져오기
    public static ReservationData getReservationData (Context ctx,int position) {
        ReservationData tempReservationData = new ReservationData();
        tempReservationData.setStu_num(getSharedPreferences(ctx).getString(PREF_STD_NUM+"흠"+position, "error"));
        tempReservationData.setRoom_num(getSharedPreferences(ctx).getInt(PREF_ROOM_NUM+"흠"+position, -1));
        long millis = (getSharedPreferences(ctx).getLong(PREF_R_DATE +"흠"+position,0L));
        tempReservationData.setR_date(new Date(millis));
        tempReservationData.setStart_time(getSharedPreferences(ctx).getInt(PREF_START_TIME+"흠"+position, -1));
        tempReservationData.setEnd_time(getSharedPreferences(ctx).getInt(PREF_END_TIME+"흠"+position, -1));
        tempReservationData.setStu_num(getSharedPreferences(ctx).getString(PREF_DELEGATOR+"흠"+position, "error"));
        tempReservationData.setAt_check(getSharedPreferences(ctx).getInt(PREF_AT_CHECK+"흠"+position, 0));
        return tempReservationData;
    }
    public static int getLength(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(PREF_LENGTH,-1);
    }
    //
    public static void clearALL(Context ctx) {
        SharedPreferences.Editor SingleEditor = getSharedPreferences(ctx).edit();
        for(int i = 0 ;i<getSharedPreferences(ctx).getInt(PREF_LENGTH,-1)+1;i++) {
            SingleEditor.remove(PREF_STD_NUM + "흠" + i);
            SingleEditor.remove(PREF_ROOM_NUM + "흠" + i);
            SingleEditor.remove(PREF_R_DATE + "흠" + i);
            SingleEditor.remove(PREF_START_TIME + "흠" + i);
            SingleEditor.remove(PREF_END_TIME + "흠" + i);
            SingleEditor.remove(PREF_DELEGATOR + "흠" + i);
            SingleEditor.remove(PREF_AT_CHECK + "흠" + i);
        }
        SingleEditor.remove(PREF_LENGTH);
        SingleEditor.commit();
    }
    //미완성 이거 하나빼고 정렬해주는거까지 구현해야함
    public static void clearONE(Context ctx,int position) {
        int temposition;
        SharedPreferences.Editor SingleEditor =  getSharedPreferences(ctx).edit();
        SingleEditor.remove(PREF_STD_NUM+"흠"+position);
        SingleEditor.remove(PREF_ROOM_NUM+"흠"+position);
        SingleEditor.remove(PREF_R_DATE+"흠"+position);
        SingleEditor.remove(PREF_START_TIME+"흠"+position);
        SingleEditor.remove(PREF_END_TIME+"흠"+position);
        SingleEditor.remove(PREF_DELEGATOR+"흠"+position);
        SingleEditor.remove(PREF_AT_CHECK+"흠"+position);
        temposition = getSharedPreferences(ctx).getInt(PREF_LENGTH,-1);
        temposition = temposition-1;
        SingleEditor.putInt(PREF_LENGTH,temposition);
        SingleEditor.commit();
    }
}