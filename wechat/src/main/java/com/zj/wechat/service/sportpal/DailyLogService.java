package com.zj.wechat.service.sportpal;

import com.zj.wechat.dto.DailyLogUpsertRequest;
import com.zj.wechat.dto.DailyLogVO;
import com.zj.wechat.entity.sportpal.SpDailyLog;
import com.zj.wechat.entity.sportpal.SpDailyLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DailyLogService {

    private static final Logger logger = LoggerFactory.getLogger(DailyLogService.class);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** 区间查询最大跨度，避免单次拉取过多数据。 */
    private static final int RANGE_MAX_DAYS = 90;

    @Resource
    private SpDailyLogDao spDailyLogDao;

    /**
     * 上报/更新某日饮食指标。未传入的字段（null）不会清空已有值；
     * exerciseMinutes 不在此接口维护，交由打卡联动写入。
     */
    @Transactional
    public DailyLogVO upsert(Long userId, DailyLogUpsertRequest request) {
        if (request == null || request.getDate() == null) {
            throw new IllegalArgumentException("date 不能为空");
        }
        validateNonNegative("calories", request.getCalories());
        validateNonNegative("protein", request.getProtein());
        validateNonNegative("fat", request.getFat());
        validateNonNegative("carbs", request.getCarbs());

        SpDailyLog log = new SpDailyLog();
        log.setUserId(userId);
        log.setLogDate(request.getDate());
        log.setCalories(request.getCalories());
        log.setProtein(request.getProtein());
        log.setFat(request.getFat());
        log.setCarbs(request.getCarbs());
        // exercise_minutes 传 null：ON DUPLICATE 时保留原值
        log.setExerciseMinutes(null);

        spDailyLogDao.upsert(log);
        return toVO(spDailyLogDao.queryByUserAndDate(userId, request.getDate()));
    }

    /**
     * 供内部联动（打卡）累加当日运动时长。支持一天多次运动累加，
     * 饮食字段保持原值不动。
     */
    @Transactional
    public void addExerciseMinutes(Long userId, LocalDate date, int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("运动时长必须大于 0");
        }
        spDailyLogDao.addExerciseMinutes(userId, date, minutes);
    }

    /**
     * 区间查询：区间内每一天均返回一条，缺失记录补 0，供前端柱状图绘制完整刻度。
     */
    public List<DailyLogVO> range(Long userId, String fromStr, String toStr) {
        LocalDate from = parseDate(fromStr, "from");
        LocalDate to = parseDate(toStr, "to");
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from 不能晚于 to");
        }
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        if (days > RANGE_MAX_DAYS) {
            throw new IllegalArgumentException("查询区间不能超过 " + RANGE_MAX_DAYS + " 天");
        }

        List<SpDailyLog> records = spDailyLogDao.queryRange(userId, from, to);
        Map<LocalDate, SpDailyLog> byDate = new HashMap<>(records.size());
        for (SpDailyLog r : records) {
            byDate.put(r.getLogDate(), r);
        }

        List<DailyLogVO> result = new ArrayList<>((int) days);
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            DailyLogVO vo = toVO(byDate.get(d));
            vo.setDate(d);
            result.add(vo);
        }
        return result;
    }

    /**
     * 累计运动分钟数（个人主页统计用）。
     */
    public long totalExerciseMinutes(Long userId) {
        Long sum = spDailyLogDao.sumExerciseMinutes(userId);
        return sum == null ? 0L : sum;
    }

    private DailyLogVO toVO(SpDailyLog log) {
        DailyLogVO vo = new DailyLogVO();
        if (log != null) {
            vo.setDate(log.getLogDate());
            vo.setCalories(log.getCalories() == null ? 0 : log.getCalories());
            vo.setProtein(log.getProtein() == null ? 0 : log.getProtein());
            vo.setFat(log.getFat() == null ? 0 : log.getFat());
            vo.setCarbs(log.getCarbs() == null ? 0 : log.getCarbs());
            vo.setExerciseMinutes(log.getExerciseMinutes() == null ? 0 : log.getExerciseMinutes());
        }
        return vo;
    }

    private static LocalDate parseDate(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
        try {
            return LocalDate.parse(s.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + " 格式应为 YYYY-MM-DD");
        }
    }

    private static void validateNonNegative(String field, Integer val) {
        if (val != null && val < 0) {
            throw new IllegalArgumentException(field + " 不能为负数");
        }
    }
}
