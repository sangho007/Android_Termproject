package com.example.term_project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.term_project.Model.SensorDTO;
import com.example.term_project.Parameter.SENSOR;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainScreen extends AppCompatActivity implements View.OnClickListener {
    Button btn_secure_mode;
    Button btn_detected_list;
    Button btn_reset_password;

    View secure_state_rect;
    ImageView secure_state_img;
    TextView secure_state_text;

    private AppVariable app_variable;

    private SocketService socketService; // SocketService 객체 선언
    private boolean isBound = false; // 서비스가 바인드되었는지 여부를 나타내는 변수

    private Handler handler;

    SENSOR sensor;
    SensorDTO sensorVO;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            // 액션바의 제목(텍스트)를 변경합니다.
            actionBar.setTitle("라즈베리파이 보안 시스템");
        }

        Intent intent = new Intent(this, SocketService.class);
        // SocketService와 연결
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        app_variable = (AppVariable) getApplication();

        btn_secure_mode = findViewById(R.id.btn_secure_mode);
        btn_detected_list = findViewById(R.id.btn_detected_list);
        btn_reset_password = findViewById(R.id.btn_reset_password);

        secure_state_rect = findViewById(R.id.secure_state_rect);
        secure_state_img = findViewById(R.id.secure_state_img);
        secure_state_text = findViewById(R.id.secure_state_text);

        btn_secure_mode.setOnClickListener(this);
        btn_detected_list.setOnClickListener(this);
        btn_reset_password.setOnClickListener(this);


        if( app_variable.getSecureState().equals("enabled")){
            secure_state_rect.setBackgroundResource(R.drawable.secure_enabled_background);
            secure_state_text.setText("Secure Enabled");
            secure_state_img.setImageResource(R.drawable.sheild_icon);
        }

        handler = new Handler(Looper.getMainLooper());




    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_secure_mode) { // 버튼 1 이벤트 처리 코드 작성
            if (app_variable.getSecureState().equals("disabled")) {
                app_variable.setSecureState("enabled");
                secure_state_rect.setBackgroundResource(R.drawable.secure_enabled_background);
                secure_state_text.setText("Secure Enabled");
                secure_state_img.setImageResource(R.drawable.sheild_icon);
                btn_secure_mode.setText("보안모드 비활성화");
                try {
                    sensor = new SENSOR();
                    sensorVO = new SensorDTO();
                    sensorVO.setMotorUsage("true");
                    sensorVO.setMotorOption("close");
                    sensorVO.setLcdUsage("true");
                    sensorVO.setLcdLine1("Secure Enabled");
                    sensorVO.setLcdLine2("");
                    JSONObject sensorData = sensor.sendSensorData(sensorVO);
                    sendJson(sensorData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (app_variable.getSecureState().equals("enabled")) {
                app_variable.setSecureState("disabled");
                secure_state_rect.setBackgroundResource(R.drawable.secure_disabled_background);
                secure_state_text.setText("Secure Disabled");
                secure_state_img.setImageResource(R.drawable.home_icon);
                btn_secure_mode.setText("보안모드 활성화");
                try {
                    sensor = new SENSOR();
                    sensorVO = new SensorDTO();
                    sensorVO.setMotorUsage("true");
                    sensorVO.setMotorOption("open");
                    sensorVO.setLcdUsage("true");
                    sensorVO.setLcdLine1("Secure Disabled");
                    sensorVO.setLcdLine2("");
                    JSONObject sensorData = sensor.sendSensorData(sensorVO);
                    sendJson(sensorData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (id == R.id.btn_detected_list) { // 버튼 2 이벤트 처리 코드 작성
            Intent intent1 = new Intent(this, DetectedList.class);
            startActivity(intent1);
        } else if (id == R.id.btn_reset_password) { // 버튼 3 이벤트 처리 코드 작성
            Intent intent2 = new Intent(this, ResetPassword.class);
            startActivity(intent2);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 여기에 주기적으로 비교하고 싶은 작업을 넣으세요
            if( app_variable.getSecureState().equals("enabled")){
                if(!secure_state_text.getText().toString().equals("Secure Enabled")){
                    try {
                        sensor = new SENSOR();
                        sensorVO = new SensorDTO();
                        sensorVO.setMotorUsage("true");
                        sensorVO.setMotorOption("close");
                        sensorVO.setLcdUsage("true");
                        sensorVO.setLcdLine1("Secure Enabled");
                        sensorVO.setLcdLine2("");
                        JSONObject sensorData = sensor.sendSensorData(sensorVO);
                        sendJson(sensorData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                secure_state_rect.setBackgroundResource(R.drawable.secure_enabled_background);
                secure_state_text.setText("Secure Enabled");
                secure_state_img.setImageResource(R.drawable.sheild_icon);

            } else if (app_variable.getSecureState().equals("disabled")) {
                if(!secure_state_text.getText().toString().equals("Secure Disabled")){
                    sensor = new SENSOR();
                    sensorVO = new SensorDTO();
                    sensorVO.setMotorUsage("true");
                    sensorVO.setMotorOption("open");
                    sensorVO.setLcdUsage("true");
                    sensorVO.setLcdLine1("Secure Disabled");
                    sensorVO.setLcdLine2("");
                    JSONObject sensorData = sensor.sendSensorData(sensorVO);
                    sendJson(sensorData);
                }
                secure_state_rect.setBackgroundResource(R.drawable.secure_disabled_background);
                secure_state_text.setText("Secure Disabled");
                secure_state_img.setImageResource(R.drawable.home_icon);
            }

            sensor = new SENSOR();
            sensorVO = new SensorDTO();
            sensorVO.setLight("check_password");
            JSONObject sensorData = sensor.sendSensorData(sensorVO);
            sendJson(sensorData);

            // 주기적으로 실행할 작업 등록
            handler.postDelayed(this, 1000); // 1000밀리초(1초) 마다 실행
        }
    };
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    private void sendJson(JSONObject sensorData) {
        if (isBound) {
            // 스레드 풀에서 작업 실행
            executorService.submit(() -> {
                try {
                    socketService.send(String.valueOf(sensorData)).get();
                    String received = socketService.receive().get();

                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}