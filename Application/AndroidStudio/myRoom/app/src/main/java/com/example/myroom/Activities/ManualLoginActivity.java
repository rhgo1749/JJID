/*
 * 로그인 하는 클래스이다 추후 DB와 연동할 예정이다.
 *
 *
 *
 *
 *
 */
package com.example.myroom.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myroom.Adapters.TempReservAdapter;
import com.example.myroom.Items.ReservationData;
import com.example.myroom.Items.StuData;
import com.example.myroom.Items.TempData;
import com.example.myroom.R;
import com.example.myroom.Services.NetworkUtils;
import com.example.myroom.SharedPreferences.AutoLogin;

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
import java.util.ArrayList;

public class ManualLoginActivity extends AppCompatActivity {
    int exitCount = 1; //종료를 위한 카운트
    EditText et_id, et_pw;
    String sId, sPw;
    int loginOK = 0;

    //// db연동하여 로그인 후 유저 데이터 받아오기 용도

    String mJsonString; // JSON 쿼리결과 받아오는거

    private static String TAG = "phpquerytest"; // 이건 왜 있는지 몰겟슴

    private static final String TAG_JSON = "webnautes"; // json에서 배열에서 속성명이 뭔지
    private static final String TAG_STU_NUM = "stu_num";
    private static final String TAG_STU_NAME = "name";
    private static final String TAG_STU_PHONE = "phone_num";
    private static final String TAG_STU_EMAIL = "email";


    /////

    //키보드 숨기는 함수
    private void hidekeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!NetworkUtils.isNetworkConnected(this))
        {
            Toast.makeText(getApplicationContext(), "네트워크 연결오류", Toast.LENGTH_SHORT).show();
        }
        Intent intent = getIntent();
        if(intent.getBooleanExtra("자동로그인실패",false))
        {
            Toast.makeText(getApplicationContext(),"자동로그인실패",Toast.LENGTH_LONG).show();
        }
        et_id = (EditText) findViewById(R.id.inputStdNumber);
        et_pw = (EditText) findViewById(R.id.inputPassword);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    hidekeyboard(et_id);
                    hidekeyboard(et_pw);
                    sId = et_id.getText().toString();
                    sPw = et_pw.getText().toString();
                } catch (NullPointerException e) {
                    Log.e("err", e.getMessage());
                }

                GetUserData lDB = new GetUserData();
                lDB.execute();
                if(et_id.getText().toString().compareTo("123")==0) {
                    Intent startIntent = new Intent(getApplicationContext(), MainHomeActivity.class);
                    startIntent.putExtra("로그인완료", true);
                    startActivity(startIntent);
                }

            } // onClick(v)
        });
        et_id.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                        et_pw.requestFocus();
                        // 다음 동작
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });
        et_pw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        loginButton.performClick();
                        // 완료 동작
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (exitCount == 1) {
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.finishAffinity(this);
            System.runFinalization();
            System.exit(0);
        }
        exitCount--;
    }

    public class LoginDB extends AsyncTask<Void, Integer, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(ManualLoginActivity.this); // 로딩창 보이게 하는용도로 써야되는듯


        @Override
        protected void onPreExecute() { // 로딩창 보이게 하는 용도
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "";
            Log.e("POST", param);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://" + getString(R.string.server_ip) + "/login/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA", data);


                if (data.equals("0")) {
                    Log.e("RESULT", "성공적으로 처리되었습니다!");
                    asyncDialog.dismiss();
                    AutoLogin.clearAutoLogin(ManualLoginActivity.this);
                    AutoLogin.clearAutoLoginPW(ManualLoginActivity.this);
                    AutoLogin.setAutoLoginName(ManualLoginActivity.this, et_id.getText().toString());
                    AutoLogin.setAutoLoginPW(ManualLoginActivity.this,et_pw.getText().toString());
                    Intent startIntent = new Intent(getApplicationContext(), MainHomeActivity.class);
                    startIntent.putExtra("로그인완료", true);
                    startActivity(startIntent);

                } else {
                    asyncDialog.dismiss();
                    Log.e("RESULT","에러 발생! ERRCODE = "+data);
                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(getApplicationContext(), "아이디나 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) { // 로딩창 보이게 하는 용도 // 종료



            super.onPostExecute(result);
        }

    } // LoginDB

    // 해당 클래스 및 함수는 로그인 후 유저데이터 받아오기 위함

    public class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(ManualLoginActivity.this); // 로딩창 보이게 하는용도로 써야되는듯
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
                Log.d(TAG, "에러코드 - 10, 데이터 받아오기 실패" ); // 질의문 확인 가능
            } else {
                mJsonString = result;
                saveResult(); // 결과 넣기
            }
            LoginDB lDB2 = new LoginDB();
            lDB2.execute();


        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + getString(R.string.server_ip) + "/login/search_userdata.php"; // 서버 접속용
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
    } //GetUserData()

    private void saveResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString); // jsonString 형태를 json오브젝트에 넣음. 해당 배열은 각 속성 갖고있음
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);

            String stu_num = item.getString(TAG_STU_NUM);
            String stu_name = item.getString(TAG_STU_NAME);
            String stu_email = item.getString(TAG_STU_EMAIL);
            String stu_phone = item.getString(TAG_STU_PHONE);

            MainHomeActivity.userData[0] = new StuData(stu_num, stu_name, "dummy", stu_email, stu_phone, 0);
            /*
            MainHomeActivity.userData.setStu_num(stu_num);
            MainHomeActivity.userData.setName(stu_name);
            MainHomeActivity.userData.setEmail(stu_email);
            MainHomeActivity.userData.setPhone_num(stu_phone);*/
            // 비밀번호와 경고값은 사용하지 않기때문에 임시값을 넣어놓음. 절대 사용하지 말자

        } catch (JSONException e) { // 실패시

            Log.d(TAG, "showResult : ", e);
        }

    } //



}
