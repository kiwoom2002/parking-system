<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.parking.model.ParkingSession" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>출차 및 요금 결과</title>
</head>
<body>
<h2>출차 및 요금 결과</h2>

<%
    String error = (String) request.getAttribute("error");
    ParkingSession parkingSession = (ParkingSession) request.getAttribute("session");

    if (error != null) {
%>
    <p style="color:red;"><%= error %></p>
<%
    } else if (session != null) {
%>
    <p>차량 번호: <%= parkingSession.getPlate() %></p>
    <p>입차 시간: <%= parkingSession.getInTime() %></p>
    <p>출차 시간: <%= parkingSession.getOutTime() %></p>
    <p>주차 시간: <%= parkingSession.getDurationMinutes() %> 분</p>
    <p>주차 요금: <%= parkingSession.getFee() %> 원</p>
<%
    } else {
%>
    <p>결과 정보가 없습니다.</p>
<%
    }
%>

<p><a href="index.jsp">메인으로 돌아가기</a></p>

</body>
</html>
