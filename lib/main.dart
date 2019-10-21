import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);


  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  Map<PermissionGroup, PermissionStatus> permissions;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    getPermission();


  }

  void getPermission() async {
    permissions = await PermissionHandler().requestPermissions([
      PermissionGroup.phone,
      PermissionGroup.storage,
      PermissionGroup.microphone,
    ]);


  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'This is the flutter Rcording application',
            ),
            Text('It will record the incoming and outgoing'),
            Text(' call conversation in the background as well'),

          ],
        ),
      ),
    );
  }
}
