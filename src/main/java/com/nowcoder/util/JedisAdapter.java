package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    public static void printer(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();  //全部删掉

        jedis.set("hello", "world");
        printer(1, jedis.get("hello"));

        jedis.rename("hello", "reHello");
        printer(2, jedis.get("reHello"));

        jedis.setex("hello2", 15, "world1");
        printer(3, jedis.get("hello2"));
        jedis.set("pv", "100");
        jedis.incr("pv");
        printer(4, jedis.get("pv"));
        jedis.incrBy("pv", 6);
        printer(5, jedis.get("pv"));
        //List
        String listName = "list";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a-" + String.valueOf(i));
        }
        printer(6, jedis.lrange(listName, 0, 12));
        printer(7, jedis.llen(listName));
        printer(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a-2", "xx"));
        printer(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a-2", "YY"));
        printer(10, jedis.lrange(listName, 0, 12));
        //HashMap
        String userKey = "user1";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "15");
        jedis.hset(userKey, "phone", "18717396111");
        printer(11, jedis.hgetAll(userKey));

        jedis.hdel(userKey, "phone");
        printer(12, jedis.hkeys(userKey));
        printer(13, jedis.hvals(userKey));
        printer(14, jedis.hexists(userKey, "email"));
        printer(15, jedis.hexists(userKey, "name"));

        jedis.hsetnx(userKey, "name", "chenhaowei");
        jedis.hsetnx(userKey, "school", "xupt");
        printer(16, jedis.hgetAll(userKey));

        //Set
        String like1 = "newLike1";
        String like2 = "newLike2";

        for (int i = 0; i < 10; i++) {
            jedis.sadd(like1, String.valueOf(i));
            jedis.sadd(like2, String.valueOf(i * i));
        }
        printer(17, jedis.smembers(like1));
        printer(18, jedis.smembers(like2));
        printer(19, jedis.sinter(like1, like2));
        printer(20, jedis.sunion(like1, like2));
        printer(21, jedis.sdiff(like2, like1));
        printer(22, jedis.sdiff(like1, like2));

        jedis.srem(like1, "5");
        printer(23, jedis.smembers(like1));
        printer(24, jedis.scard(like1));
        //Sorted Set

        String rankKey = "rankKey";
        jedis.zadd(rankKey, 100, "jim");
        jedis.zadd(rankKey, 90, "tom");
        jedis.zadd(rankKey, 85, "lucy");
        jedis.zadd(rankKey, 95, "Hao");
        jedis.zadd(rankKey, 60, "xiaoming");

        printer(25, jedis.zcard(rankKey));
        printer(26, jedis.zcount(rankKey, 90, 100));
        printer(27, jedis.zscore(rankKey, "jim"));
        jedis.zincrby(rankKey, 2, "lucy");
        printer(28, jedis.zscore(rankKey, "lucy"));
        printer(29, jedis.zrange(rankKey, 1, 3));
        printer(30, jedis.zrevrange(rankKey, 1, 3));

        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100")) {
            printer(31, tuple.getElement() + " " + tuple.getScore());
        }

        printer(32, jedis.zrank(rankKey, "tom"));
        printer(33, jedis.zrevrank(rankKey, "tom"));


        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("Pool " + i);
        }
    }

    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("127.0.0.1", 6379);

    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}

