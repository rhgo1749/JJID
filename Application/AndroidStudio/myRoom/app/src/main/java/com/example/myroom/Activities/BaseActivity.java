package com.example.myroom.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView std_info;
    TextView time_now;
    ProgressHandler handler;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String time;

    @Override
    protected void onRestart() {

        // TODO Auto-generated method stub
        super.onRestart();
        Intent i = getIntent();  //your class
        finish();
        startActivity(i);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        handler = new ProgressHandler();
        runTime();
    }
    public void runTime(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        time= sdf.format(new Date(System.currentTimeMillis()));

                        Message message = handler.obtainMessage();
                        handler.sendMessage(message);

                        Thread.sleep(1000);
                    }catch (InterruptedException ex) {}
                }
            }
        });
        thread.start();

    }

    public class ProgressHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            time_now.setText("현재 시간 : "+time);
            std_info.setText("학번 : "+MainHomeActivity.userData[0].stu_num+" 이름 : "+MainHomeActivity.userData[0].name);
        }
    }
    //툴바를 사용할지 말지 정함

    @Override
    public void setContentView(int layoutResID){
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바를 id 찾아서 참조



        //툴바 사용여부 결정(기본적으로 사용)
        if(useToolbar()){
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);// drawer 즉, 사이드바 메뉴를 id를 찾아서 참조
            //drawer.setScrimColor(Color.TRANSPARENT);//메뉴 열었을때 어두워지는거 삭제
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);// drawer 이벤트 매니저 생성
            toggle.syncState();// 자세히는 모름

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view); //사이드바 id 찾아서 참조
            navigationView.setNavigationItemSelectedListener(this);// 사이드바 이벤트매니저 생성
            View nav_header_view = navigationView.getHeaderView(0);

            Button refreshButton = (Button) findViewById(R.id.refresh_button);
            std_info = (TextView) nav_header_view.findViewById(R.id.nav_header_std_text);
            time_now = (TextView) nav_header_view.findViewById(R.id.nav_header_time_text);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRestart();

                }
            });

        } else {
            toolbar.setVisibility(View.GONE);
        }

    }
    protected boolean useToolbar(){
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //메뉴 등록하기
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_menu_attend) {
            Intent homeIntent = new Intent(getApplicationContext(), MainHomeActivity.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.drawer_menu_myreservation) {
            Intent MyReservationStatusIntent = new Intent(getApplicationContext(), MyReservationStatusActivity.class);
            startActivity(MyReservationStatusIntent);
        } else if (id == R.id.drawer_menu_reservation) {
            Intent reservationIntent = new Intent(getApplicationContext(), MainReservationActivity.class);
            startActivity(reservationIntent);
        } else if (id == R.id.drawer_menu_FAQ) {
            Intent faqIntent = new Intent(getApplicationContext(), FAQMenuActivity.class);
            startActivity(faqIntent);
        } else if (id == R.id.drawer_menu_settings) {
            Intent settingIntent = new Intent(getApplicationContext(), MainSettingsActivity.class);
            startActivity(settingIntent);
        }
        else
            return super.onOptionsItemSelected(item);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);// 실행됬다면
        drawer.closeDrawer(GravityCompat.START); // 드러울러 원상복귀
        return true;
    }

}
