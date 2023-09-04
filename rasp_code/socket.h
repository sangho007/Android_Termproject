#ifndef __SOCKET_H__
#define __SOCKET_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <pthread.h>
#include "sensor.h"

#define SERV_PORT 8080
#define BACKLOG 10
#define BUFFSIZE 512
#define CLNT_MAX 2

#define SBUFF 3
#define LIGHTSEN_OUT 12

#define INIT_MSG "Socket Connected!"

void *clnt_connection(void *arg); 
void send_all_clnt(char *msg, int my_sock);

void Pir_motion_detected();
void *Pir_interrupt_async(void *arg);
void send_pir_interrupt_detected();
int Recive_Keypad_data(int);

#endif

