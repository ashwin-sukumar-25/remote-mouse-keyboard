import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;

/**
 * RemoteServer
 * ------------
 * A cross-platform Java server that allows a mobile client
 * to remotely control mouse and keyboard input on a PC.
 *
 * Features:
 * - UDP-based discovery so the mobile app can find the PC automatically
 * - TCP-based command channel for real-time mouse & keyboard control
 * - Uses java.awt.Robot to simulate system-level input
 *
 * Supported Platforms:
 * - Windows
 * - Linux
 * - macOS
 *
 * Author: Ashwin Sukumar
 * License: Apache 2.0
 */
public class RemoteServer {

    /** Port used for TCP control communication */
    private static final int TCP_PORT = 5000;

    /** Port used for UDP discovery broadcast */
    private static final int UDP_PORT = 6000;

    /**
     * Entry point of the server.
     * Starts:
     * 1. UDP discovery listener
     * 2. TCP control server
     */
    public static void main(String[] args) {
        try {
            startUdpDiscovery();
            startTcpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= UDP DISCOVERY =================

    /**
     * Starts a background thread that listens for UDP broadcast messages
     * from the mobile app.
     *
     * When the message "DISCOVER_REMOTE_SERVER" is received:
     * - The server replies with its hostname
     * - This allows the mobile app to locate the PC automatically
     */
    private static void startUdpDiscovery() {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(UDP_PORT);
                socket.setBroadcast(true);

                System.out.println("UDP discovery listening on port " + UDP_PORT);

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

    // ================= TCP CONTROL =================

    /**
     * Starts a TCP server that listens for control commands
     * from the mobile application.
     *
     * Each connected client can:
     * - Move mouse
     * - Perform left/right clicks
     * - Send keyboard input
     *
     * @throws Exception if socket or Robot initialization fails
     */
    private static void startTcpServer() throws Exception {

        Robot robot = new Robot();
        ServerSocket serverSocket = new ServerSocket(TCP_PORT);

        System.out.println("TCP server started on port " + TCP_PORT);

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

    // ================= COMMAND HANDLING =================

    /**
     * Parses and executes a command received from the client.
     *
     * Supported Commands:
     * MOVE dx dy      → Moves mouse relative to current position
     * LEFT_CLICK     → Performs left mouse click
     * RIGHT_CLICK    → Performs right mouse click
     * KEY x          → Types a character
     * BACKSPACE      → Presses backspace
     * ENTER          → Presses enter
     * SPACE          → Presses space
     *
     * @param cmd   Command string from client
     * @param robot Robot instance used for system input control
     * @throws Exception if command parsing fails
     */
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

    /**
     * Types a single character using the Robot API.
     * Handles uppercase characters using SHIFT key.
     *
     * @param robot Robot instance
     * @param c     Character to type
     */
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
