package com.zj.out.entity;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface WeChatProcessDao {

    WeChatProcess query(Long id);

    int insert(WeChatProcess weChatProcess);

    int deleteBatchByIds(@Param("ids") List<Long> ids);

}
