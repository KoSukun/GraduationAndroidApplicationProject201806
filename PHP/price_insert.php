<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $data_stream = "'".$_POST['pricesystem']."','".$_POST['basicprice']."','".$_POST['perprice']."'";
    $query = "INSERT INTO charge (pricesystem,basicprice,perprice) VALUES (".$data_stream.")";
    $result = mysqli_query($conn, $query);

    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>