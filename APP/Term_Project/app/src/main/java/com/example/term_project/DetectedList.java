package com.example.term_project;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.term_project.Model.SensorDTO;
import com.example.term_project.Parameter.SENSOR;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DetectedList extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    private SocketService socketService; // SocketService 객체 선언
    private boolean isBound = false; // 서비스가 바인드되었는지 여부를 나타내는 변수

    private Handler handler;

    SENSOR sensor;
    SensorDTO sensorVO;

    TextView secure_state_text;

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


    private AppVariable app_variable;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected_list);

        Intent intent = new Intent(this, SocketService.class);
        // SocketService와 연결
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 액션바의 제목(텍스트)를 변경합니다.
            actionBar.setTitle("감지 내역 조회");
        }

        app_variable = (AppVariable) getApplication();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> imageNames = getImageNames();
        Collections.sort(imageNames, Collections.reverseOrder()); // 이미지 이름을 기준으로 내림차순 정렬
        imageAdapter = new ImageAdapter(imageNames);
        recyclerView.setAdapter(imageAdapter);
        secure_state_text = (TextView) findViewById(R.id.secure_state_text);

        handler = new Handler(Looper.getMainLooper());

        String host = app_variable.getIpAddress();
        int port = 22; // 기본 SSH 포트는 22입니다.
        String username = "pi";
        String password = "pi";
        String remoteFolder = "/home/pi/C_Socket/picture"; // 라즈베리파이에 있는 원격 폴더 경로
        String localFolder = app_variable.getPicturePath(); // 안드로이드 기기에 저장할 로컬 폴더 경로

        app_variable.downloadAllFilesFromRemoteFolder(host, port, username, password, remoteFolder, localFolder);
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

    private List<String> getImageNames() {
        List<String> names = new ArrayList<>();
        File imageDir = new File(app_variable.getPicturePath());
        if (imageDir.exists() && imageDir.isDirectory()) {
            for (File imageFile : imageDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png");
                }
            })) {
                names.add(imageFile.getName());
            }
        }
        return names;
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        List<String> imageNames;

        public ImageAdapter(List<String> imageNames) {
            this.imageNames = imageNames;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout, parent, false);
            return new ImageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            final String imageName = imageNames.get(position);
            holder.imageTitle.setText(formatImageName(imageName));
            holder.viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DetectedList.this, ShowDetectedImg.class);
                    intent.putExtra("image_name", imageName);
                    startActivity(intent);
                }
            });
        }
        private String formatImageName(String imageName) {
            if (!imageName.startsWith("img_") || !imageName.endsWith(".jpg")) {
                Log.d("ImageNameError", "Invalid image name: " + imageName);
                return imageName;
            }

            String nameWithoutExtension = imageName.substring(4, imageName.length() - 4);
            String[] nameComponents = nameWithoutExtension.split("_");
            if (nameComponents.length != 6) {
                Log.d("ImageNameError", "Invalid image name components: " + Arrays.toString(nameComponents));
                return imageName;
            }

            String formattedName = String.format("%s년 %s월 %s일 %s시 %s분 %s초",
                    nameComponents[0], nameComponents[1], nameComponents[2], nameComponents[3], nameComponents[4], nameComponents[5]);
            return formattedName;
        }

        @Override
        public int getItemCount() {
            return imageNames.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            TextView imageTitle;
            Button viewButton;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageTitle = itemView.findViewById(R.id.detected_image_title);
                viewButton = itemView.findViewById(R.id.btn_view_detected_img);
            }
        }
    }
}
