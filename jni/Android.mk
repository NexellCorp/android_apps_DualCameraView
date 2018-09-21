LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libCGE

#*********************** CGE Library ****************************

CGE_ROOT=$(LOCAL_PATH)

CGE_INCLUDE=$(CGE_ROOT)/include

#### CGE Library headers ###########
LOCAL_C_INCLUDES := \
    $(CGE_ROOT)/interface \
    $(CGE_INCLUDE) \
    $(CGE_INCLUDE)/filters

LOCAL_SRC_FILES := \
	cge/common/cgeCommonDefine.cpp \
	cge/common/cgeGLFunctions.cpp \
	cge/common/cgeImageFilter.cpp \
	cge/common/cgeImageHandler.cpp \
	cge/common/cgeShaderFunctions.cpp \
	cge/common/cgeGlobal.cpp \
	cge/common/cgeTextureUtils.cpp \
    \
    cge/filters/cgeAdvancedEffects.cpp \
    cge/filters/cgeAdvancedEffectsCommon.cpp \
    cge/filters/cgeBilateralBlurFilter.cpp \
    cge/filters/cgeMosaicBlurFilter.cpp \
    cge/filters/cgeBeautifyFilter.cpp \
    \
    cge/filters/cgeBrightnessAdjust.cpp \
    cge/filters/cgeColorLevelAdjust.cpp \
    cge/filters/cgeContrastAdjust.cpp \
    cge/filters/cgeCurveAdjust.cpp \
    cge/filters/cgeExposureAdjust.cpp \
    cge/filters/cgeFilterBasic.cpp \
    cge/filters/cgeHueAdjust.cpp \
    cge/filters/cgeMonochromeAdjust.cpp \
    cge/filters/cgeSaturationAdjust.cpp \
    cge/filters/cgeSelectiveColorAdjust.cpp \
    cge/filters/cgeShadowHighlightAdjust.cpp \
    cge/filters/cgeSharpenBlurAdjust.cpp \
    cge/filters/cgeTiltshiftAdjust.cpp \
    cge/filters/cgeVignetteAdjust.cpp \
    cge/filters/cgeWhiteBalanceAdjust.cpp \
    cge/filters/cgeColorBalanceAdjust.cpp \
    cge/filters/cgeLookupFilter.cpp \
    \
    cge/filters/cgeBlendFilter.cpp \
    \
    cge/filters/cgeDataParsingEngine.cpp \
    cge/filters/cgeMultipleEffects.cpp \
    cge/filters/cgeMultipleEffectsCommon.cpp \
    \
    cge/filters/cgeHazeFilter.cpp \
    cge/filters/cgePolarPixellateFilter.cpp \
    cge/filters/cgePolkaDotFilter.cpp \
    cge/filters/cgeHalftoneFilter.cpp \
    cge/filters/cgeEdgeFilter.cpp \
    cge/filters/cgeEmbossFilter.cpp \
    cge/filters/cgeCrosshatchFilter.cpp \
    cge/filters/cgeLiquidationFilter.cpp \
    cge/filters/cgeRandomBlurFilter.cpp \
    cge/filters/cgeMinValueFilter.cpp \
    cge/filters/cgeMaxValueFilter.cpp \
    cge/filters/cgeSketchFilter.cpp \
    cge/filters/cgeLerpblurFilter.cpp \
    \
    cge/filters/cgeDynamicFilters.cpp \
    cge/filters/cgeDynamicWaveFilter.cpp \
    cge/filters/cgeMotionFlowFilter.cpp \
    cge/filters/cgeColorMappingFilter.cpp \
    cge/extends/cgeThread.cpp \
    \
	interface/cgeNativeLibrary.cpp \
    interface/cgeFFmpegNativeLibrary.cpp \
    interface/cgeSharedGLContext.cpp \
    interface/cgeFrameRenderer.cpp \
    interface/cgeFrameRendererWrapper.cpp \
    interface/cgeFrameRecorder.cpp \
    interface/cgeFrameRecorderWrapper.cpp \
    interface/cgeVideoEncoder.cpp \
    interface/cgeUtilFunctions.cpp \
    interface/cgeVideoDecoder.cpp \
    interface/cgeVideoPlayer.cpp \
    interface/cgeImageHandlerAndroid.cpp \
    interface/cgeImageHandlerWrapper.cpp

LOCAL_CPPFLAGS := -frtti -std=gnu++11
#LOCAL_LDLIBS :=  -llog -lEGL -lGLESv2 -ljnigraphics -latomic
LOCAL_LDLIBS :=  -llog -lEGL -lGLESv2 -ljnigraphics
LOCAL_CFLAGS := \
    -DDEBUG -DANDROID_NDK -DCGE_LOG_TAG=\"libCGE\" \
	-DCGE_TEXTURE_PREMULTIPLIED=1 -D__STDC_CONSTANT_MACROS -D_CGE_DISABLE_GLOBALCONTEXT_ \
    -O3 -ffast-math -D_CGE_ONLY_FILTERS_

include $(BUILD_SHARED_LIBRARY)
