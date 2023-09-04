#ifndef SERVO_H_
#define SERVO_H_

#include <softPwm.h>
#include <wiringPi.h>

#define servo 3

void Servo_init() {
    // wiringPiSetup();
    pinMode(servo, OUTPUT);
    softPwmCreate(servo, 0, 200);
}

void Move_N90() {
    softPwmWrite(servo, 10);  // -90 degree
    // delay(500);
}
void Move_0() {
    softPwmWrite(servo, 15);  // 0 degree
    // delay(500);
}
void Move_P90() {
    softPwmWrite(servo, 20);  // +90 degree 
    // delay(500);
}

#endif /* SERVO_H_ */
