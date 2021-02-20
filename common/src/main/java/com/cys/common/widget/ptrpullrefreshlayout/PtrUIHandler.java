package com.cys.common.widget.ptrpullrefreshlayout;


import com.cys.common.widget.ptrpullrefreshlayout.indicator.PtrIndicator;

public interface PtrUIHandler {
  void onUIReset(PtrFrameLayout paramPtrFrameLayout);
  
  void onUIRefreshPrepare(PtrFrameLayout paramPtrFrameLayout);
  
  void onUIRefreshBegin(PtrFrameLayout paramPtrFrameLayout);
  
  void onUIRefreshComplete(PtrFrameLayout paramPtrFrameLayout);
  
  void onUIPositionChange(PtrFrameLayout paramPtrFrameLayout, boolean paramBoolean, byte paramByte, PtrIndicator paramPtrIndicator);
}