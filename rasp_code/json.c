#include "./json.h"
#include <json-c/json.h>

// json_object를 파싱하여 센서 데이터를 추출하는 함수
sensors json_parser(json_object* data) {
  json_object* raw_data = json_object_object_get(data, "data");


  // LED 데이터 추출
  json_object* led_data = json_object_object_get(raw_data, "LED");

  // BUZZER 데이터 추출
  json_object* buzzer_data = json_object_object_get(raw_data, "BUZZER");
  json_object* buzzer_usage = json_object_object_get(buzzer_data, "usage");
  json_object* buzzer_option = json_object_object_get(buzzer_data, "option");

  // MOTOR 데이터 추출
  json_object* motor_data = json_object_object_get(raw_data, "MOTOR");
  json_object* motor_usage = json_object_object_get(motor_data, "usage");
  json_object* motor_option = json_object_object_get(motor_data, "option");

  // LCD 데이터 추출
  json_object* lcd_data = json_object_object_get(raw_data, "LCD");
  json_object* lcd_usage = json_object_object_get(lcd_data, "usage");
  json_object* lcd_line1 = json_object_object_get(lcd_data, "line1");
  json_object* lcd_line2 = json_object_object_get(lcd_data, "line2");

  // SOUND, LIGHT 데이터 추출
  json_object* sound_data = json_object_object_get(raw_data, "SOUND");
  json_object* light_data = json_object_object_get(raw_data, "LIGHT");

  // KEYPAD 데이터 추출
  json_object* keypad_data = json_object_object_get(raw_data, "KEYPAD");
  json_object* keypad_usage = json_object_object_get(keypad_data, "usage");
  json_object* keypad_option = json_object_object_get(keypad_data, "option");
  json_object* keypad_num = json_object_object_get(keypad_data, "num");

  // CAM_PICTURE 데이터 추출
  json_object* cam_picture_data = json_object_object_get(raw_data, "CAMPICTURE");
  json_object* cam_picture_usage = json_object_object_get(cam_picture_data, "usage");
  json_object* cam_picture_name = json_object_object_get(cam_picture_data, "name");
  json_object* cam_picture_contents = json_object_object_get(cam_picture_data, "contents");

  // 추출한 데이터를 저장할 구조체 변수 생성
  sensors sensor = {
      0,
  };

  // 데이터 저장
  sensor.led = json_object_get_string(led_data);

  sensor.buzzer_usage = json_object_get_string(buzzer_usage);
  sensor.buzzer_option = json_object_get_string(buzzer_option);

  sensor.motor_usage = json_object_get_string(motor_usage);
  sensor.motor_option = json_object_get_string(motor_option);

  sensor.lcd_usage = json_object_get_string(lcd_usage);
  sensor.lcd_line1 = json_object_get_string(lcd_line1);
  sensor.lcd_line2 = json_object_get_string(lcd_line2);

  sensor.sound = json_object_get_string(sound_data);
  sensor.light = json_object_get_string(light_data);

  sensor.keypad_usage = json_object_get_string(keypad_usage);
  sensor.keypad_option = json_object_get_string(keypad_option);
  sensor.keypad_num = json_object_get_string(keypad_num);

  sensor.cam_picture_usage = json_object_get_string(cam_picture_usage);
  sensor.cam_picture_name = json_object_get_string(cam_picture_name);
  sensor.cam_picture_contents = json_object_get_string(cam_picture_contents);

  // 추출한 센서 데이터를 반환
  return sensor;
}


// 문자열 슬라이싱 함수
char* str_slicing(char* s, int start, int end) {
  // 시작과 끝 인덱스를 이용하여 문자열의 크기를 계산
  int str_size = end - start;
  // 슬라이싱된 문자열을 저장할 포인터 변수를 동적 할당
  char* slice_str = (char*)malloc(sizeof(char) * str_size);

  // 슬라이싱된 문자열을 생성
  for (int i = 0; i < str_size; i++) {
    slice_str[i] = s[start + i];
  }

  // 슬라이싱된 문자열을 반환
  return slice_str;
}

