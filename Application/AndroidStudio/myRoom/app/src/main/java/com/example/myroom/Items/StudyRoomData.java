package com.example.myroom.Items;

import java.sql.Date;

public class StudyRoomData {
    public String name; // 스터디룸 이름 및 층
    public int min; // 최소 수용인원
    public int full; // 최대 수용인원
    public int opentime; // 스터디룸 개방시간
    public int closetime; // 스터디룸 폐쇄시간
    public int maxtime; // 스터디룸 최대 예약시간
    public int todayTF; // 당일예약 여부, 1 = 맞음
    public int beaconNum; // 비콘번호

   public StudyRoomData(String d_name, int d_min, int d_full, int d_opentime, int d_closetime, int d_maxtime,  int d_todayTF, int d_beaconNum) {
       this.name = d_name;
       this.min = d_min;
       this.full = d_full;
       this.opentime = d_opentime;
       this.closetime = d_closetime;
       this.maxtime = d_maxtime;
       this.todayTF = d_todayTF;
       this.beaconNum = d_beaconNum;
    }

    // get method
    public String getName() { return name; }
    public int getMin() { return min; }
    public int getFull() { return full; }
    public int getOpentime() { return opentime; }
    public int getClosetime() { return closetime; }
    public int getMaxtime() { return maxtime; }
    public int getTodayTF() { return todayTF; }
    public int getBeaconNum() { return beaconNum; }

    // set method
    public void setName(String d_name) { this.name = d_name; };
   public void setMin(int d_min) { this.min = d_min;};
    public void setFull(int d_full) { this.full = d_full; };
    public void setOpentime(int d_opentime) { this.opentime= d_opentime; };
    public void setClosetime(int d_closetime) { this.closetime = d_closetime; };
    public void setMaxtime(int d_maxtime) { this.maxtime = d_maxtime; };
    public void setTodayTF(int d_todayTF) { this.todayTF = d_todayTF; };
    public void setBeaconnum(int d_beaconNum) { this.beaconNum = d_beaconNum; }

}
