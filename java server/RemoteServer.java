import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;

public class RemoteServer {

    private static final int TCP_PORT = 5000;
    private static final int UDP_PORT = 6000;

    public static void main(String[] args) {
        try {
            startUdpDiscovery();
            startTcpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- UDP DISCOVERY ----------------
    private static void startUdpDiscovery() {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(UDP_PORT);
                socket.setBroadcast(true);

                System.out.println("UDP discovery listening on " + UDP_PORT);

                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet =
                            new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());

                    if ("DISCOVER_REMOTE_SERVER".equals(msg)) {
                        String pcName = InetAddress.getLocalHost().getHostName();
                        String response = "REMOTE_SERVER:" + pcName;

                        byte[] data = response.getBytes();
                        DatagramPacket resp =
                                new DatagramPacket(
                                        data,
                                        data.length,
                                        packet.getAddress(),
                                        packet.getPort());

                        socket.send(resp);

                        System.out.println("Discovery request from "
                                + packet.getAddress());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ---------------- TCP CONTROL ----------------
    private static void startTcpServer() throws Exception {
        Robot robot = new Robot();
        ServerSocket serverSocket = new ServerSocket(TCP_PORT);
        System.out.println("TCP server started on " + TCP_PORT);

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(client.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                handleCommand(line, robot);
            }

            client.close();
            System.out.println("Client disconnected");
        }
    }

    // ---------------- COMMANDS ----------------
    private static void handleCommand(String cmd, Robot robot) throws Exception {

        if (cmd.startsWith("MOVE")) {
            String[] p = cmd.split(" ");
            int dx = Integer.parseInt(p[1]);
            int dy = Integer.parseInt(p[2]);

            Point cur = MouseInfo.getPointerInfo().getLocation();
            robot.mouseMove(cur.x + dx, cur.y + dy);
        }

        else if (cmd.equals("LEFT_CLICK")) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }

        else if (cmd.equals("RIGHT_CLICK")) {
            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        }

        else if (cmd.startsWith("KEY ")) {
            char c = cmd.substring(4).charAt(0);
            typeChar(robot, c);
        }

        else if (cmd.equals("BACKSPACE")) {
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
        }

        else if (cmd.equals("ENTER")) {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        else if (cmd.equals("SPACE")) {
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);
        }
    }

    private static void typeChar(Robot robot, char c) {
        try {
            boolean upper = Character.isUpperCase(c);
            int code = KeyEvent.getExtendedKeyCodeForChar(c);
            if (code == KeyEvent.VK_UNDEFINED) return;

            if (upper) robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(code);
            robot.keyRelease(code);
            if (upper) robot.keyRelease(KeyEvent.VK_SHIFT);
        } catch (Exception ignored) {}
    }
}
