package com.example.lenovo.elf.util;

import android.annotation.SuppressLint;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.example.lenovo.elf.R;

import java.io.Serializable;
import java.util.List;
public class LrcView extends View implements ILrcView ,Serializable {

    public final static String TAG = "LrcView";

    /**
     * 正常歌词模式
     */
    public final static int DISPLAY_MODE_NORMAL = 0;
    /**
     * 拖动歌词模式
     */
    public final static int DISPLAY_MODE_SEEK = 1;
    /**
     * 缩放歌词模式
     */
    public final static int DISPLAY_MODE_SCALE = 2;
    /**
     * 歌词的当前展示模式
     */
    private int mDisplayMode = DISPLAY_MODE_NORMAL;

    /**
     * 歌词集合，包含所有行的歌词
     */
    private List<LrcRow> mLrcRows;
    /**
     * 最小移动的距离，当拖动歌词时如果小于该距离不做处理
     */
    private int mMinSeekFiredOffset = 10;

    /**
     * 当前高亮歌词的行数
     */
    private int mHignlightRow = 1;
    /**
     * 当前高亮歌词的字体颜色为黄色
     */
    private int mHignlightRowColor = R.color.OnlrcColor;
    /**
     * 不高亮歌词的字体颜色为白色
     */
    private int mNormalRowColor = R.color.lrcColor;

    /**
     * 拖动歌词时，在当前高亮歌词下面的一条直线的字体颜色
     **/
    private int mSeekLineColor = Color.CYAN;
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体颜色
     **/
    private int mSeekLineTextColor = Color.CYAN;
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小默认值
     **/
    private int mSeekLineTextSize = 15;
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小最小值
     **/
    private int mMinSeekLineTextSize = 13;
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小最大值
     **/
    private int mMaxSeekLineTextSize = 18;

    /**
     * 歌词字体大小默认值
     **/
    public int mLrcFontSize = 50;    // font size of lrc
    /**
     * 歌词字体大小最小值
     **/
    private int mMinLrcFontSize = 15;
    /**
     * 歌词字体大小最大值
     **/
    private int mMaxLrcFontSize = 35;

    /**
     * 两行歌词之间的间距
     **/
    private int mPaddingY = 35;
    /**
     * 拖动歌词时，在当前高亮歌词下面的一条直线的起始位置
     **/
    private int mSeekLinePaddingX = 0;

    /**
     * 拖动歌词的监听类，回调LrcViewListener类的onLrcSeeked方法
     **/
    private ILrcViewListener mLrcViewListener;

    /**
     * 当没有歌词的时候展示的内容
     **/
    private String mLoadingLrcTip = "Downloading lrc...";

    private Paint mPaint;

    public LrcView(Context context, AttributeSet attr) {
        super(context, attr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mLrcFontSize);

    }

    public void setListener(ILrcViewListener l) {
        mLrcViewListener = l;
    }

    public void setLoadingTipText(String text) {
        mLoadingLrcTip = text;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight(); // height of this view
        final int width = getWidth(); // width of this view
        //当没有歌词的时候
        if (mLrcRows == null || mLrcRows.size() == 0) {
            if (mLoadingLrcTip != null) {
                // draw tip when no lrc.
                mPaint.setColor(mHignlightRowColor);
                mPaint.setTextSize(mLrcFontSize);
                mPaint.setTextAlign(Align.CENTER);
                canvas.drawText(mLoadingLrcTip, width / 2, height / 2 - mLrcFontSize, mPaint);
            }
            return;
        }

        int rowY = 0; // vertical point of each row.
        final int rowX = width / 2;
        int rowNum = 0;
        /**
         * 分以下三步来绘制歌词：
         *
         * 	第1步：高亮地画出正在播放的那句歌词
         *	第2步：画出正在播放的那句歌词的上面可以展示出来的歌词
         *	第3步：画出正在播放的那句歌词的下面的可以展示出来的歌词
         */
        // 1、 高亮地画出正在要高亮的的那句歌词
       if (mHignlightRow>=mLrcRows.size()) {
    for (;;mHignlightRow--){
        if (mHignlightRow<mLrcRows.size()){
            break;
        }
    }
       }
        String highlightText = mLrcRows.get(mHignlightRow).content;
        int highlightRowY = height / 2 - mLrcFontSize;
        mPaint.setColor(mHignlightRowColor);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Align.CENTER);
        canvas.drawText(highlightText, rowX, highlightRowY, mPaint);


        // 上下拖动歌词的时候 画出拖动要高亮的那句歌词的时间 和 高亮的那句歌词下面的一条直线
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            // 画出高亮的那句歌词下面的一条直线
            mPaint.setColor(mSeekLineColor);
            //该直线的x坐标从0到屏幕宽度  y坐标为高亮歌词和下一行歌词中间
            canvas.drawLine(mSeekLinePaddingX, highlightRowY + mPaddingY, width - mSeekLinePaddingX, highlightRowY + mPaddingY, mPaint);

            // 画出高亮的那句歌词的时间
            mPaint.setColor(mSeekLineTextColor);
            mPaint.setTextSize(mSeekLineTextSize);
            mPaint.setTextAlign(Align.LEFT);
            canvas.drawText(mLrcRows.get(mHignlightRow).strTime, 0, highlightRowY, mPaint);
        }

        // 2、画出正在播放的那句歌词的上面可以展示出来的歌词
        mPaint.setColor(mNormalRowColor);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Align.CENTER);
        rowNum = mHignlightRow - 1;
        rowY = highlightRowY - mPaddingY - mLrcFontSize;
        //只画出正在播放的那句歌词的上一句歌词
//        if (rowY > -mLrcFontSize && rowNum >= 0) {
//            String text = mLrcRows.get(rowNum).content;
//            canvas.drawText(text, rowX, rowY, mPaint);
//        }

        //画出正在播放的那句歌词的上面所有的歌词
        while( rowY > -mLrcFontSize && rowNum >= 0){
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text, rowX, rowY, mPaint);
            rowY -=  (mPaddingY + mLrcFontSize);
            rowNum --;
        }

        // 3、画出正在播放的那句歌词的下面的可以展示出来的歌词
        rowNum = mHignlightRow + 1;
        rowY = highlightRowY + mPaddingY + mLrcFontSize;

        //只画出正在播放的那句歌词的下一句歌词
//        if (rowY < height && rowNum < mLrcRows.size()) {
//            String text2 = mLrcRows.get(rowNum).content;
//            canvas.drawText(text2, rowX, rowY, mPaint);
//        }

        //画出正在播放的那句歌词的所有下面的可以展示出来的歌词
        while( rowY < height && rowNum < mLrcRows.size()){
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text, rowX, rowY, mPaint);
            rowY += (mPaddingY + mLrcFontSize);
            rowNum ++;
        }

    }

    /**
     * 设置要高亮的歌词为第几行歌词
     *
     * @param position 要高亮的歌词行数
     * @param cb       是否是手指拖动后要高亮的歌词
     */
    public void seekLrc(int position, boolean cb) {
        if (mLrcRows == null || position < 0 || position > mLrcRows.size()) {
            return;
        }
        LrcRow lrcRow = mLrcRows.get(position);
        mHignlightRow = position;
        invalidate();
        //如果是手指拖动歌词后
        if (mLrcViewListener != null && cb) {
            //回调onLrcSeeked方法，将音乐播放器播放的位置移动到高亮歌词的位置
            mLrcViewListener.onLrcSeeked(position, lrcRow);
        }
    }

    private float mLastMotionY;
    /**
     * 第一个手指的坐标
     **/
    private PointF mPointerOneLastMotion = new PointF();
    /**
     * 第二个手指的坐标
     **/
    private PointF mPointerTwoLastMotion = new PointF();
    /**
     * 是否是第一次移动，当一个手指按下后开始移动的时候，设置为true,
     * 当第二个手指按下的时候，即两个手指同时移动的时候，设置为false
     */
    private boolean mIsFirstMove = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLrcRows == null || mLrcRows.size() == 0) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            //手指按下
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "down,mLastMotionY:" + mLastMotionY);
                mLastMotionY = event.getY();
                mIsFirstMove = true;
                invalidate();
                break;
            //手指移动
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    Log.d(TAG, "two move");
                    return true;
                }
                Log.d(TAG, "one move");
                // single pointer mode ,seek
                //如果是双指同时按下，进行歌词大小缩放，抬起其中一个手指，另外一个手指不离开屏幕地移动的话，不做任何处理
                if (mDisplayMode == DISPLAY_MODE_SCALE) {
                    //if scaling but pointer become not two ,do nothing.
                    return true;
                }
                //如果一个手指按下，在屏幕上移动的话，拖动歌词上下
                doSeek(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                //手指抬起
            case MotionEvent.ACTION_UP:
                if (mDisplayMode == DISPLAY_MODE_SEEK) {
                    //高亮手指抬起时的歌词并播放从该句歌词开始播放
                    seekLrc(mHignlightRow, true);
                }
                mDisplayMode = DISPLAY_MODE_NORMAL;
                invalidate();
                break;
        }
        return true;
    }
    private void doSeek(MotionEvent event) {
        float y = event.getY();//手指当前位置的y坐标
        float offsetY = y - mLastMotionY; //第一次按下的y坐标和目前移动手指位置的y坐标之差
        //如果移动距离小于10，不做任何处理
        if (Math.abs(offsetY) < mMinSeekFiredOffset) {
            return;
        }
        //将模式设置为拖动歌词模式
        mDisplayMode = DISPLAY_MODE_SEEK;
        int rowOffset = Math.abs((int) offsetY / mLrcFontSize); //歌词要滚动的行数

        Log.d(TAG, "move to new hightlightrow : " + mHignlightRow + " offsetY: " + offsetY + " rowOffset:" + rowOffset);

        if (offsetY < 0) {
            //手指向上移动，歌词向下滚动
            mHignlightRow += rowOffset;//设置要高亮的歌词为 当前高亮歌词 向下滚动rowOffset行后的歌词
        } else if (offsetY > 0) {
            //手指向下移动，歌词向上滚动
            mHignlightRow -= rowOffset;//设置要高亮的歌词为 当前高亮歌词 向上滚动rowOffset行后的歌词
        }
        //设置要高亮的歌词为0和mHignlightRow中的较大值，即如果mHignlightRow < 0，mHignlightRow=0
        mHignlightRow = Math.max(0, mHignlightRow);
        //设置要高亮的歌词为0和mHignlightRow中的较小值，即如果mHignlight > RowmLrcRows.size()-1，mHignlightRow=mLrcRows.size()-1
        mHignlightRow = Math.min(mHignlightRow, mLrcRows.size() - 1);
        //如果歌词要滚动的行数大于0，则重画LrcView
        if (rowOffset > 0) {
            mLastMotionY = y;
            invalidate();
        }
    }

    public void setLrc(List<LrcRow> lrcRows) {
        mLrcRows = lrcRows;
        invalidate();
    }
    public void seekLrcToTime(long time) {
        if (mLrcRows == null || mLrcRows.size() == 0) {
            return;
        }
        if (mDisplayMode != DISPLAY_MODE_NORMAL) {
            return;
        }
        Log.d(TAG, "seekLrcToTime:" + time);

        for (int i = 0; i < mLrcRows.size(); i++) {
            LrcRow current = mLrcRows.get(i);
            LrcRow next = i + 1 == mLrcRows.size() ? null : mLrcRows.get(i + 1);
            /**
             *  正在播放的时间大于current行的歌词的时间而小于next行歌词的时间， 设置要高亮的行为current行
             *  正在播放的时间大于current行的歌词，而current行为最后一句歌词时，设置要高亮的行为current行
             */
            if ((time >= current.time && next != null && time < next.time)
                    || (time > current.time && next == null)){
                seekLrc(i, false);
                return;
            }
        }
    }

}
