package com.cys.common.widget.ptrpullrefreshlayout.header;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.cys.common.R;
import com.cys.common.widget.ptrpullrefreshlayout.Listener.ScrollOffsetListener;
import com.cys.common.widget.ptrpullrefreshlayout.PtrFrameLayout;
import com.cys.common.widget.ptrpullrefreshlayout.PtrUIHandler;
import com.cys.common.widget.ptrpullrefreshlayout.indicator.PtrIndicator;
import com.cys.common.widget.ptrpullrefreshlayout.util.RefreshTimeHelper;

import java.lang.reflect.Field;


public class CircleAnimHeader extends View implements PtrUIHandler {
    private RectF mCircleBounds = null;
    private Paint mPaint = null;
    private Paint mPaintArc = null;
    private Paint mPaintArcBack = null;
    private int mCurrentScrollDistance = 0;

    private int mAnimationDistance;
    private int mShowArcDistance;
    private float mCentX;
    private float mCentY;
    private float mRadius = 30.0F;
    private float mRingWidth = 5.0F;
    private int mTextSize = 40;
    private int mTextMarginTop = 30;

    private int mRectMarginTop;
    private int mTextColor = 1073741824;
    private int mForegroundColor = -11227562;

    private String mTimeText;

    private String mPullToRefreshText;

    private String mIsRefreshingText;
    private String mReleaseToRefreshText;
    private int mPaintArcBackDefaultColor = 637534208;

    private int mNewForegroundColor;
    private int mNewPaintArcBackColor;
    private int mNewForegroundColorAlpha;
    private int mNewPaintArcBackColorAlpha;
    private int mTextColorAlpha;
    private Animator mAnimSet;
    private float mStartAngle;
    private float mSweepAngle;
    private final long LOADING_ANIM_DURATION = 1120L;
    private final long BEFORE_LOADING_ANIM_DURATION = 560L;

    public static final String START_ANGLE_PROPERTY = "startAngle";
    public static final String SWEEP_ANGLE_PROPERTY = "sweepAngle";
    private float mFontTop = 0.0F;
    private float mTextBaseLineX = 0.0F;
    private float mTextBaseLineY = 0.0F;

    private int refreshState;
    private final int STATE_POSITION_CHANGE = 1;
    private final int STATE_REACH_ANIM_DISTANCE = 2;
    private final int STATE_REFRESHING = 4;
    private final int STATE_STOP_REFRESH = 8;

    private final RefreshTimeHelper mRefreshTimeHelper;

    private ScrollOffsetListener mScrollOffsetListener;

    public static final int SCROLLER_FLING_END = 21020;

    private static Field mShellHapticFeedBackMotor;


    public CircleAnimHeader(Context context) {
        this(context, (AttributeSet) null);
    }

    public CircleAnimHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshTimeHelper = new RefreshTimeHelper(context);
        init(context);
    }

    public RefreshTimeHelper getRefreshTimeHelper() {
        return this.mRefreshTimeHelper;
    }


    public void onUIReset(PtrFrameLayout frame) {
        this.refreshState = 0;
        this.refreshState = 1;
        setVisibility(View.GONE);
    }


    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if (!TextUtils.isEmpty(this.mRefreshTimeHelper.getLastRefreshTimeKey()) && this.mRefreshTimeHelper.getLastRefreshTime() != null) {
            this.mTimeText = this.mRefreshTimeHelper.getLastTime();
        } else {
            this.mTimeText = this.mPullToRefreshText;
        }
        this.refreshState = 1;
        setVisibility(View.VISIBLE);
    }


    public void onUIRefreshBegin(PtrFrameLayout frame) {
        if (this.mAnimSet == null) {
            this.mAnimSet = createRefreshAnimator();
            this.mAnimSet.start();
        }
        this.refreshState = 4;
    }


    public void onUIRefreshComplete(PtrFrameLayout frame) {
        this.refreshState = 8;
        this.mRefreshTimeHelper.updateRefreshTime();
        if (this.mAnimSet != null) {
            this.mAnimSet.cancel();
        }
        this.mAnimSet = null;
    }

    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        this.mCurrentScrollDistance = ptrIndicator.getCurrentPosY();
        if (this.mScrollOffsetListener != null) {
            this.mScrollOffsetListener.updateScrollOffset(this.mCurrentScrollDistance);
        }

        if (this.mCurrentScrollDistance == 0) {
            this.refreshState = 1;
        } else if (this.mCurrentScrollDistance < this.mAnimationDistance) {
            int yDelta = ptrIndicator.getCurrentPosY() - ptrIndicator.getLastPosY();
            if ((this.refreshState != 8 && this.refreshState != 4) || yDelta > 0) {
                this.refreshState = 1;
            }
        } else if (this.mCurrentScrollDistance > this.mAnimationDistance && this.refreshState != 4 && this.refreshState != 8 && this.refreshState != 2) {

            this.refreshState = 2;

            try {
                if (hasFlymeFeature()) {
                    performHapticFeedback(21020);
                }
            } catch (Exception ignored) {
            }
        }

        invalidate();
    }


    private boolean hasFlymeFeature() {
        boolean has = false;
        try {
            if (mShellHapticFeedBackMotor == null) {
                Class<?> mCls = Class.forName("flyme.config.FlymeFeature");
                mShellHapticFeedBackMotor = mCls.getDeclaredField("SHELL_HAPTICFEEDBACK_MOTOR");
            }
            has = mShellHapticFeedBackMotor.getBoolean(null);
        } catch (Exception exception) {
        }
        return has;
    }

    private void init(Context context) {
        this.mAnimationDistance = context.getResources().getDimensionPixelOffset(R.dimen.ptr_pullRefresh_holdheight);
        this.mShowArcDistance = context.getResources().getDimensionPixelOffset(R.dimen.ptr_pullRefresh_showarcheight);
        this.mRadius = context.getResources().getDimension(R.dimen.ptr_pullRefresh_radius);
        this.mRingWidth = context.getResources().getDimension(R.dimen.ptr_pullRefresh_ringwidth);
        this.mTextSize = context.getResources().getDimensionPixelOffset(R.dimen.ptr_pullRefresh_textsize);
        this.mTextMarginTop = context.getResources().getDimensionPixelOffset(R.dimen.ptr_pullRefresh_textmargintop);
        this.mRectMarginTop = context.getResources().getDimensionPixelOffset(R.dimen.ptr_pullRefresh_margin_top);

        this.mPullToRefreshText = context.getResources().getString(R.string.ptr_pull_refresh);
        this.mTimeText = this.mPullToRefreshText;
        this.mIsRefreshingText = context.getResources().getString(R.string.ptr_is_Refreshing);
        this.mReleaseToRefreshText = context.getResources().getString(R.string.ptr_go_Refreshing);

        this.mPaint = new Paint(1);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mTextColor);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextAlign(Paint.Align.CENTER);
        this.mPaint.setTextSize(this.mTextSize);

        this.mPaintArc = new Paint(1);
        this.mPaintArc.setAntiAlias(true);
        this.mPaintArc.setColor(this.mForegroundColor);
        this.mPaintArc.setStyle(Paint.Style.STROKE);
        this.mPaintArc.setStrokeCap(Paint.Cap.ROUND);
        this.mPaintArc.setStrokeWidth(this.mRingWidth);

        this.mPaintArcBack = new Paint(1);
        this.mPaintArcBack.setAntiAlias(true);
        this.mPaintArcBack.setColor(this.mPaintArcBackDefaultColor);
        this.mPaintArcBack.setStyle(Paint.Style.STROKE);
        this.mPaintArcBack.setStrokeWidth(this.mRingWidth);

        this.mNewForegroundColor = this.mForegroundColor;
        this.mNewForegroundColorAlpha = Color.alpha(this.mNewForegroundColor);
        this.mNewPaintArcBackColor = this.mPaintArcBackDefaultColor;
        this.mNewPaintArcBackColorAlpha = Color.alpha(this.mNewPaintArcBackColor);
        this.mTextColorAlpha = Color.alpha(this.mTextColor);

        Paint.FontMetrics fontMetrics = this.mPaint.getFontMetrics();
        this.mFontTop = -fontMetrics.ascent;
    }


    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initRect();
    }

    private void initRect() {
        this.mCircleBounds = new RectF();
        this.mCentX = (getWidth() / 2 + getLeft());
        this.mCentY = this.mRectMarginTop + this.mRadius + this.mRingWidth;
        this.mCircleBounds.left = this.mCentX - this.mRadius - this.mRingWidth / 2.0F;
        this.mCircleBounds.top = this.mCentY - this.mRadius - this.mRingWidth / 2.0F;
        this.mCircleBounds.right = this.mCentX + this.mRadius + this.mRingWidth / 2.0F;
        this.mCircleBounds.bottom = this.mCentY + this.mRadius + this.mRingWidth / 2.0F;
        this.mTextBaseLineX = this.mCentX;
        this.mTextBaseLineY = this.mCentY + this.mRadius + this.mRingWidth + this.mTextMarginTop + this.mFontTop;
    }


    protected void onDraw(Canvas canvas) {
        drawArcView(canvas);
    }


    private void drawArcView(Canvas canvas) {
        this.mPaintArc.setAlpha(this.mNewForegroundColorAlpha);
        this.mPaintArcBack.setAlpha(this.mNewPaintArcBackColorAlpha);
        this.mPaint.setAlpha(this.mTextColorAlpha);

        float distance = 0.0F;

        if (this.mCurrentScrollDistance <= this.mAnimationDistance && this.mCurrentScrollDistance >= this.mShowArcDistance) {
            distance = ((this.mCurrentScrollDistance - this.mShowArcDistance) * 360 / (this.mAnimationDistance - this.mShowArcDistance));
        } else if (this.mCurrentScrollDistance < this.mShowArcDistance) {
            distance = 0.0F;
        } else if (this.mCurrentScrollDistance > this.mAnimationDistance) {
            distance = 360.0F;
        }
        float fraction = distance / 360.0F;

        this.mPaintArcBack.setAlpha((int) (this.mNewPaintArcBackColorAlpha * fraction));


        if (this.mCircleBounds != null) {
            canvas.drawArc(this.mCircleBounds, -90.0F, 360.0F, false, this.mPaintArcBack);

            switch (this.refreshState) {
                case 1:
                    this.mPaintArc.setAlpha((int) (this.mNewForegroundColorAlpha * fraction));
                    this.mPaint.setAlpha((int) (this.mTextColorAlpha * fraction));
                    canvas.drawText(this.mTimeText, this.mTextBaseLineX, this.mTextBaseLineY, this.mPaint);
                    canvas.drawArc(this.mCircleBounds, -90.0F, distance, false, this.mPaintArc);
                    break;

                case 2:
                    canvas.drawText(this.mReleaseToRefreshText, this.mTextBaseLineX, this.mTextBaseLineY, this.mPaint);
                    canvas.drawArc(this.mCircleBounds, -90.0F, distance, false, this.mPaintArc);
                    break;

                case 4:
                    canvas.drawArc(this.mCircleBounds, this.mStartAngle, this.mSweepAngle, false, this.mPaintArc);
                    canvas.drawText(this.mIsRefreshingText, this.mTextBaseLineX, this.mTextBaseLineY, this.mPaint);
                    break;

                case 8:
                    this.mPaintArc.setAlpha((int) (this.mNewForegroundColorAlpha * fraction));
                    this.mPaint.setAlpha((int) (this.mTextColorAlpha * fraction));
                    canvas.drawArc(this.mCircleBounds, this.mStartAngle, this.mSweepAngle, false, this.mPaintArc);
                    canvas.drawText(this.mIsRefreshingText, this.mTextBaseLineX, this.mTextBaseLineY, this.mPaint);
                    break;
            }
        }
    }


    public void setTextColor(int colorValue) {
        this.mTextColor = colorValue;
        this.mTextColorAlpha = Color.alpha(this.mTextColor);
        if (this.mPaint != null) {
            this.mPaint.setColor(this.mTextColor);
        }
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public void setPaintArcColor(int color) {
        if (this.mPaintArc != null) {
            this.mPaintArc.setColor(color);
            this.mNewForegroundColor = color;
            this.mNewForegroundColorAlpha = Color.alpha(this.mNewForegroundColor);
        }
    }

    public int getPaintArcColor() {
        return this.mNewForegroundColor;
    }

    public void setPaintArcBackColor(int color) {
        if (this.mPaintArcBack != null) {
            this.mPaintArcBack.setColor(color);
            this.mNewPaintArcBackColor = color;
            this.mNewPaintArcBackColorAlpha = Color.alpha(this.mNewPaintArcBackColor);
        }
    }

    public int getPaintArcBackColor() {
        return this.mNewPaintArcBackColor;
    }

    @Deprecated
    public void resetRingColor() {
        if (this.mPaintArc != null && this.mPaintArcBack != null) {
            this.mPaintArc.setColor(this.mForegroundColor);
            this.mPaintArcBack.setColor(this.mPaintArcBackDefaultColor);
            this.mNewForegroundColor = this.mForegroundColor;
            this.mNewPaintArcBackColor = this.mPaintArcBackDefaultColor;
            this.mNewForegroundColorAlpha = Color.alpha(this.mNewForegroundColor);
            this.mNewPaintArcBackColorAlpha = Color.alpha(this.mNewPaintArcBackColor);
        }
    }


    private Animator createRefreshAnimator() {
        PropertyValuesHolder pvhStart = PropertyValuesHolder.ofFloat("startAngle", new float[]{-90.0F, 270.0F});

        PropertyValuesHolder pvhSweep = PropertyValuesHolder.ofFloat("sweepAngle", new float[]{-360.0F, 0.0F});


        ObjectAnimator beforeLoadingAnim = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{pvhStart, pvhSweep});
        beforeLoadingAnim.setDuration(560L);
        beforeLoadingAnim.setInterpolator((TimeInterpolator) new LinearInterpolator());

        Animator loadingAnim = createLoadingAnimator();

        AnimatorSet setList = new AnimatorSet();
        setList.play((Animator) beforeLoadingAnim).before(loadingAnim);
        return (Animator) setList;
    }


    private Animator createLoadingAnimator() {
        Keyframe key1 = Keyframe.ofFloat(0.0F, -90.0F);
        Keyframe key2 = Keyframe.ofFloat(0.5F, 330.0F);
        Keyframe key3 = Keyframe.ofFloat(1.0F, 630.0F);
        PropertyValuesHolder pvhStart = PropertyValuesHolder.ofKeyframe("startAngle", new Keyframe[]{key1, key2, key3});
        PropertyValuesHolder pvhSweep = PropertyValuesHolder.ofFloat("sweepAngle", new float[]{0.2F, -144.0F, 0.0F});

        ObjectAnimator loadingAnim = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{pvhStart, pvhSweep});
        loadingAnim.setDuration(1120L);
        loadingAnim.setInterpolator((TimeInterpolator) new LinearInterpolator());
        loadingAnim.setRepeatCount(-1);
        return (Animator) loadingAnim;
    }


    private float getSweepAngle() {
        return this.mSweepAngle;
    }


    private void setSweepAngle(float sweepAngle) {
        this.mSweepAngle = sweepAngle;
    }


    private float getStartAngle() {
        return this.mStartAngle;
    }


    private void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
        invalidate();
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = this.mAnimationDistance;
        setMeasuredDimension(resolveSize(w, widthMeasureSpec), resolveSize(h, heightMeasureSpec));
    }

    public static int resolveSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case 0:
                result = specSize;
                break;
            case -2147483648:
                if (specSize < size) {
                    result = specSize;
                    break;
                }
                result = size;
                break;

            case 1073741824:
                result = specSize;
                break;
        }
        return result;
    }


    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mRefreshTimeHelper.readRefreshTimeFromSh();
    }


    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mRefreshTimeHelper.saveRefreshTimeToSh();
    }

    public void setPullRefreshLayoutListener(ScrollOffsetListener listener) {
        this.mScrollOffsetListener = listener;
    }

    public void setRefreshText(String pullToRefreshText, String releaseText, String isRefreshingText) {
        if (pullToRefreshText != null) {
            this.mPullToRefreshText = pullToRefreshText;
            this.mTimeText = this.mPullToRefreshText;
        }

        if (releaseText != null) {
            this.mReleaseToRefreshText = releaseText;
        }

        if (isRefreshingText != null)
            this.mIsRefreshingText = isRefreshingText;
    }
}