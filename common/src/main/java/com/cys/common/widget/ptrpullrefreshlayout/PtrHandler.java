package com.cys.common.widget.ptrpullrefreshlayout;

import android.view.View;

public interface PtrHandler {
    boolean checkCanDoRefresh(PtrFrameLayout paramPtrFrameLayout, View paramView1, View paramView2);

    void onRefreshBegin(PtrFrameLayout paramPtrFrameLayout);
}