#pragma once
#include<opencv.hpp>
#include<iostream>
using namespace std;
using namespace cv;

class DealImage
{
public:
	DealImage();
	~DealImage();
	/// <summary>
	/// 处理
	/// </summary>
	/// <param name="src">原图bgr</param>
	/// <param name="out">输出</param>
	/// <param name="fxy">缩小比例 默认为0</param>
	/// <returns></returns>
	static	bool MyDeal(const Mat& src, Mat& out, float fxy = 1.0);
private:
	/**
	水平链接
	*/
	static	void RLSA_H(const Mat& src, Mat& out, int hor_thresh);

	/**
	垂直链接
	*/
	static	void RLSA_V(const Mat& src, Mat& out, int hor_thresh);

	//孔洞填充
	static	void fillHole(const Mat srcimage, Mat& dstimage);


};

