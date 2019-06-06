/*
 * 알람 세팅의 스위치버튼을 누를 시 매개변수로 스위치에 관한 정보를 저장하고 뿌리는 클래스이다.
 *
 *
 *
 *
 *
 */

package com.example.myroom.Items;

import android.widget.Switch;

public class AlarmSettingItem {
    public String textStr ;
    public Switch listSwitch;
    public Switch.OnCheckedChangeListener switchListener;
    public void setText(String text) {
        this.textStr = text ;
    }
    public void setSwitch(Switch onOff) {
        this.listSwitch = onOff ;
    }

    public String getText() {
        return this.textStr ;
    }
    public Switch getSwitch() {
        return this.listSwitch ;
    }
}
