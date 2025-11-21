package com.parking.ocr;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Python OCR 서버( http://localhost:5000/ocr )에
 * 이미지 바이트를 보내서 번호판 텍스트를 받아오는 헬퍼.
 */
public class OCRClient {

    private static final String OCR_URL = "http://localhost:5000/ocr";

    public static String recognizePlate(byte[] imageBytes) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OCR_URL))
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(imageBytes))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("OCR 서버 응답 코드: " + response.statusCode());
        }

        String body = response.body();
        if (body == null) return null;

        String plate = body.trim();
        if (plate.isEmpty() || "UNKNOWN".equalsIgnoreCase(plate)) {
            return null;
        }
        return plate;
    }
}
