package xyz.dreature.smit.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.Instant;

@Schema(description = "响应结果")
public class Result<T> implements Serializable {
    // ===== 字段 =====
    @Schema(description = "操作是否成功", example = "true")
    private boolean success;

    @Schema(description = "业务状态码", example = "SUCCESS")
    private String code;

    @Schema(description = "人类可读消息", example = "操作成功")
    private String message;

    @Schema(description = "业务数据")
    private T data;

    @Schema(description = "响应时间戳（毫秒）")
    private long timestamp;

    // ===== 构造方法 =====
    // 无参构造器
    public Result() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    // 全参构造器
    private Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    // 复制构造器
    public Result(Result<T> result) {
        this.success = result.success;
        this.code = result.code;
        this.message = result.message;
        this.data = result.data;
        this.timestamp = result.timestamp;
    }

    // ===== 成功响应 =====
    public static <T> Result<T> success() {
        return new Result<>(true, "SUCCESS", "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "SUCCESS", "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, "SUCCESS", message, data);
    }

    // ===== 错误响应 =====
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> error(String code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    // ===== Getter 与 Setter 方法 =====
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // ===== 其他 =====
    // 字符串表示
    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
