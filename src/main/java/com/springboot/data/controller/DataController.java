package com.springboot.data.controller;

import com.springboot.data.common.pojo.Data;
import com.springboot.data.common.vo.HTTPResult;
import com.springboot.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class DataController {

	@Autowired
	private DataService dataService;

	// 依次查询
	@ResponseBody
	@RequestMapping("/data/select")
	public HTTPResult selectData(String id) throws Exception {
		return HTTPResult.success(dataService.selectData(id));
	}

	// 批量查询
	@ResponseBody
	@RequestMapping("/data/bselect")
	public HTTPResult batchSelectData(String ids) throws Exception {
		List<String> idList = Arrays.asList(ids.split(","));
		return HTTPResult.success(dataService.batchSelectData(idList));
	}

	// 分页查询
	@ResponseBody
	@RequestMapping("/data/pselect")
	public HTTPResult pageSelectData(String ids) throws Exception {
		List<String> idList = Arrays.asList(ids.split(","));
		return HTTPResult.success(dataService.batchSelectData(idList));
	}

	// 获取参数
	@ResponseBody
	@RequestMapping("/data/params")
	public HTTPResult getDataParams() {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		return HTTPResult.success(paramsList);
	}

	// 依次同步请求
	@ResponseBody
	@RequestMapping("/data/request")
	public HTTPResult requestData() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		List<String> responses = dataService.requestData(paramsList);
		return HTTPResult.success(responses);
	}

	// 批量异步请求
	@ResponseBody
	@RequestMapping("/data/brequest")
	public HTTPResult batchRequestData() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		List<String> responses = dataService.batchRequestData(paramsList);
		return HTTPResult.success(responses);
	}

	// 分页异步请求
	@ResponseBody
	@RequestMapping("/data/prequest")
	public HTTPResult pageRequestData() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		List<String> responses = dataService.pageRequestData(paramsList);
		return HTTPResult.success(responses);
	}

	// 传统加工
	@ResponseBody
	@RequestMapping("/data/process")
	public HTTPResult processData() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		List<String> responses = dataService.requestData(paramsList);
		List<Data> dataList = dataService.processData(responses);
		return HTTPResult.success(dataList);
	}

	// 流水线加工
	@ResponseBody
	@RequestMapping("/data/pprocess")
	public HTTPResult pipelineProcessData() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		List<String> responses = dataService.pageRequestData(paramsList);
		List<Data> dataList = dataService.pielineProcessData(responses);
		return HTTPResult.success(dataList);
	}

	// 集成
	@ResponseBody
	@RequestMapping("/data/integrate")
	public HTTPResult integrateData() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		dataService.integrateData(paramsList);
		return HTTPResult.success(null);
	}

	// 优化集成
	@ResponseBody
	@RequestMapping("/data/integratex")
	public HTTPResult integrateDataOptimized() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		dataService.integrateDataOptimized(paramsList);
		return HTTPResult.success(null);
	}

	// 分页优化集成
	@ResponseBody
	@RequestMapping("/data/pintegratex")
	public HTTPResult pageIntegrateDataOptimized() throws Exception {
		List<Map<String, String>> paramsList = dataService.getDataParams();
		dataService.pageIntegrateDataOptimized(paramsList);
		return HTTPResult.success(null);
	}

}
