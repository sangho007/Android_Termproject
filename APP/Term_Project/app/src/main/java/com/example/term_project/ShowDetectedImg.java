package com.example.term_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.Arrays;

public class ShowDetectedImg extends AppCompatActivity {

    private AppVariable app_variable;

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

    private void loadImageFromExternalStorage(String imagePath, ImageView imageView) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Log.d("LoadImageError", "File does not exist.");
            }
        } catch (Exception e) {
            Log.e("LoadImageError", "Error loading image from external storage.", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detected_img);

        ActionBar actionBar = getSupportActionBar();
        ImageView detectedImg = findViewById(R.id.detected_img);

        app_variable = (AppVariable) getApplication();

        Intent intent = getIntent();
        if (intent != null && actionBar != null && detectedImg != null) {
            String imageName = intent.getStringExtra("image_name");

            // 포맷한 이미지 이름을 액션바에 설정합니다.
            actionBar.setTitle(formatImageName(imageName));

            // 이미지를 detectedImg 이미지뷰에 로드합니다.
            String imageUrl = app_variable.getPicturePath() + "/" + imageName;
//            System.out.println(imageUrl);
            loadImageFromExternalStorage(imageUrl, detectedImg);
        }

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료
            }
        });
    }
}
