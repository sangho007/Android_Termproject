package com.example.term_project;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.term_project.Model.SensorDTO;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class AppVariable extends Application {
    private static final String PREFERENCES_NAME = "settings";
    private static final String KEY_IP_ADDRESS = "ipAddress";
    private static final String KEY_SECURE_STATE = "secureState";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PICTURE_PATH = "picturePath";
    private SharedPreferences sharedPreferences;
    private SensorDTO sensorDTO;





    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        // 초기값 지정 (필요한 경우)
        if (!sharedPreferences.contains(KEY_IP_ADDRESS)) {
            setIpAddress("192.168.1.1");
        }
        if (!sharedPreferences.contains(KEY_SECURE_STATE)) {
            setSecureState("enabled");
        }
        if (!sharedPreferences.contains(KEY_PASSWORD)) {
            setPassword("0000");
        }
        if (!sharedPreferences.contains(KEY_PICTURE_PATH)) {
            File dir = createDirectoryForPictures();
            if (dir != null) {
                setPicturePath(dir.getAbsolutePath());
            }
        }


    }




    public SensorDTO getSensorDTO() {
        return sensorDTO;
    }

    public void setSensorDTO(SensorDTO sensorData) {
        this.sensorDTO = sensorData;
    }

    public String getIpAddress() {
        return sharedPreferences.getString(KEY_IP_ADDRESS, null);
    }

    public void setIpAddress(String ipAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_IP_ADDRESS, ipAddress);
        editor.apply();
    }

    public String getSecureState() {
        return sharedPreferences.getString(KEY_SECURE_STATE, null);
    }

    public void setSecureState(String secureState) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SECURE_STATE, secureState);
        editor.apply();
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public String getPicturePath() {
        return sharedPreferences.getString(KEY_PICTURE_PATH, null);
    }

    public void setPicturePath(String picturePath) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PICTURE_PATH, picturePath);
        editor.apply();
    }
    public File createDirectoryForPictures() {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AppPictures");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("AppPictures", "Directory not created");
                return null;
            }
        }
        return storageDir;
    }

    public void showNotification(String title, String content) {
        int notificationId = 1;
        String channelId = "your_channel_id";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Channel Description");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, DetectedList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }

    public void connectToFTP() {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(getIpAddress(), 21);
            System.out.println("하이");
            if (ftpClient.login("pi", "pi")) {
                String remoteDirectory = "/home/pi/C_Socket";
                String localDirectory = getPicturePath();
                FTPFile[] ftpFiles = ftpClient.listFiles(remoteDirectory);

                for (FTPFile file : ftpFiles) {
                    System.out.println(file);
                    if (file.isFile()) {
                        // 파일을 안드로이드 폰으로 다운로드하는 로직을 작성하십시오.
                        String remoteFile = remoteDirectory + "/" + file.getName();
                        File localFile = new File(localDirectory, file.getName());

                        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
                            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
                            if (success) {
                                System.out.println("Successfully downloaded: " + file.getName());
                            } else {
                                System.out.println("Download failed: " + file.getName());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                ftpClient.logout();
                ftpClient.disconnect();
            } else {
                // 로그인 실패 처리
                System.out.println("로긴실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 연결 실패 처리
        }
    }

    public void downloadAllFilesFromRemoteFolder(final String host, final int port, final String username, final String password, final String remoteFolder, final String localFolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSch jsch = new JSch();
                    Session session = jsch.getSession(username, host, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();

                    ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                    channelSftp.connect();

                    // 원격 폴더에서 파일 목록을 가져옵니다.
                    Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteFolder);

                    // 파일 목록에 있는 각 파일을 다운로드합니다.
                    for (ChannelSftp.LsEntry file : files) {
                        if (!file.getAttrs().isDir()) { // 디렉토리가 아니면 다운로드합니다.
                            String remoteFile = remoteFolder + "/" + file.getFilename();
                            String localFile = localFolder + "/" + file.getFilename();
                            channelSftp.get(remoteFile, localFile);
                        }
                    }

                    channelSftp.disconnect();
                    session.disconnect();
                } catch (JSchException | SftpException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



}
