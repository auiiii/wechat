package com.zj.wechat.controller.sportpal;

import com.zj.wechat.dto.ApiResponse;
import com.zj.wechat.dto.CheckinRequest;
import com.zj.wechat.dto.CheckinVO;
import com.zj.wechat.service.sportpal.CheckinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    private static final Logger logger = LoggerFactory.getLogger(CheckinController.class);

    @Resource
    private CheckinService checkinService;

    @PostMapping("/create")
    public ApiResponse<CheckinVO> checkin(@RequestBody CheckinRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            CheckinVO vo = checkinService.checkin(userId, request.getExerciseType(),
                    request.getImages(), request.getLocationName(), request.getNote(),
                    request.getDuration());
            return ApiResponse.ok(vo);
        } catch (IllegalStateException e) {
            return ApiResponse.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("打卡失败", e);
            return ApiResponse.fail("打卡失败");
        }
    }

    @GetMapping("/list")
    public ApiResponse<List<CheckinVO>> listCheckins(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<CheckinVO> list = checkinService.listCheckins(userId, page, size);
        return ApiResponse.ok(list);
    }

    @GetMapping("/today")
    public ApiResponse<Boolean> hasCheckedInToday(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        boolean checked = checkinService.hasCheckedInToday(userId);
        return ApiResponse.ok(checked);
    }
}
