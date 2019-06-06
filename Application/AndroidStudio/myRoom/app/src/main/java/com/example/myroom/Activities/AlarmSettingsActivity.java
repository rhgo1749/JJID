/*
* 설정->알람설정에 해당하는 액티비티로 각종 알람에 대한 설정을 할 수 있다.
*
*
*
*
*
*
 */
package com.example.myroom.Activities;
import android.os.Bundle;
import android.widget.ListView;

import com.example.myroom.Adapters.AlarmSettingAdapter;
import com.example.myroom.Items.AlarmSettingItem;
import com.example.myroom.R;

import java.util.ArrayList;

public class AlarmSettingsActivity extends BaseActivity {
    private ListView m_oListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm_settings); //activity_main_home뷰를 참조

        // 데이터 1000개 생성--------------------------------.
        String[] alertMenu = {"출석전알람"};
        ArrayList<AlarmSettingItem> oData = new ArrayList<>();
        for (int i=0; i<alertMenu.length; ++i)
        {
            final AlarmSettingItem oItem = new AlarmSettingItem();
            oItem.textStr = alertMenu[i];
            oData.add(oItem);
        }

        // ListView, Adapter 생성 및 연결 ------------------------
        m_oListView = (ListView)findViewById(R.id.settings_Alarm_ListView);
        AlarmSettingAdapter oAdapter = new AlarmSettingAdapter(oData,this);
        m_oListView.setAdapter(oAdapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_hold);
    }
}
