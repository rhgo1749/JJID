/*
 * 예약 1단계이다
 * 달력에서 날짜를 고르고 시간을 골라 DB의 겹치는 시간대의 스터디룸을 찾는다.
 * 출석시간과 퇴실시간을 고를때 퇴실시간을 출석시간 최대+2시간으로 설정하게
 * 해야되는데 아직 못했다.
 *
 *
 *
 *
 */

package com.example.myroom.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.example.myroom.Items.RoomInfo;
import com.example.myroom.Items.WeekendDisabledDecorator;
import com.example.myroom.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainReservationActivity extends BaseActivity {
    private void hidekeyboard(EditText et)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(),0);
    }
    int usingTime = 1;//사용할 시간
    RoomInfo searchingTime = new RoomInfo();// step1클래스에 넘겨줄 검색정보
    String people;
    Intent GoHome;
    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat Hour = new SimpleDateFormat("HH");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reservation);//activity_main_reservation뷰를 참조
        long now = System.currentTimeMillis(); // 현재시간 가져오기
        final Date nowDate = new Date(now); // sql date 자료형 생성후 현재시간 대입
        String nowHour = Hour.format(nowDate);
        final int nowintHour = Integer.parseInt(nowHour);//현재 시간의 시를 인트형으로 받음

        final Spinner start_Spinner = (Spinner)findViewById(R.id.reserve_TimeSpinner1);
        final Spinner finish_Spinner = (Spinner)findViewById(R.id.reserve_TimeSpinner2);
        final EditText people_EditText = (EditText)findViewById(R.id.reserve_EditText);
        final Button nextButton = (Button)findViewById(R.id.reservation_step1_next_button);
        Calendar cd = Calendar.getInstance();
        Date tommorrow = new Date();

        cd.setTime(nowDate); //현재날짜의 calendar 객채를 생성
        cd.add(Calendar.DATE,1);//하루뒤의 날짜계산
        tommorrow = cd.getTime();

        nextButton.setEnabled(false);//초기설정은 비활성화 상태
        searchingTime.searchDate = nowDate; //현재시간을 예약 클래스에 대입
        GoHome = new Intent(getApplicationContext(), MainHomeActivity.class);

        //캘린더뷰 선언과 속성 정하기
        MaterialCalendarView calendarView = (MaterialCalendarView) findViewById(R.id.reservationCalendarView) ; //캘린더뷰 id 찾아서 참조 Ui쪽 캘린더

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView.addDecorator(new WeekendDisabledDecorator(getApplicationContext()));
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_DECORATED_DISABLED);

        final String[] available_Time = {"00:00~","01:00~","02:00~","03:00~","04:00~","05:00~","06:00~","07:00~","08:00~","09:00~","10:00~"
                ,"11:00~","12:00~","13:00~","14:00~","15:00~","16:00~","17:00~","18:00~","19:00~","20:00~","21:00~","22:00~","23:00~"};//가능한 시간을 데이터베이스에서 불러올것
        final String[] available_Time_fixed = new String[24-nowintHour-1];
        String[] available_Time_Term = {"1시간","2시간","3시간","4시간"};
        for(int i =0;i< available_Time_fixed.length;i++)
        {
            available_Time_fixed[i]=available_Time[i+nowintHour+1];
        }

        ArrayAdapter adtRegion;
        ArrayAdapter adtRegion2 = new ArrayAdapter<String>(this, R.layout.items_spinner_dropdown,available_Time_Term );
        if(Hour.format(nowDate).equals("23"))//23시면 내일날짜를 가르키도록
        {
            calendarView.setSelectedDate(tommorrow);
            adtRegion = new ArrayAdapter<String>(this, R.layout.items_spinner_dropdown,available_Time);
        }

       else //오늘이면
        {
            calendarView.setSelectedDate(CalendarDay.today());
            adtRegion = new ArrayAdapter<String>(this, R.layout.items_spinner_dropdown,available_Time_fixed);

        }
        start_Spinner.setAdapter(adtRegion);
        finish_Spinner.setAdapter(adtRegion2);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
            //현재시간보다 미래이면
                if(date.getDate().after(nowDate)) {
                    start_Spinner.setAdapter(null);
                    ArrayAdapter adtRegionTemp = new ArrayAdapter<String>(getApplicationContext(), R.layout.items_spinner_dropdown, available_Time);
                    //adtRegionTemp.setDropDownViewResource(R.layout.items_spinner_dropdown);
                    start_Spinner.setAdapter(adtRegionTemp);
                    start_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv = (TextView)parent.getChildAt(0);
                            tv.setTextColor(Color.BLACK);
                            searchingTime.startTime = position;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
                else{
                    for (int i = 0; i < available_Time_fixed.length; i++) {
                        available_Time_fixed[i] = available_Time[i + nowintHour + 1];
                    }
                    start_Spinner.setAdapter(null);
                    ArrayAdapter adtRegionTemp = new ArrayAdapter<String>(getApplicationContext(), R.layout.items_spinner_dropdown, available_Time_fixed);
                    //adtRegionTemp.setDropDownViewResource(R.layout.items_spinner_dropdown);
                    start_Spinner.setAdapter(adtRegionTemp);
                    start_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv = (TextView)parent.getChildAt(0);
                            tv.setTextColor(Color.BLACK);
                            searchingTime.startTime = position+nowintHour+1;

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                searchingTime.searchDate=date.getDate();

            }
        });

        start_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)parent.getChildAt(0);
                tv.setTextColor(Color.BLACK);
                searchingTime.startTime = position+nowintHour+1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        finish_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usingTime = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(people.length()>0) {
                    Intent startIntent = new Intent(getApplicationContext(), MainReservationActivity_step1.class);
                    int searchingPeople = Integer.parseInt(people);//스트링을 인트로 바꿔서 보냄
                    startIntent.putExtra("사용시간", usingTime);
                    startIntent.putExtra("시작시간", searchingTime.startTime);
                    startIntent.putExtra("날짜", searchingTime.searchDate);
                    startIntent.putExtra("인원", searchingPeople);
                    startIntent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(startIntent);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_hold);
                }
                else
                {

                }
            }
        });

        people_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                people = people_EditText.getText().toString();
                checkWrited(nextButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        people_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        nextButton.performClick();
                        hidekeyboard(people_EditText);
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
        startActivity(GoHome);
    }
    void checkWrited(Button btn)
    {
        if(people.length()>0)
            btn.setEnabled(true);//초기설정은 비활성화 상태
        else
            btn.setEnabled(false);//초기설정은 비활성화 상태
    }
}
