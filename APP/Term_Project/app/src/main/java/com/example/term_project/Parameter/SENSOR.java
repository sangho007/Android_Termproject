package com.example.term_project.Parameter;

import com.example.term_project.Model.SensorDTO;

import org.json.JSONObject;

import java.util.HashMap;

public class SENSOR {
    public JSONObject sendSensorData(SensorDTO sensorVO){
        HashMap<String, Object> headerMap = new HashMap<>();
        HashMap<String, Object> bodyMap = new HashMap<>();
        HashMap<String, Object> Buzzer = new HashMap<>();
        HashMap<String, Object> Motor = new HashMap<>();
        HashMap<String, Object> Lcd = new HashMap<>();
        HashMap<String, Object> Keypad = new HashMap<>();
        HashMap<String, Object> Campicture = new HashMap<>();

        Buzzer.put("usage", sensorVO.getBuzzerUsage());
        Buzzer.put("option", sensorVO.getBuzzerOption());

        Motor.put("usage", sensorVO.getMotorUsage());
        Motor.put("option", sensorVO.getMotorOption());

        Lcd.put("usage", sensorVO.getLcdUsage());
        Lcd.put("line1", sensorVO.getLcdLine1());
        Lcd.put("line2", sensorVO.getLcdLine2());

        Keypad.put("usage",sensorVO.getKeypadUsage());
        Keypad.put("option",sensorVO.getKeypadOption());
        Keypad.put("num",sensorVO.getKeypadNum());

        Campicture.put("usage",sensorVO.getCamPictureUsage());
        Campicture.put("name",sensorVO.getCamPictureName());
        Campicture.put("contents",sensorVO.camPictureContents());

        bodyMap.put("LED", sensorVO.getLed());
        bodyMap.put("BUZZER", Buzzer);
        bodyMap.put("MOTOR", Motor);
        bodyMap.put("LCD", Lcd);
        bodyMap.put("SOUND", sensorVO.getSound());
        bodyMap.put("LIGHT", sensorVO.getLight());
        bodyMap.put("KEYPAD", Keypad);
        bodyMap.put("CAMPICTURE", Campicture);

        headerMap.put("data", bodyMap);
        headerMap.put("status", "success");

        JSONObject jsonObject = new JSONObject(headerMap);
        return jsonObject;
    }
}
