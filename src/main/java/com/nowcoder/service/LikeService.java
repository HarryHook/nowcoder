package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
赞踩功能
主要三个业务：
1.用户点赞
2.用户点踩
3.判断用户是否喜欢（是否点过赞）
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    /*
    喜欢返回1， 不喜欢返回-1， 否则返回0
     */
    public int getLikeStatus(int userId, int entityId, int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDiSLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(disLikeKey, String.valueOf(userId))) {
            return -1;
        }
        return 0;
    }

    public long like(int userId, int entityId, int entityType) {
        // 在喜欢集合里增加
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        // 从反对里删除
        String disLikeKey = RedisKeyUtil.getDiSLikeKey(entityId, entityType);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);

    }

    public long disLike(int userId, int entityId, int entityType) {
        // 在反对集合里增加
        String disLikeKey = RedisKeyUtil.getDiSLikeKey(entityId, entityType);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        // 从喜欢里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);

    }
}
