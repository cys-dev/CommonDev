package com.cys.common.widget.loading;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.cys.common.R;

public class LoadingTextView extends AppCompatTextView {
    //文字画笔
    private Paint mTextPaint;
    private Paint mDotPaint;
    private CharSequence mLoadText;
    private int mRadius ;

    private int[] dotAlphas;

    //两个点向右移动距离用于做动画
    private float mDottransX;

    //两个点动画
    private AnimatorSet mDotAnimationSet = null;
    //透明度动画
    private ValueAnimator mDotalphaAnim = null ;
    //位移动画
    private ValueAnimator mDotMoveAnimation = null;
    //动画持续的时间
    private int mAnimatorDuration;
    //动画X轴上的移动位移
    private int mDistance;

    //文本跟小点的颜色
    private int mLoadingTextColor;
    private int mDotColor;

    private int dotNum;
    private static int DEFAULT_DOT_NUM = 3;

    private static final int LOADING_STATUS = 0;
    private static final int ERROR_STATUS = 1;

    public static final int NULL_BITMAP = 0;
    public static final int ARROW_BITMAP = 1;
    public static final int REFRESH_BITMAP = 2;

    private CharSequence mErrorMsgText;
    private int mCurrentStatus = LOADING_STATUS;
    private int mErrorBitmapType = NULL_BITMAP;
    private float mMarginIcon;
    private int mBackgroundColor;
    private int WIDTH_DURATION = 400;
    private int ALPHA_DURATION = WIDTH_DURATION / 2;
    private int mBackgroundAlpha = 51;

    private Paint mBackgroundPaint;
    private Rect mBackgroundRect = new Rect();
    private AnimatorSet mBackgroundAnimationSet = null;
    //透明度动画
    private ValueAnimator mBackgroundAlphaAnimIn = null ;
    private ValueAnimator mBackgroundAlphaAnimOut = null ;
    //宽度动画
    private ValueAnimator mBackgroundWidthAnimation = null;

    public LoadingTextView(Context context) {
        this(context, null);
    }

    public LoadingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
        setupAnimations();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LoadingTextView, R.attr.Widget_LoadingTextStyle, 0);
        if (attr == null) {
            return;
        }
        try {
            mLoadText = attr.getString(R.styleable.LoadingTextView_mcLoadingText);
            mErrorMsgText = attr.getString(R.styleable.LoadingTextView_mcErrorText);
            mRadius = attr.getInt(R.styleable.LoadingTextView_mcDotRadius, (int) getResources().getDimension(R.dimen.down_dot_radius));
            mLoadingTextColor = attr.getColor(R.styleable.LoadingTextView_mcLoadingTextColor,getResources().getColor(R.color.down_load_text_color));
            mDotColor = attr.getColor(R.styleable.LoadingTextView_mcDotColor,getResources().getColor(R.color.down_load_dot_color));
            dotNum = attr.getInt(R.styleable.LoadingTextView_mcDotNum, DEFAULT_DOT_NUM);
        } finally {
            attr.recycle();
        }
    }

    private void init() {
        this.setGravity(Gravity.CENTER);

        //设置文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        Typeface font = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        mTextPaint.setTypeface(font);
        mTextPaint.setColor(mLoadingTextColor);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.down_load_text_size));

        //设置第一个点画笔
        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setColor(mDotColor);
        mDotPaint.setTextSize(getResources().getDimension(R.dimen.down_load_dot_size));

        dotAlphas = new int[dotNum];
        resetDotAlpha();
        mDistance = (int) getResources().getDimension(R.dimen.down_dot_translate);
        mMarginIcon = getResources().getDimension(R.dimen.error_icon_margin);

        mBackgroundColor = getResources().getColor(R.color.list_hovered_background);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setTextSize(getResources().getDimension(R.dimen.down_load_dot_size));
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentStatus == ERROR_STATUS) {
            drawErrorText(canvas);
        } else {
            drawing(canvas);
        }
    }

    private void resetDotAlpha() {
        for (int i = 0; i < dotAlphas.length; i++) {
            dotAlphas[i] = 0;
        }
    }

    private void drawing(Canvas canvas) {
        drawTextAbove(canvas);
    }

    private void drawTextAbove(Canvas canvas) {
        final float y = canvas.getHeight() / 2 - (mTextPaint.descent() / 2 + mTextPaint.ascent() / 2);
        if (mLoadText == null) {
            mLoadText = "";
        }
        final float textWidth = mTextPaint.measureText(mLoadText.toString());
        canvas.drawText(mLoadText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
        mTextPaint.setShader(null);
        for (int i = 0; i < dotNum; i++) {
            mDotPaint.setAlpha(dotAlphas[i]);
            canvas.drawCircle((getMeasuredWidth() + textWidth) / 2 + i * getResources().getDimension(R.dimen.down_dot_gap) + mDottransX, y, mRadius, mDotPaint);
        }
    }

    private void drawErrorText(Canvas canvas) {
        final float y = canvas.getHeight() / 2 - (mTextPaint.descent() / 2 + mTextPaint.ascent() / 2);
        if (mErrorMsgText == null) {
            mErrorMsgText = "";
        }
        final float textWidth = mTextPaint.measureText(mErrorMsgText.toString());
        canvas.drawText(mErrorMsgText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);

//        float mTop = canvas.getHeight() / 2 - ((BitmapDrawable)mArrowDrawable).getBitmap().getHeight() / 2;
//        if (mErrorBitmapType == ARROW_BITMAP) {
//            canvas.drawBitmap(((BitmapDrawable)mArrowDrawable).getBitmap(), (getMeasuredWidth() + textWidth) / 2 + mMarginIcon, mTop, null);
//        } else if (mErrorBitmapType == REFRESH_BITMAP) {
//            canvas.drawBitmap(((BitmapDrawable)mRefreshDrawable).getBitmap(), (getMeasuredWidth() + textWidth) / 2 + mMarginIcon, mTop, null);
//        }

        canvas.drawRect(mBackgroundRect, mBackgroundPaint);
    }

    private static int DURATION = 83;
    private static int MAINTAIN = 917;
    private static int GAP = 160;
    private static float CHANGE_PER_TIME = 255f / DURATION;
    private void calculateDotAlpha(float time) {
        for (int i = 0; i < dotAlphas.length; i++) {
            float result = Math.max(0, Math.min(Math.min(255, Math.max(0, time - GAP * i) * CHANGE_PER_TIME), 255f - CHANGE_PER_TIME * (time - (GAP * i + (DURATION + MAINTAIN)))));
            dotAlphas[dotAlphas.length - 1 - i] = (int) result;
        }

    }

    private void setupAnimations() {
        //两个点向右移动动画
        mDotMoveAnimation = ValueAnimator.ofFloat(0, mDistance);
        TimeInterpolator pathInterpolator = new PathInterpolatorCompat(0.11f, 0f, 0.12f, 1f);
        mDotMoveAnimation.setInterpolator(pathInterpolator);
        mDotMoveAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float transX = (float) animation.getAnimatedValue();
                mDottransX = transX;
                invalidate();
            }
        });
        mDotMoveAnimation.setDuration(mAnimatorDuration);
        mDotMoveAnimation.setRepeatMode(ValueAnimator.RESTART);
        mDotMoveAnimation.setRepeatCount(ValueAnimator.INFINITE);
        //两个点渐渐显渐隐动画
        mAnimatorDuration = GAP * (dotNum - 1) + DURATION + MAINTAIN + DURATION;
        mDotalphaAnim = ValueAnimator.ofFloat(0, mAnimatorDuration);
        mDotalphaAnim.setDuration(mAnimatorDuration);
        mDotalphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float time = (Float) mDotalphaAnim.getAnimatedValue();
                calculateDotAlpha(time);
            }
        });
        mDotalphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                resetDotAlpha();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetDotAlpha();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mDotalphaAnim.setRepeatMode(ValueAnimator.RESTART);
        mDotalphaAnim.setRepeatCount(ValueAnimator.INFINITE);
        //两个点的动画集合
        mDotAnimationSet = new AnimatorSet();
        mDotAnimationSet.play(mDotMoveAnimation).with(mDotalphaAnim);
    }

    /**
     * 设置两小点的颜色
     * @param dotColor
     */
    public void setDotColor(int dotColor) {
        mDotPaint.setColor(dotColor);
    }

    /**
     * 设置文本的颜色
     * @param loadingTextColor
     */
    public void setLoadingTextColor(int loadingTextColor) {
        mTextPaint.setColor(loadingTextColor);
    }

    /**
     * 获取正在加载的文本
     * @return
     */
    public String getLoadText() {
        return (String) mLoadText;
    }

    /**
     * 设置正在加载的文本
     * @param loadText
     */
    public void setLoadText(String loadText) {
        mLoadText = loadText;
    }

    public void setLoadTextSize(int size) {
        mTextPaint.setTextSize(size);
        invalidate();
    }

    public void setLoadTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    /**
     * 开始动画
     */
    public void startAnim() {
        startLoadingAnimation();
    }

    /**
     * 结束动画
     */
    public void stopAnimator() {
        if (null != mDotAnimationSet) {
            mDotAnimationSet.cancel();
            mDotAnimationSet.removeAllListeners();
            mDotAnimationSet = null;
        }
        if(null != mDotalphaAnim){
            mDotalphaAnim.cancel();
            mDotalphaAnim.removeAllUpdateListeners();
            mDotalphaAnim = null ;
        }
        if(null != mDotMoveAnimation){
            mDotMoveAnimation.cancel();
            mDotMoveAnimation.removeAllUpdateListeners();
            mDotMoveAnimation = null;
        }
    }

    private void startLoadingAnimation() {
        if (null != mDotAnimationSet && mDotAnimationSet.isRunning()) {
            return;
        }
        setupAnimations();
        mDotAnimationSet.start();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            if(mCurrentStatus == ERROR_STATUS) {
                stopBackgroundAnimator();
            } else {
                stopAnimator();
            }
        } else if (this.isShown()) {
            if(mCurrentStatus == ERROR_STATUS) {
                startBackgroundAnimation();
            } else {
                startLoadingAnimation();
            }
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != View.VISIBLE) {
            if(mCurrentStatus == ERROR_STATUS) {
                stopBackgroundAnimator();
            } else {
                stopAnimator();
            }
        } else if (this.isShown()) {
            if(mCurrentStatus == ERROR_STATUS) {
                startBackgroundAnimation();
            } else {
                startLoadingAnimation();
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            if(mCurrentStatus == ERROR_STATUS) {
                startBackgroundAnimation();
            } else {
                startLoadingAnimation();
            }
        } else if (visibility == View.INVISIBLE || visibility == View.GONE) {
            if(mCurrentStatus == ERROR_STATUS) {
                stopBackgroundAnimator();
            } else {
                stopAnimator();
            }
        }
    }

    public void setErrorStatus(String errorMsg) {
        if (errorMsg != null && !TextUtils.isEmpty(errorMsg)) {
            mErrorMsgText = errorMsg;
        }
        if (mBackgroundRect == null) {
            mBackgroundRect = new Rect();
        }
        mBackgroundRect.set(0, 0, 0, getHeight());
        mCurrentStatus = ERROR_STATUS;
        stopAnimator();
        startBackgroundAnimation();
        invalidate();
    }

    public void setLoadingStatus() {
        mCurrentStatus = LOADING_STATUS;
        stopBackgroundAnimator();
        startLoadingAnimation();
        invalidate();
    }

    public void setErrorBitmapType(int type) {
        mErrorBitmapType = type;
        invalidate();
    }

    public void setBackgroundAlpha(int alpha) {
        mBackgroundAlpha = alpha;
    }

    private void setupBackgroundAnimations() {

        mBackgroundWidthAnimation = ValueAnimator.ofInt(0, getWidth());
        mBackgroundWidthAnimation.setInterpolator(new PathInterpolatorCompat(0.1f, 0.57f, 0.2f, 1f));
        mBackgroundWidthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int mWidth = (int) animation.getAnimatedValue();
                mBackgroundRect.right = mWidth;
                invalidate();
            }
        });
        mBackgroundWidthAnimation.setDuration(WIDTH_DURATION);

        mBackgroundAlphaAnimOut = ValueAnimator.ofInt(mBackgroundAlpha, 0);
        mBackgroundAlphaAnimOut.setDuration(ALPHA_DURATION);
        mBackgroundAlphaAnimOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int mAlpha = (int) animation.getAnimatedValue();
                mBackgroundPaint.setAlpha(mAlpha);
            }
        });

        mBackgroundAlphaAnimIn = ValueAnimator.ofInt(mBackgroundAlpha, 0);
        mBackgroundAlphaAnimIn.setDuration(WIDTH_DURATION);
        mBackgroundWidthAnimation.setInterpolator(new PathInterpolatorCompat(0.33f, 0f, 0.67f, 1f));
        mBackgroundAlphaAnimIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int mAlpha = (int) animation.getAnimatedValue();
                mBackgroundPaint.setAlpha(mAlpha);
            }
        });

        mBackgroundAnimationSet = new AnimatorSet();
        mBackgroundAnimationSet.play(mBackgroundWidthAnimation).with(mBackgroundAlphaAnimIn);
    }

    public void stopBackgroundAnimator() {
        if (null != mBackgroundWidthAnimation) {
            mBackgroundWidthAnimation.cancel();
            mBackgroundWidthAnimation.removeAllListeners();
            mBackgroundWidthAnimation = null;
        }
        if(null != mBackgroundAlphaAnimIn){
            mBackgroundAlphaAnimIn.cancel();
            mBackgroundAlphaAnimIn.removeAllUpdateListeners();
            mBackgroundAlphaAnimIn = null ;
        }
        if(null != mBackgroundAlphaAnimOut){
            mBackgroundAlphaAnimOut.cancel();
            mBackgroundAlphaAnimOut.removeAllUpdateListeners();
            mBackgroundAlphaAnimOut = null ;
        }
        if(null != mBackgroundWidthAnimation){
            mBackgroundWidthAnimation.cancel();
            mBackgroundWidthAnimation.removeAllUpdateListeners();
            mBackgroundWidthAnimation = null;
        }
    }

    private void startBackgroundAnimation() {
        if (null != mBackgroundAnimationSet && mBackgroundAnimationSet.isRunning()) {
            return;
        }
        setupBackgroundAnimations();
        mBackgroundAnimationSet.start();
    }
}
