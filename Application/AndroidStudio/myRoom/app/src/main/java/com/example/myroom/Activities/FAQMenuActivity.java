/*
 * 설정메뉴다 로그아웃과 알림중 하나를 선택해서 들어갈 수 있다.
 *
 *
 *
 *
 */
package com.example.myroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myroom.R;

public class FAQMenuActivity extends BaseActivity {
    private ListView m_oListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings_faq); //activity_main_home뷰를 참조

        // 설정메뉴
        String[] faqMenuItem = {"출석체크는 어떻게하나요?", "스터디룸예약은 어떻게하나요?","출석체크 시간은 언제까지인가요?","출석체크를 못하면 어떻게되나요?","예약 취소를 하고싶은데 어떻게하나요?"
        ,"결석에 대해 이의 제기는 어떻게하나요?"};

        // ListView, Adapter 생성 및 연결 ------------------------
        m_oListView = (ListView)findViewById(R.id.faq_ListView);
        ArrayAdapter oAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,faqMenuItem );
        m_oListView.setAdapter(oAdapter);
        final Intent faqMenuSet = new Intent(getApplicationContext(), FAQDetailActivity.class);
        m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // adapter.getItem(position)의 return 값은 Object 형
                // 실제 Item의 자료형은 CustomDTO 형이기 때문에
                // 형변환을 시켜야 getResId() 메소드를 호출할 수 있습니다.
                // new Intent(현재 Activity의 Context, 시작할 Activity 클래스)
                if(position==0){
                    faqMenuSet.putExtra("질문Title", "질문 : 출석체크는 어떻게하나요?");
                    faqMenuSet.putExtra("질문Answer", "답변 : 대표자를 포함해서 예약하신 스터디룸의 최소 수용인원에 해당하는 사람이 전부 해당 스터디룸 위치까지 이동하신후 본 앱을 통해 출석체크를 진행하시면됩니다.");
                }
                else if(position==1){
                    faqMenuSet.putExtra("질문Title", "질문 : 스터디룸예약은 어떻게하나요?");
                    faqMenuSet.putExtra("질문Answer", "답변 : 학술정보원 홈페이지를 통해 하시거나 본앱의 예약하기 버튼을 누르신뒤 원하는 날짜, 시간, 기간을 선택하신뒤 검색된 스터디룸중 원하는 스터디룸을 고르신뒤 예약을 진행" +
                            "하시면됩니다.");
                }
                else if(position==2) {
                    // putExtra(key, value)
                    faqMenuSet.putExtra("질문Title", "질문 : 출석체크 시간은 언제까지인가요?");
                    faqMenuSet.putExtra("질문Answer", "답변 : 출석체크 시간은 예약시간 10분내외로 진행됩니다.");
                }
                else if(position==3) {
                    faqMenuSet.putExtra("질문Title", "질문 : 출석체크를 못하면 어떻게되나요?");
                    faqMenuSet.putExtra("질문Answer", "답변 : 출결을 못하면 경고없이 학술정보원 스터디룸 예약서비스가 7일정지됩니다.");
                }
                else if(position==4){
                    faqMenuSet.putExtra("질문Title", "질문 : 예약 취소를 하고싶은데 어떻게하나요?");
                    faqMenuSet.putExtra("질문Answer", "답변 : 예약 취소는 예약 시간 30분전까지만 가능하며 홈페이지나 앱의 내 예약현황에서 취소하고자하는 현황을 누르신 뒤 진행하시면됩니다.");
                }
                else if(position==5){
                    faqMenuSet.putExtra("질문Title", "질문 : 결석에 대해 이의 제기는 어떻게하나요?");
                    faqMenuSet.putExtra("질문Answer", "답변 : 내 예약 현황에서 출석체크가 완료되지 않아 결석처리된 항목을 누르신 뒤 하단의 이의제기 버튼을 누르면 관리자에게 이의제기 메일을 보내 실" +
                            "수 있습니다.");
                }
                else {
                    faqMenuSet.putExtra("질문Title", "질문은 그만!");
                    faqMenuSet.putExtra("질문Answer", "왈도");
                }
                faqMenuSet.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(faqMenuSet);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_hold);

            }
        });
    }

}