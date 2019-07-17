package gns.ece436_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;

import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    CheckBox server;
    TextView log;
    EditText ip;
    Button do_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        server = findViewById(R.id.server);
        log = findViewById(R.id.log);
        ip = findViewById(R.id.ip);
        do_test = findViewById(R.id.do_test);
        addServerListener();
        addTestListener();
    }

    public void addServerListener() {
        server.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // if enabled, disable; if disabled, start listening
                if (server.isChecked()) {
                    server.setChecked(false);
                    log.append("\nSVR :: prep for socket reading...");
                    int port = 5000;
                    String buffer;
                    try {
                        ServerSocket socket = new ServerSocket(port);
                        log.append("\nSVR :: server listening...");
                        boolean cont = true;
                        while (cont) {
                            Socket clientSocket = socket.accept();
                            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            while ((buffer = br.readLine()) != null) {
                                log.append("\n");
                                log.append(buffer);
                                if (buffer.contains("exit")) {
                                    cont = false;
                                }
                            }
                            br.close();
                        }
                        socket.close();
                    } catch (Exception e) {}
                    log.append("\nSVR :: server disabled...");
                }
                server.setChecked(false);
            }
        });
    }

    public void sendPacket(String payload, String host, String proto, int port) {
        switch (proto) {
            case "UDP":
                break;
            case "TCP":
                log.append("\nCLI :: Sending TCP packet...");
                try {
                    Socket socket = new Socket(host, port);
                    OutputStream os = socket.getOutputStream();
                    os.write(payload.getBytes("US-ASCII"));
                    os.close();
                    socket.close();
                    break;
                } catch (Exception e) {}
        }
    }

    public void addTestListener() {
        do_test.setOnClickListener(new OnClickListener()  {
            @Override
            public void onClick(View v) {
                // send test packets in TCP for now
                log.append("\nCLI :: Preparing to send packets...");
                for (int i=0; i<10; i++) {
                    sendPacket("test segment " + String.valueOf(i), ip.toString(), "TCP", 5000);
                }
            }
        });
    }
}
