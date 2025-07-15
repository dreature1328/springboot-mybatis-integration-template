package xyz.dreature.smit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// 测试接口（Redis 操作）
@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ===== Key 操作 =====
    // 删除指定键
    @RequestMapping("/key/delete")
    public ResponseEntity<Result> deleteKey(@RequestParam String key) {
        Boolean result = redisTemplate.delete(key);
        return ResponseEntity.ok(Result.success(result ? "删除成功" : "键不存在", result));
    }

    // 检查键是否存在
    @RequestMapping("/key/exist")
    public ResponseEntity<Result> hasKey(@RequestParam String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return ResponseEntity.ok(Result.success(exists ? "键存在" : "键不存在", exists));
    }

    // 设置键过期时间
    @RequestMapping("/key/expire")
    public ResponseEntity<Result> setExpire(
            @RequestParam String key,
            @RequestParam long timeout,
            @RequestParam(required = false, defaultValue = "SECONDS") TimeUnit unit) {
        Boolean result = redisTemplate.expire(key, timeout, unit);
        return ResponseEntity.ok(Result.success(result ? "设置过期成功" : "键不存在", result));
    }

    // ===== String 操作 =====
    // 设置键值对
    @RequestMapping("/string/set")
    public ResponseEntity<Result> setStringValue(
            @RequestParam String key,
            @RequestBody Data value,
            @RequestParam(required = false) Long timeout,
            @RequestParam(required = false, defaultValue = "SECONDS") TimeUnit unit) {

        if (timeout != null && unit != null) {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return ResponseEntity.ok(Result.success("设置带过期时间的键值对成功", null));
        } else {
            redisTemplate.opsForValue().set(key, value);
            return ResponseEntity.ok(Result.success("设置键值对成功", null));
        }
    }

    // 获取键值
    @RequestMapping("/string/get")
    public ResponseEntity<Result> getStringValue(@RequestParam String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ?
                ResponseEntity.ok(Result.success("获取值成功", value)) :
                ResponseEntity.ok(Result.success("键不存在", null));
    }

    // 键不存在时设置值
    @RequestMapping("/string/set-if-absent")
    public ResponseEntity<Result> setStringIfAbsent(
            @RequestParam String key,
            @RequestBody Object value) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
        return ResponseEntity.ok(Result.success(result ? "设置成功" : "键已存在", result));
    }

    // 值递增操作
    @RequestMapping("/string/increment")
    public ResponseEntity<Result> incrementStringValue(
            @RequestParam String key,
            @RequestParam long delta) {
        Long result = redisTemplate.opsForValue().increment(key, delta);
        return ResponseEntity.ok(Result.success("递增成功", result));
    }

    // ===== Hash 操作 =====
    // 设置哈希字段值
    @RequestMapping("/hash/set")
    public ResponseEntity<Result> setHash(
            @RequestParam String key,
            @RequestParam String field,
            @RequestBody Object value) {
        redisTemplate.opsForHash().put(key, field, value);
        return ResponseEntity.ok(Result.success("设置哈希字段成功", null));
    }

    // 获取哈希字段值
    @RequestMapping("/hash/get")
    public ResponseEntity<Result> getHash(
            @RequestParam String key,
            @RequestParam String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ?
                ResponseEntity.ok(Result.success("获取哈希字段成功", value)) :
                ResponseEntity.ok(Result.success("字段不存在", null));
    }

    // 获取整个哈希表
    @RequestMapping("/hash/all")
    public ResponseEntity<Result> getAllHash(@RequestParam String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return ResponseEntity.ok(Result.success("获取整个哈希表成功", entries));
    }

    // 删除哈希字段
    @DeleteMapping("/hash/delete")
    public ResponseEntity<Result> deleteHash(
            @RequestParam String key,
            @RequestParam String field) {
        Long result = redisTemplate.opsForHash().delete(key, field);
        return ResponseEntity.ok(Result.success("删除哈希字段成功", result));
    }

    // ===== List 操作 =====
    // 左端添加元素
    @RequestMapping("/list/push-left")
    public ResponseEntity<Result> leftPushToList(
            @RequestParam String key,
            @RequestBody Object value) {
        Long size = redisTemplate.opsForList().leftPush(key, value);
        return ResponseEntity.ok(Result.success("左端添加元素成功", size));
    }

    // 右端添加元素
    @RequestMapping("/list/push-right")
    public ResponseEntity<Result> rightPushToList(
            @RequestParam String key,
            @RequestBody Object value) {
        Long size = redisTemplate.opsForList().rightPush(key, value);
        return ResponseEntity.ok(Result.success("右端添加元素成功", size));
    }

    // 获取列表范围元素
    @RequestMapping("/list/range")
    public ResponseEntity<Result> getListRange(
            @RequestParam String key,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "-1") long end) {
        List<Object> range = redisTemplate.opsForList().range(key, start, end);
        return ResponseEntity.ok(Result.success("获取列表范围成功", range));
    }

    // 获取列表长度
    @RequestMapping("/list/size")
    public ResponseEntity<Result> getListSize(
            @RequestParam String key) {
        Long size = redisTemplate.opsForList().size(key);
        return ResponseEntity.ok(Result.success("获取列表长度成功", size));
    }

    // 左端弹出元素
    @RequestMapping("/list/pop-left")
    public ResponseEntity<Result> leftPopFromList(
            @RequestParam String key) {
        Object value = redisTemplate.opsForList().leftPop(key);
        return ResponseEntity.ok(Result.success("左端弹出元素成功", value));
    }

    // 右端弹出元素
    @RequestMapping("/list/pop-right")
    public ResponseEntity<Result> rightPopFromList(
            @RequestParam String key) {
        Object value = redisTemplate.opsForList().rightPop(key);
        return ResponseEntity.ok(Result.success("右端弹出元素成功", value));
    }


    // ===== Set 操作 =====
    // 添加集合元素
    @RequestMapping("/set/add")
    public ResponseEntity<Result> addToSet(
            @RequestParam String key,
            @RequestBody Object value) {
        Long result = redisTemplate.opsForSet().add(key, value);
        return ResponseEntity.ok(Result.success("添加集合元素成功", result));
    }

    // 获取集合所有元素
    @RequestMapping("/set/members")
    public ResponseEntity<Result> getSetMembers(@RequestParam String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        return ResponseEntity.ok(Result.success("获取集合所有元素成功", members));
    }

    // ===== ZSet 操作 =====
    // 添加有序集合元素
    @RequestMapping("/zset/add")
    public ResponseEntity<Result> addToZSet(
            @RequestParam String key,
            @RequestBody Object value,
            @RequestParam double score) {
        Boolean result = redisTemplate.opsForZSet().add(key, value, score);
        return ResponseEntity.ok(Result.success("添加有序集合元素成功", result));
    }

    // 获取有序集合范围元素
    @RequestMapping("/zset/range")
    public ResponseEntity<Result> getZSetRange(
            @RequestParam String key,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "-1") long end) {
        Set<Object> range = redisTemplate.opsForZSet().range(key, start, end);
        return ResponseEntity.ok(Result.success("获取有序集合范围成功", range));
    }
}
