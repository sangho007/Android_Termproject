package com.example.term_project.Model;

public class SensorDTO {
    private String led = "OFF";
    private String buzzerUsage = "False";
    private String buzzerOption = "0";
    private String motorUsage = "False";
    private String motorOption = "0";
    private String lcdUsage = "False";
    private String lcdLine1 = "";
    private String lcdLine2 = "";

    private String sound = "False";
    private String light = "False";
    private String keypadUsage = "False";
    private String keypadOption = "B";
    private String keypadNum = "asdf";
    private String camPictureUsage = "False";
    private String camPictureName = "a";
    private String camPictureContents = "0";


    public SensorDTO(){}

    public String getLed() {
        return led;
    }

    public String getBuzzerUsage() {
        return buzzerUsage;
    }

    public String getBuzzerOption() {
        return buzzerOption;
    }

    public String getMotorUsage() {
        return motorUsage;
    }

    public String getMotorOption() {
        return motorOption;
    }

    public String getLcdUsage() {
        return lcdUsage;
    }
    public String getLcdLine1(){return lcdLine1;}
    public String getLcdLine2(){return lcdLine2;}

    public String getSound() {
        return sound;
    }

    public String getLight() {
        return light;
    }

    public String getKeypadUsage() {
        return keypadUsage;
    }

    public String getKeypadOption() {
        return keypadOption;
    }

    public String getKeypadNum() {
        return keypadNum;
    }

    public String getCamPictureUsage() {
        return camPictureUsage;
    }

    public String getCamPictureName() {
        return camPictureName;
    }

    public String camPictureContents() {
        return camPictureContents;
    }

    //setter
    public void setLed(String led) {
        this.led = led;
    }

    public void setBuzzerUsage(String buzzerUsage) {
        this.buzzerUsage = buzzerUsage;
    }

    public void setBuzzerOption(String buzzerOption) {
        this.buzzerOption = buzzerOption;
    }

    public void setMotorUsage(String motorUsage) {
        this.motorUsage = motorUsage;
    }

    public void setMotorOption(String motorOption) {
        this.motorOption = motorOption;
    }

    public void setLcdUsage(String lcdUsage) {
        this.lcdUsage = lcdUsage;
    }
    public void setLcdLine1(String lcdLine1){this.lcdLine1 = lcdLine1;}
    public void setLcdLine2(String lcdLine2){this.lcdLine2 = lcdLine2;}

    public void setSound(String sound) {
        this.sound = sound;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public void setKeypadUsage(String keypadUsage) {
        this.keypadUsage = keypadUsage;
    }

    public void setKeypadOption(String keypadOption) {
        this.keypadOption = keypadOption;
    }

    public void setKeypadNum(String keypadNum) {
        this.keypadNum = keypadNum;
    }

    public void setCamPictureUsage(String camPictureUsage) {
        this.camPictureUsage = camPictureUsage;
    }

    public void setCamPictureName(String camPictureName) {
        this.camPictureName = camPictureName;
    }

    public void camPictureContents(String camPictureContents) {
        this.camPictureContents = camPictureContents;
    }
}
