/*
 * 큰 버튼 두개 있는 메인 화면이다.
 * 추 후 출석화면으로 쓸 예정이다.
 *
 *
 *
 *
 *
 */

package com.example.myroom.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;


import com.example.myroom.Items.StuData;
import com.example.myroom.Items.StudyRoomData;
import com.example.myroom.R;
import com.example.myroom.Services.MyService;
import com.example.myroom.SharedPreferences.AutoLogin;


public class MainHomeActivity extends BaseActivity{

    TextView timeNow;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public static StudyRoomData[] studyRoomArr = new StudyRoomData[37]; // 스터디룸 정보 검색 위해 DB왔다갔다 하는거 방지 위함
    public static StuData[] userData = new StuData[1];

    ImageView topBarImage;
    ImageView middleBarImage;

    Intent loginIntent;
    int exitCount = 1; //종료를 위한 카운트
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home); //activity_main_home뷰를 참조
        topBarImage = (ImageView) findViewById(R.id.home_topbar);
        middleBarImage = (ImageView) findViewById(R.id.home_middlebar);
        //킷캣이하면 하드웨어 가속 끔끔
       if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            topBarImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
           middleBarImage.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }

        Intent intent = new Intent(MainHomeActivity.this, MyService.class);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap smallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_top_bar, options);
        Bitmap smallBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.home_middle_image, options);

        topBarImage.setImageBitmap(smallBitmap);
        middleBarImage.setImageBitmap(smallBitmap2);


        loginIntent = getIntent();

        studyRoomArr[0] = new StudyRoomData("01 스터디룸(7층)", 3,8, 10,21,2, 0,26837);
        studyRoomArr[1] = new StudyRoomData("02 스터디룸(7층)", 3, 6, 10,21,2, 0,26950);
        studyRoomArr[2] = new StudyRoomData("03 스터디룸(7층)", 3, 6, 10,21,2, 0,26837);
        studyRoomArr[3] = new StudyRoomData("04 스터디룸(7층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[4] = new StudyRoomData("05 스터디룸(7층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[5] = new StudyRoomData("06 스터디룸(7층)", 3,8, 10,21,2, 0,26950);
        studyRoomArr[6] = new StudyRoomData("07 스터디룸(7층)", 4,9, 10,21,2, 1,26837);
        studyRoomArr[7] = new StudyRoomData("08 스터디룸(7층)", 4,8, 10,21,2, 0,26950);
        studyRoomArr[8] = new StudyRoomData("09 스터디룸(7층)", 4,8, 10,21,2, 0,26837);
        studyRoomArr[9] = new StudyRoomData("10 스터디룸(7층)", 4,8, 10,21,2, 0,26950);
        studyRoomArr[10] = new StudyRoomData("11 스터디룸(7층)", 4,18, 10,21,2, 0,26837);
        studyRoomArr[11] = new StudyRoomData("12 스터디룸(7층)", 5,10, 10,21,2, 0,26950);
        studyRoomArr[12] = new StudyRoomData("13 스터디룸(7층)", 3,8, 10,21,2, 0,26837);
        studyRoomArr[13] = new StudyRoomData("14 스터디룸(4층)", 3,8, 10,21,2, 0,26950);
        studyRoomArr[14] = new StudyRoomData("15 스터디룸(4층)", 3,8, 10,21,2, 0,26837);
        studyRoomArr[15] = new StudyRoomData("16 스터디룸(4층)", 3,8, 10,21,2, 0,26950);
        studyRoomArr[16] = new StudyRoomData("17 스터디룸(4층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[17] = new StudyRoomData("18 스터디룸(4층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[18] = new StudyRoomData("19 스터디룸(4층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[19] = new StudyRoomData("20 스터디룸(4층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[20] = new StudyRoomData("21 스터디룸(4층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[21] = new StudyRoomData("22 스터디룸(4층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[22] = new StudyRoomData("23 스터디룸(4층)", 3,8, 10,21,2, 0,26837);
        studyRoomArr[23] = new StudyRoomData("24 스터디룸(4층)", 3,8, 10,21,2, 1,26950);
        studyRoomArr[24] = new StudyRoomData("25 스터디룸(1층)", 2,4, 00,24,4, 0,26837);
        studyRoomArr[25] = new StudyRoomData("26 스터디룸(1층)", 2,4, 00,24,4, 0,26950);
        studyRoomArr[26] = new StudyRoomData("27 스터디룸(1층)", 2,4, 00,24,4, 0,26837);
        studyRoomArr[27] = new StudyRoomData("28 스터디룸(1층)", 2,4, 00,24,4, 0,26950);
        studyRoomArr[28] = new StudyRoomData("29 스터디룸(1층)", 2,5, 00,24,4, 0,26837);
        studyRoomArr[29] = new StudyRoomData("30 스터디룸(1층)", 2,5, 00,24,4, 0,26950);
        studyRoomArr[30] = new StudyRoomData("31 VS 스터디룸(1층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[31] = new StudyRoomData("32 VR 스터디룸(1층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[32] = new StudyRoomData("33 R1 스터디룸(1층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[33] = new StudyRoomData("34 R2 스터디룸(1층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[34] = new StudyRoomData("35 R3 스터디룸(1층)", 3,6, 10,21,2, 0,26837);
        studyRoomArr[35] = new StudyRoomData("36 3D 스터디룸(1층)", 3,6, 10,21,2, 0,26950);
        studyRoomArr[36] = new StudyRoomData("37 WS 스터디룸(1층)", 3,6, 10,21,2, 0,26837);



        //위치 권한 묻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("이 앱의 출석체크 기능을 이용하려면 위치정보이용권한이 필요합니다.");
                builder.setMessage("위치정보 이용이 가능하게 권한을 승인해주세요");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        if(AutoLogin.getLFlag(getApplicationContext())==null) {
            if (loginIntent.getBooleanExtra("로그인완료", false)) {
                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                AutoLogin.setLFLAG(getApplicationContext(),true);
            }
        }
        if(AutoLogin.getALFLAG(getApplicationContext())==true) {
            if (loginIntent.getBooleanExtra("자동로그인완료", false)){
                Toast.makeText(getApplicationContext(), "자동로그인 되었습니다.", Toast.LENGTH_LONG).show();
                AutoLogin.setALFLAG(getApplicationContext(),false);
            }
        }
        // timeNow= (TextView) findViewById(R.id.home_time_now);

        // handler = new PH();

        final Button attendButton = (Button) findViewById(R.id.attendButton);
        Button reservationButton = (Button) findViewById(R.id.reservationButton);
        // Button settingButton = (Button) findViewById(R.id.settingButton);
        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendIntent = new Intent(getApplicationContext(), MyReservationStatusActivity.class);
                startActivity(attendIntent);
            }
        });
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reservationIntent = new Intent(getApplicationContext(), MainReservationActivity.class);
                startActivity(reservationIntent);

            }
        });
        /*
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), MainSettingsActivity.class);
                startActivity(settingIntent);
            }
        });*/
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(exitCount==1)
            {
                Toast.makeText(getApplicationContext(),"한번 더 뒤로가기를 누르면 종료됩니다.",Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.finishAffinity(this);
                System.runFinalization();
                System.exit(0);
            }
            exitCount--;

        }
    }

    //위치권한여부를 사용자의 선택에따라 안내
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("위치정보 이용 불가능");
                    builder.setMessage("위치정보 이용이 불가능해서 출석체크 기능이 제한됩니다.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }

                        });
                    }
                    builder.show();
                }
                return;
            }
        }
    }
    @Override
    public void onStop()
    {
        super.onStop();
        topBarImage.setImageDrawable(null);
        middleBarImage.setImageDrawable(null);
    }
}
