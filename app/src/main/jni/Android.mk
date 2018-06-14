LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := jniExample
LOCAL_SRC_FILES := jniMain.cpp
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := soundExample
LOCAL_SRC_FILES := sound.cpp
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := jnipiezo
LOCAL_SRC_FILES := piezo.cpp
LOCAL_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := soundExample
include $(BUILD_SHARED_LIBRARY)


