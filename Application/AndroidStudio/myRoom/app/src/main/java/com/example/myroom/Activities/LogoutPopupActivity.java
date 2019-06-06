/*
 *로그아웃 메뉴를 누를시 팝업이라는 미니 레이아웃을 뜨게하고 버튼에 따라 동작을 수행
 *
 *
 *
 *
 *
 */
package com.example.myroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;
import com.example.myroom.SharedPreferences.ReservationStatusSave;
import com.example.myroom.SharedPreferences.SettingSave;

public class LogoutPopupActivity extends AppCompatActivity {
    Button okBtn, cancleBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 제거 ( 전체화면 모드 )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_popup);

        okBtn = (Button) findViewById(R.id.logout_Popup_OkBtn);
        cancleBtn = (Button) findViewById(R.id.logout_Popup_CancelBtn);

    }

    //동작 버튼 클릭
    public void mOk(View v){
        Intent Logout = new Intent(getApplicationContext(), FirstAutoLogin_Activity.class);
        Toast.makeText(getApplicationContext(),"로그아웃되었습니다.",Toast.LENGTH_SHORT).show();
        AutoLogin.clearALLFLAG(getApplicationContext());
        AutoLogin.clearAutoLogin(getApplicationContext());
        AutoLogin.clearAutoLoginPW(getApplicationContext());
        SettingSave.clearCurrentSettings(getApplicationContext(),"출석전알람");
        SettingSave.clearCurrentSettings(getApplicationContext(),"퇴실전알람");
        ReservationStatusSave.clearALL(getApplicationContext());
        startActivity(Logout);
        finish();
    }


    //취소 버튼 클릭
    public void mCancel(View v){
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}