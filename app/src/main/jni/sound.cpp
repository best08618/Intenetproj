#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <errno.h>
#include <com_example_jinji_internetproj_ShowIpPort.h>

JNIEXPORT jint JNICALL Java_com_example_jinji_internetproj_ShowIpPort_sPiezo( JNIEnv* env, jobject thiz, jint value )
{
   int fd,ret;
   int data = value;

   fd = open("/dev/fpga_piezo",O_WRONLY);

   if(fd < 0) return -errno;

   ret = write(fd, &data, 1);
   close(fd);

   if(ret == 1) return 0;

   return -1;
}