package com.zj.wechat.controller.sportpal;

import com.zj.wechat.dto.ApiResponse;
import com.zj.wechat.dto.DailyLogRangeVO;
import com.zj.wechat.dto.DailyLogUpsertRequest;
import com.zj.wechat.dto.DailyLogVO;
import com.zj.wechat.service.sportpal.DailyLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/daily-log")
public class DailyLogController {

    private static final Logger logger = LoggerFactory.getLogger(DailyLogController.class);

    @Resource
    private DailyLogService dailyLogService;

    @PostMapping("/upsert")
    public ApiResponse<DailyLogVO> upsert(@RequestBody DailyLogUpsertRequest request,
                                           HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            DailyLogVO vo = dailyLogService.upsert(userId, request);
            return ApiResponse.ok(vo);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("上报每日指标失败", e);
            return ApiResponse.fail("上报失败");
        }
    }

    @GetMapping("/range")
    public ApiResponse<DailyLogRangeVO> range(@RequestParam("from") String from,
                                                @RequestParam("to") String to,
                                                HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            List<DailyLogVO> list = dailyLogService.range(userId, from, to);
            return ApiResponse.ok(new DailyLogRangeVO(list));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        } catch (Exception e) {
            logger.error("查询每日指标区间失败", e);
            return ApiResponse.fail("查询失败");
        }
    }
}
