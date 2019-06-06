/*
 * 알람 세팅의 스위치버튼을 누를 시 발동되는 이벤트를 리스트뷰에 adapt시켜주는 adapter이다
 * 현재 스위치 누른 상태가 저장이 안되는 버그가 있다. -> 2019/5/22 해결
 *
 *
 *
 *
 *
 */
package com.example.myroom.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.myroom.Items.AlarmSettingItem;
import com.example.myroom.R;
import com.example.myroom.SharedPreferences.SettingSave;

import java.util.ArrayList;

public class AlarmSettingAdapter extends BaseAdapter
{
    LayoutInflater inflater = null;
    private ArrayList<AlarmSettingItem> m_oData = null;
    private int nListCnt = 0;
    private Context ct;

    public AlarmSettingAdapter(ArrayList<AlarmSettingItem> _oData, Context context)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
        ct = context;
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
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.items_alarm_setting_catecory, parent, false);
        }

        Switch oSwitchTitle = (Switch) convertView.findViewById(R.id.alarm_Setting_Switch);
        m_oData.get(position).listSwitch=oSwitchTitle;//아이템의 스위치에 대입
        oSwitchTitle.setText(m_oData.get(position).textStr);

        oSwitchTitle.setChecked(SettingSave.getSettings(ct,m_oData.get(position).textStr));//초기값설정
        //알람 목록이 눌러졌을경우
        oSwitchTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingSave.clearCurrentSettings(ct,m_oData.get(position).textStr);
                SettingSave.setSettings(ct,m_oData.get(position));//상태저장
                if(isChecked)
                {
                    /*
                    if(m_oData.get(position).textStr=="출석전알람")
                    {
                        SettingSave.clearCurrentSettings(ct,m_oData.get(position),position);
                        SettingSave.setSettings(ct,m_oData.get(position),position);//상태저장
                    }
                    else if(m_oData.get(position).textStr=="퇴실전알람")
                    {
                        Intent attendIntent = new Intent(ct,MainReservationActivity.class);
                        ct.startActivity(attendIntent);
                    }//이걸 이제 저장해야함 상태를
                    */
                }
            }
        });
        return convertView;
    }
}