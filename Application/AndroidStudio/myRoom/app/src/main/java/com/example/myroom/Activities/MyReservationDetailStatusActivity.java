/*
 * 예약 3단계이다
 * 선택한 스터디룸의 정보와 예약자의 정보를 나열하며
 * 스터디룸에 따라 동행자 정보를 입력받아 예약하면 DB로 예약정보를 전송한다.
 *
 *
 *
 */
package com.example.myroom.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

import com.example.myroom.Items.ReservationData;
import com.example.myroom.R;
import com.example.myroom.SharedPreferences.AutoLogin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MyReservationDetailStatusActivity extends BaseActivity {
    Intent selectedIntent; //선택스터디룸정보 받아오는 인텐트
    Intent GoMyReser;

    Date time; //현재시간 대조
    ReservationData selectedDetail = new ReservationData(); //넘겨받은 정보

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static String TAG = "phpquerytest"; // 이건 왜 있는지 몰겟슴
    private static final String TAG_JSON = "webnautes"; // json에서 배열에서 속성명이 뭔지
    String mJsonString; // JSON 쿼리결과 받아오는거
    String sId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_myreservationstatus_detail);//activity_main_reservation뷰를 참조
        GoMyReser = new Intent(getApplicationContext(),MyReservationStatusActivity.class);
        sId = AutoLogin.getAutoLoginName(getApplicationContext()); // 현재 사용자의 아이디(학번) 알기 위해 오토로그인에 저장된 내용에서 가져옴

        //문자열 합성용 문자열
        String startTimeText;
        String endTimeText;
        //현재시간과 예약시간 비교
        long different =200;

        selectedIntent=getIntent();
        selectedDetail.setStu_num(selectedIntent.getStringExtra("STD_NUM"));
        selectedDetail.setRoom_num(selectedIntent.getIntExtra("ROOM_NUM",0));
        selectedDetail.setR_date((Date)selectedIntent.getSerializableExtra("R_DATE"));
        selectedDetail.setStart_time(selectedIntent.getIntExtra("START_TIME", -1));
        selectedDetail.setEnd_time(selectedIntent.getIntExtra("END_TIME", -1));
        selectedDetail.setDelegator(selectedIntent.getStringExtra("DELEGATOR"));
        selectedDetail.setAt_check(selectedIntent.getIntExtra("AT_CHECK", -1));

        if(selectedDetail.getStart_time()<10)
            startTimeText="0"+selectedDetail.getStart_time();
        else
            startTimeText=""+selectedDetail.getStart_time();
        if(selectedDetail.getEnd_time()<10)
            endTimeText="0"+selectedDetail.getEnd_time();
        else
            endTimeText=""+selectedDetail.getEnd_time();


        final TextView roomName = findViewById(R.id.myreservation_Selected_RoomName); //스터디룸명
        final TextView rightDate = findViewById(R.id.myreservation_Selected_ReservationDay); //예약날짜
        final TextView term = findViewById(R.id.myreservation_Selected_ReservationTime); //예약시간
        final TextView stdNum = findViewById(R.id.myreservation_Selected_Delegator); // 대표자 학번
        final TextView minFull = findViewById(R.id.myreservation_Selected_RoomMinFull); //대표자 이름

        TextView checkText = findViewById(R.id.myreservation_detail_atcheck_text);// 체크 되었나
        ImageView checkImage = findViewById(R.id.myreservation_detail_atcheck_image); // 체크되었나 이미지
        Button resultButton = findViewById(R.id.myreservation_detail_button); //의사 결정 버튼

        roomName.setText(MainHomeActivity.studyRoomArr[selectedDetail.room_num-1].getName());
        rightDate.setText(sdf.format(selectedDetail.getR_date()));
        term.setText(startTimeText+":00"+" ~ "+endTimeText+":00");
        stdNum.setText(selectedDetail.getDelegator());
        minFull.setText(MainHomeActivity.studyRoomArr[selectedDetail.room_num-1].getMin()+"명 ~ "+MainHomeActivity.studyRoomArr[selectedDetail.room_num-1].getFull()+"명");

        //뒤로가기 혹은 예약취소 혹은
        //3600000은 1시간이므로 날짜+시작시간>현재시간이면 출석전이므로 예약취소 버튼소환
        different = (selectedDetail.r_date.getTime()+(3600000*selectedDetail.getStart_time())-System.currentTimeMillis())/60000;
        //출석 30분전이면 예약취소버튼 소환
        if(different >= 30 )
        {
            checkText.setText("가능");
            checkImage.setImageResource(R.drawable.myreservationdetail_yet_icon);
            resultButton.setText("예약취소");
            resultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG","Click");
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyReservationDetailStatusActivity.this);
                    if(sId.equals(selectedDetail.getDelegator())) { // 대표자 학번과 같을때만
                        alertBuilder
                                .setTitle("알림")
                                .setMessage("스터디룸 이름 : " + MainHomeActivity.studyRoomArr[selectedDetail.getRoom_num() - 1].getName() + "\n" +
                                        "예약 날짜 : " + sdf.format(selectedDetail.getR_date()) + "\n" +
                                        "예약 시간 : " + selectedDetail.getStart_time() + ":00 ~ " + selectedDetail.getEnd_time() + ":00\n" +
                                        "대표자 학번 : " + selectedDetail.getDelegator() + "\n" +
                                        "해당 예약을 정말 취소하시겠습니까?\n(대표자만 가능)"
                                )
                                .setCancelable(true)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeleteDB IDB = new DeleteDB();
                                        IDB.execute();
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                    } else { // 대표자 학번과 같지 않을 때
                        alertBuilder
                                .setTitle("알림")
                                .setMessage("대표자가 아닌 사람은 예약을 취소하실 수 없습니다."
                                )
                                .setCancelable(true)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                });

                    } // if-else 대표자 학번과 같은지 검사

                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
            });
        }
        //현재시간이 출석시간 30분 전 이내고 출석시간 10분후이면 뒤로가기 버튼 소환
        else if(different<30 && different>-10)
        {
            if(MainHomeActivity.studyRoomArr[selectedDetail.room_num-1].getMin()>selectedDetail.at_check) {
                checkImage.setImageResource(R.drawable.myreservationdetail_yet_icon);
                checkText.setText("가능");
            }
            else {
                checkImage.setImageResource(R.drawable.myreservationdetail_attend_icon);
                checkText.setText("완료");
            }
            resultButton.setText("뒤로가기");
            resultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        //이미 출석을 했어야하는 상황이므로 이의제기 버튼 혹은 뒤로가기 소환
        else
        {
            //최소인원보다 출석체크 인원이 적으면
            if(MainHomeActivity.studyRoomArr[selectedDetail.room_num-1].getMin()>selectedDetail.at_check) {
                checkImage.setImageResource(R.drawable.myreservationdetail_absent_icon);
                checkText.setText("불가능");
                resultButton.setText("이의제기");
                resultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        String emailInfo = "날짜 : "+rightDate.getText().toString()+" 시간 : "+term.getText().toString()+"\n"+"방 정보 : "+roomName.getText().toString()+ " 출석 이의제기";
                        String tmep = term.getText().toString();
                        email.setType("plain/text");
                        // email setting 배열로 해놔서 복수 발송 가능
                        String[] address = {"rhgo1749@naver.com"};//관리자 메일주소
                        email.putExtra(Intent.EXTRA_EMAIL, address);
                        email.putExtra(Intent.EXTRA_SUBJECT,emailInfo);
                        email.putExtra(Intent.EXTRA_TEXT,"이의제기 내용을 입력해주세요");
                        startActivity(email);

                    }
                });
            }
            else {
                checkImage.setImageResource(R.drawable.myreservationdetail_attend_icon);
                checkText.setText("완료");
                resultButton.setText("뒤로가기");
                resultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
    } // onCreate();

    public class DeleteDB extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(MyReservationDetailStatusActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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

            if (result.equals("10")) { // json으로 받아온거 없을때 : delete 성공
                AlertDialog.Builder alertBuilder7 = new AlertDialog.Builder(MyReservationDetailStatusActivity.this);
                alertBuilder7
                        .setTitle("알림")
                        .setMessage("예약취소가 성공하였습니다. 예약현황을 확인해주세요.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent statusIntent = new Intent(getApplicationContext(), MyReservationStatusActivity.class);
                                startActivity(statusIntent);
                            }
                        });
                AlertDialog dialog7 = alertBuilder7.create();
                dialog7.show();

            } else { // delete 실패 아직 고려안함
                mJsonString = result;
                AlertDialog.Builder alertBuilder6 = new AlertDialog.Builder(MyReservationDetailStatusActivity.this);
                alertBuilder6
                        .setTitle("알림")
                        .setMessage("예약취소에 실패하였습니다 \n대표자가 자신인지 확인해주세요.\n에러코드 : "+result)
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

            String serverURL = "http://" + getString(R.string.server_ip) + "/reservation/delete_myreservation.php"; // 서버 접속용
            String postParameters ="u_id="+sId+
                    "&u_room_num="+selectedDetail.getRoom_num()+
                    "&u_date="+sdf.format(selectedDetail.getR_date())+
                    "&u_start_time="+selectedDetail.getStart_time();
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

}
