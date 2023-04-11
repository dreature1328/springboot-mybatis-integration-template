package com.springboot.data.common.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HTTPResult {

	// 响应码
	private Integer code;
	// 消息
	private String message;
	// 数据
	private Object data;

	// HTTP 状态码对应的英文提示语
	public static Map<Integer, String> statusCodesEn = new HashMap<Integer, String>() {{
		put(100, "Continue");
		put(101, "Switching Protocols");
		put(200, "OK");
		put(201, "Created");
		put(202, "Accepted");
		put(204, "No Content");
		put(301, "Moved Permanently");
		put(302, "Found");
		put(304, "Not Modified");
		put(400, "Bad Request");
		put(401, "Unauthorized");
		put(403, "Forbidden");
		put(404, "Not Found");
		put(500, "Internal Server Error");
		put(501, "Not Implemented");
		put(503, "Service Unavailable");
	}};

	// HTTP 状态码对应的中文提示语
	public static Map<Integer, String> statusCodesZh = new HashMap<Integer, String>() {{
		put(100, "继续");
		put(101, "切换协议");
		put(200, "成功");
		put(201, "已创建");
		put(202, "已接受");
		put(204, "无内容");
		put(301, "永久移动");
		put(302, "临时移动");
		put(304, "未修改");
		put(400, "错误请求");
		put(401, "未授权");
		put(403, "禁止访问");
		put(404, "未找到");
		put(500, "内部服务器错误");
		put(501, "未实现");
		put(503, "服务不可用");
	}};

	// 构造函数
	public HTTPResult(Integer code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	// Getter 和 Setter 函数
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	// 成功
	public static HTTPResult success(Object data) {
		Integer code = 200;
		return new HTTPResult(code, statusCodesZh.get(code), data);
	}
	public static HTTPResult success(String message, Object data) {
		Integer code = 200;
		return new HTTPResult(code, message, data);
	}
	// 错误请求
	public static HTTPResult badRequest() {
		Integer code = 400;
		return new HTTPResult(code, statusCodesZh.get(code), null);
	}
	public static HTTPResult badRequest(String message) {
		Integer code = 400;
		return new HTTPResult(code, message, null);
	}
	// 未授权
	public static HTTPResult unauthorized() {
		Integer code = 401;
		return new HTTPResult(code, statusCodesZh.get(code), null);
	}
	public static HTTPResult unauthorized(String message) {
		Integer code = 401;
		return new HTTPResult(code, message, null);
	}
	// 禁止访问
	public static HTTPResult forbidden() {
		Integer code = 403;
		return new HTTPResult(code, statusCodesZh.get(code), null);
	}
	public static HTTPResult forbidden(String message) {
		Integer code = 403;
		return new HTTPResult(code, message, null);
	}
	// 未找到
	public static HTTPResult notFound() {
		Integer code = 404;
		return new HTTPResult(code, statusCodesZh.get(code), null);
	}
	public static HTTPResult notFound(String message) {
		Integer code = 404;
		return new HTTPResult(code, message, null);
	}
	// 内部服务器错误
	public static HTTPResult internalServerError() {
		Integer code = 500;
		return new HTTPResult(code, statusCodesZh.get(code), null);
	}
	public static HTTPResult internalServerError(String message) {
		Integer code = 500;
		return new HTTPResult(code, message, null);
	}
	// 服务不可用
	public static HTTPResult serviceUnavailable() {
		Integer code = 503;
		return new HTTPResult(code, statusCodesZh.get(code), null);
	}
	public static HTTPResult serviceUnavailable(String message) {
		Integer code = 503;
		return new HTTPResult(code, message, null);
	}

	// 获取状态码对应的英文提示语
	public String getMessageEn() {
		return statusCodesEn.get(this.code);
	}
	// 获取状态码对应的中文提示语
	public String getMessageZh() {
		return statusCodesZh.get(this.code);
	}

	// 将返回结果对象转化为 JSON 对象
	public JSONObject toJSONObject() {
		return (JSONObject) JSON.toJSON(this);
	}
	// 将返回结果对象转化为 JSON 字符串
	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}
