#ifndef PIR_H_
#define PIR_H_

#pragma once

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <wiringPi.h>

#define MOTION_IN 2

volatile int eventCounter;
volatile unsigned char humandetect;
volatile int counter;


void Detect_human_1time() {
    if (humandetect == 1) {
        printf("Detect %d\n", eventCounter);
        humandetect = 0;
        while (digitalRead(MOTION_IN)) {
            printf("high %d \n", counter++);
            delay(1000);
        }
        counter = 0;
    } else {
        printf(" No detect\n");
    }
    // eventCounter = 0;
    delay(500);  // wait 1 second
}

void Pir_init() {
    eventCounter = 0;
    humandetect = 0;
    counter = 0;  
}
#endif /* PIR_H_ */
