package com.example.myroom.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myroom.R;
import com.example.myroom.Items.TempData;

import java.util.ArrayList;

public class TempAdapter extends BaseAdapter // 리스트 사용 위한 클래스
{
    LayoutInflater inflater = null;
    private ArrayList<TempData> m_oData = null;
    private int nListCnt = 0;

    public TempAdapter(ArrayList<TempData> _oData)
    {
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

        oTextTitle.setText(m_oData.get(position).strDate);
        oTextDate.setText(m_oData.get(position).strName);

        convertView.setTag(""+position);
        return convertView;
    }
}
