<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $data_stream = "'".$_POST['CarNo']."','".$_POST['OutTime']."'";
	$CarNo = "'".$_POST['CarNo']."'";
    $query = "INSERT INTO carout (CarNo, OutTime) VALUES (".$data_stream.")";
    $result = mysqli_query($conn, $query);
	$query2 = "UPDATE carin SET IsPaid = 'Paid' WHERE CarNo = (".$CarNo.")";
	$result2 = mysqli_query($conn, $query2);


    echo $query;

    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>