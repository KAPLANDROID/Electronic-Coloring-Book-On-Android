# In order to compile your application under cygwin
# you need to define NDK_USE_CYGPATH=1 before calling ndk-build

USER_LOCAL_PATH:=$(LOCAL_PATH)
LOCAL_PATH:=$(subst ?,,$(firstword ?$(subst \, ,$(subst /, ,$(call my-dir)))))

OPENCV_THIS_DIR:=$(patsubst $(LOCAL_PATH)\\%,%,$(patsubst $(LOCAL_PATH)/%,%,$(call my-dir)))
OPENCV_LIBS_DIR:=$(OPENCV_THIS_DIR)/../..
OPENCV_BASEDIR:=
OPENCV_LOCAL_C_INCLUDES:="$(LOCAL_PATH)/$(OPENCV_THIS_DIR)/../../include/opencv" "$(LOCAL_PATH)/$(OPENCV_THIS_DIR)/../../include"

OPENCV_MODULES := contrib calib3d objdetect features2d video imgproc highgui ml legacy flann core

ifeq (${OPENCV_CAMERA_MODULES},off)
	OPENCV_CAMERA_MODULES:=
else
	OPENCV_CAMERA_MODULES:= native_camera_r2.3.3 native_camera_r2.2.2
endif

OPENCV_LIB_TYPE:=STATIC

ifeq ($(OPENCV_LIB_TYPE),SHARED)
	OPENCV_LIB_SUFFIX:=so
	OPENCV_EXTRA_COMPONENTS:=
else
	OPENCV_LIB_SUFFIX:=a
	ifeq (ON,ON)
		OPENCV_MODULES+= androidcamera
	endif
	OPENCV_EXTRA_COMPONENTS:=libjpeg libpng libtiff libjasper zlib
endif

define add_opencv_module
	include $(CLEAR_VARS)
	LOCAL_MODULE:=opencv_$1
	LOCAL_SRC_FILES:=$(OPENCV_LIBS_DIR)/libs/$(TARGET_ARCH_ABI)/libopencv_$1.$(OPENCV_LIB_SUFFIX)
	include $(PREBUILT_STATIC_LIBRARY)
endef

define add_opencv_extra_component
	include $(CLEAR_VARS)
	LOCAL_MODULE:=$1
	LOCAL_SRC_FILES:=$(OPENCV_THIS_DIR)/3rdparty/libs/$(TARGET_ARCH_ABI)/lib$1.a
	include $(PREBUILT_STATIC_LIBRARY)
endef

define add_opencv_camera_module
	include $(CLEAR_VARS)
	LOCAL_MODULE:=$1
	LOCAL_SRC_FILES:=$(OPENCV_LIBS_DIR)/libs/$(TARGET_ARCH_ABI)/lib$1.so
	include $(PREBUILT_SHARED_LIBRARY)
endef

$(foreach module,$(OPENCV_MODULES),$(eval $(call add_opencv_module,$(module))))
$(foreach module,$(OPENCV_EXTRA_COMPONENTS),$(eval $(call add_opencv_extra_component,$(module))))
$(foreach module,$(OPENCV_CAMERA_MODULES),$(eval $(call add_opencv_camera_module,$(module))))

ifneq ($(OPENCV_BASEDIR),)
	OPENCV_LOCAL_C_INCLUDES += $(foreach mod, $(OPENCV_MODULES), $(OPENCV_BASEDIR)/modules/$(mod)/include)
endif

ifeq ($(OPENCV_LIB_TYPE),STATIC)
	OPENCV_LOCAL_LIBRARIES += $(foreach mod, $(OPENCV_MODULES), opencv_$(mod))
endif

OPENCV_LOCAL_LIBRARIES += $(OPENCV_EXTRA_COMPONENTS)
OPENCV_LOCAL_CFLAGS := -fPIC -DANDROID -fsigned-char

include $(CLEAR_VARS)
LOCAL_C_INCLUDES         += $(OPENCV_LOCAL_C_INCLUDES)
LOCAL_STATIC_LIBRARIES   += $(OPENCV_LOCAL_LIBRARIES)
LOCAL_CFLAGS             += $(OPENCV_LOCAL_CFLAGS)

LOCAL_PATH:=$(USER_LOCAL_PATH)
