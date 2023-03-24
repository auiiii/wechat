package com.zj.wechat.entity.gpr;

import java.util.List;

public class GprRsp {

    private String id;
    private String object;
    private String model;
    private String created;
    private List<GprChoice> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<GprChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<GprChoice> choices) {
        this.choices = choices;
    }
}
