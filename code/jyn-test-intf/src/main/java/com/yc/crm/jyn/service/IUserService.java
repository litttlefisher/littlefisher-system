package com.yc.crm.jyn.service;

import java.util.List;

import com.yc.core.exception.BaseAppException;
import com.yc.crm.jyn.dto.UserDto;

/**
 * Description: IUserService Created on 2016年12月30日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public interface IUserService {

    /**
     * Description: getAll
     * 
     * @author jinyanan
     * @return List<UserDto>
     * @throws BaseAppException BaseAppException
     */
    List<UserDto> selectAllUser() throws BaseAppException;

}
