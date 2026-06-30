package com.zj.wechat.entity.sportpal;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpDailyLogDao {

    /**
     * 按 (user_id, log_date) upsert：首次插入缺失字段补 0；命中已存在记录时，
     * 入参为 null 的字段保留原值，非 null 字段覆盖。
     * 仅用于饮食指标上报，exercise_minutes 不在此维护。
     */
    int upsert(SpDailyLog log);

    /**
     * 在当日 exercise_minutes 上累加 minutes（打卡联动用，支持一天多次运动累加）。
     * 首次写入则等于 minutes，饮食字段保持 0 不动。
     */
    int addExerciseMinutes(@Param("userId") Long userId,
                           @Param("logDate") LocalDate logDate,
                           @Param("minutes") int minutes);

    SpDailyLog queryByUserAndDate(@Param("userId") Long userId, @Param("logDate") LocalDate logDate);

    List<SpDailyLog> queryRange(@Param("userId") Long userId,
                                @Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate);

    /**
     * 累计运动分钟数；无记录返回 0。
     */
    Long sumExerciseMinutes(@Param("userId") Long userId);
}
