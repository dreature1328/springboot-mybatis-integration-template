package com.springboot.data.common.vo;

public class HttpResult {

	// 响应码
	private Integer code;
	private String msg;
	private Object data;

	public HttpResult(Integer code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static HttpResult success(Object data){
		return success("", data);
	}
	public static HttpResult success(String msg, Object data){
		Integer code = 20000;
		Boolean success = true;
		return new HttpResult(code, msg, data);
	}
	public static HttpResult fail(Object data){
		return fail("", data);
	}
	public static HttpResult fail(String msg, Object data){
		Integer code = 50000;
		Boolean success = false;
		return new HttpResult(code, msg, data);
	}

}