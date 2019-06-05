<?php

	error_reporting(E_ERROR | E_PARSE);
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호, db선택)
  $con=mysqli_connect( "localhost", "root", "tjrgml","studyroom") or die( "SQL server에 연결할 수 없습니다.");
 
  mysqli_set_charset($con,"utf8");
  
 
  // 세션 시작
  session_start();  
  
  $date = isset($_POST['u_date']) ? $_POST['u_date']: '';
  $start_time = isset($_POST['u_start_time']) ? $_POST['u_start_time']: '';
  $end_time = isset($_POST['u_end_time']) ? $_POST['u_end_time']: '';
  $usingPeople = isset($_POST['u_people']) ? $_POST['u_people']: '';
  
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($date != "" ){ 

    $sql="select ROOM_NUM
		from studyroom_db
		where ROOM_NUM not in (select distinct ROOM_NUM from reservation where r_date = '$date' AND start_time < '$end_time' AND end_time > '$start_time')
		AND OPENTIME <= '$start_time'
		AND CLOSETIME >= '$end_time'
		AND MAXTime >= $end_time-$start_time
		AND MIN <= '$usingPeople'
		AND FUll >= '$usingPeople'";
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
		  $data = array();	  
		  
		  while($row=mysqli_fetch_array($result)){

        	extract($row);

            array_push($data, 
                array('room_num'=>$row[ROOM_NUM]				
            ));
			}
			
			if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
			}else
			{
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
			}
		}
	  }
	  else
	  {
	   echo mysqli_errno($con);
	  }
	  
	  }
else {
    // echo "검색할 내용을 입력하세요 ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
			날짜: <input type = "text" name = "u_date" />
			시작시간: <input type = "text" name = "u_start_time" /> 
			종료시간: <input type = "text" name = "u_end_time" />
			사용인원: <input type = "text" name = "u_people" /> 			
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?> 