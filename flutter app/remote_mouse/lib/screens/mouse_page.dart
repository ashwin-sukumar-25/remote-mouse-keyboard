import 'package:flutter/material.dart';
import '../services/socket_service.dart';
import 'keyboard_page.dart';
import 'connect_page.dart';

class MousePage extends StatefulWidget {
  const MousePage({super.key});

  @override
  State<MousePage> createState() => _MousePageState();
}

class _MousePageState extends State<MousePage> {
  Offset? last;

  void onPanStart(DragStartDetails d) => last = d.localPosition;

  void onPanUpdate(DragUpdateDetails d) {
    if (last == null) return;
    final cur = d.localPosition;
    final dx = (cur.dx - last!.dx).toInt();
    final dy = (cur.dy - last!.dy).toInt();
    last = cur;

    socketService.send("MOVE ${dx * 2} ${dy * 2}");
  }

  void onPanEnd(_) => last = null;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Touchpad"),
        actions: [
          IconButton(
            icon: const Icon(Icons.keyboard),
            onPressed: () {
              Navigator.pushReplacement(context,
                  MaterialPageRoute(builder: (_) => const KeyboardPage()));
            },
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: GestureDetector(
              onPanStart: onPanStart,
              onPanUpdate: onPanUpdate,
              onPanEnd: onPanEnd,
              child: Container(
                margin: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(24),
                  gradient: const LinearGradient(
                    colors: [Color(0xFF232526), Color(0xFF414345)],
                  ),
                ),
                child: const Center(
                  child: Text(
                    "Touch here",
                    style: TextStyle(color: Colors.white54),
                  ),
                ),
              ),
            ),
          ),
          Row(
            children: [
              Expanded(
                child: TextButton(
                  onPressed: () => socketService.send("LEFT_CLICK"),
                  child: const Text("LEFT"),
                ),
              ),
              Expanded(
                child: TextButton(
                  onPressed: () => socketService.send("RIGHT_CLICK"),
                  child: const Text("RIGHT"),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          TextButton(
            onPressed: () {
              socketService.disconnect();
              Navigator.pushReplacement(context,
                  MaterialPageRoute(builder: (_) => const ConnectPage()));
            },
            child: const Text("DISCONNECT"),
          ),
        ],
      ),
    );
  }
}
