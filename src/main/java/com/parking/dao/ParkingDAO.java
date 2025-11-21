package com.parking.dao;

import com.parking.model.ParkingSession;

import java.sql.*;
import java.time.Instant;

public class ParkingDAO {

    // 🔢 전체 주차 가능 대수 (원하는 숫자로 바꿔도 됨)
    private static final int TOTAL_SPACES = 50;

    // === 공용 ===
    public static int getTotalSpaces() {
        return TOTAL_SPACES;
    }

    // 현재 주차 중(입차 상태)의 차량 수
    public int getOccupiedCount() {
        String sql = "SELECT COUNT(*) FROM parking_session WHERE status = 'IN'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 잔여 주차공간 개수
    public int getRemainingSpaces() {
        int occupied = getOccupiedCount();
        int remain = TOTAL_SPACES - occupied;
        return Math.max(remain, 0);
    }

    // 특정 차량이 이미 주차 중인지 확인
    public boolean isAlreadyParked(String plate) {
        String sql = "SELECT COUNT(*) FROM parking_session WHERE plate = ? AND status = 'IN'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    return cnt > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // === 입차 처리 ===
    // 이미 입차 중인 경우 false, 정상 입차면 true
    public boolean startParking(String plate) {
        // 1) 이미 주차 중인지 체크
        if (isAlreadyParked(plate)) {
            return false;
        }

        // 2) (선택) 만차 여부 체크 – 만차면 입차 막고 싶으면 주석 해제
        /*
        if (getRemainingSpaces() <= 0) {
            return false;
        }
        */

        String sql = "INSERT INTO parking_session (plate, in_time, status) VALUES (?, NOW(), 'IN')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plate);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === 출차 처리 ===
    public ParkingSession endParking(String plate) {
        ParkingSession session = null;

        String selectSql = "SELECT * FROM parking_session WHERE plate = ? AND status = 'IN' ORDER BY in_time DESC LIMIT 1";
        String updateSql = "UPDATE parking_session SET out_time = ?, fee = ?, status = 'OUT' WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {

            // 1) 현재 주차중인 세션 조회
            selectPstmt.setString(1, plate);
            try (ResultSet rs = selectPstmt.executeQuery()) {
                if (!rs.next()) {
                    // 주차중인 기록이 없음
                    return null;
                }

                session = new ParkingSession();
                int id = rs.getInt("id");
                Timestamp inTime = rs.getTimestamp("in_time");

                session.setId(id);
                session.setPlate(rs.getString("plate"));
                session.setInTime(inTime);
                session.setStatus(rs.getString("status"));

                // 2) 시간 차이 계산 (분 단위)
                Timestamp now = Timestamp.from(Instant.now());
                long diffMs = now.getTime() - inTime.getTime();
                long minutes = diffMs / (1000 * 60);

                if (minutes <= 0) {
                    minutes = 1; // 최소 1분
                }

                // 요금 계산 (예: 1시간당 1,000원, 올림)
                long hours = (minutes + 59) / 60;
                int fee = (int) (hours * 1000);

                session.setOutTime(now);
                session.setDurationMinutes(minutes);
                session.setFee(fee);
                session.setStatus("OUT");

                // 3) DB 업데이트
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                    updatePstmt.setTimestamp(1, now);
                    updatePstmt.setInt(2, fee);
                    updatePstmt.setInt(3, id);

                    updatePstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return session;
    }
}
