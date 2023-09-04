/*copyright*/

#ifndef JSON_H_
#define JSON_H_

#include <json-c/json.h>

typedef struct SENSORS {
  char* led;  // LED 사용 여부

  char* buzzer_usage;  // 부저 사용 여부
  char* buzzer_option;  // 부저 옵션

  char* motor_usage;  // 모터 사용 여부
  char* motor_option;  // 모터 옵션

  char* lcd_usage;  // LCD 사용 여부
  char* lcd_line1;  // LCD 첫번째 라인 텍스트
  char* lcd_line2;  // LCD 두번째 라인 텍스트

  char* sound;  // 소리 센서 값
  char* light;  // 조도 센서 값

  char* keypad_usage; // 키패드 사용 여부
  char* keypad_option; // 키패드 옵션
  char* keypad_num; // 키패드 번호

  char* cam_picture_usage; // 카메라 사용 여부
  char* cam_picture_name; // 카메라 사진 이름
  char* cam_picture_contents; // 카메라 사진 내용 (Base64 인코딩 된 문자열)

} sensors;


// JSON 데이터를 파싱하는 함수
sensors json_parser(json_object* data);

// 문자열을 자르는 함수
char* str_slicing(char* s, int start, int end);

#endif  // JSON_H_

