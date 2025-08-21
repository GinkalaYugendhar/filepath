<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<center>
		<form action="./uploadfile" method="post" enctype="multipart/form-data">
			<input type="file" name="file" accept=".pdf, .xls, .xlsx" required/>
			<input type="submit" value="Submit">
		</form><br>
		<!-- <a href="./allsongs">All Songs🎵</a>  -->
	</center>
</body>
</html>