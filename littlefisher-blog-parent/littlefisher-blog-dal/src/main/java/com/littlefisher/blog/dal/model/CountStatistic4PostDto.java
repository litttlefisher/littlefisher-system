package com.littlefisher.blog.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 *
 * Description: count_statistic_4_post 实体
 *
 * Created on 2018年03月30日
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@Table(name = "count_statistic_4_post")
public class CountStatistic4PostDto implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * 数量统计主键
     */
    @Id
    @Column(name = "count_statistic_id")
    private Long countStatisticId;

    /**
     * 博文主键
     */
    @Column(name = "post_id")
    private Long postId;

    /**
     * 阅读数
     */
    @Column(name = "read_times")
    private Long readTimes;

    /**
     * 评论数
     */
    @Column(name = "commented_times")
    private Long commentedTimes;

    /**
     * 点赞数
     */
    @Column(name = "liked_times")
    private Long likedTimes;

    public Long getCountStatisticId() {
        return countStatisticId;
    }

    public void setCountStatisticId(Long countStatisticId) {
        this.countStatisticId = countStatisticId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(Long readTimes) {
        this.readTimes = readTimes;
    }

    public Long getCommentedTimes() {
        return commentedTimes;
    }

    public void setCommentedTimes(Long commentedTimes) {
        this.commentedTimes = commentedTimes;
    }

    public Long getLikedTimes() {
        return likedTimes;
    }

    public void setLikedTimes(Long likedTimes) {
        this.likedTimes = likedTimes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", countStatisticId=").append(countStatisticId);
        sb.append(", postId=").append(postId);
        sb.append(", readTimes=").append(readTimes);
        sb.append(", commentedTimes=").append(commentedTimes);
        sb.append(", likedTimes=").append(likedTimes);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public static class Builder {
        private CountStatistic4PostDto instance;

        private Builder() {
            super();
        }

        public static Builder getInstance() {
            Builder builder = new Builder();
            builder.instance = new CountStatistic4PostDto();
            return builder;
        }

        public static Builder getInstance(CountStatistic4PostDto instance) {
            Builder builder = new Builder();
            builder.instance = instance;
            return builder;
        }

        public Builder addCountStatisticId(Long countStatisticId) {
            this.instance.setCountStatisticId(countStatisticId);
            return this;
        }

        public Builder addPostId(Long postId) {
            this.instance.setPostId(postId);
            return this;
        }

        public Builder addReadTimes(Long readTimes) {
            this.instance.setReadTimes(readTimes);
            return this;
        }

        public Builder addCommentedTimes(Long commentedTimes) {
            this.instance.setCommentedTimes(commentedTimes);
            return this;
        }

        public Builder addLikedTimes(Long likedTimes) {
            this.instance.setLikedTimes(likedTimes);
            return this;
        }

        public CountStatistic4PostDto build() {
            return this.instance;
        }
    }
}