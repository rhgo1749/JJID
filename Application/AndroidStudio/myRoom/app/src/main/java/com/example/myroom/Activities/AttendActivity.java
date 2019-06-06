/*
1. 예약이 있나 검색

select stu_num, room_num, r_date, r_time, delegator, at_check, dele_check from reservation where room_num = x, r_date = x, r_time = x;
// 단, room과 join하여 at_check<min이여야함.

결과가 0이면 해당 룸,날,시간에 예약이 없거나 이미 출석체크가 완료됨 -> 종료

결과가 1이라도 있으면 학번 대조, 사용자와 같은 학번이 있나 검색

1-1. 같은 학번이 있다
-> 받아온 값중 delegator와 dele_check 사용하여 해당 사용자가 대표자인지 검색

1-1-1. 같은 학번이 있지만 대표자가 아니다
-> 이미 해당 방에 출석 체크를 했음. -> 종료

1-1-2. 같은 학번이 있고 대표자 본인이다.
-> dele_check값 검색 	-> 0이면 2번 진행 후 4번으로 진행 및 dele_check값을 1로 증가시키는 진행
			-> 1이면 해당 대표자도 해당 방에 출석 체크를 했음 -> 종료

1-2. 같은 학번이 없다.
-> 해당 사용자 출석체크 진행

2. warn 검색
해당 사용자의 경고값 검색.	-> 0이면 출석체크 진행
			-> 1이면 경고로 인해 출석체크/예약 사용 불가 -> 종료

3. insert 예약
해당 사용자의 해당 룸 날짜 시간 출첵 그대로 복사하여 튜플 하나 삽입. 예약현황을 만듬

4. 해당 예약 관련 모든 튜플 검색하여 at_check++해줌.
// 대표자 본인일경우 dele_check값도 ++해줌
 */


package com.example.myroom.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date; //날짜용도
import java.util.List;

import com.example.myroom.Items.TempData;
import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AttendActivity extends BaseActivity implements BeaconConsumer {

    int checkRoom = 0; // 방 골라야만 다음 동작 하게끔
    int select_Room_num = 0; // 고른 방 배열(방번호) 번호
    String nowDate;
    int nowHour = 0; // 예약 예상 시간
    int nowMin = 0; // 예약 예상 분
    String sId; // 사용자 학번
    String sDelegator; // 대표자 학번 받아올거
    String[] sIdArr = new String[10]; // 예약에 관련된 학번들 받아올거 (최대 10개-> 요구최대출석인원이니까)
    int sIdArrIndex=0; // 배열 접근 위한 값
    int arrCnt = 0; // 배열 최대 갯수
    int iAt_check = 0; // at_check값 비교위하여
    int iDele_check = 0; // dele_check값 비교위하여 // 대표자가 출첵 했나안했나 여부
    int iEnd_time = 0; // 혹시 끝나는 시간 필요할까봐

    int sIdFlag = 0; // 같은 학번이 있는지 검사 플래그 0이 기본 및 같은거 없음. 1은 있음
    int deleFlag = 0; // 같은 학번이 있는데 해당사용자가 대표자인경우의 플래그 0이 기본 및 대표자아님. 1은 대표자임
    int exitFlag = 0; // 종료 위한 플래그 0이 종료 안함 1이 종료함

    private static String TAG = "phpquerytest"; // 이건 왜 있는지 몰겟슴

    private static final String TAG_JSON = "webnautes"; // json에서 배열에서 속성명이 뭔지
    private static final String TAG_STU_NUM = "stu_num";
    private static final String TAG_ROOM_NUM = "room_num";
    private static final String TAG_R_DATE = "r_date";
    private static final String TAG_START_TIME = "start_time";
    private static final String TAG_END_TIME = "end_time";
    private static final String TAG_DELEGATOR = "delegator";
    private static final String TAG_AT_CHECK = "at_check";
    private static final String TAG_DELE_CHECK = "dele_check";
    private static final String TAG_WARN = "warn";

    String mJsonString; // JSON 쿼리결과 받아오는거

    //비콘 관련 변수들
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    private List<Integer> BeaconminerInfo = new ArrayList<>(); // 비콘을 담는 배열
    private List<Integer> BeaconmajorInfo = new ArrayList<>(); // 비콘을 담는 배열
    private int beaconhere; // 처음엔 검사전이므로 비콘은없는걸로침
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);

        beaconhere = 0;
        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);



        // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
        // 필자의 경우에는 아래처럼 하니 잘 동작했음.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);

        //>


        sId = AutoLogin.getAutoLoginName(getApplicationContext()); // 현재 사용자의 아이디(학번) 알기 위해 오토로그인에 저장된 내용에서 가져옴

        // 초기화 근데 string은 안했음
        checkRoom = 0;
        select_Room_num = 0; // 고른 방 배열(방번호) 번호
        nowHour = 0; // 예약 예상 시간
        nowMin = 0; // 예약 예상 분
        sIdArrIndex=0; // 배열 접근 위한 값
        arrCnt = 0; // 배열 최대 갯수
        iAt_check = 0; // at_check값 비교위하여
        iDele_check = 0; // dele_check값 비교위하여 // 대표자가 출첵 했나안했나 여부
        iEnd_time = 0; // 혹시 끝나는 시간 필요할까봐
        sIdFlag = 0; // 같은 학번이 있는지 검사 플래그 0이 기본 및 같은거 없음. 1은 있음
        deleFlag = 0; // 같은 학번이 있는데 해당사용자가 대표자인경우의 플래그 0이 기본 및 대표자아님. 1은 대표자임
        exitFlag = 0; // 종료 위한 플래그
        //

        Spinner spnSelect_Room = (Spinner)findViewById(R.id.activity_attend_selectRoomNumSpin); // 스피너

        TextView tvInfo =findViewById(R.id.activity_attend_infoTv); // 정보 안내 텍스트뷰
        TextView tvSelectRoomNum =findViewById(R.id.activity_attend_selectRoomNumTv); // 방 번호 안내해주는 텍스트뷰

        Button atBtn = (Button) findViewById(R.id.activity_attend_atBtn); // 출석체크 버튼

        tvInfo.setText("출석체크 안내\n" +
                "1. 하루에 한 예약만 사용하는 것을 원칙으로 합니다.\n"+
                "2. 예약대표자는 당일 자신의 예약에만 출석체크 할 수 있습니다.\n" +
                "3. 현재 날짜 및 시간, 선택한 스터디룸 번호를 바탕으로 예약을 찾습니다.\n" +
                "4. 출석체크 성공 시 동반자 또한 예약현황이 표시되며, 당일 다른 스터디룸은 이용할 수 없습니다.\n" +
                "5. 해당 스터디룸의 최소 출석인원보다 출석인원이 적을 시 대표자가 패널티를 받습니다.");


        //atBtn.setText("출석체크");
        // 스피너 텍스트 넣기

        String[] studyRoomName = {MainHomeActivity.studyRoomArr[0].getName(),MainHomeActivity.studyRoomArr[1].getName(),
                MainHomeActivity.studyRoomArr[2].getName(),MainHomeActivity.studyRoomArr[3].getName(),
                MainHomeActivity.studyRoomArr[4].getName(),MainHomeActivity.studyRoomArr[5].getName(),
                MainHomeActivity.studyRoomArr[6].getName(),MainHomeActivity.studyRoomArr[7].getName(),
                MainHomeActivity.studyRoomArr[8].getName(),MainHomeActivity.studyRoomArr[9].getName(),
                MainHomeActivity.studyRoomArr[10].getName(),MainHomeActivity.studyRoomArr[11].getName(),
                MainHomeActivity.studyRoomArr[12].getName(),MainHomeActivity.studyRoomArr[13].getName(),
                MainHomeActivity.studyRoomArr[14].getName(),MainHomeActivity.studyRoomArr[15].getName(),
                MainHomeActivity.studyRoomArr[16].getName(),MainHomeActivity.studyRoomArr[17].getName(),
                MainHomeActivity.studyRoomArr[18].getName(),MainHomeActivity.studyRoomArr[19].getName(),
                MainHomeActivity.studyRoomArr[20].getName(),MainHomeActivity.studyRoomArr[21].getName(),
                MainHomeActivity.studyRoomArr[22].getName(),MainHomeActivity.studyRoomArr[23].getName(),
                MainHomeActivity.studyRoomArr[24].getName(),MainHomeActivity.studyRoomArr[25].getName(),
                MainHomeActivity.studyRoomArr[26].getName(),MainHomeActivity.studyRoomArr[27].getName(),
                MainHomeActivity.studyRoomArr[28].getName(),MainHomeActivity.studyRoomArr[29].getName(),
                MainHomeActivity.studyRoomArr[30].getName(),MainHomeActivity.studyRoomArr[31].getName(),
                MainHomeActivity.studyRoomArr[32].getName(),MainHomeActivity.studyRoomArr[33].getName(),
                MainHomeActivity.studyRoomArr[34].getName(),MainHomeActivity.studyRoomArr[35].getName(),
                MainHomeActivity.studyRoomArr[36].getName()};

        ArrayAdapter adpSelect_Room= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,studyRoomName);

        spnSelect_Room.setAdapter(adpSelect_Room);

        spnSelect_Room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               checkRoom = 1;
                select_Room_num = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                checkRoom = 0;
            }
        });

        /// 여기까지 스피너



        //< 버튼 구현

        atBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkRoom == 0) { //방 아직 안골랐을때
                }
                else { // 방 고른 후에만
                    long now = System.currentTimeMillis(); // 현재 시간 받기
                    Date tempDate = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
                    SimpleDateFormat sdfMin = new SimpleDateFormat("mm");
                    nowDate = sdf.format(tempDate);
                    nowHour = Integer.parseInt(sdfHour.format(tempDate));
                    nowMin = Integer.parseInt(sdfMin.format(tempDate));
                    int ableCheckFlag = 0;

                    // 현재시분 바탕으로 xx:50 ~ xx:10 일때 xx계산위함
                    // 50~00은 xx +되어야하고 00~10은 xx그대로 시간 계산
                    if(nowMin >= 50) {
                        ableCheckFlag = 1;
                        if(nowHour >= 23) {
                            nowHour  = 0;
                        } else nowHour++;
                    } else if (nowMin < 10) {
                        ableCheckFlag = 1;
                    }

                    if(ableCheckFlag == 1) { // 현재 시간이 출석체크 가능한 시간일때만 작동
                        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AttendActivity.this);
                        alertBuilder
                                .setTitle("알림")
                                .setMessage("스터디룸 이름 : "+MainHomeActivity.studyRoomArr[select_Room_num-1].getName()+"\n"+
                                        "현재 날짜 : "+nowDate+"\n"+
                                        "예약 시간 : "+nowHour+":00 부터\n"+
                                        "해당 정보에 출석체크 하시겠습니까?"
                                        )
                                .setCancelable(true)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CheckReservationDB IDB01 = new CheckReservationDB();
                                        IDB01.execute();
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        }
                                 });

                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();
                    } // if ableCheckFlag
                    else if(ableCheckFlag == 0) {
                        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AttendActivity.this);
                        alertBuilder
                                .setTitle("알림")
                                .setMessage("출석체크가 가능한 시간이 아닙니다. \n정각 10분 전후로 시도해주세요.")
                                .setCancelable(true)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();
                    } // else-if ableCheckFlag

                } // if-else

            } // onClick
        });

        //> 버튼 구현

    } // onCreate()

    // 1. 예약이 있나 검색
    public class CheckReservationDB extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(AttendActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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
                final AlertDialog.Builder alertBuilder3 = new AlertDialog.Builder(AttendActivity.this);
                alertBuilder3
                        .setTitle("알림")
                        .setMessage("스터디룸 이름 : "+MainHomeActivity.studyRoomArr[select_Room_num-1].getName()+"\n"+
                                "현재 날짜 : "+nowDate+"\n"+
                                "예약 시간 : "+nowHour+":00 부터\n"+
                                "해당 예약이 없거나 이미 최소인원 이상이 출석하였습니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog3 = alertBuilder3.create();
                dialog3.show();

            } else {
                mJsonString = result;
                checkReservationDBResult(); // 그 날 예약이 있긴 있음. 이제 검사하자

            } //if-else

        } // onPostExecute()

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/attend/search_reservation.php"; // 서버 접속용
            String postParameters = "u_date="+nowDate+"&u_start_time="+nowHour+"&u_room_num="+select_Room_num; // 쿼리문 전해지는 id(학번)
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
    } //CheckReservationDB()

    private void checkReservationDBResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            arrCnt = jsonArray.length(); // 배열 최대 갯수
            sIdArrIndex=0; // 배열 접근 위한 값

            int fillFlag = 0; // 배열에서 이름만 받아오고 나머지 정보는 중복이니까 나머지 정보 더 안받게 하기위한 값

            // 각 속성에서 가져와서 안드로이드 어플에서 쓸 데이터 구조체에 넣고->
            // 해당 내용들을 배열리스트에 넣고 -> ui편집위해  view리스트에 들어갈만한 리스트에 넣기

            for(int i=0;i<jsonArray.length();i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                sIdArr[i] = item.getString(TAG_STU_NUM);

                if(fillFlag == 0) {
                    iEnd_time = item.getInt(TAG_END_TIME);
                    sDelegator = item.getString(TAG_DELEGATOR); // 대표자 학번 받아올거
                    iAt_check = item.getInt(TAG_AT_CHECK);
                    iDele_check = item.getInt(TAG_DELE_CHECK);
                    fillFlag = 1;
                }
            } // for

            for(int i=0; i<arrCnt; i++) {
                Log.e(TAG,"sIdArr["+i+"]="+sIdArr[i]);
                if(sId.equals(sIdArr[i])) { // 같은 학번이 있나 검색
                    sIdFlag = 1;
                    exitFlag = 1;
                    if(sId.equals(sDelegator)) { // 같은 학번이 있을경우 대표자인지 검색
                        deleFlag = 1;
                        if(iDele_check == 0) { // 같은 학번 튜플이 있고 대표자이며 출첵을 하지 않았을 경우
                            exitFlag = 0;
                            iDele_check++;
                        } else if(iDele_check >= 1) { // 같은 학번 튜플이 있고 대표자였지만 이미 대표자 출첵을 한 경우 종료
                            exitFlag = 1;
                        }
                    } // equals(sDelegator)
                    break;
                } // if equals(sIdArr[i])
            } // for

            if(sIdFlag ==1 && exitFlag == 1) { // 해당 사용자의 튜플이 이미 있고 대표자가 아니면
                final AlertDialog.Builder alertBuilder2 = new AlertDialog.Builder(AttendActivity.this);
                alertBuilder2
                        .setTitle("알림")
                        .setMessage("스터디룸 이름 : "+MainHomeActivity.studyRoomArr[select_Room_num-1].getName()+"\n"+
                                "현재 날짜 : "+nowDate+"\n"+
                                "예약 시간 : "+nowHour+":00 부터\n"+
                                "해당 예약에 이미 출석체크 하셨습니다. 예약 현황을 확인해주세요.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog2 = alertBuilder2.create();
                dialog2.show();
            } else if(sIdFlag == 0 || exitFlag == 0) { // 같은 학번 튜플이 없거나 같은학번 튜플이 있지만 대표자이며 출첵을 하지않았을경우
                //비콘 인증 후 다음 단계로 넘어감
                for(Beacon beacon : beaconList){BeaconmajorInfo.add(beacon.getId2().toInt());
                    BeaconminerInfo.add(beacon.getId3().toInt());} //Id3이 마이너 Id2가 메이저
                for(int i=0;i<BeaconminerInfo.size();i++)
                {
                    Log.d(TAG, "InsertData: "+ (BeaconminerInfo.get(i).toString())+"/"+(BeaconmajorInfo.get(i).toString()));
                    if(BeaconminerInfo.get(i).intValue()==MainHomeActivity.studyRoomArr[select_Room_num-1].beaconNum && BeaconmajorInfo.get(i).intValue()==40001)// 40001은 우리 메이저시드
                    {
                        beaconhere = 1;
                        break;
                    }
                }
                BeaconmajorInfo.clear();
                BeaconminerInfo.clear();
                if(beaconhere==1) {
                    Toast.makeText(getApplicationContext(), "비콘인증완료", Toast.LENGTH_SHORT).show();
                    CheckWarn lDB2 = new CheckWarn(); // 해당내용이 과거 예약목록 불러오기
                    lDB2.execute();
                }
                else{
                    final AlertDialog.Builder alertBuilderB = new AlertDialog.Builder(AttendActivity.this);
                    alertBuilderB
                            .setTitle("알림")
                            .setMessage("위치인증에 실패했습니다.\n"+
                                    "블루투스 연결상태를 확인해주세요.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialogB = alertBuilderB.create();
                    dialogB.show();
                    Toast.makeText(getApplicationContext(), "비콘인증실패", Toast.LENGTH_SHORT).show();
                } // if-else beaconhere
            } // exitFlag

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }
    } // checkReservationDBResult()

    public class CheckWarn extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(AttendActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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
            if (result.equals("10")) { // json으로 받아온거 없을때 : 경고 누적이 없을경우

                if(deleFlag == 0) {
                    InsertDB lDB3 = new InsertDB(); // 해당내용이 과거 예약목록 불러오기
                    lDB3.execute();
                } else { // deleFlag == 1
                    UpdateAttendDB IDB5 = new UpdateAttendDB();
                    IDB5.execute();
                }

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
    } //CheckWarn()

    private void checkWarnResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            ArrayList<TempData> oData = new ArrayList<>(); // 데이터 넣을 리스트

            JSONObject item = jsonArray.getJSONObject(0);

            // 각 속성에서 가져와서 안드로이드 어플에서 쓸 데이터 구조체에 넣고->
            // 해당 내용들을 배열리스트에 넣고 -> ui편집위해  view리스트에 들어갈만한 리스트에 넣기

            int warn = item.getInt(TAG_WARN);

            AlertDialog.Builder alertBuilder4 = new AlertDialog.Builder(this);
            alertBuilder4
                    .setTitle("알림")
                    .setMessage("예약 결석에 따른 경고 "+warn+" 회로 출석할 수 없습니다..\n")
                    .setCancelable(true)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog4 = alertBuilder4.create();
            dialog4.show();

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }
    } // checkWarnResult()

    public class InsertDB extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(AttendActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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
                UpdateAttendDB IDB4 = new UpdateAttendDB();
                IDB4.execute();

            } else { // insert 실패 아직 고려안함
                mJsonString = result;
                AlertDialog.Builder alertBuilder5 = new AlertDialog.Builder(AttendActivity.this);
                alertBuilder5
                        .setTitle("알림")
                        .setMessage("출석->예약에 실패하였습니다. 에러코드 : "+result)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                            }
                        });
                AlertDialog dialog5 = alertBuilder5.create();
                dialog5.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/insert_reservation.php"; // 서버 접속용
            String postParameters = "u_id="+sId+
                    "&u_room_num="+select_Room_num+
                    "&u_date="+nowDate+
                    "&u_start_time="+nowHour+
                    "&u_end_time="+iEnd_time+
                    "&u_delegator="+sDelegator+
                    "&u_at_check="+iAt_check+
                    "&u_dele_check="+iDele_check; // 쿼리문 전해지는 데이터들
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
    } //InserDB()

    public class UpdateAttendDB extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(AttendActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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
                AlertDialog.Builder alertBuilder7 = new AlertDialog.Builder(AttendActivity.this);
                alertBuilder7
                        .setTitle("알림")
                        .setMessage("출석에 성공하였습니다. 예약현황을 확인해주세요.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog7 = alertBuilder7.create();
                dialog7.show();

            } else { // insert 실패 아직 고려안함
                mJsonString = result;
                AlertDialog.Builder alertBuilder6 = new AlertDialog.Builder(AttendActivity.this);
                alertBuilder6
                        .setTitle("알림")
                        .setMessage("출석에 실패하였습니다. 에러코드 : "+result)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog6 = alertBuilder6.create();
                dialog6.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/attend/attend.php"; // 서버 접속용
            String postParameters ="u_room_num="+select_Room_num+
                    "&u_date="+nowDate+
                    "&u_start_time="+nowHour+
                    "&u_at_check="+(iAt_check+1)+
                    "&u_dele_check="+iDele_check; // 쿼리문 전해지는 데이터들
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
    } //UpdateAttendDB()




    ///비콘 관련 함수들
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }


} // AttendActivity Class
