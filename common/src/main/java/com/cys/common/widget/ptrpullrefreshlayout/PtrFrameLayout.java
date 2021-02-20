package com.cys.common.widget.ptrpullrefreshlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.TextView;

import com.cys.common.widget.ptrpullrefreshlayout.indicator.PtrIndicator;

public class PtrFrameLayout
        extends ViewGroup {
    public static final byte PTR_STATUS_INIT = 1;
    public static final byte PTR_STATUS_PREPARE = 2;
    public static final byte PTR_STATUS_LOADING = 3;
    public static final byte PTR_STATUS_COMPLETE = 4;
    private static int ID = 1;


    private static byte FLAG_AUTO_REFRESH_AT_ONCE = 1;
    private static byte FLAG_AUTO_REFRESH_BUT_LATER = 2;
    private static byte FLAG_ENABLE_NEXT_PTR_AT_ONCE = 4;
    private static byte FLAG_PIN_CONTENT = 8;

    private static byte MASK_AUTO_REFRESH = 3;

    protected View mContent;

    private int mHeaderId = 0;
    private int mContainerId = 0;

    private int mDurationToClose = 160;
    private int mDurationToCloseHeader = 320;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;
    private View mHeaderView;
    private PtrUIHandlerHolder mPtrUIHandlerHolder = PtrUIHandlerHolder.create();

    private PtrHandler mPtrHandler;

    private ScrollChecker mScrollChecker;
    private int mPagingTouchSlop;
    private int mHeaderHeight;
    private byte mStatus = 1;
    private boolean mDisableWhenHorizontalMove = false;
    private int mFlag = 0;


    private boolean mPreventForHorizontal = false;

    private MotionEvent mLastMoveEvent;

    private PtrUIHandlerHook mRefreshCompleteHook;

    private int mLoadingMinTime = 0;
    private long mLoadingStartTime = 0L;

    private PtrIndicator mPtrIndicator;
    private boolean mHasSendCancelEvent = false;
    private float mTotalYOffset = 0.0F;

    private boolean mHeaderNormalMode = false;

    private boolean mHandlingTouch = false;

    private float mTouchDownX = 0.0F;
    private float mTouchDownY = 0.0F;
    private boolean mIsAutoRefreshing = false;
    private ValueAnimator mPosAnimator;

    private Runnable mRefreshCompleteRunnable = new Runnable() {
        public void run() {
            PtrFrameLayout.this.performRefreshComplete();
        }
    };


    public PtrFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.mPtrIndicator = new PtrIndicator();

        this.mScrollChecker = new ScrollChecker();

        ViewConfiguration conf = ViewConfiguration.get(getContext());
        this.mPagingTouchSlop = conf.getScaledTouchSlop();
    }


    protected void onFinishInflate() {
        int childCount = getChildCount();
        if (childCount > 2)
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        if (childCount == 2) {
            if (this.mHeaderId != 0 && this.mHeaderView == null) {
                this.mHeaderView = findViewById(this.mHeaderId);
            }
            if (this.mContainerId != 0 && this.mContent == null) {
                this.mContent = findViewById(this.mContainerId);
            }


            if (this.mContent == null || this.mHeaderView == null) {
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof PtrUIHandler) {
                    this.mHeaderView = child1;
                    this.mContent = child2;
                } else if (child2 instanceof PtrUIHandler) {
                    this.mHeaderView = child2;
                    this.mContent = child1;

                } else if (this.mContent == null && this.mHeaderView == null) {
                    this.mHeaderView = child1;
                    this.mContent = child2;


                } else if (this.mHeaderView == null) {
                    this.mHeaderView = (this.mContent == child1) ? child2 : child1;
                } else {
                    this.mContent = (this.mHeaderView == child1) ? child2 : child1;
                }

            }

        } else if (childCount == 1) {
            this.mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(-39424);
            errorView.setGravity(17);
            errorView.setTextSize(20.0F);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            this.mContent = (View) errorView;
            addView(this.mContent);
        }
        if (this.mHeaderView != null) {
            this.mHeaderView.bringToFront();
        }

        super.onFinishInflate();
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (this.mHeaderView != null) {
            measureChildWithMargins(this.mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) this.mHeaderView.getLayoutParams();
            this.mHeaderHeight = this.mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            this.mPtrIndicator.setHeaderHeight(this.mHeaderHeight);
        }

        if (this.mContent != null) {
            measureContentView(this.mContent, widthMeasureSpec, heightMeasureSpec);
        }
    }


    private void measureContentView(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetX = this.mPtrIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (this.mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) this.mHeaderView.getLayoutParams();
            int left = paddingLeft + lp.leftMargin;
            int top = paddingTop + lp.topMargin + offsetX - this.mHeaderHeight;
            int right = left + this.mHeaderView.getMeasuredWidth();
            int bottom = top + this.mHeaderView.getMeasuredHeight();
            if (this.mHeaderNormalMode) {
                this.mHeaderView.layout(left, top, right, bottom);
            } else if (bottom <= this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading()) {
                this.mHeaderView.layout(left, top, right, bottom);
            }
        }

        if (this.mContent != null) {
            if (isPinContent()) {
                offsetX = 0;
            }
            MarginLayoutParams lp = (MarginLayoutParams) this.mContent.getLayoutParams();
            int left = paddingLeft + lp.leftMargin;
            int top = paddingTop + lp.topMargin + offsetX;
            int right = left + this.mContent.getMeasuredWidth();
            int bottom = top + this.mContent.getMeasuredHeight();
            this.mContent.layout(left, top, right, bottom);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    public boolean dispatchTouchEvent(MotionEvent e) {
        float offsetX, offsetY, changeY;
        boolean moveDown, moveUp, canMoveUp;
        if (this.mIsAutoRefreshing && this.mPosAnimator != null) {
            this.mIsAutoRefreshing = false;
            this.mPosAnimator.cancel();
        }
        if (!isEnabled() || this.mContent == null || this.mHeaderView == null) {
            return dispatchTouchEventSupper(e);
        }
        int action = e.getAction();
        switch (action) {
            case 1:
            case 3:
                this.mHandlingTouch = false;
                this.mPtrIndicator.onRelease();
                if (this.mPtrIndicator.hasLeftStartPosition()) {
                    onRelease(false);
                    if (this.mPtrIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                }
                return dispatchTouchEventSupper(e);


            case 0:
                this.mHasSendCancelEvent = false;
                this.mPtrIndicator.onPressDown(e.getX(), e.getY());
                this.mTouchDownX = e.getX();
                this.mTouchDownY = e.getY();

                this.mScrollChecker.abortIfWorking();

                this.mPreventForHorizontal = false;
                this.mTotalYOffset = 0.0F;
                dispatchTouchEventSupper(e);
                return true;

            case 2:
                this.mPreventForHorizontal = false;
                this.mLastMoveEvent = e;
                this.mPtrIndicator.onMove(e.getX(), e.getY());
                offsetX = e.getX() - this.mTouchDownX;
                offsetY = e.getY() - this.mTouchDownY;
                changeY = this.mPtrIndicator.getOffsetY();
                this.mTotalYOffset += Math.abs(offsetY);

                if (this.mDisableWhenHorizontalMove && !this.mPreventForHorizontal && ((Math.abs(offsetX) > this.mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY)) || this.mTotalYOffset < this.mPagingTouchSlop) &&
                        this.mPtrIndicator.isInStartPosition()) {
                    this.mPreventForHorizontal = true;
                }

                if (this.mPreventForHorizontal) {
                    if (this.mHandlingTouch && !this.mPtrIndicator.isInStartPosition()) {
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                }


                moveDown = (changeY > 0.0F && this.mTotalYOffset > this.mPagingTouchSlop);
                moveUp = (changeY < 0.0F && this.mTotalYOffset > this.mPagingTouchSlop);
                canMoveUp = this.mPtrIndicator.hasLeftStartPosition();


                if (moveDown && this.mPtrHandler != null && !this.mPtrHandler.checkCanDoRefresh(this, this.mContent, this.mHeaderView)) {
                    if (this.mHandlingTouch && !this.mPtrIndicator.isInStartPosition()) {
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                }


                if ((moveUp && canMoveUp) || moveDown) {
                    requestDisallowInterceptTouchEvent(true);
                    this.mHandlingTouch = true;
                    movePos(changeY);
                    return true;
                }
                break;
        }
        if (this.mHandlingTouch && !this.mPtrIndicator.isInStartPosition()) {
            return true;
        }
        return dispatchTouchEventSupper(e);
    }


    private void movePos(float deltaY) {
        if (deltaY < 0.0F && this.mPtrIndicator.isInStartPosition()) {
            return;
        }

        int to = this.mPtrIndicator.getCurrentPosY() + (int) deltaY;


        if (this.mPtrIndicator.willOverTop(to)) {
            to = 0;
        }

        if (to > this.mPtrIndicator.getMaxDragDistance()) {
            to = (int) this.mPtrIndicator.getMaxDragDistance();
        }

        this.mPtrIndicator.setCurrentPos(to);
        int change = to - this.mPtrIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }

        boolean isUnderTouch = this.mPtrIndicator.isUnderTouch();


        if (isUnderTouch && !this.mHasSendCancelEvent && this.mPtrIndicator.hasMovedAfterPressedDown()) {
            this.mHasSendCancelEvent = true;
            sendCancelEvent();
        }


        if ((this.mPtrIndicator.hasJustLeftStartPosition() && this.mStatus == 1) || (this.mPtrIndicator
                .goDownCrossFinishPosition() && this.mStatus == 4 && isEnabledNextPtrAtOnce())) {

            this.mStatus = 2;
            this.mPtrUIHandlerHolder.onUIRefreshPrepare(this);
        }


        if (this.mPtrIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();


            if (isUnderTouch) {
                sendDownEvent();
            }
        }


        if (this.mStatus == 2) {

            if (isUnderTouch && !isAutoRefresh() && this.mPullToRefresh && this.mPtrIndicator
                    .crossRefreshLineFromTopToBottom()) {
                tryToPerformRefresh();
            }

            if (performAutoRefreshButLater() && this.mPtrIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }


        if (this.mHeaderNormalMode) {
            this.mHeaderView.offsetTopAndBottom(change);
            if (!isPinContent()) {
                this.mContent.offsetTopAndBottom(change);
            }
        } else if (isPinContent()) {
            this.mHeaderView.offsetTopAndBottom(change);
        } else {

            if (this.mPtrIndicator.getLastPosY() <= this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading()) {
                if (this.mPtrIndicator.getLastPosY() + change > this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading()) {
                    this.mHeaderView.offsetTopAndBottom(this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading() - this.mHeaderView.getBottom());
                } else {
                    this.mHeaderView.offsetTopAndBottom(change);
                }
            } else if (this.mPtrIndicator.getLastPosY() + change < this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading()) {


                int headerMoveOffset = this.mPtrIndicator.getLastPosY() - this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading() + change;
                this.mHeaderView.offsetTopAndBottom(headerMoveOffset);
            }

            this.mContent.offsetTopAndBottom(change);
        }


        invalidate();

        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIPositionChange(this, isUnderTouch, this.mStatus, this.mPtrIndicator);
        }
        onPositionChange(isUnderTouch, this.mStatus, this.mPtrIndicator);
    }


    protected void onPositionChange(boolean isInTouching, byte status, PtrIndicator mPtrIndicator) {
    }


    public int getHeaderHeight() {
        return this.mHeaderHeight;
    }


    private void onRelease(boolean stayForLoading) {
        tryToPerformRefresh();

        if (this.mStatus == 3) {

            if (this.mKeepHeaderWhenRefresh) {

                if (this.mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                    this.mScrollChecker.tryToScrollTo(this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading(), this.mDurationToClose);
                } else if (this.mPtrIndicator.getCurrentPosY() < this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading() && !stayForLoading && this.mStatus == 3) {

                    refreshComplete();
                }
            } else {

                tryScrollBackToTopWhileLoading();
            }

        } else if (this.mStatus == 4) {
            notifyUIRefreshComplete(false);
        } else {
            tryScrollBackToTopAbortRefresh();
        }
    }


    public void setRefreshCompleteHook(PtrUIHandlerHook hook) {
        this.mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            public void run() {
                PtrFrameLayout.this.notifyUIRefreshComplete(true);
            }
        });
    }


    private void tryScrollBackToTop() {
        if (!this.mPtrIndicator.isUnderTouch()) {
            this.mScrollChecker.tryToScrollTo(0, this.mDurationToCloseHeader);
        }
    }


    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }


    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }


    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private boolean tryToPerformRefresh() {
        if (this.mStatus != 2) {
            return false;
        }


        if ((this.mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh()) || this.mPtrIndicator.isOverOffsetToRefresh()) {
            this.mStatus = 3;
        }

        return false;
    }

    private void performRefresh() {
        this.mLoadingStartTime = System.currentTimeMillis();
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIRefreshBegin(this);
        }
        if (this.mPtrHandler != null) {
            this.mPtrHandler.onRefreshBegin(this);
        }
    }


    private boolean tryToNotifyReset() {
        if ((this.mStatus == 4 || this.mStatus == 2) && this.mPtrIndicator.isInStartPosition()) {
            if (this.mPtrUIHandlerHolder.hasHandler()) {
                this.mPtrUIHandlerHolder.onUIReset(this);
            }
            this.mStatus = 1;
            clearFlag();
            this.mIsAutoRefreshing = false;
            return true;
        }
        return false;
    }

    protected void onPtrScrollAbort() {
        if (this.mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        }
    }

    protected void onPtrScrollFinish() {
        if (this.mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        } else if (this.mStatus == 3) {
            performRefresh();
        }
    }


    public boolean isRefreshing() {
        return (this.mStatus == 3);
    }


    public final void refreshComplete() {
        if (this.mRefreshCompleteHook != null) {
            this.mRefreshCompleteHook.reset();
        }

        long delay = this.mLoadingMinTime - System.currentTimeMillis() - this.mLoadingStartTime;
        delay = (this.mLoadingStartTime == 0L) ? 0L : delay;
        if (delay <= 0L) {
            performRefreshComplete();
        } else {
            postDelayed(this.mRefreshCompleteRunnable, delay);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mRefreshCompleteRunnable);
    }


    private void performRefreshComplete() {
        this.mStatus = 4;


        if (this.mScrollChecker.mIsRunning && isAutoRefresh()) {
            return;
        }

        notifyUIRefreshComplete(false);
    }


    private void notifyUIRefreshComplete(boolean ignoreHook) {
        if (this.mPtrIndicator.hasLeftStartPosition() && !ignoreHook && this.mRefreshCompleteHook != null) {
            this.mRefreshCompleteHook.takeOver();
            return;
        }
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIRefreshComplete(this);
        }
        this.mPtrIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }


    public void autoRefresh() {
        autoRefresh(true, this.mDurationToCloseHeader);
    }


    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, this.mDurationToCloseHeader);
    }


    private void clearFlag() {
        this.mFlag &= MASK_AUTO_REFRESH ^ 0xFFFFFFFF;
    }


    public void autoRefresh(boolean atOnce, int duration) {
        if (this.mStatus != 1) {
            return;
        }

        this.mFlag |= atOnce ? FLAG_AUTO_REFRESH_AT_ONCE : FLAG_AUTO_REFRESH_BUT_LATER;

        this.mStatus = 2;
        if (this.mPtrUIHandlerHolder.hasHandler()) {
            this.mPtrUIHandlerHolder.onUIRefreshPrepare(this);
        }
        this.mScrollChecker.tryToScrollTo(this.mPtrIndicator.getOffsetToRefresh(), duration);
        if (atOnce) {
            this.mStatus = 3;
            performRefresh();
        }
    }


    public boolean isAutoRefresh() {
        return ((this.mFlag & MASK_AUTO_REFRESH) > 0);
    }

    private boolean performAutoRefreshButLater() {
        return ((this.mFlag & MASK_AUTO_REFRESH) == FLAG_AUTO_REFRESH_BUT_LATER);
    }


    public void setEnabledNextPtrAtOnce(boolean enable) {
        if (enable) {
            this.mFlag |= FLAG_ENABLE_NEXT_PTR_AT_ONCE;
        } else {
            this.mFlag &= FLAG_ENABLE_NEXT_PTR_AT_ONCE ^ 0xFFFFFFFF;
        }
    }

    public boolean isEnabledNextPtrAtOnce() {
        return ((this.mFlag & FLAG_ENABLE_NEXT_PTR_AT_ONCE) > 0);
    }


    public void setPinContent(boolean pinContent) {
        if (pinContent) {
            this.mFlag |= FLAG_PIN_CONTENT;
        } else {
            this.mFlag &= FLAG_PIN_CONTENT ^ 0xFFFFFFFF;
        }
    }

    public boolean isPinContent() {
        return ((this.mFlag & FLAG_PIN_CONTENT) > 0);
    }


    public void disableWhenHorizontalMove(boolean disable) {
        this.mDisableWhenHorizontalMove = disable;
    }


    public void setLoadingMinTime(int time) {
        this.mLoadingMinTime = time;
    }


    public View getContentView() {
        return this.mContent;
    }

    public void setPtrHandler(PtrHandler ptrHandler) {
        this.mPtrHandler = ptrHandler;
    }

    public void addPtrUIHandler(PtrUIHandler ptrUIHandler) {
        PtrUIHandlerHolder.addHandler(this.mPtrUIHandlerHolder, ptrUIHandler);
    }


    public void removePtrUIHandler(PtrUIHandler ptrUIHandler) {
        this.mPtrUIHandlerHolder = PtrUIHandlerHolder.removeHandler(this.mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void setPtrIndicator(PtrIndicator slider) {
        if (this.mPtrIndicator != null && this.mPtrIndicator != slider) {
            slider.convertFrom(this.mPtrIndicator);
        }
        this.mPtrIndicator = slider;
    }


    public float getResistance() {
        return this.mPtrIndicator.getResistance();
    }

    public void setResistance(float resistance) {
        this.mPtrIndicator.setResistance(resistance);
    }


    public float getDurationToClose() {
        return this.mDurationToClose;
    }


    public void setDurationToClose(int duration) {
        this.mDurationToClose = duration;
    }


    public long getDurationToCloseHeader() {
        return this.mDurationToCloseHeader;
    }


    public void setDurationToCloseHeader(int duration) {
        this.mDurationToCloseHeader = duration;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        this.mPtrIndicator.setRatioOfHeaderHeightToRefresh(ratio);
    }

    public int getOffsetToRefresh() {
        return this.mPtrIndicator.getOffsetToRefresh();
    }


    public void setOffsetToRefresh(int offset) {
        this.mPtrIndicator.setOffsetToRefresh(offset);
    }


    public float getRatioOfHeaderToHeightRefresh() {
        return this.mPtrIndicator.getRatioOfHeaderToHeightRefresh();
    }


    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        this.mPtrIndicator.setOffsetToKeepHeaderWhileLoading(offset);
    }


    public int getOffsetToKeepHeaderWhileLoading() {
        return this.mPtrIndicator.getOffsetToKeepHeaderWhileLoading();
    }


    public boolean isKeepHeaderWhenRefresh() {
        return this.mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        this.mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isPullToRefresh() {
        return this.mPullToRefresh;
    }

    public void setPullToRefresh(boolean pullToRefresh) {
        this.mPullToRefresh = pullToRefresh;
    }


    public View getHeaderView() {
        return this.mHeaderView;
    }

    public void setHeaderView(View header) {
        if (this.mHeaderView != null && header != null && this.mHeaderView != header) {
            removeView(this.mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            LayoutParams layoutParams = new LayoutParams(-1, -2);
            header.setLayoutParams((ViewGroup.LayoutParams) layoutParams);
        }
        this.mHeaderView = header;
        addView(header);
    }


    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }


    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup.LayoutParams) new LayoutParams(-1, -1);
    }


    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return (ViewGroup.LayoutParams) new LayoutParams(p);
    }


    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return (ViewGroup.LayoutParams) new LayoutParams(getContext(), attrs);
    }

    private void sendCancelEvent() {
        if (this.mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = this.mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), 3, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        if (this.mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = this.mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), 0, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    public boolean isHeaderNormalMode() {
        return this.mHeaderNormalMode;
    }

    public void setHeaderNormalMode(boolean headerNormalMode) {
        this.mHeaderNormalMode = headerNormalMode;
    }

    public static class LayoutParams
            extends MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }


        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


    class ScrollChecker {
        private int mLastFlingY;

        private boolean mIsRunning = false;
        private int mStart;
        private ValueAnimator mScrollAnimator;

        public ScrollChecker() {
            this.mScrollAnimator = new ValueAnimator();
            if (Build.VERSION.SDK_INT < 21) {
                this.mScrollAnimator.setInterpolator((TimeInterpolator) new DecelerateInterpolator());
            } else {
                this.mScrollAnimator.setInterpolator((TimeInterpolator) new PathInterpolator(0.33F, 0.0F, 0.33F, 1.0F));
            }
            this.mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int curY = ((Integer) animation.getAnimatedValue()).intValue();
                    int deltaY = curY - ScrollChecker.this.mLastFlingY;
                    if (ScrollChecker.this.mScrollAnimator.isRunning() || deltaY != 0) {
                        ScrollChecker.this.mLastFlingY = curY;
                        PtrFrameLayout.this.movePos(deltaY);
                    }
                }
            });


            this.mScrollAnimator.addListener((Animator.AnimatorListener) new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ScrollChecker.this.finish();
                    super.onAnimationEnd(animation);
                }
            });
        }


        private void finish() {
            reset();
            PtrFrameLayout.this.onPtrScrollFinish();
        }

        private void reset() {
            this.mLastFlingY = 0;
            this.mIsRunning = false;
        }

        public void abortIfWorking() {
            if (this.mScrollAnimator.isRunning()) {
                this.mScrollAnimator.cancel();
                PtrFrameLayout.this.onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (PtrFrameLayout.this.mPtrIndicator.isAlreadyHere(to)) {
                return;
            }
            this.mStart = PtrFrameLayout.this.mPtrIndicator.getCurrentPosY();
            int distance = to - this.mStart;
            if (this.mScrollAnimator.isRunning()) {
                this.mScrollAnimator.cancel();
            }
            reset();
            this.mScrollAnimator.setIntValues(new int[]{0, distance});
            this.mScrollAnimator.setDuration(duration);
            this.mScrollAnimator.start();
            this.mIsRunning = true;
        }
    }


    public void beginAutoRefresh(long duration) {
        this.mIsAutoRefreshing = true;
        if (this.mPosAnimator != null && this.mPosAnimator.isRunning()) {
            this.mPosAnimator.cancel();
        }
        this.mPosAnimator = new ValueAnimator();
        this.mPosAnimator.setIntValues(new int[]{0, (int) this.mPtrIndicator.getMaxDragDistance() * 2});
        this.mPosAnimator.setDuration(duration);
        this.mPosAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                PtrFrameLayout.this.mPtrIndicator.onMove(0.0F, ((Integer) animation.getAnimatedValue()).intValue());
                PtrFrameLayout.this.movePos(PtrFrameLayout.this.mPtrIndicator.getOffsetY());
            }
        });
        this.mPosAnimator.addListener((Animator.AnimatorListener) new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                PtrFrameLayout.this.mHandlingTouch = false;
                PtrFrameLayout.this.mPtrIndicator.onRelease();
                PtrFrameLayout.this.onRelease(false);
            }
        });
        this.mPosAnimator.start();
    }

    public byte getStatus() {
        return this.mStatus;
    }


    public boolean isEnablePull() {
        return isEnabled();
    }


    public void setEnablePull(boolean enablePull) {
        setEnabled(enablePull);
        boolean needScrollToTop = (!enablePull && this.mPtrIndicator.hasLeftStartPosition());

        if (needScrollToTop) {
            this.mHandlingTouch = false;
            this.mPtrIndicator.onRelease();
            refreshComplete();
        }
    }


    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(PtrFrameLayout.class.getName());
    }


    public boolean hasBeginAutoRefresh() {
        return this.mIsAutoRefreshing;
    }
}