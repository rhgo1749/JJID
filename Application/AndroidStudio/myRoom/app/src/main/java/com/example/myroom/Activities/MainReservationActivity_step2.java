/*
 * 예약 3단계이다
 * 선택한 스터디룸의 정보와 예약자의 정보를 나열하며
 * 스터디룸에 따라 동행자 정보를 입력받아 예약하면 DB로 예약정보를 전송한다.
 *
 *
 *
 */
package com.example.myroom.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myroom.Items.RoomInfo;
import com.example.myroom.Items.TempData;
import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MainReservationActivity_step2 extends BaseActivity {
    Intent selectedIntent; //선택스터디룸정보 받아오는 인텐트
    String sId;
    int room_num; // 예약 방 번호
    String sDate; //어플 사용자의 예약일자
    Date dDate;
    int iStart_time; // 어플 사용자의 예약 시작 시간
    int iEnd_time; // 어플 사용자의 예약 종료 시간
    String sDelegator;
    int iPeople; // 예상 사용인원

    //db 접근 위함

    //PHP에서 JSON 받아오기 위한 태그들

    private static String TAG = "phpquerytest"; // 이건 왜 있는지 몰겟슴

    private static final String TAG_JSON = "webnautes"; // json에서 배열에서 속성명이 뭔지
    private static final String TAG_ROOM_NUM = "room_num";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "end_time";

    String mJsonString; // JSON 쿼리결과 받아오는거
    Intent GoMyReser;
    Intent GoFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reservation_step2);//activity_main_reservation뷰를 참조

        TextView tvNotice =findViewById(R.id.tvNotice_reservation);
        GoMyReser = new Intent(getApplicationContext(), MyReservationStatusActivity.class);
        GoFirst = new Intent(getApplicationContext(), MainReservationActivity.class);
        tvNotice.setText("예약 정보 안내\n" +
                "1.해당 예약날짜 및 시간 10분 전후로 최소인원 이상이\n" +
                "반드시 출석체크를 해야합니다.\n" +
                "2. 예약취소는 예약 시작 시간 30분 전까지만 가능합니다.\n" +
                "3. 해당 예약 10분 후까지 출석이 완료되지 않을 경우 대표자는 패널티를 받습니다.");

        Button reservationButton = (Button) findViewById(R.id.reservation_Selected_Button);
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDB lDB1 = new InsertDB(); // 해당내용이 과거 예약목록 불러오기
                lDB1.execute();
            }
        });

        selectedIntent=getIntent();
        sId = selectedIntent.getStringExtra("학번");
        room_num = selectedIntent.getIntExtra("스터디룸번호",0);
        sDate = selectedIntent.getStringExtra("예약날짜");
        iStart_time = selectedIntent.getIntExtra("시작시간",0);
        iEnd_time = selectedIntent.getIntExtra("종료시간",0);
        sDelegator = selectedIntent.getStringExtra("대표자학번");
        iPeople = selectedIntent.getIntExtra("예상사용인원",0);

        TextView tvStdNum = findViewById(R.id.reservation_Selected_StdNum);
        TextView tvUserName = findViewById(R.id.reservation_Selected_UserName);

        TextView tvReservDay = findViewById(R.id.reservation_Selected_ReservationDay);
        TextView tvReservTime = findViewById(R.id.reservation_Selected_ReservationTime);
        TextView tvReservRoomMinFull = findViewById(R.id.reservation_Selected_RoomMinFull);

        TextView tvRoomName = findViewById(R.id.reservation_Selected_RoomName);
        TextView tvReservPhone = findViewById(R.id.reservation_Selected_Phone);
        TextView tvReservEmail = findViewById(R.id.reservation_Selected_Email);

        tvStdNum.setText(sId);
        tvUserName.setText(MainHomeActivity.userData[0].getName());

        tvReservDay.setText(sDate);
        tvReservTime.setText(iStart_time+":00 ~ "+iEnd_time+":00");
        tvReservRoomMinFull.setText(MainHomeActivity.studyRoomArr[room_num-1].getMin()+"명 ~ "+MainHomeActivity.studyRoomArr[room_num-1].getFull()+"명");

        tvRoomName.setText(MainHomeActivity.studyRoomArr[room_num-1].getName());
        tvReservPhone.setText(MainHomeActivity.userData[0].getphone_num());
        tvReservEmail.setText(MainHomeActivity.userData[0].getemail());
    }
    @Override
    public void onBackPressed() {
        startActivity(GoFirst);
    }
    public class InsertDB extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(MainReservationActivity_step2.this); // 로딩창 보이게 하는용도로 써야되는듯
        //ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() { // 로딩창 보이게 하는 용도
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        // 백그라운드에서 쿼리문통해 json결과가 나오면 그 종료됨을 확인하고 해당 질의결과문 string 형태로 함수 가져가줌
        // 로그 통해 질의결과문 확인도 가능
        @Override
        protected void onPostExecute(String result) {

            Log.d(TAG, "응답코드 - " + result); // 질의문 확인 가능

            asyncDialog.dismiss();
            if (result.equals("10")) { // json으로 받아온거 없을때 : insert 성공
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainReservationActivity_step2.this);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("예약에 성공하였습니다.\n")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();

                                startActivity(GoMyReser);
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();

            } else { // insert 실패 아직 고려안함
                mJsonString = result;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainReservationActivity_step2.this);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("예약에 실패하였습니다. 에러코드 : "+result)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();

                                startActivity(GoMyReser);
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/insert_reservation.php"; // 서버 접속용
            String postParameters = "u_id="+sId+
                    "&u_room_num="+room_num+
                    "&u_date="+sDate+
                    "&u_start_time="+iStart_time+
                    "&u_end_time="+iEnd_time+
                    "&u_delegator="+sId+
                    "&u_at_check=0"+
                    "&u_dele_check=0"; // 쿼리문 전해지는 데이터들
            Log.e("POST", postParameters);

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000); // 일정시간내로 접속못하면 강제종료
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                // 아래 과정은 잘 모름
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "Http response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                //결과문 가져와서 string 형태로 반환

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e); // 결과문 없는 = 실패했을 경우
                errorString = e.toString();

                return null;
            }
        }
    } //getAlreadyThatDay()


}
