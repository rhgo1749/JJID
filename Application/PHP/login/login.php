<?php

	error_reporting(E_ERROR | E_PARSE);
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호, db선택)
  $con=mysqli_connect( "localhost", "root", "tjrgml","studyroom") or die( "SQL server에 연결할 수 없습니다.");
 
  mysqli_set_charset($con,"utf8");
  
 
  // 세션 시작
  session_start();
 
  $id = $_POST['u_id'];
  $pw = $_POST['u_pw'];
  
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
  
  $sql = "SELECT IF(!strcmp(PASSWORD,'$pw'),0,1) pw_chk FROM user_db WHERE stu_num = '$id'";
  //if(조건, 참일때, 거짓일때)
  //$sql = "SELECT PASSWORD pw_chk FROM user_db WHERE stu_num = '$id'";
 
  $result = mysqli_query($con,$sql);	
	
  // result of sql query	
  if($result)
  {
    $row = mysqli_fetch_array($result);
    if(is_null($row[pw_chk])) // sql결과가 없는, row가 0일때 -> 아이디가 없어 cmp자체가 안됨
    {
      echo "Can not find ID";
    }
    else
    {
      echo "$row[pw_chk]";
    }
  }
  else
  {
   echo mysqli_errno($con);
  }
?>

<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST"> 
		학번 : <input type = "text" name = "u_id" />		
		비밀번호 : <input type = "text" name = "u_pw" />			
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>
