<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $data_stream = "'".$_POST['ID']."','".$_POST['Sale']."'";
	$ID = "'".$_POST['ID']."'";
	$Sale = "'".$_POST['Sale']."'";
    $query = "UPDATE customer SET Sale = (".$Sale.") WHERE ID = (".$ID.")";
    $result = mysqli_query($conn, $query);
	$query2 = "SELECT Sale FROM customer WHERE ID = (".$ID.")";
	$result2 = mysqli_query($conn, $query2);
	$row = mysqli_fetch_array($result2);


    echo $row[0];

    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>