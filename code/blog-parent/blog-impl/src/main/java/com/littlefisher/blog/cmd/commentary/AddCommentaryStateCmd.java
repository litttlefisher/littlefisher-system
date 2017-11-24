package com.littlefisher.blog.cmd.commentary;

import com.littlefisher.blog.dao.commentary.CommentaryStateDtoMapper;
import com.littlefisher.blog.model.commentary.CommentaryStateDto;
import com.littlefisher.core.interceptor.AbstractCommand;

/**
 * 
 * Description: 
 *  
 * Created on 2017年5月25日 
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public class AddCommentaryStateCmd extends AbstractCommand<CommentaryStateDto> {
    
    /**
     * commentaryStateDto
     */
    private CommentaryStateDto commentaryStateDto;

    /** 
     * Description: 构造函数
     *
     * @author jinyanan
     *
     * @param commentaryStateDto commentaryStateDto 
     */ 
    public AddCommentaryStateCmd(CommentaryStateDto commentaryStateDto) {
        super();
        this.commentaryStateDto = commentaryStateDto;
    }

    @Override
    public CommentaryStateDto execute() {
        CommentaryStateDtoMapper commentaryStateDtoMapper = this.getMapper(CommentaryStateDtoMapper.class);
        commentaryStateDtoMapper.insert(commentaryStateDto);
        return commentaryStateDto;
    }

}