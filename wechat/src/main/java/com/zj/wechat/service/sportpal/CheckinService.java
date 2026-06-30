package com.zj.wechat.service.sportpal;

import com.alibaba.fastjson.JSON;
import com.zj.wechat.dto.CheckinVO;
import com.zj.wechat.entity.sportpal.SpCheckin;
import com.zj.wechat.entity.sportpal.SpCheckinDao;
import com.zj.wechat.utils.TimeAgoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Service
public class CheckinService {

    private static final Logger logger = LoggerFactory.getLogger(CheckinService.class);

    @Resource
    private SpCheckinDao spCheckinDao;

    @Resource
    private DailyLogService dailyLogService;

    @Transactional
    public CheckinVO checkin(Long userId, String exerciseType, List<String> images,
                             String locationName, String note, Integer duration) {
        // 一天内允许多次打卡（如先跑步后游泳），每次打卡单独入库，
        // 运动时长在 daily_log 上累加，不相互覆盖。
        SpCheckin checkin = new SpCheckin();
        checkin.setUserId(userId);
        checkin.setExerciseType(exerciseType);
        checkin.setImages(images != null ? JSON.toJSONString(images) : null);
        checkin.setLocationName(locationName);
        checkin.setNote(note);

        spCheckinDao.insert(checkin);

        // 联动：累加今日运动时长到 daily_log，饮食字段保留原值
        if (duration != null && duration > 0) {
            dailyLogService.addExerciseMinutes(userId, LocalDate.now(), duration);
        }
        return toCheckinVO(checkin);
    }

    public List<CheckinVO> listCheckins(Long userId, int page, int size) {
        List<SpCheckin> checkins = spCheckinDao.queryByUserId(userId, (page - 1) * size, size);
        List<CheckinVO> voList = new ArrayList<>();
        for (SpCheckin c : checkins) {
            voList.add(toCheckinVO(c));
        }
        return voList;
    }

    public boolean hasCheckedInToday(Long userId) {
        return spCheckinDao.countTodayByUserId(userId) > 0;
    }

    private CheckinVO toCheckinVO(SpCheckin checkin) {
        CheckinVO vo = new CheckinVO();
        vo.setId(checkin.getId());
        vo.setExerciseType(checkin.getExerciseType());

        if (checkin.getImages() != null && !checkin.getImages().isEmpty()) {
            try {
                vo.setImages(JSON.parseArray(checkin.getImages(), String.class));
            } catch (Exception e) {
                vo.setImages(Collections.emptyList());
            }
        } else {
            vo.setImages(Collections.emptyList());
        }

        vo.setLocationName(checkin.getLocationName());
        vo.setNote(checkin.getNote());
        vo.setCreatedAt(checkin.getCreatedAt() != null ? checkin.getCreatedAt().toString() : null);
        vo.setTimeAgo(TimeAgoUtil.format(checkin.getCreatedAt()));
        return vo;
    }
}
