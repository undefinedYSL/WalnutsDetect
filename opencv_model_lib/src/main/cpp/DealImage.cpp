#include "DealImage.h"



DealImage::DealImage()
{
}


DealImage::~DealImage()
{
}

void DealImage::RLSA_H(const Mat& src, Mat& out, int hor_thresh)
{
	out = src.clone();
	bool one_flag = false;
	int zeros_count = 0;
	for (int i = 0; i < out.rows; i++)
	{
		one_flag = false;
		for (int j = 0; j < out.cols; j++) {
			if (out.at<uchar>(i, j) == 255) {
				if (one_flag) {
					if (zeros_count <= hor_thresh) {
						int k = j - zeros_count;
						k = k > 0 ? k : 0;
						for (; k < j; k++)
							out.at<uchar>(i, k) = 255;

					}
					else
						one_flag = false;
				}
				zeros_count = 0;
				one_flag = true;
			}
			else {
				if (one_flag)
					zeros_count = zeros_count + 1;

			}
		}
	}
}

void DealImage::RLSA_V(const Mat& src, Mat& out, int hor_thresh)
{
	out = src.clone();
	bool one_flag = false;
	int zeros_count = 0;

	for (int j = 0; j < out.cols; j++) {
		one_flag = false;
		for (int i = 0; i < out.rows; i++)
		{
			if (out.at<uchar>(i, j) == 255) {
				if (one_flag) {
					if (zeros_count <= hor_thresh) {
						int k = i - zeros_count;
						k = k > 0 ? k : 0;
						for (; k < i; k++)
							out.at<uchar>(k, j) = 255;
					}
					else
						one_flag = false;
				}
				one_flag = true;
				zeros_count = 0;
			}
			else {
				if (one_flag)
					zeros_count = zeros_count + 1;
			}
		}
	}
}

void DealImage::fillHole(const Mat srcimage, Mat& dstimage)
{
	Size m_Size = srcimage.size();
	Mat temimage = Mat::zeros(m_Size.height + 2, m_Size.width + 2, srcimage.type());//延展图像
	srcimage.copyTo(temimage(Range(1, m_Size.height + 1), Range(1, m_Size.width + 1)));
	floodFill(temimage, Point(0, 0), Scalar(255));
	Mat cutImg;//裁剪延展的图像
	temimage(Range(1, m_Size.height + 1), Range(1, m_Size.width + 1)).copyTo(cutImg);
	dstimage = srcimage | (~cutImg);
}


bool DealImage::MyDeal(const Mat& src, Mat& out, float fxy)
{
	out = src.clone();
	Mat img;
	cv::resize(src, img, Size(), fxy, fxy);
    //  中值滤波
	medianBlur(img, img, 7);

	int spatialRad = 50;  //空间窗口大小
	int colorRad = 15;   //色彩窗口大小
	int maxPyrLevel = 5;  //金字塔层数
	Mat res;
	pyrMeanShiftFiltering(img, res, spatialRad, colorRad, maxPyrLevel); //色彩聚类平滑滤波
	//转灰度
	Mat gray;
	cvtColor(img, gray, COLOR_BGR2GRAY);
	//二值化
	Mat thImg;
	threshold(gray, thImg, 0, 255, THRESH_BINARY_INV | THRESH_OTSU);//OTSU 阈值和取反
	RLSA_H(thImg, thImg, 25);//水平链接
	RLSA_V(thImg, thImg, 25);//垂直链接
	fillHole(thImg, thImg);//空洞填充
	double th_arr = countNonZero(thImg);


	RNG rng = theRNG();
	Mat mask(res.rows + 2, res.cols + 2, CV_8UC1, Scalar::all(0));  //掩模
	int k = 1;

	//洪水填充
	Mat tempRes;
	res.convertTo(tempRes, CV_32S);
	vector<int> indexs;
	for (int y = 0; y < res.rows; y++)
	{
		for (int x = 0; x < res.cols; x++)
		{
			if (mask.at<uchar>(y + 1, x + 1) == 0)  //非0处即为1，表示已经经过填充，不再处理
			{
				Scalar newVal1(k, k, k);
				Rect rect;
				int area = floodFill(tempRes, mask, Point(x, y), newVal1, &rect, Scalar::all(5), Scalar::all(5)); //执行漫水填充

				if (area > 200)
				{
					indexs.push_back(k);

					//					rectangle(res, rect, Scalar(255, 0, 255), 5);
				}
				k++;

			}
		}
	}


	Mat mask2;
	cv::extractChannel(tempRes, mask2, 0);

	//背景过滤
	vector<int> t_indexs;
	int maxIndex = 0;
	double maxArr = 0;
	for (int i = 0; i < indexs.size(); i++) {
		int t = indexs[i];

		//找到面积最大的地方
		double arr1 = countNonZero(mask2 == t&thImg);
		double arr2 = countNonZero(mask2 == t);
		double rate = arr1 /arr2;

		if (arr1>100 && rate>0.6)//记录下主体地方
		{
			t_indexs.push_back(t);

		}
		if (maxArr < arr1) {
			maxArr = arr1;
			maxIndex = t;
		}
	}

	Scalar newVal(rng(256), rng(256), rng(256));
	Mat mask_targ = mask2 == maxIndex;//获取当前的mask
	RLSA_H(mask_targ, mask_targ, 25);//水平链接
	RLSA_V(mask_targ, mask_targ, 25);//垂直链接
	fillHole(mask_targ, mask_targ);//空洞填充


	//大轮廓
	Mat maskTemp = mask_targ.clone();
	cv::resize(maskTemp, maskTemp, Size(), 1 / fxy, 1 / fxy);
	threshold(maskTemp, maskTemp, 0, 255,  THRESH_OTSU);//OTSU 阈值和取反
	vector<vector<Point>> countous;
	findContours(maskTemp, countous, RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
	//画轮廓
	if (countous.size()>0)
		drawContours(out, countous, 0, Scalar(0, 0, 0), 15);

	//损坏查找
	if (t_indexs.size() <= 1)
		return false;

	Mat mask3 = Mat::zeros(mask2.size(), CV_8UC1);
	for (int i = 0; i < t_indexs.size(); i++) {
		int t = t_indexs[i];

		double arr2 = countNonZero(mask2 == t&mask_targ);
		if (t != maxIndex&&arr2>100)
		{
			mask3.setTo(Scalar::all(255), mask2 == t);
		}

	}
	RLSA_H(mask3, mask3, 3);//水平链接
	RLSA_V(mask3, mask3, 3);//垂直链接
	fillHole(mask3, mask3);//空洞填充


	//缺陷
	maskTemp = mask3.clone();
	cv::resize(maskTemp, maskTemp, Size(), 1 / fxy, 1 / fxy);
	threshold(maskTemp, maskTemp, 0, 255, THRESH_OTSU);//OTSU 阈值和取反
	findContours(maskTemp, countous, RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
	for (int i = 0; i < countous.size(); i++) {
		drawContours(out, countous, i, Scalar(0, 0, 255), 12);
	}
//	cv::resize(img, out, Size(), 1 / fxy, 1 / fxy);
//	out = img.clone();
	return true;
}
