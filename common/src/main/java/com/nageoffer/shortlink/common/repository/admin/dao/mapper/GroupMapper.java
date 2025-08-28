package com.nageoffer.shortlink.common.repository.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.GroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 短链接分组持久层
 */
@Mapper
public interface GroupMapper extends BaseMapper<GroupDO> {

    /**
     * 根据分组标识查询分组信息
     */
    GroupDO selectOneByGid(@Param("gid") String gid);

    /**
     * 根据用户名查询分组列表
     */
    List<GroupDO> selectListByUsername(@Param("username") String username);

    /**
     * 根据用户名和分组标识查询分组信息
     */
    GroupDO selectOneByUsernameAndGid(@Param("username") String username, @Param("gid") String gid);
}
