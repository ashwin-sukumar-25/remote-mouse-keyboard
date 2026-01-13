# RemoteServer – Java Desktop Controller

RemoteServer is the desktop-side component of the Remote Mouse & Keyboard system.
It allows a mobile app to control mouse and keyboard input on a PC using
network communication.

---

## Architecture Overview

Mobile App (Flutter)
        |
        |  UDP Broadcast (Discovery)
        |
        v
Java Server (RemoteServer)
        |
        |  TCP Commands
        |
        v
System Input (Mouse + Keyboard)

---

## Communication Model

### 1. UDP Discovery
- Port: 6000
- The mobile app sends:
  DISCOVER_REMOTE_SERVER
- The server replies:
  REMOTE_SERVER:<PC_NAME>

This allows the app to automatically find the PC on the same network.

---

### 2. TCP Control Channel
- Port: 5000
- Used for real-time input commands.

---

## Supported Commands

| Command        | Description                              |
|----------------|------------------------------------------|
| MOVE dx dy     | Moves mouse relative to current cursor   |
| LEFT_CLICK     | Performs left mouse click                |
| RIGHT_CLICK    | Performs right mouse click               |
| KEY x          | Types a single character                 |
| BACKSPACE      | Deletes previous character               |
| ENTER          | Presses Enter key                        |
| SPACE          | Presses Space key                        |

---

## Core Technologies

- Java Sockets (TCP & UDP)
- java.awt.Robot for system input simulation
- Multithreading for discovery service
- Buffered I/O for command processing

---

## Security Note

This version is designed for:
- Local network usage
- Educational and prototyping purposes

For production usage, consider adding:
- Authentication
- Encryption (TLS)
- Command validation
- Rate limiting

---

## License

Apache License 2.0
