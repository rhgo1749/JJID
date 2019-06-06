package com.example.myroom.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.example.myroom.Adapters.MuAdapter;
import com.example.myroom.Adapters.TempAdapter;
import com.example.myroom.Items.MuTempData;
import com.example.myroom.Items.ReservationData;
import com.example.myroom.Items.StuData;
import com.example.myroom.Items.StudyRoomData;
import com.example.myroom.Items.TempData;
import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;
import com.example.myroom.SharedPreferences.ReservationStatusSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class MyReservationStatusActivity extends BaseActivity {

    //0530 머훈꺼랑 합친코드드
    public ArrayList<ReservationData> tempReserDataSaving  = new ArrayList<>();
    //수정전 public ArrayList<StudyRoomData> tempReserDataSaving;
    private int position;
    private ListView m_oListView = null;

    String sId; //어플 사용자 학번

    //PHP에서 JSON 받아오기 위한 태그들

    private static String TAG = "phpquerytest"; // 이건 왜 있는지 몰겟슴

    private static final String TAG_JSON = "webnautes"; // json에서 배열에서 속성명이 뭔지
    private static final String TAG_STU_NUM = "stu_num";
    private static final String TAG_ROOM_NUM = "room_num";
    private static final String TAG_R_DATE = "r_date";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "end_time";
    private static final String TAG_DELEGATOR = "delegator";
    private static final String TAG_AT_CHECK = "at_check";

    String mJsonString; // JSON 쿼리결과 받아오는거

    //public static StudyRoomData[] studyRoomArr = new StudyRoomData[31]; // 스터디룸 정보 검색 위해 DB왔다갔다 하는거 방지 위함

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_myreservationstatus);
        position=0;
        ReservationStatusSave.clearALL(getApplicationContext());//이거 한번초기화후 다시 불러들이는용도
        // 스터디룸 정보 검색 위해 DB왔다갔다 하는거 방지 위함 -> 나중에 프로젝트가 진짜 서버와 연결되면 구현고려


        sId = AutoLogin.getAutoLoginName(getApplicationContext()); // 현재 사용자의 아이디(학번) 알기 위해 오토로그인에 저장된 내용에서 가져옴


        TextView nowReservationStudyRoomAttendButton = (Button) findViewById(R.id.now_Reservation_Studyroom_AttendButton);

        //이 아래에 출첵이랑 퇴실 눌렀을때 이벤트 구현하시오
        nowReservationStudyRoomAttendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendIntent = new Intent(getApplicationContext(), AttendActivity.class);
                startActivity(attendIntent);
            }
        });
        GetData lDB = new GetData(); // 해당내용이 과거 예약목록 불러오기
        lDB.execute();

    }

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(getApplicationContext(),MainHomeActivity.class);
        startActivity(goHome);
    }

    // DB에서 쿼리문통해 해당 사용자의 과거 예약기록 가져오기
    public class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(MyReservationStatusActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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

            Log.d(TAG, "response - " + result); // 질의문 확인 가능

            if (result.equals("10")) { // json으로 받아온거 없을때
                TextView tnnv2 = (TextView) findViewById(R.id.reservation_noNow2);
                tnnv2.setVisibility(View.VISIBLE);
            } else {
                mJsonString = result;
                showResult(); // 여기서 에러 나네 자꾸 // 결과 출력위해 리스트에 넣는 함수
            }
            asyncDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/user_myreservation.php"; // 서버 접속용
            String postParameters = "u_id="+sId; // 쿼리문 전해지는 id(학번)
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
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            int nDatCnt = jsonArray.length(); // 데이터 튜플 갯수
            ArrayList<MuTempData> oData = new ArrayList<>(); // 데이터 넣을 리스트

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);


                // 각 속성에서 가져와서 안드로이드 어플에서 쓸 데이터 구조체에 넣고->
                // 해당 내용들을 배열리스트에 넣고 -> ui편집위해  view리스트에 들어갈만한 리스트에 넣기

                String stu_num = item.getString(TAG_STU_NUM);
                int room_num = item.getInt(TAG_ROOM_NUM);
                String r_date = item.getString(TAG_R_DATE);
                Date dr_date = new Date();
                try { // string을 date형태로 저장할때 transFormat.parse 써야되는데 try구문에 없으면 에러남
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dr_date = transFormat.parse(r_date);
                } catch(Exception e) {

                }
                int start_time = item.getInt(TAG_START_TIME);
                int end_time = item.getInt(TAG_END_TIME);
                String delegator = item.getString(TAG_DELEGATOR);
                int at_check = item.getInt(TAG_AT_CHECK);

                //
                final ReservationData tempReserData = new ReservationData(stu_num, room_num, dr_date, start_time, end_time, delegator, at_check);
                ReservationStatusSave.setMyReservation(getApplicationContext(),tempReserData,position);
                //0530 머훈꺼랑 합친코드
                tempReserDataSaving.add(tempReserData);
                //
                position++;//다음행으로

                MuTempData oItem = new MuTempData();
                oItem.strDate = r_date+"  /  "+start_time+":00 ~ "+end_time+":00";
                oItem.strName = MainHomeActivity.studyRoomArr[room_num-1].getName(); // 만약 room_num이 0이 존재하여 -1 들어가면 어플 터짐
                oItem.detail_room_num=room_num;
                //0530 머훈꺼랑 합친코드
                oItem.detail_at_check = at_check;
                //
                oData.add(oItem);
            }  //for
            // ListView, Adapter 생성 및 연결 ------------------------
            m_oListView = (ListView) findViewById(R.id.pastListView);
            //0530머훈꺼랑 합친코드
            MuAdapter oAdapter = new MuAdapter(getApplicationContext(),oData);
            //수정전 TempAdapter oAdapter = new TempAdapter(oData);

            m_oListView.setAdapter(oAdapter);
            if (oData.size() == 0) {
                TextView tnnv2 = (TextView) findViewById(R.id.reservation_noNow2);
                tnnv2.setVisibility(View.VISIBLE);
            }

            //여기에 목록눌렀을때 이벤트 구현하시오
            m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //0530 머훈꺼랑 합친코드
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent goDetail;
                        tempReserDataSaving.get(position).getStu_num();
                        goDetail= new Intent(MyReservationStatusActivity.this, MyReservationDetailStatusActivity.class);
                        goDetail.putExtra("STD_NUM", tempReserDataSaving.get(position).getStu_num());
                        goDetail.putExtra("ROOM_NUM", tempReserDataSaving.get(position).getRoom_num());
                        goDetail.putExtra("R_DATE", tempReserDataSaving.get(position).getR_date());
                        goDetail.putExtra("START_TIME",tempReserDataSaving.get(position).getStart_time());
                        goDetail.putExtra("END_TIME", tempReserDataSaving.get(position).getEnd_time());
                        goDetail.putExtra("DELEGATOR", tempReserDataSaving.get(position).getDelegator());
                        goDetail.putExtra("AT_CHECK", tempReserDataSaving.get(position).getAt_check());
                        startActivity(goDetail);
                }
                //
            });

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }

    }

    public void nowShow(TextView tv) {
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

        String strMsg = "선택한 아이템의 position 은 " + "1" + " 입니다.\nTitle 텍스트 :" + tv.getText();
        oDialog.setMessage(strMsg)
                .setPositiveButton("확인", null)
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }
} //MyReservationStatusActivity



