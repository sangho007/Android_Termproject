#ifndef KEYPAD_H_
#define KEYPAD_H_

#include <stdio.h>
#include <string.h>
#include <wiringPi.h>

unsigned char row_pins[4];
unsigned char col_pins[4];
unsigned char key_code[16];

void Keypad_init() {
    unsigned char row[4] = {25, 24, 23, 22};
    unsigned char col[4] = {29, 28, 27, 26};

    unsigned char key[16] = {'1', '2', '3', 'A', '4', '5', '6', 'B',
                             '7', '8', '9', 'C', 'D', '0', 'E', 'F'};

    memcpy(row_pins, row, sizeof(char) * 4);
    memcpy(col_pins, col, sizeof(char) * 4);
    memcpy(key_code, key, sizeof(char) * 16);
}

unsigned char Read_Keypad_1time() {
    int i, j;
    unsigned char idx, scan;

    for (i = 0; i < 4; i++) {
        pinMode(row_pins[i], OUTPUT);
        digitalWrite(row_pins[i], HIGH);
        pinMode(col_pins[i], INPUT);
        pullUpDnControl(col_pins[i], PUD_UP);
    }

    while (1) {
        for (i = 0, idx = 0, scan = 0xff; i < 4; i++) {
            digitalWrite(row_pins[i], LOW);
            delay(10);
            for (j = 0; j < 4; j++, idx++) {
                if (digitalRead(col_pins[j]) == LOW) {
                    while (digitalRead(col_pins[j]) == LOW)
                        ;  // wait until switch is off

                    scan = idx;
                    if ((scan != 0xff) && (scan != 11) && ((scan != 14)) &&
                        (scan != 15)) {
                        //printf("%c\n", key_code[scan]);
                        return key_code[scan];
                    }
                }
            }
            digitalWrite(row_pins[i], HIGH);
        };
    }
}

#endif /* KEYPAD_H_ */
