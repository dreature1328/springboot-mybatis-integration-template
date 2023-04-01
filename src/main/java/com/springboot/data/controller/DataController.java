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

	// 迁移
	@RequestMapping("/data/migrate")
	public void migrateData() throws Exception {
		dataService.migrateData();
		return ;
	}

	// 迁移优化
	@RequestMapping("/data/migratex")
	public void migrateDataOptimized() throws Exception {
		dataService.migrateDataOptimized();
		return ;
	}

}
