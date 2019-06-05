
var express = require('express');
var mysql = require('mysql');
var app = express();
var toAdmin = express.Router();

//db접근을 위한 정보
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
// '/admin'으로 direct됐을때의 동작
toAdmin.get('/', (req, res) => {
  res.render('admin');
});
// '/admin'에서 '/roomManage'로 제출동작이 일어났을때
toAdmin.post('/roomManage', (req, res) => {
  var r_date = req.body.roomdate;
  var st = req.body.usingtime.substring(0,2)
  var start_time = st;
  var room_num = req.body.roomid;

  conn.query('select * from RESERVATION WHERE r_date=? AND start_time=? AND room_num=?',
  [r_date, start_time, room_num], (err, rs) => {
    if(rs.length == 0){
          console.log('예약 없음');
    }
    else {
      conn.query('delete from RESERVATION WHERE r_date=? AND start_time=? AND room_num=?',
      [r_date, start_time, room_num], (err, rs) => {
        if(err)
          console.log('db reservation 삭제 에러');
        else
          console.log('db reservation 삭제 완료');
      });
    }
  });

});



toAdmin.post('/userManage', (req, res) => {
      var stu_num = req.body.userid;
      var value = req.body.buttonindex;
      conn.query('select * from USER_DB WHERE stu_num=?',
      [stu_num], (err, rs) => {
        if(rs.length == 0){
            console.log("그런사람 없습니다.");
        }
        else {
          if(value == "3"){
            conn.query('delete from USER_DB WHERE stu_num=?',
            [stu_num], (err, rs) => {
              if(err)
                console.log('db user 삭제 에러');
              else
                console.log('db user 삭제 완료');
            });
          }
          else if(value == "1"){
            conn.query('update USER_DB set warn = 1 WHERE stu_num=?',
            [stu_num], (err, rs) => {
              if(err)
                console.log('경고부여 에러');
              else
                console.log(stu_num+' 경고부여 완료');
            });
          }
          else if(value == "2"){
            conn.query('update USER_DB set warn = 0 WHERE stu_num=?',
            [stu_num], (err, rs) => {
              if(err)
                console.log('경고부여 삭제 에러');
              else
                console.log(stu_num+' 경고삭제 완료');
            });
          }
        }
      });
});

module.exports = toAdmin;
