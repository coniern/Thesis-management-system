package com.thesis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thesis.entity.Paper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperMapper extends BaseMapper<Paper> {
}