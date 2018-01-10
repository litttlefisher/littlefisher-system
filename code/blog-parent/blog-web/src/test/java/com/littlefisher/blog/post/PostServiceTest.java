package com.littlefisher.blog.post;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.littlefisher.blog.model.PostDto;
import com.littlefisher.blog.service.IPostService;
import com.littlefisher.blog.model.ext.PostExtDto;
import com.littlefisher.blog.request.GetPostList4PagerByCondRequest;
import com.littlefisher.core.utils.LittleFisherLogger;

/**
 * Description: PostServiceTest.java
 *
 * Created on 2017年12月04日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest {

    private LittleFisherLogger logger = LittleFisherLogger.getLogger(PostServiceTest.class);

    @Autowired
    private IPostService postService;

    @Test
    public void testGetPostList4PagerByCond() {
        GetPostList4PagerByCondRequest req = new GetPostList4PagerByCondRequest();
        req.setPageNum(1);
        req.setPageSize(20);
        List<PostExtDto> postList = postService.getPostList4PagerByCond(req);
        logger.debug("postList: [{}]", postList);
    }

    @Test
    public void testGetPostWithoutBlobById() {
        Long postId = 1L;
        PostDto postDto = postService.getPostWithoutBlobById(postId);

        logger.debug("postDto: [{}]", postDto);
    }

}
