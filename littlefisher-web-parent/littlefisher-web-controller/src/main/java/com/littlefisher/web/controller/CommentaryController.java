package com.littlefisher.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.littlefisher.blog.biz.model.CommentaryBizDto;
import com.littlefisher.blog.biz.model.CommentaryBizExtDto;
import com.littlefisher.blog.biz.request.AddCommentaryRequest;
import com.littlefisher.blog.biz.request.GetCommentaryList4PagerByCondRequest;
import com.littlefisher.blog.biz.request.UpdateCommentaryRequest;
import com.littlefisher.core.common.stereotype.constants.BaseConstants;
import com.littlefisher.core.mybatis.pagehelper.PageInfo;
import com.littlefisher.web.dal.integration.blog.ICommentaryIntegration;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

/**
 * Description: 评价Controller
 *
 * Created on 2017年5月22日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@RestController
@RequestMapping(BaseConstants.BASE_API_PREFIX + "/blog/v1/commentaries")
@Api(value = "commentary", description = "commentary 接口API")
public class CommentaryController {

    @Autowired
    private ICommentaryIntegration commentaryIntegration;

    /**
     * 查询评价列表，分页用
     *
     * @param req 请求
     * @return 分页返回信息
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation("查询评价列表，分页用")
    public PageInfo<CommentaryBizExtDto> getCommentaryList4PageByCond(
        @ApiParam(required = true, value = "查询评价列表条件") @ModelAttribute GetCommentaryList4PagerByCondRequest req) {
        return commentaryIntegration.getCommentaryList4PageByCond(req);
    }

    /**
     * Description: 根据主键查询评价
     *
     * @param commentaryId commentaryId
     * @return CommentaryDto
     */
    @RequestMapping(value = "/{commentaryId}", method = RequestMethod.GET)
    @ApiOperation("根据主键查询评价")
    public CommentaryBizDto getCommentaryById(@ApiParam(required = true,
        value = "评价主键") @PathVariable("commentaryId") @NotNull(message = "commentaryId不能为空") Long commentaryId) {
        return commentaryIntegration.getCommentaryById(commentaryId);
    }

    /**
     * Description: 新增评价
     *
     * @param request 请求入参
     * @return CommentaryDto
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("新增评价")
    public CommentaryBizDto addCommentary(@ApiParam(required = true,
        value = "评价实体") @RequestBody @NotNull(message = "评价不能为空") AddCommentaryRequest request) {
        return commentaryIntegration.addCommentary(request);
    }

    /**
     * Description: 修改评价
     *
     * @param request 请求入参
     * @return CommentaryDto
     */
    @RequestMapping(method = RequestMethod.PATCH)
    @ApiOperation("修改评价")
    public CommentaryBizDto updateCommentary(@ApiParam(required = true,
        value = "评价实体") @RequestBody @NotNull(message = "入参不能为空") UpdateCommentaryRequest request) {
        return commentaryIntegration.updateCommentary(request);
    }

    /**
     * Description: 删除评价
     *
     * @param commentaryId commentaryId
     * @return int
     */
    @RequestMapping(value = "/{commentaryId}", method = RequestMethod.DELETE)
    @ApiOperation("删除评价")
    public int deleteCommentary(@ApiParam(required = true, value = "评价主键") @PathVariable("commentaryId") @NotNull(
        message = "评价id不能为空") @NotBlank(message = "评价id不能为空") Long commentaryId) {
        return commentaryIntegration.deleteCommentary(commentaryId);
    }
}
