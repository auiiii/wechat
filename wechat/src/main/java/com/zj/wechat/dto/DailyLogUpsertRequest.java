package com.zj.wechat.dto;

import java.time.LocalDate;

/**
 * 上报/更新某日饮食指标请求。除 date 外字段全部可选，未传入的字段不会清空。
 */
public class DailyLogUpsertRequest {

    private LocalDate date;
    private Integer calories;
    private Integer protein;
    private Integer fat;
    private Integer carbs;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }

    public Integer getCarbs() {
        return carbs;
    }

    public void setCarbs(Integer carbs) {
        this.carbs = carbs;
    }
}
