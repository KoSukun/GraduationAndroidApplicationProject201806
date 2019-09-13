<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $query = "SELECT * FROM Sale";
    $result = mysqli_query($conn, $query);	

	while($row = mysqli_fetch_array($result)) {
		for($i = 0; $i < 3; $i++) {
			echo $row[$i];
			echo ",";
		}
			
	}

    if($result)
      echo " ";
    else
      echo "-1";
     
    mysqli_close($conn);
?>