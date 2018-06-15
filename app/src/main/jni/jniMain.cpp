//
// Created by samsung on 2018-06-14.
//

//
// Created by samsung on 2018-06-14.
//
///////////////////////////////////////////////testmain과 연결되는 cpp 파일이다 ////////////////////////////////////
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <errno.h>
#include<com_example_jinji_internetproj_testmain.h>

//////////////////////led 를 control하는 JNI 이다////////////////////
JNIEXPORT jint JNICALL Java_com_example_jinji_internetproj_testmain_LEDControl( JNIEnv* env,jobject obj, jint data ) // data에 값을 input으로 받음
{

    int fd,ret;
    fd = open("/dev/fpga_led",O_WRONLY); // device안에 있는 fpga_led파일에 접근한다.
    if(fd < 0) return -3;

    if(fd > 0) { // 잘 접근했을 경우
        data &= 0xff; // 0xff 와 & 연산을 할경우에는 data 값에 따라 들어가게 된다.
        ret = write(fd,&data,1); // data 값을 fd 에 써준다 , fpga_led 값을 다시 써준다
        close(fd);
    }
    else return fd;

    if(ret == 1) {
    return 0;
    }

    return -1;
}

////////////////////////////////////////////////SoundJNI

JNIEXPORT jint JNICALL Java_com_example_jinji_internetproj_testmain_soundControl( JNIEnv* env, jobject thiz, jint value ) // value를 입력으로 받음.
{
   int fd,ret;
   int data = value;

   fd = open("/dev/fpga_piezo",O_WRONLY); // 디바이스의 fpga_piezo 에 접근한다

   if(fd < 0) return -errno;

   ret = write(fd, &data, 1); // 입력받은 data값을 write하여 피에조의 값을 변경한다.
   close(fd);

   if(ret == 1) return 0;

   return -1;
}

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/////////////////////////////////////////////////textLcd////////////////////////////

#define TEXTLCD_BASE            0xbc
#define TEXTLCD_COMMAND_SET     _IOW(TEXTLCD_BASE,0,int)
#define TEXTLCD_FUNCTION_SET    _IOW(TEXTLCD_BASE,1,int)
#define TEXTLCD_DISPLAY_CONTROL _IOW(TEXTLCD_BASE,2,int)
#define TEXTLCD_CURSOR_SHIFT    _IOW(TEXTLCD_BASE,3,int)
#define TEXTLCD_ENTRY_MODE_SET  _IOW(TEXTLCD_BASE,4,int)
#define TEXTLCD_RETURN_HOME     _IOW(TEXTLCD_BASE,5,int)
#define TEXTLCD_CLEAR           _IOW(TEXTLCD_BASE,6,int)
#define TEXTLCD_DD_ADDRESS      _IOW(TEXTLCD_BASE,7,int)
#define TEXTLCD_WRITE_BYTE      _IOW(TEXTLCD_BASE,8,int)

struct strcommand_varible {
        char rows;
        char nfonts;
        char display_enable;
        char cursor_enable;

        char nblink;
        char set_screen;
        char set_rightshit;
        char increase;
        char nshift;
        char pos;
        char command;
        char strlength;
        char buf[16];
};


static struct strcommand_varible strcommand;
static int initialized = 0;

void initialize()
{
	if(!initialized)
	{
		strcommand.rows = 0;
		strcommand.nfonts = 0;
		strcommand.display_enable = 1;
		strcommand.cursor_enable = 0;
		strcommand.nblink = 0;
		strcommand.set_screen = 0;
		strcommand.set_rightshit = 1;
		strcommand.increase = 1;
		strcommand.nshift = 0;
		strcommand.pos = 10;
		strcommand.command = 1;
		strcommand.strlength = 16;
		initialized = 1;
	}
}

int TextLCDIoctl(int cmd, char *buf)
{
	int fd,ret,i;


	fd = open("/dev/fpga_textlcd",O_WRONLY | O_NDELAY);
	if(fd < 0) return -errno;

	if(cmd == TEXTLCD_WRITE_BYTE) {
		ioctl(fd,TEXTLCD_DD_ADDRESS,&strcommand,32);
		for(i=0;i<strlen(buf);i++)
		{
			strcommand.buf[0] = buf[i];
			ret = ioctl(fd, cmd, &strcommand, 32);
		}
	} else {
		ret = ioctl(fd, cmd, &strcommand, 32);
	}

	close(fd);

	return ret;
}

//data를 2개 받아 LCD의 각 줄에 하나씩 보여주는 함수이다.
jint
Java_com_example_jinji_internetproj_testmain_TextLCDOut( JNIEnv* env,
					jobject thiz, jstring data0, jstring data1 )
{
	jboolean iscopy;
	char *buf0, *buf1;
	int fd,ret;

	fd = open("/dev/fpga_textlcd",O_WRONLY | O_NDELAY);
	if(fd < 0) return -errno;

	initialize();

	buf0 = (char *)(*env).GetStringUTFChars(data0,&iscopy); //JNI에서 STRING접근하는 방법이다
	buf1 = (char *)(*env).GetStringUTFChars(data1,&iscopy);

	strcommand.pos = 0;           //첫번째 string 을 첫번째 줄에 표시해준다.
	ioctl(fd,TEXTLCD_DD_ADDRESS,&strcommand,32);
	ret = write(fd,buf0,strlen(buf0));

	strcommand.pos = 40;        // 두번째 string을 두번째 줄에 표시해준다.
	ioctl(fd,TEXTLCD_DD_ADDRESS,&strcommand,32);
	ret = write(fd,buf1,strlen(buf1));

	close(fd);

	return ret;
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlWriteByte( JNIEnv* env,
					jobject thiz,  jstring data )
{
	jboolean iscopy;
	char *buf;
	int i,ret;

	buf = (char *)(*env).GetStringUTFChars(data,&iscopy);
	initialize();

	ret = TextLCDIoctl(TEXTLCD_WRITE_BYTE,buf);

	return ret;
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlPos( JNIEnv* env,
					jobject thiz,  jint pos )
{
	initialize();
	strcommand.pos = pos;
	return TextLCDIoctl(TEXTLCD_DD_ADDRESS,NULL);
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlClear( JNIEnv* env,
						jobject thiz )
{
	initialize();       // LCD 화면을 INITIALIZE 해준다
	return TextLCDIoctl(TEXTLCD_CLEAR,NULL);
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlReturnHome( JNIEnv* env,
						jobject thiz)
{
	initialize();
	return TextLCDIoctl(TEXTLCD_RETURN_HOME,NULL);
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlDisplay( JNIEnv* env,
					jobject thiz, jboolean bOn)
{
	initialize();
	if(bOn) {
		strcommand.display_enable =  0x01;
	} else {
		strcommand.display_enable =  0x00;
	}
	return TextLCDIoctl(TEXTLCD_DISPLAY_CONTROL,NULL);
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlCursor( JNIEnv* env,
					jobject thiz , jboolean bOn)
{
	initialize();
	if(bOn) {
		strcommand.cursor_enable = 0x01;
	} else  {
		strcommand.cursor_enable = 0x00;
	}

	return TextLCDIoctl(TEXTLCD_DISPLAY_CONTROL,NULL);
}

jint
Java_com_example_jinji_internetproj_testmain_IOCtlBlink( JNIEnv* env,
					jobject thiz, jboolean bOn )
{
	initialize();
	if(bOn) {
		strcommand.nblink = 0x01;
	} else {
		strcommand.nblink = 0x00;
	}
	return TextLCDIoctl(TEXTLCD_DISPLAY_CONTROL,NULL);
}

