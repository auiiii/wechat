package com.zj.wechat.dto;

import java.util.List;

/**
 * 区间查询响应：区间内每一天一条记录（缺失日期补 0）。
 */
public class DailyLogRangeVO {

    private List<DailyLogVO> list;

    public DailyLogRangeVO() {
    }

    public DailyLogRangeVO(List<DailyLogVO> list) {
        this.list = list;
    }

    public List<DailyLogVO> getList() {
        return list;
    }

    public void setList(List<DailyLogVO> list) {
        this.list = list;
    }
}
