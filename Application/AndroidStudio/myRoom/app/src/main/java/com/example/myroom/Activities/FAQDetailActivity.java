package com.example.myroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myroom.R;

public class FAQDetailActivity extends BaseActivity {
    Intent faqIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqgag);
        faqIntent = getIntent();
        TextView faqTitle = (TextView) findViewById(R.id.faq_TextTitle) ;
        TextView faqText = (TextView) findViewById(R.id.faq_TextDetail);
        faqTitle.setText(faqIntent.getStringExtra("질문Title"));
        faqText.setText(faqIntent.getStringExtra("질문Answer"));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_hold);
    }
}
