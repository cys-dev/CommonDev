package com.cys.common.widget.ptrpullrefreshlayout.indicator;

import android.graphics.PointF;
import android.util.Log;

public class PtrIndicator {
    public static final int POS_START = 0;
    protected int mOffsetToRefresh = 0;
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;
    private float mOffsetY;
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mHeaderHeight;
    private int mPressedPos = 0;

    private float mRatioOfHeaderHeightToRefresh = 1.0F;
    private float mResistance = 1.5F;
    private float mAppendResistance = 1.2F;
    private boolean mIsUnderTouch = false;
    private int mOffsetToKeepHeaderWhileLoading = -1;

    private int mRefreshCompleteY = 0;

    private float maxDistance = 1.25F;
    private float maxDragDistance;

    public boolean isUnderTouch() {
        return this.mIsUnderTouch;
    }


    public float getResistance() {
        return this.mResistance;
    }

    public void setResistance(float resistance) {
        this.mResistance = resistance;
    }


    public void onRelease() {
        this.mIsUnderTouch = false;
    }


    public void onUIRefreshComplete() {
        this.mRefreshCompleteY = this.mCurrentPos;
    }


    public boolean goDownCrossFinishPosition() {
        return (this.mCurrentPos >= this.mRefreshCompleteY);
    }


    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        if (getCurrentPosY() >= this.maxDragDistance && offsetY > 0.0F) {
            setOffset(offsetX, 0.0F);
            return;
        }
        float r = 1.2F;
        try {
            r = this.mResistance + (this.mCurrentPos / this.mOffsetToRefresh) * this.mAppendResistance;
        } catch (ArithmeticException ae) {
            ae.printStackTrace();
            Log.e("PtrIndicator", "!!!beginAutoRefresh(long duration) 调用时机不对,应该在View layout完后调用!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        float changeY = offsetY / r;
        if (this.mCurrentPos + changeY > this.maxDragDistance) {
            changeY = this.maxDragDistance - this.mCurrentPos;
        }
        setOffset(offsetX, changeY);
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        this.mRatioOfHeaderHeightToRefresh = ratio;
        this.mOffsetToRefresh = (int) (this.mHeaderHeight * ratio);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return this.mRatioOfHeaderHeightToRefresh;
    }

    public int getOffsetToRefresh() {
        return this.mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        if (offset > 0 && this.mHeaderHeight > 0) {
            this.mRatioOfHeaderHeightToRefresh = offset * 1.0F / this.mHeaderHeight;
            this.mOffsetToRefresh = offset;
        }
    }

    public void onPressDown(float x, float y) {
        this.mIsUnderTouch = true;
        this.mPressedPos = this.mCurrentPos;
        this.mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - this.mPtLastMove.x;
        float offsetY = y - this.mPtLastMove.y;
        processOnMove(x, y, offsetX, offsetY);
        this.mPtLastMove.set(x, y);
    }

    protected void setOffset(float x, float y) {
        this.mOffsetX = x;
        this.mOffsetY = y;
    }

    public float getOffsetX() {
        return this.mOffsetX;
    }

    public float getOffsetY() {
        return this.mOffsetY;
    }

    public int getLastPosY() {
        return this.mLastPos;
    }

    public int getCurrentPosY() {
        return this.mCurrentPos;
    }


    public final void setCurrentPos(int current) {
        this.mLastPos = this.mCurrentPos;
        this.mCurrentPos = current;
        onUpdatePos(current, this.mLastPos);
    }


    protected void onUpdatePos(int current, int last) {
    }


    public int getHeaderHeight() {
        return this.mHeaderHeight;
    }

    public void setHeaderHeight(int height) {
        this.mHeaderHeight = height;
        this.maxDragDistance = this.mHeaderHeight * this.maxDistance;
        updateHeight();
    }

    protected void updateHeight() {
        this.mOffsetToRefresh = (int) (this.mRatioOfHeaderHeightToRefresh * this.mHeaderHeight);
    }

    public void convertFrom(PtrIndicator ptrSlider) {
        this.mCurrentPos = ptrSlider.mCurrentPos;
        this.mLastPos = ptrSlider.mLastPos;
        this.mHeaderHeight = ptrSlider.mHeaderHeight;
    }

    public boolean hasLeftStartPosition() {
        return (this.mCurrentPos > 0);
    }

    public boolean hasJustLeftStartPosition() {
        return (this.mLastPos == 0 && hasLeftStartPosition());
    }

    public boolean hasJustBackToStartPosition() {
        return (this.mLastPos != 0 && isInStartPosition());
    }

    public boolean isOverOffsetToRefresh() {
        return (this.mCurrentPos > getOffsetToRefresh());
    }

    public boolean hasMovedAfterPressedDown() {
        return (this.mCurrentPos != this.mPressedPos);
    }

    public boolean isInStartPosition() {
        return (this.mCurrentPos == 0);
    }

    public boolean crossRefreshLineFromTopToBottom() {
        return (this.mLastPos < getOffsetToRefresh() && this.mCurrentPos >= getOffsetToRefresh());
    }

    public boolean hasJustReachedHeaderHeightFromTopToBottom() {
        return (this.mLastPos < this.mHeaderHeight && this.mCurrentPos >= this.mHeaderHeight);
    }

    public boolean isOverOffsetToKeepHeaderWhileLoading() {
        return (this.mCurrentPos > getOffsetToKeepHeaderWhileLoading());
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        this.mOffsetToKeepHeaderWhileLoading = offset;
    }

    public int getOffsetToKeepHeaderWhileLoading() {
        return (this.mOffsetToKeepHeaderWhileLoading >= 0) ? this.mOffsetToKeepHeaderWhileLoading : this.mHeaderHeight;
    }

    public boolean isAlreadyHere(int to) {
        return (this.mCurrentPos == to);
    }

    public float getLastPercent() {
        float oldPercent = (this.mHeaderHeight == 0) ? 0.0F : (this.mLastPos * 1.0F / this.mHeaderHeight);
        return oldPercent;
    }

    public float getCurrentPercent() {
        float currentPercent = (this.mHeaderHeight == 0) ? 0.0F : (this.mCurrentPos * 1.0F / this.mHeaderHeight);
        return currentPercent;
    }

    public boolean willOverTop(int to) {
        return (to < 0);
    }

    public float getMaxDragDistance() {
        return this.maxDragDistance;
    }
}