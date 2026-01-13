import 'package:flutter/material.dart';
import '../services/socket_service.dart';
import 'mouse_page.dart';
import 'connect_page.dart';

class KeyboardPage extends StatefulWidget {
  const KeyboardPage({super.key});

  @override
  State<KeyboardPage> createState() => _KeyboardPageState();
}

class _KeyboardPageState extends State<KeyboardPage> {
  String lastText = "";

  void onChanged(String text) {
    if (text.length > lastText.length) {
      final ch = text.substring(lastText.length);
      socketService.send("KEY $ch");
    } else if (text.length < lastText.length) {
      socketService.send("BACKSPACE");
    }
    lastText = text;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Keyboard"),
        actions: [
          IconButton(
            icon: const Icon(Icons.mouse),
            onPressed: () {
              Navigator.pushReplacement(context,
                  MaterialPageRoute(builder: (_) => const MousePage()));
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            TextField(
              autofocus: true,
              onChanged: onChanged,
              decoration: InputDecoration(
                filled: true,
                fillColor: Colors.white10,
                hintText: "Type here...",
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
              ),
            ),
            const Spacer(),
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
      ),
    );
  }
}
