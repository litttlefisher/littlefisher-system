package com.littlefisher.blog.user.cmd;

import com.littlefisher.blog.user.dao.UserDtoMapper;
import com.littlefisher.core.exception.BaseAppException;
import com.littlefisher.core.interceptor.AbstractCommand;

/**
 * 
 * Description: 
 *  
 * Created on 2017年5月17日 
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public class DeleteUserCmd extends AbstractCommand<Integer> {
    
    /**
     * userId
     */
    private Long userId;

    /**
     * 
     * Description: 构造函数
     *
     * @author jinyanan
     *
     * @param userId userId
     */
    public DeleteUserCmd(Long userId) {
        this.userId = userId;
    }

    @Override
    public Integer execute() throws BaseAppException {
        UserDtoMapper userDtoMapper = this.getMapper(UserDtoMapper.class);
        return userDtoMapper.deleteByPrimaryKey(userId);
    }

}
