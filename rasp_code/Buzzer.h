#ifndef BUZZER_H_
#define BUZZER_H_

#include <softTone.h>
#include <wiringPi.h>
#include <pthread.h>

#define SPKR 0

void Buzzer_init() {
    // wiringPiSetup();
    softToneCreate(SPKR);
}

void *Buzzer_on_3s(void *arg) {
    for (int i = 0; i < 6; i++) {
        softToneWrite(SPKR, 440);
        delay(250);
        softToneWrite(SPKR, 329.63);
        delay(250);
    }
    softToneWrite(SPKR, 0);
    return NULL;
}

void *Buzzer_open(void *arg) {
    softToneWrite(SPKR, 261.63);
    delay(250);
    softToneWrite(SPKR, 293.66);
    delay(250);
    softToneWrite(SPKR, 391);
    delay(250);
    softToneWrite(SPKR, 0);
    delay(250);
    return NULL;
}

void *Buzzer_closed(void *arg) {
    softToneWrite(SPKR, 391);
    delay(250);
    softToneWrite(SPKR, 293.66);
    delay(250);
    softToneWrite(SPKR, 261.63);
    delay(250);
    softToneWrite(SPKR, 0);
    delay(250);
    return NULL;
}

void Threaded_Buzzer_on_3s()
{
    pthread_t t;
    pthread_create(&t, NULL, Buzzer_on_3s, NULL);
    pthread_detach(t);
}

void Threaded_Buzzer_open()
{
    pthread_t t;
    pthread_create(&t, NULL, Buzzer_open, NULL);
    pthread_detach(t);
}

void Threaded_Buzzer_closed()
{
    pthread_t t;
    pthread_create(&t, NULL, Buzzer_closed, NULL);
    pthread_detach(t);
}

#endif /* BUZZER_H_ */

