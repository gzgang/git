#include <stdio.h>
#include "com_lab_ugcmodule_media_ffmpeg_FFmpegNative.h"
#include "ffmpeg.h"
#include "android_log.h"

JNIEnv* jniEnv = NULL;
jclass javaFFmpegNative = NULL;
jmethodID ffmpegLogCallbackMethod = NULL;

JNIEXPORT jint JNICALL Java_com_lab_ugcmodule_media_ffmpeg_FFmpegNative_run(JNIEnv *env, jclass obj, jobjectArray commands)
{
    if(jniEnv == NULL) {
        jniEnv = env;
        ALOGI("init jniEnv");
    }

    if(javaFFmpegNative == NULL) {
        jclass tmpJclass = (*jniEnv)->FindClass(jniEnv,"com/lab/ugcmodule/media/ffmpeg/FFmpegNative");
        if(tmpJclass == NULL){
            ALOGE("init javaFFmpegNative fail");
            return -1;
        }

        javaFFmpegNative = (*jniEnv)->NewGlobalRef(jniEnv,tmpJclass);
        ALOGI("init javaFFmpegNative");
    }

   if (ffmpegLogCallbackMethod == NULL) {
        ffmpegLogCallbackMethod = (*jniEnv)->GetStaticMethodID(jniEnv, javaFFmpegNative, "ffmpegLogCallback","(Ljava/lang/String;)V");
        if (ffmpegLogCallbackMethod == NULL) {
            (*jniEnv)->DeleteLocalRef(jniEnv, javaFFmpegNative);

            ALOGE("init ffmpegLogCallbackMethod fail");
            return -1;
        }

        ALOGI("init ffmpegLogCallbackMethod");
    }


    int argc = (*env)->GetArrayLength(env, commands);
    char *argv[argc];
    jstring jsArray[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jsArray[i] = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env, jsArray[i], 0);
    }

    int result = run(argc,argv);
    for (i = 0; i < argc; ++i) {
        (*env)->ReleaseStringUTFChars(env, jsArray[i], argv[i]);
    }

//    int argc = (*env)->GetArrayLength(env, commands);
//    char *argv[argc];
//    int i;
//    for (i = 0; i < argc; i++) {
//        jstring js = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
//        argv[i] = (char*) (*env)->GetStringUTFChars(env, js, 0);
//    }
//
//    int result = run(argc, argv);

    return result;
}



JNIEXPORT void JNICALL Java_com_lab_ugcmodule_media_ffmpeg_FFmpegNative_registerCallbackForFFmpeg
  (JNIEnv *env, jclass obj) {
    /*
    if(jniEnv == NULL) {
        jniEnv = env;
    }

    if(javaFFmpegNative == NULL) {
        jclass tmpJclass = (*jniEnv)->FindClass(jniEnv,"com/lab/ugcmodule/media/ffmpeg/FFmpegNative");
        if(tmpJclass == NULL){
            return;
        }

        javaFFmpegNative = (*jniEnv)->NewGlobalRef(jniEnv,tmpJclass);
    }

   if (ffmpegLogCallbackMethod == NULL) {
        ffmpegLogCallbackMethod = (*jniEnv)->GetStaticMethodID(jniEnv, javaFFmpegNative, "ffmpegLogCallback","(Ljava/lang/String;)V");
        if (ffmpegLogCallbackMethod == NULL) {
            (*jniEnv)->DeleteLocalRef(jniEnv, javaFFmpegNative);

            return;
        }
    }
    */
  }

  void showLogForJava(char* input) {
    if(jniEnv == NULL || javaFFmpegNative == NULL || ffmpegLogCallbackMethod == NULL) {
        return;
    }

    jstring jstrMSG = NULL;
    jstrMSG =(*jniEnv)->NewStringUTF(jniEnv,input);

    (*jniEnv)->CallStaticVoidMethod(jniEnv, javaFFmpegNative, ffmpegLogCallbackMethod, jstrMSG);
    (*jniEnv)->DeleteLocalRef(jniEnv, jstrMSG);
  }