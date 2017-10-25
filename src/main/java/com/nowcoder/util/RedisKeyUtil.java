package com.nowcoder.util;


public class RedisKeyUtil {
    private static String SPILT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";

    public static String getLikeKey(int entityId, int entityType) {
        return BIZ_LIKE + SPILT + String.valueOf(entityType) + SPILT + String.valueOf(entityId);
    }
    public static String getDiSLikeKey(int entityId, int entityType) {
        return BIZ_DISLIKE + SPILT + String.valueOf(entityType) + SPILT + String.valueOf(entityId);
    }
}
