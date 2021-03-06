package com.littlefisher.user.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.littlefisher.core.datasource.interceptor.service.ServiceImpl;
import com.littlefisher.user.cmd.station.GetUserContactStation4EmailByUserIdCmd;
import com.littlefisher.user.cmd.station.GetUserContactStation4PhoneByUserIdCmd;
import com.littlefisher.user.cmd.station.GetUserContactStation4SocialByUserIdCmd;
import com.littlefisher.user.cmd.station.GetUserContactStation4WebsiteByUserIdCmd;
import com.littlefisher.user.model.UserContactStation4EmailBizDto;
import com.littlefisher.user.model.UserContactStation4PhoneBizDto;
import com.littlefisher.user.model.UserContactStation4SocialBizDto;
import com.littlefisher.user.model.UserContactStation4WebsiteBizDto;
import com.littlefisher.user.service.IUserContactStationService;

/**
 * Description: UserContactStationServiceImpl
 *
 * Created on 2018年03月28日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@Component
@Service(interfaceClass = IUserContactStationService.class)
public class UserContactStationServiceImpl extends ServiceImpl implements
        IUserContactStationService {

    @Override
    public List<UserContactStation4EmailBizDto> getUserContactStation4EmailByUserId(Long userId) {
        return this.execute(getCommand(GetUserContactStation4EmailByUserIdCmd.class).setUserId(userId));
    }

    @Override
    public List<UserContactStation4PhoneBizDto> getUserContactStation4PhoneByUserId(Long userId) {
        return this.execute(getCommand(GetUserContactStation4PhoneByUserIdCmd.class).setUserId(userId));
    }

    @Override
    public List<UserContactStation4SocialBizDto> getUserContactStation4SocialByUserId(Long userId) {
        return this.execute(getCommand(GetUserContactStation4SocialByUserIdCmd.class).setUserId(userId));
    }

    @Override
    public List<UserContactStation4WebsiteBizDto> getUserContactStation4WebsiteByUserId(Long userId) {
        return this.execute(getCommand(GetUserContactStation4WebsiteByUserIdCmd.class).setUserId(userId));
    }
}
