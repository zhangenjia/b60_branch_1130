LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := .apk
#APK名
LOCAL_MODULE := AdayoAvm
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_SRC_FILES := $(LOCAL_MODULE).apk
LOCAL_MODULE_PATH := $(TARGET_OUT)/app
LOCAL_MULTILIB := 64
LOCAL_DEX_PREOPT = false
include $(BUILD_PREBUILT)


include $(CLEAR_VARS)
# Include the sub-makefiles
include $(call all-makefiles-under,$(LOCAL_PATH))

