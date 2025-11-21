package com.parking.servlet;
import com.parking.hardware.ServoGateController;
import com.parking.dao.ParkingDAO;
import com.parking.model.ParkingSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckOutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String plate = request.getParameter("plate");

        if (plate == null || plate.trim().isEmpty()) {
            request.setAttribute("error", "번호판을 입력하세요.");
            request.getRequestDispatcher("/checkout_result.jsp").forward(request, response);
            return;
        }

        ParkingDAO dao = new ParkingDAO();
        ParkingSession session = dao.endParking(plate.trim());

        if (session == null) {
            request.setAttribute("error", "현재 주차중인 차량 기록이 없습니다. (이미 출차했거나 입차 이력이 없음)");
        } else {
            request.setAttribute("session", session);
            ServoGateController.openGate();
        }

        request.getRequestDispatcher("/checkout_result.jsp").forward(request, response);
    }
}
