<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","root","tnrns8256","graduationproject");
    $string = "".$_POST['Name']."";
	//$string = "영화 할인,식사 할인,";
	$strTok = explode(",", $string);
	$count = count($strTok);
	$scount = $count - 1;
	for($n = 0; $n < $scount; $n++) {
		$strTokk[$n] = "'".$strTok[$n]."'";
		$query = "SELECT * FROM sale WHERE Name = (".$strTokk[$n].")";	
		$result = mysqli_query($conn, $query);
		$row = mysqli_fetch_array($result);
		for($i = 0; $i < 3; $i++) {
			echo $row[$i];
			echo ",";
		}
	}
	

    if($result)
      echo "";
    else
      echo "-1";
     
    mysqli_close($conn);
?>