
var express = require('express');
var mysql = require('mysql');
var nodemailer = require('nodemailer');
var app = express();
var toLogin = express.Router();



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
/*
var conn = mysql.createConnection({
    host:'222.109.23.17',
    user:'musung',    // dbUser
    password :'whfwkr3',   // dbPassword
    database : 'JJDB'  // dbDatabase
});*/

//로그인 폼
toLogin.get('/', (req, res) => {
  // 로그인 되어 있지 않다면 ...?
  if(!req.session.login_member){
      res.render('login',{pass : true});
  }else {
  // 로그인 되어 있다면 ...?
      res.redirect('/main');
  }
});
//로그인 액션
toLogin.post('/', (req, res) => {
       var stu_num = req.body.stu_num;
       var password = req.body.password;
       var flag = 0;
       if(stu_num == "admin" && password == "1234"){
         flag = 1;
         res.redirect('/admin');
       }
       if(flag == 0){
       conn.query('SELECT stu_num, password, name, email, phone_num, warn FROM USER_DB WHERE stu_num=? AND password=?',
               [stu_num, password], (err, rs) => {
                   if(rs.length != 1) {
                       console.log('로그인 실패~');
                       res.render('login',{pass : false});
                   }else {
                       // session에 저장
                       console.log('로그인 성공!');
                       req.session.login_member = {stu_num:rs[0].stu_num, password:rs[0].password, name:rs[0].name, phone_num:rs[0].phone_num, email:rs[0].email, warn:rs[0].warn};
                       console.log('로그인 ID : ' + req.session.login_member.stu_num);

                       res.redirect('/main');
                   }
               });
      }
   });
//로그아웃
toLogin.get('/logout', (req, res) => {
    req.session.destroy((err) => {
        res.redirect('/');
    });
});

//가입
toLogin.post('/join', (req, res) => {
        var stu_num = req.body.stu_num;
        var name = req.body.name;
        var email = req.body.email;
        var phone_num = req.body.phone_num;
        var password = req.body.password;
        var passwordconfirm = req.body.passwordconfirm;
        var warn = 0;

       if((password == passwordconfirm))
       {
           conn.query('SELECT stu_num FROM USER_DB WHERE stu_num=? ',
                   [stu_num], (err, rs) => {
                       if(!rs[0]) { //동일한 아이디가 없다
                           console.log('아이디 중복 없음~');
                           conn.query('INSERT INTO USER_DB(stu_num, password, email, phone_num, name, warn) VALUES(?,?,?,?,?,?)'
                              ,[stu_num, password, email, phone_num, name, warn], (err, rs) => {
                          if(err) {
                              console.log(err)
                              console.log('DB 조건 불만족!');
                              //res.redirect('/join');
                              //res.send("<script> alert('DB 조건 불만족으로 인한 실패'); window.close();</script>");
                          }else {
                              console.log('가입 완료!');
                              conn.query('SELECT stu_num, password, email, phone_num, name, warn FROM USER_DB WHERE stu_num=?',
                                      [stu_num], (err, rs) => {
                                              // session에 저장
                                              console.log('로그인 성공!');
                                              req.session.login_member = {stu_num:rs[0].stu_num, password:rs[0].password, name:rs[0].name, phone_num:rs[0].phone_num, email:rs[0].email, warn:rs[0].warn};
                                              console.log('로그인 ID : ' + req.session.login_member.stu_num);
                                              res.redirect('/main');
                                      });
                          }
                       });
                     }
                     else {
                         //동일한 아이디가 있다
                           console.log('아이디 중복!');
                           //res.send("<script> alert('아이디 중복으로 인한 실패'); window.close();</script>");
                       }
                     });
        }
        else
        {
          console.log('비밀번호불일치로 인한 실패~');
          //res.redirect('/join');
          //res.send("<script> alert('비밀번호불일치로 인한 실패'); window.close();</script>");
        }
      });


toLogin.post('/find', (req, res) => {
  var stu_num = req.body.stu_num;
  var name = req.body.name;
  var email = req.body.email;
  var phone_num = req.body.phone_num;

  conn.query('SELECT stu_num, password, email, phone_num, name FROM USER_DB WHERE stu_num=? AND name=? AND email=? AND phone_num=? ',
  [stu_num, name, email, phone_num], (err, rs) => {
    if(!rs[0]) { //동일한 아이디가 없다
      console.log('없음~');
    }else {
      console.log('완료!');
      var fd_email = rs[0].email;
      var fd_pw = rs[0].password;
      var message = {
         from : 'studyroom011@gmail.com',
         to : fd_email,
         subject : '비밀번호 알림 메일입니다.',
         text : 'password: '+ fd_pw +' 추가 문의사항은 메일로 보내주세요.'
       };

       var smtpConfig = {
         host : 'smtp.gmail.com',
         port : 465,
         secure : true, //SSL
         auth : {
                 user : 'studyroom011@gmail.com',
                 pass : 'study1234!'
         }
       };
       var transporter = nodemailer.createTransport(smtpConfig);
       transporter.sendMail(message, function(err, response){
         console.log(err || response);
       });
      res.redirect('/');
    }
  });
});


module.exports = toLogin;
