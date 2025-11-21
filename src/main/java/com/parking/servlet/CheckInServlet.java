package com.parking.servlet;
import com.parking.hardware.ServoGateController;

import com.parking.dao.ParkingDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/checkin")
public class CheckInServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	request.setCharacterEncoding("UTF-8");

        String plate = request.getParameter("plate");
        ParkingDAO dao = new ParkingDAO();

        String message;
        if (plate == null || plate.trim().isEmpty()) {
            message = "번호판을 입력하세요.";
        } else {
            String trimmed = plate.trim();

            // 이미 입차 중인지 먼저 확인
            if (dao.isAlreadyParked(trimmed)) {
                message = "이미 입차 중인 차량입니다: " + trimmed;
            } else {
                boolean ok = dao.startParking(trimmed);
                if (ok) {
                    message = "입차 완료: " + trimmed;
                    ServoGateController.openGate();
                } else {
                    message = "입차 처리 중 오류가 발생했습니다.";
                }
            }
        }

        request.setAttribute("message", message);
        int remain = dao.getRemainingSpaces();
        int total = ParkingDAO.getTotalSpaces();
        request.setAttribute("remain", remain);
        request.setAttribute("total", total);
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }
}
