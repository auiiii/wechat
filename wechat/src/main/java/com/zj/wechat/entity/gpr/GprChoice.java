package com.zj.wechat.entity.gpr;

import com.alibaba.fastjson.JSONObject;

public class GprChoice {

    private int index;
    private String finish_reason;
    private JSONObject delta;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }

    public JSONObject getDelta() {
        return delta;
    }

    public void setDelta(JSONObject delta) {
        this.delta = delta;
    }
}
