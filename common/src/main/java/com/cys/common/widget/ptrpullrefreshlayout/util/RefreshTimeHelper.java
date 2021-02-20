package com.cys.common.widget.ptrpullrefreshlayout.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cys.common.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RefreshTimeHelper {
    Date mLastRefreshTime;
    static final String SHAREDPREFERENCES_NAME = "pull_to_refresh";
    SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    String mLastRefreshStr;
    String mHoursAgo;
    String mMinutesAgo;
    String mSecondsAgo;
    String mLastRefreshTimeKey;
    Context mContext;
    private boolean mIsOptionalLastTimeSet;
    private int mOptionalSeconds;
    private String mOptionalText;

    public void readRefreshTimeFromSh() {
        if (!TextUtils.isEmpty(this.mLastRefreshTimeKey)) {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("pull_to_refresh", 0);
            long time = sharedPreferences.getLong(this.mLastRefreshTimeKey, 0L);
            if (time != 0L) {
                this.mLastRefreshTime = new Date(time);
            }
        }
    }

    public void saveRefreshTimeToSh() {
        if (!TextUtils.isEmpty(this.mLastRefreshTimeKey) && this.mLastRefreshTime != null) {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("pull_to_refresh", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(this.mLastRefreshTimeKey, this.mLastRefreshTime.getTime());
            editor.commit();
        }
    }

    public void updateRefreshTime() {
        if (!TextUtils.isEmpty(this.mLastRefreshTimeKey)) {
            this.mLastRefreshTime = new Date();
        }
    }

    public void setLastRefreshTimeKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        this.mLastRefreshTimeKey = key;
        if (TextUtils.isEmpty(this.mLastRefreshStr)) {
            this.mLastRefreshStr = this.mContext.getString(R.string.ptr_last_refresh);
            this.mHoursAgo = this.mContext.getString(R.string.ptr_last_refresh_hour);
            this.mMinutesAgo = this.mContext.getString(R.string.ptr_last_refresh_minute);
            this.mSecondsAgo = this.mContext.getString(R.string.ptr_last_refresh_second);
        }
    }

    public String getLastTime() {
        if (this.mIsOptionalLastTimeSet) {
            return getLastTimeOptional();
        }
        if (this.mLastRefreshTime != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mLastRefreshStr);
            sb.append(" ");
            Date now = new Date();
            long d = now.getTime() - this.mLastRefreshTime.getTime();
            if (d > 0L) {
                if (d < 60000L) {
                    sb.append(d / 1000L);
                    sb.append(this.mSecondsAgo);
                } else if (d < 3600000L) {
                    sb.append(d / 60000L);
                    sb.append(this.mMinutesAgo);
                } else if (d < 86400000L) {
                    sb.append(d / 3600000L);
                    sb.append(this.mHoursAgo);
                } else {
                    sb.append(this.mDateFormat.format(this.mLastRefreshTime));
                }
            }
            return sb.toString();
        }
        return null;
    }


    public RefreshTimeHelper(Context context) {
        this.mIsOptionalLastTimeSet = false;
        this.mOptionalSeconds = 60;
        this.mOptionalText = null;
        this.mContext = context;
    }


    public void setOptionalLastTimeDisplay(int seconds, String displayText) {
        this.mIsOptionalLastTimeSet = true;
        this.mOptionalSeconds = (seconds >= 60) ? seconds : 60;
        if (displayText == null) {
            this.mOptionalText = this.mContext.getResources().getString(R.string.ptr_last_refresh_just_now);
        } else {
            this.mOptionalText = displayText;
        }
    }

    public boolean isOptionalLastTimeDisplaySet() {
        return this.mIsOptionalLastTimeSet;
    }

    public String getLastTimeOptional() {
        if (this.mLastRefreshTime != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mLastRefreshStr);
            sb.append(" ");
            Date now = new Date();
            long d = now.getTime() - this.mLastRefreshTime.getTime();
            if (d >= 0L) {
                if (d < (this.mOptionalSeconds * 1000)) {
                    sb.append(this.mOptionalText);
                } else if (d < 3600000L) {
                    sb.append(d / 60000L);
                    sb.append(this.mMinutesAgo);
                } else if (d < 86400000L) {
                    sb.append(d / 3600000L);
                    sb.append(this.mHoursAgo);
                } else {
                    sb.append(this.mDateFormat.format(this.mLastRefreshTime));
                }
            }
            return sb.toString();
        }
        return null;
    }

    public void removeLastRefreshTimeKey() {
        if (!TextUtils.isEmpty(this.mLastRefreshTimeKey) && this.mContext != null) {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("pull_to_refresh", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(this.mLastRefreshTimeKey);
            editor.commit();
            this.mLastRefreshTimeKey = null;
            this.mLastRefreshTime = null;
        }
    }


    public static void removeLastRefreshTimeKeys(Context context, String[] keys) {
        if (context != null && keys != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("pull_to_refresh", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (String key : keys) {
                if (!TextUtils.isEmpty(key)) {
                    editor.remove(key);
                }
            }
            editor.commit();
        }
    }

    public Date getLastRefreshTime() {
        return this.mLastRefreshTime;
    }

    public String getLastRefreshStr() {
        return this.mLastRefreshStr;
    }

    public String getLastRefreshTimeKey() {
        return this.mLastRefreshTimeKey;
    }
}