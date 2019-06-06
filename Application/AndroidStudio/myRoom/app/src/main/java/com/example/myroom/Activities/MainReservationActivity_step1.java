/*
 * 예약 2단계이다
 * DB의 겹치는 스터디룸을 찾아 리스트뷰로 나타낸 것이며
 * 추후 리스트뷰의 아이템을 클릭하면 3단계로 넘어가서 방상세정보 출력및
  * 동반자 정보를 입력받을 것이다.
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myroom.Adapters.SearchRoomAdapter;
import com.example.myroom.Adapters.TempAdapter;
import com.example.myroom.Adapters.TempReservAdapter;
import com.example.myroom.Items.ReservationData;
import com.example.myroom.Items.RoomInfo;
import com.example.myroom.Items.TempData;
import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;
import com.example.myroom.SharedPreferences.ReservationStatusSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class MainReservationActivity_step1 extends BaseActivity {
    Intent searchIntent; //검색정보 받아오는 인텐트
    Intent GoFirst;
    private List<RoomInfo> list;
    private ListView listView;          // 검색을 보여줄 리스트변수
    private SearchRoomAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<RoomInfo> arrayList; // 정 어레이리스트
    private ArrayList<RoomInfo> arrayDivision; // 반전 어레이리스트 값의 비교를 위해 모든 값을 채워넣는다.
    int usingTime = 0;
    int tempUsingPeople =0;

    private ListView m_oListView = null;

    ReservationData[] tempReserDataArr = new ReservationData[37];
    int ableRoomCount=0;
    int ableRoomIndex=0;

    RoomInfo searchedTime = new RoomInfo(); //넘겨받은 정보
    //RoomInfo viewRoom = new RoomInfo();

    ///// 아래는 db 접근용 변수/상수들

    String sId;
    String sDate; //어플 사용자의 예약일자
    Date dDate;
    int iStart_time; // 어플 사용자의 예약 시작 시간
    int iEnd_time; // 어플 사용자의 예약 종료 시간

    //PHP에서 JSON 받아오기 위한 태그들

    private static String TAG = "phpquerytest"; // 이건 왜 있는지 몰겟슴

    private static final String TAG_JSON = "webnautes"; // json에서 배열에서 속성명이 뭔지
    private static final String TAG_ROOM_NUM = "room_num";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "end_time";
    private static final String TAG_WARN = "warn";

    String mJsonString; // JSON 쿼리결과 받아오는거

    // 현재 시각과 비교하여 현재 시각보다 이전의 시간은 못하게끔 만들기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reservation_step1);//activity_main_reservation뷰를 참조

        sId = AutoLogin.getAutoLoginName(getApplicationContext()); // 현재 사용자의 아이디(학번) 알기 위해 오토로그인에 저장된 내용에서 가져옴

        searchIntent = getIntent();
        GoFirst = new Intent(getApplicationContext(), MainReservationActivity.class);
        usingTime = searchIntent.getIntExtra("사용시간",1);
        searchedTime.searchDate = (Date)searchIntent.getSerializableExtra("날짜");
        searchedTime.startTime = searchIntent.getIntExtra("시작시간",0);
        tempUsingPeople = searchIntent.getIntExtra("인원",2);
        searchedTime.endTime = searchedTime.startTime+usingTime;

        dDate = searchedTime.searchDate;
        DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        sDate = sdFormat.format(dDate);

        iStart_time = searchedTime.startTime;
        iEnd_time = searchedTime.endTime;

        // 1차로 경고 횟수 알아봄. 1이상이면 불가 0이면 진행
        CheckWarn lDB1 = new CheckWarn(); // 해당내용이 과거 예약목록 불러오기
        lDB1.execute();

        /*
        // 위에서 결과가 없을때만 수행. 순차적 수행 위하여 CheckWarn 에 있음

        // 이미 그 날 예약한게 / 출첵한게 있나
        GetAlreadyThatDay lDB2 = new GetAlreadyThatDay(); // 해당내용이 과거 예약목록 불러오기
        lDB2.execute();

        // 위에서 결과가 없을때만 수행. 순차적 수행 위하여 GetAlreadyThatDay에 있음

        // 해당 예약에 대하여 검색조건에 맞는 스터디룸이 존재하나
        GetReservData lDB3 = new GetReservData(); // 해당내용이 과거 예약목록 불러오기
        lDB3.execute();
        */

    }

    public class CheckWarn extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(MainReservationActivity_step1.this); // 로딩창 보이게 하는용도로 써야되는듯
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
            if (result.equals("10")) { // json으로 받아온거 없을때 : 기존 예약이 없는 경우
                // 이미 그 날 예약한게 / 출첵한게 있나
                GetAlreadyThatDay lDB2 = new GetAlreadyThatDay(); // 해당내용이 과거 예약목록 불러오기
                lDB2.execute();
            } else {
                mJsonString = result;
                checkWarnResult(); // 경고받은 유저라고 창 띄워주기
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/check_warn.php"; // 서버 접속용
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
    } //getAlreadyThatDay()

    private void checkWarnResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            ArrayList<TempData> oData = new ArrayList<>(); // 데이터 넣을 리스트

            JSONObject item = jsonArray.getJSONObject(0);

            // 각 속성에서 가져와서 안드로이드 어플에서 쓸 데이터 구조체에 넣고->
            // 해당 내용들을 배열리스트에 넣고 -> ui편집위해  view리스트에 들어갈만한 리스트에 넣기

            int warn = item.getInt(TAG_WARN);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder
                    .setTitle("알림")
                    .setMessage("예약 결석에 따른 경고 "+warn+" 회로 예약할 수 없습니다..\n")
                    .setCancelable(true)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(GoFirst);
                           dialog.cancel();
                        }
                    });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }
    } // checkWarnResult()

    public class GetAlreadyThatDay extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(MainReservationActivity_step1.this); // 로딩창 보이게 하는용도로 써야되는듯
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
            if (result.equals("10")) { // json으로 받아온거 없을때 : 기존 예약이 없는 경우
                // 스터디룸 검색 수행
                GetReservData lDB3 = new GetReservData();
                lDB3.execute();
            } else {
                mJsonString = result;
                alreadyThatDayResult(); // 이미 예약 있다고 창 띄워주기
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/search_already.php"; // 서버 접속용
            String postParameters = "u_id="+sId+"&u_date="+sDate; // 쿼리문 전해지는 id(학번)
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
                Log.d(TAG, " Http response code - " + responseStatusCode);

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

    private void alreadyThatDayResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            ArrayList<TempData> oData = new ArrayList<>(); // 데이터 넣을 리스트

            JSONObject item = jsonArray.getJSONObject(0);

            // 각 속성에서 가져와서 안드로이드 어플에서 쓸 데이터 구조체에 넣고->
            // 해당 내용들을 배열리스트에 넣고 -> ui편집위해  view리스트에 들어갈만한 리스트에 넣기

            int room_num = item.getInt(TAG_ROOM_NUM);
            int start_time = item.getInt(TAG_START_TIME);
            int end_time = item.getInt(TAG_END_TIME);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder
                    .setTitle("알림")
                    .setMessage("선택한 날에는 이미 사용자의 예약/ 출석체크 하여 이용한 스터디룸이 있습니다.\n" +
                            "스터디룸 : "+MainHomeActivity.studyRoomArr[room_num-1].getName()+
                            "\n날짜 : "+sDate+
                            "\n이용시간 : "+start_time+":00 ~ "+end_time+":00"
                    )
                    .setCancelable(true)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(GoFirst);
                            //finish();
                        }
                    });
            AlertDialog dialog = alertBuilder.create();
            dialog.show();

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }
    } // alreadyThatDayResult()

    public class GetReservData extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(MainReservationActivity_step1.this); // 로딩창 보이게 하는용도로 써야되는듯
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

            Log.d(TAG, "에러코드 - " + result); // 질의문 확인 가능

            asyncDialog.dismiss();
            if (result.equals("10") ) { // json으로 받아온거 없을때
                TextView tvReserv = (TextView) findViewById(R.id.reservation_noAble);
                tvReserv.setVisibility(View.VISIBLE);
            } else {
                mJsonString = result;
                showResult(); // 여기서 에러 나네 자꾸 // 결과 출력위해 리스트에 넣는 함수
            }


        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/search_reservation.php"; // 서버 접속용
            String postParameters = "u_date="+sDate+"&u_start_time="+iStart_time+"&u_end_time="+iEnd_time+"&u_people="+tempUsingPeople; // 쿼리문 전해지는 id(학번)
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
                Log.d(TAG, "response code - " + responseStatusCode);

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
    } //getData()

    private void showResult(){
        ableRoomIndex=0;
        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            ableRoomCount = jsonArray.length(); // 데이터 튜플 갯수
            ArrayList<TempData> oData = new ArrayList<>(); // 데이터 넣을 리스트

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);


                // 각 속성에서 가져와서 안드로이드 어플에서 쓸 데이터 구조체에 넣고->
                // 해당 내용들을 배열리스트에 넣고 -> ui편집위해  view리스트에 들어갈만한 리스트에 넣기

                int room_num = item.getInt(TAG_ROOM_NUM);


                tempReserDataArr[ableRoomIndex] = new ReservationData(sId, room_num, dDate, iStart_time, iEnd_time, sId, 0);
                ableRoomIndex++;

                TempData oItem = new TempData();
                oItem.strDate = sDate + "  /  " + iStart_time + ":00 ~ " + iEnd_time + ":00";
                oItem.strName = MainHomeActivity.studyRoomArr[room_num - 1].getName() + "  /  " + "최소출석요구인원 : " + MainHomeActivity.studyRoomArr[room_num - 1].getMin() + "명";
                oData.add(oItem);

            }  //for
            // ListView, Adapter 생성 및 연결 ------------------------
            m_oListView = (ListView) findViewById(R.id.reservation_Searched_ListView);
            TempReservAdapter oAdapter = new TempReservAdapter(oData);
            m_oListView.setAdapter(oAdapter);
            if (oData.size() == 0) { // 데이터가 없을때 // 2차 확인용
                TextView tvReserv = (TextView) findViewById(R.id.reservation_noAble);
                tvReserv.setVisibility(View.VISIBLE);
            }

            //여기에 목록눌렀을때 이벤트 구현하시오
            m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView oTextTitle = (TextView) view.findViewById(R.id.textTitle);

                    Intent reservActivity1_Intent = new Intent(getApplicationContext(), MainReservationActivity_step2.class);
                    reservActivity1_Intent.putExtra("학번",sId);
                    reservActivity1_Intent.putExtra("스터디룸번호",tempReserDataArr[position].getRoom_num());
                    reservActivity1_Intent.putExtra("예약날짜",sDate);
                    reservActivity1_Intent.putExtra("시작시간", iStart_time);
                    reservActivity1_Intent.putExtra("종료시간", iEnd_time);
                    reservActivity1_Intent.putExtra("대표자학번", sId);
                    reservActivity1_Intent.putExtra("예상사용인원",tempUsingPeople);
                    reservActivity1_Intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(reservActivity1_Intent);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_hold);
                }
            });

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_hold);
    }

}
