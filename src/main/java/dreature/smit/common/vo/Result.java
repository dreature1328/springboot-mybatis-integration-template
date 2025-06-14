package dreature.smit.common.vo;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private boolean success;  // 操作是否成功
    private String code;      // 业务状态码
    private String message;   // 人类可读消息
    private T data;           // 业务数据

    // 私有构造器
    private Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应
    public static <T> Result<T> success() {
        return new Result<>(true, "SUCCESS", "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "SUCCESS", "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, "SUCCESS", message, data);
    }

    // 错误响应
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(false, code, message, null);
    }
    // 基础方法
    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}