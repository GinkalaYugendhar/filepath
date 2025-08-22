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
<%
    String msg = (String) request.getAttribute("msg");
    String filePath = (String) request.getAttribute("filePath");

    if (msg != null) {
%>
    <p style="color:green;"><%= msg %></p>
<%
    }

    if (filePath != null && !filePath.isEmpty()) {
%>
    <form action="./relocatefile" method="post" enctype="multipart/form-data">
        <h1>Enter new File Path and select the File</h1>
        <input type="text" name="newpath" placeholder="Enter New Path" required/>
        <input type="file" name="file" accept=".pdf, .xls, .xlsx" required/>
        <input type="submit" value="Submit">
    </form><br>
<%
    }
%>
	</center>
</body>
</html>