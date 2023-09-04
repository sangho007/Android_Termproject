#include "sensor.h"

#include "Buzzer.h"
#include "Keypad.h"
#include "Lcd.h"
#include "Pir.h"
#include "Servo.h"

extern int secure_state = 1;

void sensors_init(){
    // wiringpi initial setting
    if (wiringPiSetup() == -1) exit(1);

    // HW initial setting
    Buzzer_init();
    Lcd_init();
    Pir_init();
    Servo_init();
    Keypad_init();

    Move_N90();
}

sensors operate_sensors(sensors sensor_state, sensors new_state) { //LCD

    
    if(!strcmp(new_state.motor_usage,"true")){
        if(!strcmp(new_state.motor_option,"open")){
            Move_P90();
            Threaded_Buzzer_open();
            printf("motor_open\n");
            secure_state = 0;
        }
        else if (!strcmp(new_state.motor_option,"close")){
            Move_N90();
            Threaded_Buzzer_closed(); 
            printf("motor_close\n");
            secure_state = 1;
        }
    }

    if(!strcmp(new_state.lcd_usage,"true")){
        ClrLcd();
        lcdLoc(LINE1);
        typeln(new_state.lcd_line1);
        lcdLoc(LINE2);
        typeln(new_state.lcd_line2);    
    }



    return new_state;
}
