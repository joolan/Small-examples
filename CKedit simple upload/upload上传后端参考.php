<?php
//参考的上传php后端
// 处理 OPTIONS 请求
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    header("Access-Control-Allow-Origin: *");
    header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
    header("Access-Control-Allow-Headers: *");//header("Access-Control-Allow-Headers: Content-Type");
    header("Access-Control-Allow-Credentials: false");
    header("Access-Control-Max-Age: 3600");
    exit;
}
//  这个是专门为CKedit5 简单上传制作的页面

header("Access-Control-Allow-Origin: *"); // 允许所有域访问，也可以指定特定域。
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: *");
// 允许上传的图片后缀
$allowedExts = array("gif", "jpeg", "jpg", "png");

//$_FILES["upload"]["name"]=$_FILES["upload"]["name"];
$temp = explode(".", $_FILES["upload"]["name"]);
//echo $_FILES["upload"]["size"];
$extension = end($temp);     // 获取文件后缀名
if ((($_FILES["upload"]["type"] == "image/gif")
|| ($_FILES["upload"]["type"] == "image/jpeg")
|| ($_FILES["upload"]["type"] == "image/jpg")
|| ($_FILES["upload"]["type"] == "image/pjpeg")
|| ($_FILES["upload"]["type"] == "image/x-png")
|| ($_FILES["upload"]["type"] == "image/png"))
&& ($_FILES["upload"]["size"] < 20480000)   // 小于 20000 kb
&& in_array($extension, $allowedExts))
{
	if ($_FILES["upload"]["error"] > 0)
	{
		echo "错误：: " . $_FILES["upload"]["error"] . "<br>";
	}
	else
	{
		//echo "上传文件名: " . $_FILES["upload"]["name"] . "<br>";
		//echo "文件类型: " . $_FILES["upload"]["type"] . "<br>";
		//echo "文件大小: " . ($_FILES["upload"]["size"] / 1024) . " kB<br>";
		//echo "文件临时存储的位置: " . $_FILES["upload"]["tmp_name"] . "<br>";
		
		// 判断当前目录下的 upload 目录是否存在该文件
		// 如果没有 upload 目录，你需要创建它，upload 目录权限为 777
		if (file_exists("upload/" . $_FILES["upload"]["name"]))
		{
			//echo $_FILES["upload"]["name"] . " 文件已经存在。 ";
		}
		else
		{
			// 如果 upload 目录不存在该文件则将文件上传到 upload 目录下
			$file_name= $_FILES["upload"]["name"].rand(1000,9999).".jpg";
			move_uploaded_file($_FILES["upload"]["tmp_name"], "upload/" . $file_name);
			//echo "文件存储在: " . "upload/" . $file_name;
			echo '{"url": "http://xxxx.com/upload/'.$file_name.'"}';
			
		}
	}
}
else
{
	//echo "非法的文件格式";
	echo '{
    "error": {
        "message": "非法的文件格式"
    }
}
';

}
?>
