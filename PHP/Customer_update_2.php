<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $ID = "'".$_POST['ID']."'";
    $CarNo = "'".$_POST['CarNo']."'";
    $query = "UPDATE customer SET CarNo = (".$CarNo.") WHERE ID = (".$ID.")";
    $result = mysqli_query($conn, $query);
	$query2 = "SELECT CarNo FROM customer WHERE ID = (".$ID.")";
	$result2 = mysqli_query($conn, $query2);
	$row = mysqli_fetch_array($result2);
	$data = $row[0];
	

    echo $data;

    if($result)
      echo "";
    else
      echo "-1";
  
	error_reporting(E_ALL);

	ini_set("display_errors", 1);
     
    mysqli_close($conn);
?>