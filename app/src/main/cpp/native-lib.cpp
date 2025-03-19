#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_festival_dailypostermaker_MyApplication_getAppBaseUrl(JNIEnv *env, jclass clazz) {
    std::string APP_BASE_URL = "https://phpstack-1219077-5222678.cloudwaysapps.com/";
    return env->NewStringUTF(APP_BASE_URL.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_festival_dailypostermaker_MyApplication_getAppDecryptKey(JNIEnv *env, jclass clazz) {
    std::string APP_DECRYPT_KEY = "88dffd1d8e897e26b66581f8da2cdae9";
    return env->NewStringUTF(APP_DECRYPT_KEY.c_str());
}