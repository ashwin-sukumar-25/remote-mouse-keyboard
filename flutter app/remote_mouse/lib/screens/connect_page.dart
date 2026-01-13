import 'package:flutter/material.dart';
import '../services/discovery_service.dart';
import '../services/socket_service.dart';
import 'mouse_page.dart';

class ConnectPage extends StatefulWidget {
  const ConnectPage({super.key});

  @override
  State<ConnectPage> createState() => _ConnectPageState();
}

class _ConnectPageState extends State<ConnectPage> {
  bool scanning = false;
  List<DiscoveredDevice> devices = [];
  final ipController = TextEditingController();

  Future<void> scan() async {
    setState(() {
      scanning = true;
      devices.clear();
    });

    final result = await discoveryService.discover();

    setState(() {
      devices = result;
      scanning = false;
    });
  }

  Future<void> connect(String ip) async {
    bool ok = await socketService.connect(ip, 5000);
    if (ok) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const MousePage()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              const SizedBox(height: 30),
              const Text(
                "Remote Control",
                style: TextStyle(
                  fontSize: 32,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              const Text("Control your PC effortlessly"),

              const SizedBox(height: 30),

              ElevatedButton(
                onPressed: scanning ? null : scan,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF6C63FF),
                  padding:
                      const EdgeInsets.symmetric(horizontal: 30, vertical: 14),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30)),
                ),
                child: scanning
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text("Scan Devices"),
              ),

              const SizedBox(height: 20),

              Expanded(
                child: devices.isEmpty
                    ? const Center(child: Text("No devices found"))
                    : ListView.builder(
                        itemCount: devices.length,
                        itemBuilder: (_, i) {
                          final d = devices[i];
                          return Card(
                            color: Colors.white10,
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(16)),
                            margin: const EdgeInsets.symmetric(
                                horizontal: 16, vertical: 8),
                            child: ListTile(
                              leading: const Icon(Icons.computer),
                              title: Text(d.name),
                              subtitle: Text(d.ip),
                              onTap: () => connect(d.ip),
                            ),
                          );
                        },
                      ),
              ),

              const Divider(color: Colors.white24),

              Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    const Text("Connect via IP"),
                    const SizedBox(height: 8),
                    TextField(
                      controller: ipController,
                      decoration: InputDecoration(
                        filled: true,
                        fillColor: Colors.white10,
                        hintText: "192.168.x.x",
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                        ),
                      ),
                    ),
                    const SizedBox(height: 10),
                    ElevatedButton(
                      onPressed: () => connect(ipController.text),
                      child: const Text("Connect"),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
