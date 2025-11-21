package com.parking.servlet;
import com.parking.hardware.ServoGateController;
import com.parking.dao.ParkingDAO;
import com.parking.ocr.OCRClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/checkinImage")
@MultipartConfig
public class CheckInImageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String message;

        try {
            Part imagePart = request.getPart("plateImage");

            if (imagePart == null || imagePart.getSize() == 0) {
                message = "번호판 사진을 업로드하세요.";
            } else {
                // 이미지 바이트 읽기
                byte[] imageBytes;
                try (InputStream is = imagePart.getInputStream()) {
                    imageBytes = is.readAllBytes();
                }

                // OCR 서버 호출
                String plate = null;
                try {
                    plate = OCRClient.recognizePlate(imageBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "OCR 서버 호출 중 오류가 발생했습니다.";
                    request.setAttribute("message", message);
                    request.getRequestDispatcher("/result.jsp").forward(request, response);
                    return;
                }

                if (plate == null) {
                    message = "번호판을 인식하지 못했습니다.";
                } else {
                    // 인식된 번호판으로 입차 처리
                    ParkingDAO dao = new ParkingDAO();
                    if (dao.isAlreadyParked(plate)) {
                        message = "이미 입차 중인 차량입니다: " + plate;
                    } else {
                        boolean ok = dao.startParking(plate);
                        if (ok) {
                            message = "사진 인식 입차 완료: " + plate;
                            ServoGateController.openGate();
                        } else {
                            message = "입차 처리 중 오류가 발생했습니다.";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "요청 처리 중 오류가 발생했습니다.";
        }

        request.setAttribute("message", message);
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }
}
