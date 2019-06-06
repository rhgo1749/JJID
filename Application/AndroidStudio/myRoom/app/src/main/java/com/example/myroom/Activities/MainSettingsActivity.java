/*
 * 설정메뉴다 로그아웃과 알림중 하나를 선택해서 들어갈 수 있다.
 *
 *
 *
 *
 */
package com.example.myroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myroom.R;

public class MainSettingsActivity extends BaseActivity {
    private ListView m_oListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings_menu); //activity_main_home뷰를 참조

        // 설정메뉴
        String[] settingMenuItem = {"알람설정", "로그아웃"};

        // ListView, Adapter 생성 및 연결 ------------------------
        m_oListView = (ListView)findViewById(R.id.settings_Menu_ListView);
        ArrayAdapter oAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,settingMenuItem );
        m_oListView.setAdapter(oAdapter);
        final Intent goAlarmMenu = new Intent(getApplicationContext(), AlarmSettingsActivity.class);
        final Intent goLogoutMenu = new Intent(getApplicationContext(), LogoutPopupActivity.class);
        m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // adapter.getItem(position)의 return 값은 Object 형
                // 실제 Item의 자료형은 CustomDTO 형이기 때문에
                // 형변환을 시켜야 getResId() 메소드를 호출할 수 있습니다.
                // new Intent(현재 Activity의 Context, 시작할 Activity 클래스)
                if(position==0) {
                    // putExtra(key, value)
                    goAlarmMenu.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goAlarmMenu);
                }
                else if(position==1) {
                    goLogoutMenu.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goLogoutMenu);
                }
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_hold);
            }
        });
    }


}
