package com.example.term_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPassword extends AppCompatActivity {
    EditText current_pw;
    EditText new_pw;
    EditText check_pw;
    Button btn_save;
    private AppVariable app_variable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            // 액션바의 제목(텍스트)를 변경합니다.
            actionBar.setTitle("비밀번호 재설정");
        }

        app_variable = (AppVariable) getApplication();

        current_pw = (EditText) findViewById(R.id.current_password);
        new_pw = (EditText) findViewById(R.id.new_password);
        check_pw = (EditText) findViewById(R.id.check_password);
        btn_save = (Button) findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!current_pw.getText().toString().equals("")){
                    if (current_pw.getText().toString().equals(app_variable.getPassword())) {
                        if(!new_pw.getText().toString().equals("")) {
                            if (new_pw.getText().toString().equals(check_pw.getText().toString())) {
                                app_variable.setPassword(new_pw.getText().toString());
                                finish();
                            } else {
                                Toast.makeText(ResetPassword.this, "새로운 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(ResetPassword.this, "새로운 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPassword.this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ResetPassword.this, "현재 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}