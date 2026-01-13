import 'dart:async';
import 'dart:io';

class DiscoveredDevice {
  final String name;
  final String ip;

  DiscoveredDevice(this.name, this.ip);
}

class DiscoveryService {
  static const int port = 6000;

  Future<List<DiscoveredDevice>> discover() async {
    List<DiscoveredDevice> devices = [];

    final socket = await RawDatagramSocket.bind(
      InternetAddress.anyIPv4,
      0,
    );

    socket.broadcastEnabled = true;

    socket.send(
      "DISCOVER_REMOTE_SERVER".codeUnits,
      InternetAddress("255.255.255.255"),
      port,
    );

    socket.listen((event) {
      if (event == RawSocketEvent.read) {
        final dg = socket.receive();
        if (dg == null) return;

        final msg = String.fromCharCodes(dg.data);
        if (msg.startsWith("REMOTE_SERVER:")) {
          final name = msg.substring(14);
          final ip = dg.address.address;

          if (!devices.any((d) => d.ip == ip)) {
            devices.add(DiscoveredDevice(name, ip));
          }
        }
      }
    });

    await Future.delayed(const Duration(seconds: 2));
    socket.close();
    return devices;
  }
}

final discoveryService = DiscoveryService();
