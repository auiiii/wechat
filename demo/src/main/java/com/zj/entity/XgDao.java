package com.zj.entity;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XgDao {
    public XgEntity selectByXgh(@Param("msisdn")String msisdn, @Param("xgh")String xgh);

    public List<XgEntity> selectAll();

    public XgEntity selectOne();

}
