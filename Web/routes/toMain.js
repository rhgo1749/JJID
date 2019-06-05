
var express = require('express');
var mysql = require('mysql');
var app = express();
var toMain = express.Router();
var date_utils = require('date-utils');
var moment = require('moment');

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
var resIndex=[ [0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],
[0],[0],[0],[0],[0],[0],[0],[0],[0],[0],[0] ];

// /main
toMain.get('/', (req, res) => {
  if(req.session.login_member == undefined)
    res.redirect('http://220.127.141.64:3000');
  else
  {
    var warn = req.session.login_member.warn;
    var stu_num = req.session.login_member.stu_num;
    var name = req.session.login_member.name;
    var email = req.session.login_member.email;
    var phone_num = req.session.login_member.phone_num;
    //req.session.login_member.pw , req.session.login_member.warn 정보도 있음

    function addNum(k){
      for(var i=0, day=0; day<8; day++)
      {
        for(var t=10; t<21; t++)
        {
          conn.query('select * from RESERVATION WHERE stu_num=delegator AND room_num=? AND r_date=? AND start_time=?',
          [k, today[day], t], (err, rs) => {
              if(rs.length != 0){
                resIndex[k][i] = 1;
                i++;
              }
              else{
                resIndex[k][i] = 0;
                i++;
              }
          });
        }
      }
    }
    function addNum2(t){
      conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=2',
      [today[0],today[7],t], (err, rs) => {
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
            resIndex[t][(Number(rs[i].START_TIME)-10)+11*(differentDays)+1] = 1;
          }
        }
      });

    }
    function add241(k){
      for(var i=0, day=0; day<8; day++)
      {
        for(var t=0; t<24; t++)
        {
          conn.query('select * from RESERVATION WHERE stu_num=delegator AND room_num=? AND r_date=? AND start_time=?',
          [k, today[day], t], (err, rs) => {
              if(rs.length != 0){
                resIndex[k][i] = 1;
                i++;
              }
              else{
                resIndex[k][i] = 0;
                i++;
              }
          });
        }
      }
    }

    function add242(a){
      conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=2',
      [today[0],today[7],a], (err, rs) => {
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
            resIndex[a][(Number(rs[i].START_TIME))+24*(differentDays)+1] = 1;
          }
        }
      });
    }
    function add243(b){
      conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=3',
      [today[0],today[7],b], (err, rs) => {
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
            resIndex[b][(Number(rs[i].START_TIME))+24*(differentDays)+1] = 1;
            resIndex[b][(Number(rs[i].START_TIME))+24*(differentDays)+2] = 1;
          }
        }
      });
    }
    function add244(c){
      conn.query('select * from RESERVATION WHERE r_date>=? AND r_date<=? AND stu_num=delegator AND room_num=? AND end_time-start_time=4',
      [today[0],today[7],c], (err, rs) => {
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
            resIndex[c][(Number(rs[i].START_TIME))+24*(differentDays)+1] = 1;
            resIndex[c][(Number(rs[i].START_TIME))+24*(differentDays)+2] = 1;
            resIndex[c][(Number(rs[i].START_TIME))+24*(differentDays)+3] = 1;
          }
        }
      });
    }
    var newDate = new Date();

    function realtimeChecker(k) {

      for(i=0; i<10; i++)
      {
        if( i+10 < newDate.getHours() )
        {
          resIndex[k][i] = 1;
        }
      }
    }
    function realtime24Checker(k) {

      for(i=0; i<24; i++)
      {
        if( i < newDate.getHours() )
        {
          resIndex[k][i] = 1;
        }
      }
    }
    function weekendChecker(q) {
    /*getday = 일(0) ~ 토(6)*/

      for(var i=0; i<8; i++)
      {
        if( ( newDate.getDay() + i ) % 7 == 0 )
        {
          /*11시간 범위 테이블*/
          for(var j=0; j<11; j++)
          {
            resIndex[q][i*11 + j] = 1;
          }
        }
      }
    }


    /*주말 예약 못하게 막아버리기*/
    function weekend24Checker(q) {
      /*getday = 일(0) ~ 토(6)*/

      for(var i=0; i<8; i++)
      {
        if( ( newDate.getDay() + i ) % 7 == 0 )
        {
          /*11시간 범위 테이블*/
          for(var j=0; j<24; j++)
          {
            resIndex[q][i*24 + j] = 1;
          }
        }
      }
    }

    /*==========================*/
/*==========================*/
    addNum(1);addNum(2);addNum(3);addNum(4);addNum(5);addNum(6);addNum(7);addNum(8);addNum(9);addNum(10);
    addNum(11);addNum(12);addNum(13);addNum(14);addNum(15);addNum(16);addNum(17);addNum(18);addNum(19);addNum(20);
    addNum(21);addNum(22);addNum(23);addNum(24);

    addNum2(1);addNum2(2);addNum2(3);addNum2(4);addNum2(5);addNum2(6);addNum2(7);
    addNum2(8);addNum2(9);addNum2(10);addNum2(11);addNum2(12);addNum2(13);addNum2(14);
    addNum2(15);addNum2(16);addNum2(17);addNum2(18);addNum2(19);addNum2(20);
    addNum2(21);addNum2(22);addNum2(23);addNum2(24);

    add241(25);add241(26);add241(27);add241(28);add241(29);add241(30);
    add242(25);add242(26);add242(27);add242(28);add242(29);add242(30);
    add243(25);add243(26);add243(27);add243(28);add243(29);add243(30);
    add244(25);add244(26);add244(27);add244(28);add244(29);add244(30);

    realtimeChecker(1);realtimeChecker(2);realtimeChecker(3);realtimeChecker(4);
    realtimeChecker(5);realtimeChecker(6);realtimeChecker(7);realtimeChecker(8);
    realtimeChecker(9);realtimeChecker(10);realtimeChecker(11);realtimeChecker(12);
    realtimeChecker(13);realtimeChecker(14);realtimeChecker(15);realtimeChecker(16);
    realtimeChecker(17);realtimeChecker(18);realtimeChecker(19);realtimeChecker(20);
    realtimeChecker(21);realtimeChecker(22);realtimeChecker(23);realtimeChecker(24);

    weekendChecker(1);weekendChecker(2);weekendChecker(3);weekendChecker(4);
    weekendChecker(5);weekendChecker(6);weekendChecker(7);weekendChecker(8);
    weekendChecker(9);weekendChecker(10);weekendChecker(11);weekendChecker(12);
    weekendChecker(13);weekendChecker(14);weekendChecker(15);weekendChecker(16);
    weekendChecker(17);weekendChecker(18);weekendChecker(19);weekendChecker(20);
    weekendChecker(21);weekendChecker(22);weekendChecker(23);weekendChecker(24);

    realtime24Checker(25);realtime24Checker(26);realtime24Checker(27);
    realtime24Checker(28);realtime24Checker(29);realtime24Checker(30);

    weekend24Checker(25);weekend24Checker(26);weekend24Checker(27);
    weekend24Checker(28);weekend24Checker(29);weekend24Checker(30);



  setTimeout(function(){
    //로그인직후?

    conn.query('select a.stu_num, a.room_num, a.r_date, a.start_time, a.end_time, a.delegator, b.name from RESERVATION a, USER_DB b WHERE a.DELE_CHECK=0 AND a.stu_num=b.stu_num AND a.stu_num=a.delegator AND a.stu_num=? AND  a.r_date>=? order by a.r_date asc, a.start_time asc',
    [stu_num, today[0]], (err, rs) => {
      if(rs.length == 0){
      //없음 = 그냥 지나가자
      req.session.length = rs.length;
      var length = rs.length;

      res.render('main',{
        stu_num:stu_num ,
        name:name,
        phone_num:phone_num,
        email:email,
        length:length,
        warn:warn,
        room_num0: "00", r_date0:"YYYY-MM-DD", r_time0:0, r_name0:"일멋엉", r_using_time0:0,
        room_num1: "00", r_date1:"YYYY-MM-DD", r_time1:0, r_name1:"일멋엉", r_using_time1:0,
        room_num2: "00", r_date2:"YYYY-MM-DD", r_time2:0, r_name2:"일멋엉", r_using_time2:0,
        room_num3: "00", r_date3:"YYYY-MM-DD", r_time3:0, r_name3:"일멋엉", r_using_time3:0,
        room_num4: "00", r_date4:"YYYY-MM-DD", r_time4:0, r_name4:"일멋엉", r_using_time4:0,
        room_num5: "00", r_date5:"YYYY-MM-DD", r_time5:0, r_name5:"일멋엉", r_using_time5:0,
        room_num6: "00", r_date6:"YYYY-MM-DD", r_time6:0, r_name6:"일멋엉", r_using_time6:0,
        room_num7: "00", r_date7:"YYYY-MM-DD", r_time7:0, r_name7:"일멋엉", r_using_time7:0,
        resIndex1:resIndex[1], resIndex12:resIndex[12],
        resIndex2:resIndex[2], resIndex13:resIndex[13],
        resIndex3:resIndex[3], resIndex14:resIndex[14],
        resIndex4:resIndex[4], resIndex15:resIndex[15],
        resIndex5:resIndex[5], resIndex16:resIndex[16],
        resIndex6:resIndex[6], resIndex17:resIndex[17],
        resIndex7:resIndex[7], resIndex18:resIndex[18],
        resIndex8:resIndex[8], resIndex19:resIndex[19],
        resIndex9:resIndex[9], resIndex20:resIndex[20],
        resIndex10:resIndex[10], resIndex21:resIndex[21],
        resIndex11:resIndex[11], resIndex22:resIndex[22],
        resIndex23:resIndex[23], resIndex24:resIndex[24],
        resIndex25:resIndex[25], resIndex29:resIndex[29],
        resIndex26:resIndex[26], resIndex30:resIndex[30],
        resIndex27:resIndex[27], resIndex28:resIndex[28]
      });
    }
      else {
              //있음
              req.session.length = rs.length;
              var length = rs.length;

              function struct(){
                var room_num;
                var r_date;
                var r_time;
                var r_name;
                var r_using_time;
              }
              var structArr = new Array(8);
              var dt = rs[0].r_date;
              var d = dt.toFormat('YYYY-MM-DD');
              req.session.studyroom = {room_num:rs[0].room_num, r_date:d, r_time:rs[0].start_time, r_name:rs[0].name,r_using_time:rs[0].end_time};

              for(var i=0;i<8;i++){
                structArr[i] = new struct();
                structArr[i].room_num = req.session.studyroom.room_num;
                structArr[i].r_date = req.session.studyroom.r_date;
                structArr[i].r_time = req.session.studyroom.r_time;
                structArr[i].r_name = req.session.studyroom.r_name;
                structArr[i].r_using_time = req.session.studyroom.r_using_time;
              }
              for(var i=0;i<length;i++){
                var dt = rs[i].r_date;
                var d = dt.toFormat('YYYY-MM-DD');
                req.session.studyroom = {room_num:rs[i].room_num, r_date:d, r_time:rs[i].start_time, r_name:rs[i].name,r_using_time:rs[i].end_time};

                structArr[i].room_num = req.session.studyroom.room_num;
                structArr[i].r_date = req.session.studyroom.r_date;
                structArr[i].r_time = req.session.studyroom.r_time;
                structArr[i].r_name = req.session.studyroom.r_name;
                structArr[i].r_using_time = req.session.studyroom.r_using_time;
              }

              res.render('main',{
                stu_num:stu_num ,
                name:name,
                phone_num:phone_num,
                email:email,
                length:length,
                warn:warn,
                room_num0: structArr[0].room_num, r_date0:structArr[0].r_date, r_time0:structArr[0].r_time, r_name0:structArr[0].r_name, r_using_time0:structArr[0].r_using_time,
                room_num1: structArr[1].room_num, r_date1:structArr[1].r_date, r_time1:structArr[1].r_time, r_name1:structArr[1].r_name, r_using_time1:structArr[1].r_using_time,
                room_num2: structArr[2].room_num, r_date2:structArr[2].r_date, r_time2:structArr[2].r_time, r_name2:structArr[2].r_name, r_using_time2:structArr[2].r_using_time,
                room_num3: structArr[3].room_num, r_date3:structArr[3].r_date, r_time3:structArr[3].r_time, r_name3:structArr[3].r_name, r_using_time3:structArr[3].r_using_time,
                room_num4: structArr[4].room_num, r_date4:structArr[4].r_date, r_time4:structArr[4].r_time, r_name4:structArr[4].r_name, r_using_time4:structArr[4].r_using_time,
                room_num5: structArr[5].room_num, r_date5:structArr[5].r_date, r_time5:structArr[5].r_time, r_name5:structArr[5].r_name, r_using_time5:structArr[5].r_using_time,
                room_num6: structArr[6].room_num, r_date6:structArr[6].r_date, r_time6:structArr[6].r_time, r_name6:structArr[6].r_name, r_using_time6:structArr[6].r_using_time,
                room_num7: structArr[7].room_num, r_date7:structArr[7].r_date, r_time7:structArr[7].r_time, r_name7:structArr[7].r_name, r_using_time7:structArr[7].r_using_time,
                resIndex1:resIndex[1], resIndex12:resIndex[12],
                resIndex2:resIndex[2], resIndex13:resIndex[13],
                resIndex3:resIndex[3], resIndex14:resIndex[14],
                resIndex4:resIndex[4], resIndex15:resIndex[15],
                resIndex5:resIndex[5], resIndex16:resIndex[16],
                resIndex6:resIndex[6], resIndex17:resIndex[17],
                resIndex7:resIndex[7], resIndex18:resIndex[18],
                resIndex8:resIndex[8], resIndex19:resIndex[19],
                resIndex9:resIndex[9], resIndex20:resIndex[20],
                resIndex10:resIndex[10], resIndex21:resIndex[21],
                resIndex11:resIndex[11], resIndex22:resIndex[22],
                resIndex23:resIndex[23], resIndex24:resIndex[24],
                resIndex25:resIndex[25], resIndex29:resIndex[29],
                resIndex26:resIndex[26], resIndex30:resIndex[30],
                resIndex27:resIndex[27], resIndex28:resIndex[28]
              });
      }
    });
},150)

  }
});

toMain.post('/myInfo', (req, res) => {
       var stu_num = req.session.login_member.stu_num;
       var password = req.session.login_member.password;
       var name = req.body.name;
       var email = req.body.email;
       var phone_num = req.body.phone_num;
       var newpassword = req.body.newpassword;
       var newpasswordconfirm = req.body.newpasswordconfirm;
       var passwordconfirm = req.body.passwordconfirm;

       conn.query('SELECT password FROM USER_DB WHERE stu_num=?',
               [stu_num], (err, rs) => {
        if(email!="" && phone_num!="")
        {
          if(rs[0].password == passwordconfirm)
          { //현재비밀번호
           if(newpassword == newpasswordconfirm)
           { //바꿀 비밀번호
             if(newpassword!="" && newpasswordconfirm!="")
             { }
             else
             {
               newpassword = rs[0].password;
               newpasswordconfirm = rs[0].password;
             }
             conn.query('UPDATE USER_DB SET password=?, email=?, phone_num=? WHERE stu_num=?',
             [newpassword, email, phone_num, stu_num], (err, rs) => {
               if(err) {
                 console.log(err);
                 console.log('DB 조건 불만족!');
                 //res.redirect('/myInfo');
               }else {
                 console.log('변경 완료!');
                 //res.send("<script> self.resizeTo(800,600); alert('변경되었습니다.'); window.close();</script>");
                 conn.query('SELECT stu_num, password, name, email, phone_num, warn FROM USER_DB WHERE stu_num=?',
                         [stu_num], (err, rs) => {
                                 // session에 저장
                                 req.session.login_member = {stu_num:rs[0].stu_num, password:rs[0].password, name:rs[0].name, phone_num:rs[0].phone_num, email:rs[0].email, warn:rs[0].warn};
                                 res.redirect('/main');
                         });
               }
             });
           }
           else
           {
             console.log('새 비밀번호 - 새 비밀번호확인 불일치');
             //res.redirect("/myInfo");
           }
         }
         else
         {
           console.log('현재비밀번호 불일치');
           //res.redirect("/myInfo");
         }
       }
       else
       {
         console.log('공란있음');
         //res.redirect("/myInfo");
       }
     });
});

toMain.post('/cancel', (req, res) => {
  var id = req.body.resindex;
  var stu_num = req.session.login_member.stu_num;
  var r_date;
  //var r_date = structArr[id].r_date;
  conn.query('select stu_num, R_DATE, start_time, DELEGATOR from RESERVATION WHERE DELE_CHECK=0 AND stu_num=DELEGATOR AND stu_num=? AND R_DATE>=? order by R_DATE asc, start_time asc', [stu_num, today[0]], (err, rs) => {
    r_date = rs[id].R_DATE;
    conn.query('delete from RESERVATION WHERE stu_num=? AND r_date=? AND delegator=? AND R_DATE>=?',
    [stu_num, r_date, stu_num, today[0]], (err, rs) => {
      if(err)
        console.log('db 삭제 에러');
    });
  });

  res.redirect("/main#myreservation");
});

module.exports = toMain;
