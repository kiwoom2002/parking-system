package com.parking.hardware;

import com.fazecast.jSerialComm.SerialPort;

public class ServoGateController {

    // ★ 여기 COM 번호를 장치관리자에서 본 값으로 맞춰줘 (예: COM3, COM4)
    private static final String PORT_NAME = "COM4";
    private static final int BAUD_RATE = 9600;

    private static SerialPort port;

    // 포트가 열려 있는지 확인하고, 안 열려 있으면 여기서 다시 연다.
    private static synchronized boolean ensureOpen() {
        try {
            // 아직 포트 객체를 안 만들었으면 생성
            if (port == null) {
                System.out.println("[ServoGate] Creating port object for " + PORT_NAME);
                port = SerialPort.getCommPort(PORT_NAME);
                port.setBaudRate(BAUD_RATE);
                port.setNumDataBits(8);
                port.setNumStopBits(SerialPort.ONE_STOP_BIT);
                port.setParity(SerialPort.NO_PARITY);
            }

            // 이미 열려 있으면 OK
            if (port.isOpen()) {
                return true;
            }

            // 안 열려 있으면 여기서 다시 열기 시도
            System.out.println("[ServoGate] Trying to open port " + PORT_NAME);

            // 디버그용: 현재 사용 가능한 포트들 출력
            SerialPort[] ports = SerialPort.getCommPorts();
            System.out.println("=== Available serial ports ===");
            for (SerialPort p : ports) {
                System.out.println("Port: " + p.getSystemPortName()
                        + " / " + p.getDescriptivePortName());
            }
            System.out.println("================================");

            boolean opened = port.openPort();
            System.out.println("[ServoGate] Serial open result = " + opened);
            return opened;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 외부에서 호출하는 메서드는 그대로 openGate() 하나만 쓰면 됨
    public static void openGate() {
        // 포트가 안 열려 있으면 시도해 보고, 그래도 안 되면 포기
        if (!ensureOpen()) {
            System.err.println("[ServoGate] Serial port not open, cannot move servo.");
            return;
        }

        try {
            String cmd = "OPEN\n";
            byte[] bytes = cmd.getBytes();
            port.writeBytes(bytes, bytes.length);
            System.out.println("[ServoGate] Sent to Arduino: " + cmd.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
