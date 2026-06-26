package com.zj.wechat.util;

import java.util.Date;

public class TimeAgoUtil {

    private static final long ONE_MINUTE = 60 * 1000L;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final long ONE_MONTH = 30 * ONE_DAY;

    public static String format(Date date) {
        if (date == null) {
            return "";
        }
        long diff = System.currentTimeMillis() - date.getTime();
        if (diff < 0) {
            return "刚刚";
        }

        if (diff < ONE_MINUTE) {
            return "刚刚";
        }
        if (diff < ONE_HOUR) {
            return (diff / ONE_MINUTE) + "分钟前";
        }
        if (diff < ONE_DAY) {
            return (diff / ONE_HOUR) + "小时前";
        }
        if (diff < ONE_MONTH) {
            return (diff / ONE_DAY) + "天前";
        }
        return (diff / ONE_MONTH) + "个月前";
    }
}
