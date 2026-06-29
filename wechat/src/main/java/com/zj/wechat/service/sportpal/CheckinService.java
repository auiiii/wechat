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
import java.util.*;

@Service
public class CheckinService {

    private static final Logger logger = LoggerFactory.getLogger(CheckinService.class);

    @Resource
    private SpCheckinDao spCheckinDao;

    @Transactional
    public CheckinVO checkin(Long userId, String exerciseType, List<String> images,
                             String locationName, String note) {
        int todayCount = spCheckinDao.countTodayByUserId(userId);
        if (todayCount > 0) {
            throw new IllegalStateException("今天已经打卡过了");
        }

        SpCheckin checkin = new SpCheckin();
        checkin.setUserId(userId);
        checkin.setExerciseType(exerciseType);
        checkin.setImages(images != null ? JSON.toJSONString(images) : null);
        checkin.setLocationName(locationName);
        checkin.setNote(note);

        spCheckinDao.insert(checkin);
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
