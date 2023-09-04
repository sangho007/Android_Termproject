CORSS =
CC=$(CORSS)gcc

EXE=socket

DEFINES=
LDFLAGS = -lpthread -ljson-c -ljpeg -lb64 -lz -lwiringPi
CFLAGS=-g -fno-common -fcommon -I./include $(DEFINES)

C_SRC=$(wildcard *.c)
CPP_SRC = $(wildcard *.cpp)
CC_SRC = $(wildcard *.cc)
SRC = $(C_SRC) $(CPP_SRC) $(CC_SRC)
OBJ = $(C_SRC:.c=.o) $(CC_SRC:.cc=.o) $(CPP_SRC:.cpp=.o)

defualt : $(EXE) clean

all : $(EXE)
	echo complete!!

$(EXE) : $(OBJ)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)
	strip $@
	
clean:
	rm *.o
