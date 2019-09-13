<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $data_stream = "'".$_POST['ID']."'";
	$query = "SELECT CarNo FROM customer WHERE ID = (".$data_stream.")";
	$result = mysqli_query($conn, $query);
	$row = mysqli_fetch_array($result);
	$data = $row[0];

	
    echo $data;

    if($result)
      echo "";
    else
      echo "-1";
     
    mysqli_close($conn);
?>