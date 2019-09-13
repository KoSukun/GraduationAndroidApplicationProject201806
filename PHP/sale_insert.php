<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $data_stream = "'".$_POST['Name']."','".$_POST['Type']."','".$_POST['Price']."'";
    $query = "INSERT INTO sale (Name,Type,Price) VALUES (".$data_stream.")";
    $result = mysqli_query($conn, $query);


    echo $query;

    if($result)
      echo "1";
    else
      echo "-1";
     
    mysqli_close($conn);
?>