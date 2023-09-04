package com.example.term_project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.term_project.Model.SensorDTO;
import com.example.term_project.Parameter.SENSOR;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText edit_ip;
    private AppVariable app_variable;

    SENSOR sensor;
    SensorDTO sensorVO;

    //---------------------------------------------------
    private SocketService socketService; // SocketService 객체 선언
    private boolean isBound = false; // 서비스가 바인드되었는지 여부를 나타내는 변수
    private boolean connection_state = false;
    TextView receivedTextView; // 수신된 메시지를 보여주는 TextView
    private ServiceConnection connection = new ServiceConnection() { // 서비스 연결을 위한 ServiceConnection 객체 생성
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) { // 서비스가 연결되었을 때 호출되는 메소드
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service; // IBinder 객체를
            // SocketService.LocalBinder 객체로 캐스팅
            socketService = binder.getService(); // SocketService 객체 가져오기
            isBound = true; // 서비스가 바인드되었음을 표시
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) { // 서비스가 연결이 끊겼을 때 호출되는 메소드
            isBound = false; // 서비스가 바인드되지 않았음을 표시
        }
    };


    //---------------------------------------------------

    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.INTERNET"};
    private static final int REQUEST_CODE_PERMISSIONS = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionsIfNecessary();

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Share variables
        app_variable = (AppVariable) getApplication();
        app_variable.setSecureState("enabled");

        // Connect ID
        edit_ip = findViewById(R.id.edit_ip);
        Button btn_ip = findViewById(R.id.btn_ip);

        //--------------------------------------------
        // SocketService와 연결하기 위한 Intent 생성
        Intent intent = new Intent(this, SocketService.class);
        // SocketService와 연결
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //--------------------------------------------

        edit_ip.setText("192.168.137.193");

        // Click listener
        btn_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = edit_ip.getText().toString();
                if (!ip.equals("")) {

                    socketService.connect(ip,8080);
                    delay(100);

                    if (true) {
                        System.out.println("success");
                        Toast.makeText(MainActivity.this, "연결되었습니다.", Toast.LENGTH_SHORT).show();

                        app_variable.setIpAddress(ip);

                        String host = app_variable.getIpAddress();
                        int port = 22; // 기본 SSH 포트는 22입니다.
                        String username = "pi";
                        String password = "pi";
                        String remoteFolder = "/home/pi/C_Socket/picture"; // 라즈베리파이에 있는 원격 폴더 경로
                        String localFolder = app_variable.getPicturePath(); // 안드로이드 기기에 저장할 로컬 폴더 경로

                        app_variable.downloadAllFilesFromRemoteFolder(host, port, username, password, remoteFolder, localFolder);

                        new Thread(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                                // intent.putExtra("message", messageEditText.getText().toString()); // 페이지 전환시 데이터 넘길일이 있다면 사용
                                startActivity(intent); // SecondActivity로 이동
                            }
                        }).start(); // 새로운 스레드에서 실행

                    } else {
                        Toast.makeText(MainActivity.this, "IP가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "IP를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!arePermissionsGranted()) {
                Toast.makeText(this, "Permission(s) not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    private void sendAndReceive() {
        if (isBound) {
            // 스레드 풀에서 작업 실행
            executorService.submit(() -> {
                try {
                    // EditText에서 메시지를 가져와 SocketService의 send() 메소드 실행
                    String message = "pw"; // messageEditText에서 텍스트를 가져와서 String으로 변환 후 message 변수에 할당
                    socketService.send(message).get(); // 소켓 서비스를 통해 메시지를 전송하고, 전송 완료까지 대기한다.

                    // SocketService의 receive() 메소드 실행하여 받은 메시지를 TextView에 출력
                    String received = socketService.receive().get(); // 소켓 서비스로부터 데이터를 받아와서 received 변수에 저장한다.
//                    runOnUiThread(() -> receivedTextView.setText(received)); // UI 스레드에서 실행되도록 함. receivedTextView에 received 값을 설정하여 화면에 출력함.

                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void sendJson(JSONObject sensorData) {
        if (isBound) {
            // 스레드 풀에서 작업 실행
            executorService.submit(() -> {
                try {
                    socketService.send(String.valueOf(sensorData)).get();
//                    String received = socketService.receive().get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private boolean arePermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissionsIfNecessary() {
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            // SocketService와 연결 해제
            unbindService(connection);
            isBound = false;
        }
    }
    public void delay(int x){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {;}
            TimeUnit.MILLISECONDS.sleep(x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
