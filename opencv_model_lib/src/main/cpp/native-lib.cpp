#include <jni.h>
#include <string>
#include<opencv.hpp>
#include<iostream>
#include "DealImage.h"
using namespace cv;
using namespace std;


extern "C"
JNIEXPORT jint JNICALL
Java_JavaNavtive_Config_nativa_1Deal(JNIEnv *env, jclass type, jlong src, jlong dst) {
    Mat srcImg = (*((Mat *) src)).clone();
   if(srcImg.channels()==4)
     cvtColor(srcImg,srcImg,CV_RGBA2BGR);
    else if(srcImg.channels()==3)
       cvtColor(srcImg,srcImg,CV_RGB2BGR);

//    Mat bMat;
//    medianBlur(srcImg, bMat, 7);//中值滤波
    Mat res;
    int s=DealImage::MyDeal(srcImg, res,0.2);//检测

    if(srcImg.channels()==4)
        cvtColor(res,res,CV_BGR2RGBA);
    else if(srcImg.channels()==3)
        cvtColor(res,res,CV_BGR2RGB);
    (*((Mat *) dst))=res.clone();

    return s;

}