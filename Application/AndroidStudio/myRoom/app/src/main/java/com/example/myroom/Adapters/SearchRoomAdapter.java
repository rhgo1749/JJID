/*
 * 방정보를 리스트뷰로 뿌릴때 쓰는 adapter이다.
 *
 *
 *
 *
 */
package com.example.myroom.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myroom.R;
import com.example.myroom.Items.RoomInfo;

import java.util.List;
/**
 * Roominfo 클래스 어레이 리스트를 리스트뷰에 연결
 */

public class SearchRoomAdapter extends BaseAdapter {

    private Context context;
    private List<RoomInfo> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public SearchRoomAdapter(List<RoomInfo> list, Context context){
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.row_listview,null);

            viewHolder = new ViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(list.get(position).searchDate.toString()+" "+list.get(position).startTime+"시");

        return convertView;
    }

    class ViewHolder{
        public TextView label;
    }

}