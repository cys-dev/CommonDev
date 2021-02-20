package com.cys.common.widget.ptrpullrefreshlayout;

import com.cys.common.widget.ptrpullrefreshlayout.indicator.PtrIndicator;

class PtrUIHandlerHolder implements PtrUIHandler {
    private PtrUIHandler mHandler;
    private PtrUIHandlerHolder mNext;

    private boolean contains(PtrUIHandler handler) {
        return (this.mHandler != null && this.mHandler == handler);
    }


    public boolean hasHandler() {
        return (this.mHandler != null);
    }

    private PtrUIHandler getHandler() {
        return this.mHandler;
    }


    public static void addHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;

            return;
        }
        PtrUIHandlerHolder current = head;
        for (; ; current = current.mNext) {


            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        PtrUIHandlerHolder current = head;
        PtrUIHandlerHolder pre = null;


        do {
            if (current.contains(handler)) {


                if (pre == null) {

                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {

                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }

        } while (current != null);

        if (head == null) {
            head = new PtrUIHandlerHolder();
        }
        return head;
    }


    public void onUIReset(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            PtrUIHandler handler = current.getHandler();
            if (null == handler)
                continue;
            handler.onUIReset(frame);
        }
        while ((current = current.mNext) != null);
    }


    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            PtrUIHandler handler = current.getHandler();
            if (null == handler)
                continue;
            handler.onUIRefreshPrepare(frame);
        }
        while ((current = current.mNext) != null);
    }


    public void onUIRefreshBegin(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            PtrUIHandler handler = current.getHandler();
            if (null == handler)
                continue;
            handler.onUIRefreshBegin(frame);
        }
        while ((current = current.mNext) != null);
    }


    public void onUIRefreshComplete(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            PtrUIHandler handler = current.getHandler();
            if (null == handler)
                continue;
            handler.onUIRefreshComplete(frame);
        }
        while ((current = current.mNext) != null);
    }


    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        PtrUIHandlerHolder current = this;
        do {
            PtrUIHandler handler = current.getHandler();
            if (null == handler)
                continue;
            handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
        }
        while ((current = current.mNext) != null);
    }
}