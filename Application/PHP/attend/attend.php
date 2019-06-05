<?php

	error_reporting(E_ERROR | E_PARSE);
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호, db선택)
  $con=mysqli_connect( "localhost", "root", "tjrgml","studyroom") or die( "SQL server에 연결할 수 없습니다.");
 
  mysqli_set_charset($con,"utf8");
  
 
  // 세션 시작
  session_start();
  
  $room_num = isset($_POST['u_room_num']) ? $_POST['u_room_num']: '';
  $date = isset($_POST['u_date']) ? $_POST['u_date']: '';
  $start_time = isset($_POST['u_start_time']) ? $_POST['u_start_time']: '';  
  $at_check = isset($_POST['u_at_check']) ? $_POST['u_at_check']: '';
  $dele_check = isset($_POST['u_dele_check']) ? $_POST['u_dele_check']: '';
  
  
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($at_check != "" ){ 

    $sql="UPDATE reservation
		SET AT_CHECK = $at_check, DELE_CHECK = $dele_check
		WHERE R_DATE = '$date'
		AND START_TIME = $start_time
		AND ROOM_NUM = $room_num;";
	//$stmt = $con->prepare($sql);
    //$stmt->execute();
	$result = mysqli_query($con,$sql);
	 // result of sql query	
	  if($result)
	  {		
		if($result->num_rows == 0) // sql결과가 없는, row가 0일때 -> 잘됨
		{
		  echo "10";//"아이구 잘됐다";
		}
		else
		{
		  echo "0"; // 출석체크 조짐
		}
	  }
	  else
	  {
	   echo mysqli_errno($con);
	  }
	  
	  }
else {
    // echo "예약할 내용을 입력하세요. ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">			
			방번호: <input type = "text" name = "u_room_num" />
			날짜: <input type = "text" name = "u_date" />
			시작시간: <input type = "text" name = "u_start_time" /> 			
			출석체크여부: <input type = "text" name = "u_at_check" />
			대표자출첵여부: <input type = "text" name = "u_dele_check" />			 			
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}