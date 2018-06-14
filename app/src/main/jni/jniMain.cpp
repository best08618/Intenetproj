//
// Created by samsung on 2018-06-14.
//
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <errno.h>
#include<com_example_jinji_internetproj_MainActivity.h>

JNIEXPORT jint JNICALL Java_com_example_jinji_internetproj_MainActivity_LEDControl( JNIEnv* env,jobject obj, jint data )
{
    int fd,ret;
    fd = open("/dev/led",O_WRONLY);
    if(fd < 0) return -errno;

    if(fd > 0) {
        data &= 0xff;
        ret = write(fd,&data,1);
        close(fd);
    }
    else return fd;

    if(ret == 1) {
    return 0;
    }

    return -1;
}