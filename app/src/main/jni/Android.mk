LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
APP_ABI := all
LOCAL_MODULE := jniExample
LOCAL_SRC_FILES := jniMain.cpp
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

