package com.zj.wechat.dto;

import java.util.List;

public class CheckinRequest {

    private String exerciseType;
    private List<String> images;
    private String locationName;
    private String note;
    /** 本次运动时长（分钟），用于联动写入每日指标 exercise_minutes */
    private Integer duration;

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
