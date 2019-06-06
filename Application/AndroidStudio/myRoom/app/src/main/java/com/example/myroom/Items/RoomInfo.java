/*
 * DB대신 방 정보를 임시로 담아놓는 클래스이다.
 *
 *
 *
 *
 */
package com.example.myroom.Items;
import java.util.Date;

public class RoomInfo
{
    public String roomName = "4층 14호";
    public int roomNo = 0;
    public Date searchDate; //sql 날짜 정보
    public int startTime = 0;
    public int endTime = 0;


    public RoomInfo() {
    }
    public RoomInfo(int roomNo, Date searchDate, int startTime, int endTime)
    {
        this.roomNo = roomNo;
        this.searchDate = searchDate;
        this.startTime = startTime;
        this.endTime = endTime;

    } // 생성자
    public RoomInfo setRoomInfo()
    {
        return this;
    }

}
