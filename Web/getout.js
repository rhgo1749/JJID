
var http = require('http');
var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');
var ejs = require('ejs');

var app = express();

//모듈가져오기 세션과 쿠키
var cookieParser = require('cookie-parser');
var session = require('express-session');

// post 미들웨어
app.use(cookieParser());
app.use(session({
  // 해킹을 막고자 비밀키를 설정
    secret : 'fakerfakerfaker', // 암호화 - 아무거나쓰자
    resave : true,  // 저장유무
    saveUninitialized : true  // 속성을 초기화 하지 않고 저장
}));

app.use(bodyParser.urlencoded({extended:true}));
// 서버 설정 (폴더경로 views로 지정, 뷰 엔진은 ejs로 설정)
app.set('views',__dirname+'/views');
app.set('view engine','ejs');

//미들웨어 css적용
app.use(express.static('public'));
//html에서 css파일에 쉽게 접근하기 위한 경로 설정
app.use('/logincss',express.static(__dirname+'/public/stylesheets/login.css'));
app.use('/maincss',express.static(__dirname+'/public/stylesheets/main.css'));
app.use('/reservationcss',express.static(__dirname+'/public/stylesheets/reservation.css'));
app.use('/reservation24css',express.static(__dirname+'/public/stylesheets/reservation24.css'));
app.use('/admincss',express.static(__dirname+'/public/stylesheets/admin.css'));

//login을 위한 라우터
var toLogin = require('./routes/toLogin');
app.use('/',toLogin);

//main을 위한 라우터
var toMain = require('./routes/toMain');
app.use('/main',toMain);
//reservation을 위한 라우터
var toReservation = require('./routes/toReservation');
app.use('/reservation',toReservation);
//24시 reservation을 위한 라우터
var toReservation24 = require('./routes/toReservation24');
app.use('/reservation24',toReservation24);
//admin을 위한 라우터
var toAdmin = require('./routes/toAdmin');
app.use('/admin',toAdmin);




//포트 3000으로 열겠다
http.createServer(app).listen(3000,function(){
  console.log('Server Running at http://localhost:3000/');
})
