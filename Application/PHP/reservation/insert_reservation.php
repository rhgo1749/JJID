<?php

	error_reporting(E_ERROR | E_PARSE);
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호, db선택)
  $con=mysqli_connect( "localhost", "root", "tjrgml","studyroom") or die( "SQL server에 연결할 수 없습니다.");
 
  mysqli_set_charset($con,"utf8");
  
 
  // 세션 시작
  session_start();
  
  $id = isset($_POST['u_id']) ? $_POST['u_id']: '';
  $room_num = isset($_POST['u_room_num']) ? $_POST['u_room_num']: '';
  $date = isset($_POST['u_date']) ? $_POST['u_date']: '';
  $start_time = isset($_POST['u_start_time']) ? $_POST['u_start_time']: '';
  $end_time = isset($_POST['u_end_time']) ? $_POST['u_end_time']: '';
  $delegator = isset($_POST['u_delegator']) ? $_POST['u_delegator']: '';
  $at_check = isset($_POST['u_at_check']) ? $_POST['u_at_check']: '';
  $dele_check = isset($_POST['u_dele_check']) ? $_POST['u_dele_check']: '';
  
  
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($id != "" ){ 

    $sql="INSERT INTO RESERVATION 
	VALUES('$id', $room_num, '$date', $start_time, $end_time, '$delegator', $at_check, $dele_check);";
	//$stmt = $con->prepare($sql);
    //$stmt->execute();
	$result = mysqli_query($con,$sql);
	 // result of sql query	
	  if($result)
	  {		
		if($result->num_rows == 0) // sql결과가 없는, row가 0일때 -> 아이디가 없어 cmp자체가 안됨
		{
		  echo "10";//"Can not find Data";
		}
		else
		{
		  echo "0";
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
			학번: <input type = "text" name = "u_id" />
			방번호: <input type = "text" name = "u_room_num" />
			날짜: <input type = "text" name = "u_date" />
			시작시간: <input type = "text" name = "u_start_time" /> 
			종료시간: <input type = "text" name = "u_end_time" />
			대표자 학번: <input type = "text" name = "u_delegator" />
			출석체크여부: <input type = "text" name = "u_at_check" />
			대표자출첵여부: <input type = "text" name = "u_dele_check" />
			 			
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?> 