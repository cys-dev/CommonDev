package com.cys.common.widget.ptrpullrefreshlayout;

import android.view.View;
import android.widget.AbsListView;

public abstract class PtrDefaultHandler
        implements PtrHandler {
    public static boolean canChildScrollUp(View view) {
        if (view instanceof AbsListView) {
            AbsListView absListView = (AbsListView) view;
            if (absListView.getChildCount() == 0) {
                return true;
            }
            return (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        }
        return view.canScrollVertically(-1);
    }


    public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }


    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }
}