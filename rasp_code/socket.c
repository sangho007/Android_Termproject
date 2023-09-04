#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <json-c/json.h>
#include "socket.h"
#include "json.h"
#include "sensor.h"
#include "Buzzer.h"
#include "Keypad.h"
#include "Lcd.h"
#include "Pir.h"
#include "Servo.h"
#include <stdbool.h>

#include "sensor.c"
#include "json.c"

pthread_mutex_t g_mutex;    // mutex 선언
int g_clnt_socks[CLNT_MAX]; // 클라이언트 소켓 배열
int g_clnt_count = 0;       // 클라이언트 수
char recvMSG[BUFFSIZE];     // 수신 메시지 버퍼
sensors sensor_state;
extern int secure_state;
int flags = MSG_DONTWAIT; // DONTWAIT 플래그를 설정하여 바로 데이터를 송신

char receive_password[10];
char lcd_password[10];

int new_fd2;


int main(int args, char **argv)
{
    sensors_init();

    // password initial setting
    memset(receive_password, 0, 10);

    int sockfd, new_fd;
    struct sockaddr_in server_addr;
    uint8_t sin_size;

    /* buffer */
    int rcv_byte;
    char sendMSG[BUFFSIZE];
    char parent_recvMSG[BUFFSIZE];

    pthread_t t_thread;
    pthread_mutex_init(&g_mutex, NULL); // mutex 초기화

    int val = 1;

    sockfd = socket(AF_INET, SOCK_STREAM, 0); // 소켓 생성
    if (sockfd == -1)
    {
        perror("Server-socket() error!");
        exit(EXIT_FAILURE);
    }
    else
        printf("Server-socket() sockfd is OK...\n");

    server_addr.sin_family = AF_INET;

    server_addr.sin_port = htons(SERV_PORT); // 포트 설정

    server_addr.sin_addr.s_addr = INADDR_ANY; // IP 주소 설정

    memset(&(server_addr.sin_zero), 0, 8);

    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (char *)&val, sizeof val) < 0) // 소켓 옵션 설정
    {
        perror("setsockopt");
        close(sockfd);
        return -1;
    }

    if (bind(sockfd, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)) == -1) // 소켓에 IP와 포트 할당
    {
        perror("Server-bind() error!");
        exit(EXIT_FAILURE);
    }
    else
        printf("Server-bind() is OK...\n");

    if (listen(sockfd, BACKLOG) == -1) // 클라이언트 연결 대기
    {
        perror("listen() error!");
        exit(EXIT_FAILURE);
    }
    else{
        printf("listen() is OK...\n\n");
    }

    ClrLcd();
    lcdLoc(LINE1);
    typeln("connect wait");

    sin_size = sizeof(struct sockaddr_in);
    new_fd = accept(sockfd, (struct sockaddr *)&server_addr, &sin_size); // 클라이언트 연결 수락
    printf("accept() is OK...\n");
    pthread_create(&t_thread, NULL, clnt_connection, (void *)new_fd); // 클라이언트 연결 처리를 위한 스레드 생성

    new_fd2 = new_fd;

    if (wiringPiISR(MOTION_IN, INT_EDGE_RISING, &Pir_motion_detected) < 0) {  
        // PIR interrupt enable
        fprintf(stderr, "Unable to setup ISR: %s\n", strerror(errno));
    }


    while (1)
    {
        Recive_Keypad_data(new_fd);
    }
    

    close(new_fd);
    close(sockfd);
    exit(0);
}



void *clnt_connection(void *arg)
{
    int new_fd = (int)arg;
    char recvMSG[BUFFSIZE];
    int recv_len;


    while (1)
    {
        recv_len = recv(new_fd, recvMSG, sizeof(recvMSG), 0); // 클라이언트로부터 메시지(JSON 객체 or 일반 문자열) 수신
        if (recv_len == 0) // 수신된 메시지가 NULL인 경우
        {
            printf("FD[%d]의 연결이 끊어졌습니다.\n", new_fd);
            close(new_fd);              // 소켓 연결 종료
            pthread_exit(EXIT_SUCCESS); // 스레드 종료
        }

        // 일반 문자열 송수신 처리
        if (recvMSG[0] != '{')
        {
            printf("%s\n", recvMSG); // 일반 문자열 수신 출력

            int i, j;
            for (j = 0; recvMSG[j]; j++)
                if (recvMSG[j] == '\n')
                    break;

            for (i = 0; i < g_clnt_count; i++)
            {
                if (new_fd != g_clnt_socks[i])
                    send(g_clnt_socks[i], recvMSG, j + 1, 0); // 연결되어있는 다른 클라이언트에게 브로드캐스트
            }
            recvMSG[0] = '\0'; // 수신 메시지 버퍼 초기화
            continue;
        }
        else{
            // JSON 객체 송수신 처리
            json_object *json_obj = json_tokener_parse(recvMSG); // 메시지로부터 JSON 객체 파싱

            sensors new_sensor_data = json_parser(json_obj); // JSON 객체를 파싱하여 센서 데이터 추출
            sensor_state = operate_sensors(sensor_state,new_sensor_data); // 센서 동작 후 현재 센서 상태 업데이트 
            
            // 처리 후, 결과를 클라이언트로 전송하려면 JSON 객체를 문자열로 변환 후 send()로 전송하면 됩니다.
            json_object* response_json_obj = json_object_new_object();
            json_object* result = json_object_new_string("hello");
            json_object_object_add(response_json_obj, "result", result);
            send(new_fd, json_object_to_json_string(response_json_obj), strlen(json_object_to_json_string(response_json_obj)), flags);
            
        }
    }
}
void send_all_clnt(char *msg, int my_sock)
{
    pthread_mutex_lock(&g_mutex); // mutex lock
    for (int i = 0; i < g_clnt_count; i++)
    {
        if (g_clnt_socks[i] != my_sock) // 자신을 제외한 모든 클라이언트에게 메시지 전송
            write(g_clnt_socks[i], msg, strlen(msg) + 1);
    }
    pthread_mutex_unlock(&g_mutex); // mutex unlock
}

volatile bool interrupt_running = false;

void *Pir_interrupt_async(void *arg) {
    // 인터럽트 시작!
    interrupt_running = true;

    send_pir_interrupt_detected(); // 이 함수를 추가합니다.

    if (secure_state) {
        


	    pinMode(LIGHTSEN_OUT,INPUT); // 조도센서 핀설정
        pinMode(13,OUTPUT); // LED 핀설정
        char detector[SBUFF];
        
        sprintf(detector, "%d", digitalRead(LIGHTSEN_OUT));
        if(!strcmp(detector,"1")){
            digitalWrite(13,HIGH);
        }


        // take a picture, file name is today's date
        system("raspistill -o picture/img_$(date '+%Y_%m_%d_%H_%M_%S').jpg &");

        Threaded_Buzzer_on_3s();
        delay(7000);
        
        digitalWrite(13,LOW);
        
    }

    // 클라이언트에게 메시지 전송하는 함수 호출

    // 인터럽트 종료!
    interrupt_running = false;
    return NULL;
}

void send_pir_interrupt_detected() {
    json_object *response_json_obj = json_object_new_object();
    json_object *result = json_object_new_string("PIR Interrupt Detected");
    json_object_object_add(response_json_obj, "pir_interrupt", result);

    send(new_fd2, json_object_to_json_string(response_json_obj), strlen(json_object_to_json_string(response_json_obj)), flags);
}


// 모션 감지가 발생하면 비동기 처리 시작
void Pir_motion_detected() {
    if (!interrupt_running) {
        pthread_t thread;
        pthread_create(&thread, NULL, Pir_interrupt_async, NULL);
    }
}

int Recive_Keypad_data(int new_fd) {
    char keypad_num;
    memset(receive_password, 0, 10);
    memset(lcd_password, 0, 10);
    ClrLcd();
    lcdLoc(LINE1);
    typeln("enter Password");
    delay(100);

    for (int i = 0; i < 8; i++) {
        ClrLcd();
        lcdLoc(LINE1);
        typeln("enter Password");
        lcdLoc(LINE2);
        memset(lcd_password, '*', i);
        typeln(lcd_password);

        keypad_num = Read_Keypad_1time();  // get 1 number from keypad

        if (keypad_num == 'A') {
            i--;
            continue;
        } else if (keypad_num == 'B') {  // door close
            if (!secure_state) {
                ClrLcd();
                typeln("secure enable");
                secure_state = 1;
                delay(2000);

                json_object* response_json_obj = json_object_new_object();
                json_object* result = json_object_new_string("close");
                json_object_object_add(response_json_obj, "door", result);
                send(new_fd, json_object_to_json_string(response_json_obj), strlen(json_object_to_json_string(response_json_obj)), flags);
                return 0;
            } else {
                ClrLcd();
                typeln("unlock plz");
                delay(2000);
                return 0;
            }
        } else if (keypad_num == 'D') {  // quit input if press 'D',
            break;
        } else {
            receive_password[i] = keypad_num;
            printf("%s\n",receive_password);
        }
    }
    // 패스워드를 전송하는 코드를 작성하세요
    json_object* response_json_obj = json_object_new_object();
    json_object* result = json_object_new_string(receive_password);
    json_object_object_add(response_json_obj, "pw", result);
    send(new_fd, json_object_to_json_string(response_json_obj), strlen(json_object_to_json_string(response_json_obj)), flags);
}
