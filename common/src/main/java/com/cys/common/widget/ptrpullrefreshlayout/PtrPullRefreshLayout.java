package com.cys.common.widget.ptrpullrefreshlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import com.cys.common.R;
import com.cys.common.widget.ptrpullrefreshlayout.Listener.OnPullRefreshListener;
import com.cys.common.widget.ptrpullrefreshlayout.Listener.ScrollOffsetListener;
import com.cys.common.widget.ptrpullrefreshlayout.header.CircleAnimHeader;
import com.cys.common.widget.ptrpullrefreshlayout.util.RefreshTimeHelper;

public class PtrPullRefreshLayout
        extends PtrFrameLayout {
    private CircleAnimHeader mHeader;
    private OnPullRefreshListener mPullRefreshGetData;

    public PtrPullRefreshLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public PtrPullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrPullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.mHeader = new CircleAnimHeader(getContext());
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PtrPullRefreshLayout, 0, 0);
        if (arr != null) {
            int ringColor = arr.getColor(R.styleable.PtrPullRefreshLayout_ptrRingColor, -1);
            if (ringColor != -1) {
                setRingColor(ringColor);
            }
            int ringBackColor = arr.getColor(R.styleable.PtrPullRefreshLayout_ptrRingBgColor, -1);
            if (ringBackColor != -1) {
                setRingBackgroundColor(ringBackColor);
            }
            int textColor = arr.getColor(R.styleable.PtrPullRefreshLayout_ptrTextColor, -1);
            if (textColor != -1) {
                setPromptTextColor(textColor);
            }
            boolean pin = arr.getBoolean(R.styleable.PtrPullRefreshLayout_ptrPinContent, false);
            setPinContent(pin);
            int offset = arr.getDimensionPixelOffset(R.styleable.PtrPullRefreshLayout_ptrAnimOffset, 0);
            setOffset(offset);
            arr.recycle();
        }
        disableWhenHorizontalMove(true);
        setKeepHeaderWhenRefresh(true);
        setHeaderView((View) this.mHeader);
        addPtrUIHandler((PtrUIHandler) this.mHeader);
        setPtrHandler(new PtrDefaultHandler() {
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (PtrPullRefreshLayout.this.mPullRefreshGetData != null) {
                    PtrPullRefreshLayout.this.mPullRefreshGetData.startGetData();
                }
            }
        });
    }


    public void setPullGetDataListener(OnPullRefreshListener listener) {
        if (null != listener) {
            this.mPullRefreshGetData = listener;
        }
    }


    public void setOnPullRefreshListener(OnPullRefreshListener listener) {
        if (null != listener) {
            this.mPullRefreshGetData = listener;
        }
    }


    public void setScrollOffsetListener(ScrollOffsetListener listener) {
        if (this.mHeader != null) {
            this.mHeader.setPullRefreshLayoutListener(listener);
        }
    }


    @Deprecated
    public void startRefresh() {
        autoRefresh(false);
    }


    public void stopRefresh() {
        refreshComplete();
    }


    public void setOffset(int offset) {
        this.mHeader.setY(offset);
    }

    public int getOffset() {
        return (int) this.mHeader.getY();
    }


    public boolean getRefreshState() {
        return isRefreshing();
    }


    public void setPromptTextColor(int colorValue) {
        this.mHeader.setTextColor(colorValue);
    }

    public int getPromptTextColor() {
        return this.mHeader.getTextColor();
    }


    @Deprecated
    public void setOverScrollDistance(int overScrollDistance) {
    }


    public void setOptionalLastTimeDisplay(int seconds, String displayText) {
        if (this.mHeader.getRefreshTimeHelper() != null) {
            this.mHeader.getRefreshTimeHelper().setOptionalLastTimeDisplay(seconds, displayText);
        }
    }

    public boolean isOptionalLastTimeDisplaySet() {
        return (this.mHeader.getRefreshTimeHelper() != null && this.mHeader.getRefreshTimeHelper().isOptionalLastTimeDisplaySet());
    }


    public void setLastRefreshTimeKey(String key) {
        if (this.mHeader.getRefreshTimeHelper() != null) {
            this.mHeader.getRefreshTimeHelper().setLastRefreshTimeKey(key);
        }
    }


    public void removeLastRefreshTimeKey() {
        if (this.mHeader.getRefreshTimeHelper() != null) {
            this.mHeader.getRefreshTimeHelper().removeLastRefreshTimeKey();
        }
    }


    public static void removeLastRefreshTimeKeys(Context context, String[] keys) {
        RefreshTimeHelper.removeLastRefreshTimeKeys(context, keys);
    }


    public void setRingColor(int color) {
        if (this.mHeader != null) {
            this.mHeader.setPaintArcColor(color);
        }
    }

    public int getRingColor() {
        return (this.mHeader != null) ? this.mHeader.getPaintArcColor() : 0;
    }


    public void setRingBackgroundColor(int color) {
        if (this.mHeader != null) {
            this.mHeader.setPaintArcBackColor(color);
        }
    }

    public int getRingBackgroundColor() {
        return (this.mHeader != null) ? this.mHeader.getPaintArcBackColor() : 0;
    }


    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(PtrPullRefreshLayout.class.getName());
    }


    public void setRefreshText(String pullToRefreshText, String releaseText, String isRefreshingText) {
        if (this.mHeader != null)
            this.mHeader.setRefreshText(pullToRefreshText, releaseText, isRefreshingText);
    }
}