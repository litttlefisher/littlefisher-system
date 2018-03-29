package com.littlefisher.core.biz.framework.dal.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.littlefisher.core.biz.framework.dal.dao.IUserContactStation4WebsiteDtoDao;
import com.littlefisher.core.biz.framework.dal.example.UserContactStation4WebsiteDtoExample;
import com.littlefisher.core.biz.framework.dal.mapper.UserContactStation4WebsiteDtoMapper;
import com.littlefisher.core.biz.framework.dal.model.UserContactStation4WebsiteDto;
import com.littlefisher.core.mybatis.dao.AbstractBaseDaoImpl;

/**
 * Description: UserContactStation4WebsiteDtoDaoImpl
 *
 * Created on 2018年03月29日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@Repository
public class UserContactStation4WebsiteDtoDaoImpl
    extends AbstractBaseDaoImpl<UserContactStation4WebsiteDto, UserContactStation4WebsiteDtoMapper>
    implements IUserContactStation4WebsiteDtoDao {

    @Autowired
    private UserContactStation4WebsiteDtoMapper userContactStation4WebsiteDtoMapper;

    @Override
    public List<UserContactStation4WebsiteDto>
        selectUserContactStation4WebsiteByStationIdList(List<Long> stationIdList) {
        UserContactStation4WebsiteDtoExample example = new UserContactStation4WebsiteDtoExample();
        example.createCriteria().andContactStationIdIn(stationIdList);
        return userContactStation4WebsiteDtoMapper.selectByExample(example);
    }
}
