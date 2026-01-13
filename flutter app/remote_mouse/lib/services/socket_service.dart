import 'dart:io';

class SocketService {
  Socket? _socket;

  bool get isConnected => _socket != null;

  Future<bool> connect(String ip, int port) async {
    try {
      _socket = await Socket.connect(
        ip,
        port,
        timeout: const Duration(seconds: 5),
      );
      return true;
    } catch (_) {
      return false;
    }
  }

  void send(String msg) {
    _socket?.write("$msg\n");
  }

  void disconnect() {
    send("DISCONNECT");
    _socket?.close();
    _socket = null;
  }
}

final socketService = SocketService();
