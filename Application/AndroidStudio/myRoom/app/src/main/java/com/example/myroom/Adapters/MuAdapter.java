package com.example.myroom.Adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myroom.Activities.MainHomeActivity;
import com.example.myroom.Items.MuTempData;
import com.example.myroom.R;

import java.util.ArrayList;

public class MuAdapter extends BaseAdapter // 리스트 사용 위한 클래스
{
    private Context context;
    LayoutInflater inflater = null;
    private ArrayList<MuTempData> m_oData = null;
    private int nListCnt = 0;

    public MuAdapter(Context context, ArrayList<MuTempData> _oData)
    {
        this.context = context;
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.items_past_reservations, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textTitle);
        TextView oTextDate = (TextView) convertView.findViewById(R.id.textDate);
        ImageView circlecolor = (ImageView) convertView.findViewById(R.id.delegatorCircle);
        TextView leftNum = (TextView) convertView.findViewById(R.id.circleLeftNum);
        TextView rightNum = (TextView) convertView.findViewById(R.id.circleRightNum);
        TextView circleSlash = (TextView) convertView.findViewById(R.id.circleSlash);


        leftNum.setText(m_oData.get(position).detail_at_check+"");
        oTextTitle.setText(m_oData.get(position).strDate);
        oTextDate.setText(m_oData.get(position).strName);
        //두글자인 경우 사이즈 조절
        leftNum.setText(m_oData.get(position).detail_at_check+"");
        rightNum.setText(MainHomeActivity.studyRoomArr[m_oData.get(position).detail_room_num-1].getMin()+"");
        oTextTitle.setText(m_oData.get(position).strDate);
        oTextDate.setText(m_oData.get(position).strName);
        //두글자인 경우 사이즈 조절
        if(m_oData.get(position).detail_at_check>9)
        {
            circleSlash.setTextSize(30);
            leftNum.setTextSize(20);
            ConstraintLayout.LayoutParams mLayoutParams = (ConstraintLayout.LayoutParams) leftNum.getLayoutParams();
            mLayoutParams.topMargin = 40;
            mLayoutParams.leftMargin = 40;
            leftNum.setLayoutParams(mLayoutParams);
        }
        else
        {
            leftNum.setTextSize(28);
        }
        if(MainHomeActivity.studyRoomArr[m_oData.get(position).detail_room_num-1].getMin()>9)
        {
            circleSlash.setTextSize(30);
            rightNum.setTextSize(20);
            ConstraintLayout.LayoutParams mLayoutParams = (ConstraintLayout.LayoutParams) rightNum.getLayoutParams();
            mLayoutParams.bottomMargin = 40;
            mLayoutParams.rightMargin = 40;
            rightNum.setLayoutParams(mLayoutParams);
        }
        else {
            rightNum.setTextSize(28);
        }


        if(m_oData.get(position).detail_at_check==0)
        {
            leftNum.setTextColor( ContextCompat.getColor(context, R.color.circleRed));
            rightNum.setTextColor( ContextCompat.getColor(context, R.color.circleRed));
            circleSlash.setTextColor( ContextCompat.getColor(context, R.color.circleRed));
            circlecolor.setImageResource(R.drawable.myreservation_redcircle);
        }
        //여기서 getmin는 최소 수용인원
        else if(m_oData.get(position).detail_at_check< MainHomeActivity.studyRoomArr[m_oData.get(position).detail_room_num-1].getMin())
        {
            leftNum.setTextColor( ContextCompat.getColor(context, R.color.circleYellow));
            rightNum.setTextColor( ContextCompat.getColor(context, R.color.circleYellow));
            circleSlash.setTextColor( ContextCompat.getColor(context, R.color.circleYellow));
            circlecolor.setImageResource(R.drawable.myreservation_yellocircle);
        }
        else
        {
            leftNum.setTextColor(ContextCompat.getColor(context, R.color.circleGreen));
            rightNum.setTextColor( ContextCompat.getColor(context, R.color.circleGreen));
            circleSlash.setTextColor( ContextCompat.getColor(context, R.color.circleGreen));
            circlecolor.setImageResource(R.drawable.myreservation_greencircle);
        }

        convertView.setTag(""+position);
        return convertView;
    }
}
