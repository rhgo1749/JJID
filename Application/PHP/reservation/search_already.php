<?php

	error_reporting(E_ERROR | E_PARSE);
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호, db선택)
  $con=mysqli_connect( "localhost", "root", "tjrgml","studyroom") or die( "SQL server에 연결할 수 없습니다.");
 
  mysqli_set_charset($con,"utf8");
  
 
  // 세션 시작
  session_start();
  
  
  $id = isset($_POST['u_id']) ? $_POST['u_id']: '';
  $date = isset($_POST['u_date']) ? $_POST['u_date']: '';
  //$pw = isset($_POST['u_pw']) ? $_POST['u_pw']: '';
  
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($id != "" ){ 

    $sql="select ROOM_NUM, START_TIME, END_TIME
		from reservation
		where stu_num='$id'
		AND r_date='$date'";
    //$stmt = $con->prepare($sql);
    //$stmt->execute();
	$result = mysqli_query($con,$sql);
	 // result of sql query	
	  if($result)
	  {		
		if($result->num_rows == 0) // sql결과가 없는, row가 0일때 -> 아이디가 없어 cmp자체가 안됨
		{
		  echo "10";//"중복되는 예약이 없음";
		}
		else
		{
		  $data = array();	  
		  
		  while($row=mysqli_fetch_array($result)){

        	extract($row);

            array_push($data, 
                array('room_num'=>$row[ROOM_NUM],
				'start_time'=>$row[START_TIME],
				'end_time'=>$row[END_TIME]				
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
    //echo "검색할 학번을 입력하세요 ";
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
		날짜 : <input type = "text" name = "u_date" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?> 
  