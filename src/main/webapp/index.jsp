<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.parking.dao.ParkingDAO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>주차관리 시스템</title>
</head>
<body>
<%
    ParkingDAO dao = new ParkingDAO();
    int totalSpaces = ParkingDAO.getTotalSpaces();
    int occupied = dao.getOccupiedCount();
    int remaining = dao.getRemainingSpaces();
%>
<h2>주차관리 시스템</h2>
<h3>주차장 현황</h3>
<ul>
    <li>전체 주차면수: <%= totalSpaces %> 대</li>
    <li>현재 주차 중: <%= occupied %> 대</li>
    <li><b>잔여 주차공간: <%= remaining %> 대</b></li>
</ul>

<hr/>
<h3>입차 처리</h3>
<form action="checkin" method="post">
    번호판: <input type="text" name="plate" />
    <input type="submit" value="입차" />
</form>
<hr/>

<h3>사진으로 입차 처리</h3>
<form action="checkinImage" method="post" enctype="multipart/form-data">
    번호판 사진: <input type="file" name="plateImage" accept="image/*" />
    <input type="submit" value="사진으로 입차" />
</form>

<hr/>

<h3>출차 처리</h3>
<form action="checkout" method="post">
    번호판: <input type="text" name="plate" />
    <input type="submit" value="출차 및 요금 계산" />
</form>

</body>
</html>
