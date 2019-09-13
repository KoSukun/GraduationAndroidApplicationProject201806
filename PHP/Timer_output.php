<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","test1234","graduationproject");
    $data_stream = "'".$_POST['ID']."'";
	$query2 = "SELECT CarNo FROM customer WHERE ID = (".$data_stream.")";
	$result2 = mysqli_query($conn, $query2);
	$row2 = mysqli_fetch_array($result2);
	$data2 = $row2[0];
    $query = "SELECT InTime FROM carin WHERE CarNo = (".$data2.")";
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