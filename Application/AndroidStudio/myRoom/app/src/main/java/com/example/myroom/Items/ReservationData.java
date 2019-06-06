package com.example.myroom.Items;

import java.util.Date;

public class ReservationData {

   //  ReservationData(){}
   public ReservationData(){}

    public ReservationData(String d_stu_num, int d_room_num, Date d_r_date, int d_start_time, int d_end_time, String d_delegator, int d_at_check){
        stu_num = d_stu_num;
        room_num =d_room_num;
        r_date = d_r_date;
        start_time = d_start_time;
        end_time = d_end_time;
        delegator = d_delegator;
        at_check = d_at_check;
    }

    public String stu_num;
    public int room_num;
    public Date r_date;
    public int start_time;
    public int end_time;
    public String delegator;
    public int at_check; // 0:출첵 아직 안함, 1: 출첵 함, 2: 퇴실 체크 함

    // get method
    public String getStu_num() { return stu_num; }
    public int getRoom_num() { return room_num; }
    public Date getR_date() { return r_date; }
    public int getStart_time() { return start_time; }
    public int getEnd_time() { return end_time; }
    public String getDelegator() { return delegator; }
    public int getAt_check() { return at_check; }

    // set method
    public void setStu_num(String d_stu_num) { this.stu_num = d_stu_num; };
    public void setRoom_num(int d_room_num) { this.room_num = d_room_num; };
    public void setR_date(Date d_r_date) { this.r_date = d_r_date; };
    public void setStart_time(int d_start_time) { this.start_time = d_start_time; };
    public void setEnd_time(int d_end_time) { this.end_time = d_end_time; };
    public void setDelegator(String d_delegator) {  this.delegator = d_delegator; };
    public void setAt_check(int d_at_check) { this.at_check = d_at_check; };

}
