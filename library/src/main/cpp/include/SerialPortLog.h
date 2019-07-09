//
// Created by frey on 2018/6/4.
//

#ifndef SERIALPORTHELPER_MYLOG_H
#define SERIALPORTHELPER_MYLOG_H


#include <android/log.h>
// Log标记
#define  LOG_TAG "SerialPortLog"
// 各个优先级的宏定义
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_write(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)

#endif //TESTJNI_MYLOG_H