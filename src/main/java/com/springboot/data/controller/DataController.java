package com.springboot.data.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Consumer;

@RestController
public class DataController {

	@Autowired
	private DataService dataService;

	// 集成数据
	@RequestMapping("/data/integrate")
	public void integrateData() throws Exception {
		dataService.integrateData(dataService.getParams());
		return ;
	}

	// 优化集成数据
	@RequestMapping("/data/integratex")
	public void integrateDataOptimized() {
		dataService.integrateDataOptimized(dataService.getParams());
		return ;
	}

	// 分页优化集成数据
	@RequestMapping("/data/pintegratex")
	public void pageIntegrateDataOptimized() {
		dataService.pageIntegrateDataOptimized(dataService.getParams());
		return ;
	}

}
