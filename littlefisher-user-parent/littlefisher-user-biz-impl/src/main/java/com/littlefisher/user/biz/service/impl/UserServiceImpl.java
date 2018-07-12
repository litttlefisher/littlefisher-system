package com.littlefisher.user.biz.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.littlefisher.core.common.utils.DateUtil;
import com.littlefisher.core.datasource.interceptor.service.ServiceImpl;
import com.littlefisher.core.mybatis.pagehelper.PageInfo;
import com.littlefisher.user.biz.cmd.user.AddUserCmd;
import com.littlefisher.user.biz.cmd.user.DeleteUserCmd;
import com.littlefisher.user.biz.cmd.user.GetUserListByCondCmd;
import com.littlefisher.user.biz.cmd.user.GetUserListByIdsCmd;
import com.littlefisher.user.biz.cmd.user.QryAllUserCmd;
import com.littlefisher.user.biz.cmd.user.QryUserByIdCmd;
import com.littlefisher.user.biz.cmd.user.UpdateUserCmd;
import com.littlefisher.user.biz.model.UserBizDto;
import com.littlefisher.user.biz.request.AddUserRequest;
import com.littlefisher.user.biz.request.GetUserList4PageByCondRequest;
import com.littlefisher.user.biz.request.UpdateUserRequest;
import com.littlefisher.user.biz.service.IUserService;
import com.littlefisher.user.common.enums.EnumUserState;

/**
 * Description:
 *
 * Created on 2017年5月17日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl implements IUserService {

    @Override
    public List<UserBizDto> getAllUser() {
        return this.execute(getCommand(QryAllUserCmd.class));
    }

    @Override
    public UserBizDto getUserById(Long userId) {
        return this.execute(getCommand(QryUserByIdCmd.class).setUserId(userId));
    }

    @Override
    public UserBizDto addUser(AddUserRequest request) {
        UserBizDto userDto = UserBizDto.Builder.getInstance().addAccNbr(request.getAccNbr())
            .addPassword(request.getPassword()).addRealName(request.getRealName()).addEnName(request.getEnName())
            .addNickName(request.getNickName()).addUserDesc(request.getUserDesc()).addState(EnumUserState.VALID)
            .addRegDate(DateUtil.getDBDateTime()).build();
        return this.execute(getCommand(AddUserCmd.class).setUserDto(userDto));
    }

    @Override
    public UserBizDto updateUser(UpdateUserRequest request) {
        UserBizDto userDto = UserBizDto.Builder.getInstance(this.getUserById(request.getId()))
            .addAccNbr(request.getAccNbr()).addPassword(request.getPassword()).addRealName(request.getRealName())
            .addNickName(request.getNickName()).addEnName(request.getEnName()).addUserDesc(request.getUserDesc())
            .addState(request.getState()).addLastLoginDate(request.getLastLoginDate()).build();
        return this.execute(getCommand(UpdateUserCmd.class).setUserDto(userDto));
    }

    @Override
    public int deleteUser(Long userId) {
        return this.execute(getCommand(DeleteUserCmd.class).setUserId(userId));
    }

    @Override
    public List<UserBizDto> getUserListByIdList(List<Long> userIdList) {
        return this.execute(getCommand(GetUserListByIdsCmd.class).setUserIdList(userIdList));
    }

    @Override
    public PageInfo<UserBizDto> getUserList4PageByCond(GetUserList4PageByCondRequest req) {
        return this.execute(getCommand(GetUserListByCondCmd.class).setReq(req));
    }

}