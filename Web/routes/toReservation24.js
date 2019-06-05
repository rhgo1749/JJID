var express = require('express');
var mysql = require('mysql');
var app = express();
var toReservation24 = express.Router();
var async = require('async');
var moment = require('moment');
var date_utils = require('date-utils');
var today = [moment().format("YYYY-MM-DD"),
moment().add(1,"days").format("YYYY-MM-DD"),
moment().add(2,"days").format("YYYY-MM-DD"),
moment().add(3,"days").format("YYYY-MM-DD"),
moment().add(4,"days").format("YYYY-MM-DD"),
moment().add(5,"days").format("YYYY-MM-DD"),
moment().add(6,"days").format("YYYY-MM-DD"),
moment().add(7,"days").format("YYYY-MM-DD")];


var conn = mysql.createConnection({
    host:'localhost',
    user:'root',    // dbUser
    password :'tjrgml',   // dbPassword
    database : 'studyroom'  // dbDatabase
});
/*
var conn = mysql.createConnection({
    host:'studyroomdb.chnbo1cxocqb.ap-northeast-2.rds.amazonaws.com',
    user:'studyroomdb',    // dbUser
    password :'studyroomdb',   // dbPassword
    database : 'studyroomdb',  // dbDatabase
    port: 3306
});*/
var roomName;
var totalReserveArray = [];
// /main
toReservation24.get('/', (req, res) => {
  if(req.session.login_member == undefined)
    res.redirect('http://121.132.130.111:3000');
  else {
        var stu_num = req.session.login_member.stu_num;
        var name = req.session.login_member.name;
        var email = req.session.login_member.email;
        var phone_num = req.session.login_member.phone_num;
        req.session.queryId = req.query.id;
        roomName = req.session.queryId;

        var roomnum =req.query.id;
        conn.query('select full, min from STUDYROOM_DB WHERE room_num=?',
        [roomnum], (err, r) => {
          req.session.full = {min:r[0].min, max:r[0].full};
        });

        for(var i=0, day=0; day<8; day++)
        {
          for(var t=0; t<24; t++)
          {
            conn.query('select * from RESERVATION WHERE stu_num=delegator AND room_num=? AND r_date=? AND start_time=?',
            [req.query.id, today[day], t], (err, rs) => {
                if(rs.length != 0){
                  totalReserveArray[i] = 1;
                  i++;
                }
                else{
                  totalReserveArray[i] = 0;
                  i++;
                }
            });
          }
        }


        conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=2',
        [today[0],today[7],req.query.id], (err, rs) => {
          if(rs.length == 0){
          }
          else {
            for(var i=0; i<rs.length; i++)
            {
              let dt = rs[i].R_DATE;
              let d = dt.toFormat('YYYY-MM-DD');
              let firstDate = new Date(d);
              let secondDate = new Date(today[0]);
              timeDifference = firstDate.getTime() - secondDate.getTime();
              let differentDays = Math.ceil(timeDifference / (1000 * 3600 * 24));
              totalReserveArray[(Number(rs[i].START_TIME))+24*(differentDays)+1] = 1;
            }
          }
        });

        conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=3',
        [today[0],today[7],req.query.id], (err, rs) => {
          if(rs.length == 0){
          }
          else {
            for(var i=0; i<rs.length; i++)
            {
              let dt = rs[i].R_DATE;
              let d = dt.toFormat('YYYY-MM-DD');
              let firstDate = new Date(d);
              let secondDate = new Date(today[0]);
              timeDifference = firstDate.getTime() - secondDate.getTime();
              let differentDays = Math.ceil(timeDifference / (1000 * 3600 * 24));
              totalReserveArray[(Number(rs[i].START_TIME))+24*(differentDays)+1] = 1;
              totalReserveArray[(Number(rs[i].START_TIME))+24*(differentDays)+2] = 1;
            }
          }
        });

        conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=4',
        [today[0],today[7],req.query.id], (err, rs) => {
          if(rs.length == 0){
          }
          else {
            for(var i=0; i<rs.length; i++)
            {
              let dt = rs[i].R_DATE;
              let d = dt.toFormat('YYYY-MM-DD');
              let firstDate = new Date(d);
              let secondDate = new Date(today[0]);
              timeDifference = firstDate.getTime() - secondDate.getTime();
              let differentDays = Math.ceil(timeDifference / (1000 * 3600 * 24));
              totalReserveArray[(Number(rs[i].START_TIME))+24*(differentDays)+1] = 1;
              totalReserveArray[(Number(rs[i].START_TIME))+24*(differentDays)+2] = 1;
              totalReserveArray[(Number(rs[i].START_TIME))+24*(differentDays)+3] = 1;
            }
          }
        });


      setTimeout(function(){
        var min = req.session.full.min;
        var max = req.session.full.max;
        var newDate = new Date();
        for(i=0; i<24; i++)
        {
          if( i < newDate.getHours() )
          {
            totalReserveArray[i] = 1;
          }
        }
        for(var i=0; i<8; i++)
        {
          if( ( newDate.getDay() + i ) % 7 == 0 )
          {
            /*11시간 범위 테이블*/
            for(var j=0; j<24; j++)
            {
              totalReserveArray[i*24 + j] = 1;
            }
          }
        }

        res.render('reservation24',{min:min, max:max, totalReserveArray:totalReserveArray, roomName:roomName, stu_num:stu_num , name:name, phone_num:phone_num, email:email});
      },700)

  }
});


toReservation24.post('/reserve', (req, res) => {
  if(req.session.login_member == undefined)
    res.redirect('http://121.132.130.111:3000');
  else{
      var stu_num = req.session.login_member.stu_num;
      var room_num = roomName;
      var warn = req.session.login_member.warn;
      //var room_num = req.session.queryId;
      var r_date = req.body.date;
      var st = req.body.startTime.substring(0,2)
      var start_time = st;

      var end_time = Number(st) + Number(req.body.usingTime);
      var delegator = req.session.login_member.stu_num;
      var at_check = 0;
      var dele_check = 0;
      var flag = 0;

      if(warn != 0){
        res.send('<script>alert("예약 권한이 없습니다."); self.close(); </script>');
      }
      else{
        conn.query('select * from RESERVATION WHERE stu_num=? AND r_date=?',
        [stu_num, r_date], (err, rs) => {
          if(rs.length != 0){
            flag = 1;
            res.send('<script>alert("해당 날짜에 다른 예약이 이미 존재합니다."); self.close(); </script>');
          }
        });

        setTimeout(function(){
          if(flag == 0){
            conn.query('INSERT INTO RESERVATION VALUES(?, ?, ?, ?, ?, ?, ?, ?);'
            ,[stu_num, room_num, r_date, start_time, end_time, delegator, at_check, dele_check], (err, rs) => {
              if(err){
                console.log('db에러');
              }
              else{
                res.redirect('/reservation24'+'?id='+req.session.queryId);
              }
            });
          }
        },0)
      }
  }




});

module.exports = toReservation24;
