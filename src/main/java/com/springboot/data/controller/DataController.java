package com.springboot.data.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.data.common.pojo.Data;
import com.springboot.data.common.utils.JSONUtils;
import com.springboot.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DataController {
	@Autowired
	private DataService dataService;
	@RequestMapping("/data/clear")
	public void clearData() throws Exception {
		dataService.clearData();
		return ;
	}
	@RequestMapping("/data/select")
	public JSONObject selectData(String id) throws Exception {
		return JSONUtils.objToJSONObj(dataService.selectData(id));
	}
	@RequestMapping("/data/bselect")
	public JSONArray batchSelectData(String ids) throws Exception {
		return JSONUtils.listToJSONArr(dataService.batchSelectData(ids));
	}
	@RequestMapping("/data/pselect")
	public JSONArray pageSelectData(String ids) throws Exception {
		return JSONUtils.listToJSONArr(dataService.pageSelectData(ids));
	}
	@RequestMapping("/data/insert")
	public void insertDate() throws Exception {
		dataService.insertData();
		return ;
	}
	@RequestMapping("/data/binsert")
	public void batchInsertData() throws Exception {
		dataService.batchInsertData();
		return ;
	}
	@RequestMapping("/data/pinsert")
	public void pageInsertData() throws Exception {
		dataService.pageInsertData();
		return ;
	}
	@RequestMapping("/data/update")
	public void updateData() throws Exception {
		dataService.updateData();
		return ;
	}
	@RequestMapping("/data/bupdate")
	public void batchUpdateData() throws Exception {
		dataService.batchUpdateData();
		return ;
	}
	@RequestMapping("/data/pupdate")
	public void pageUpdateData() throws Exception {
		dataService.pageUpdateData();
		return ;
	}
	@RequestMapping("/data/write")
	public void insertOrUpdateDate() throws Exception {
		dataService.insertOrUpdateData();
		return ;
	}
	@RequestMapping("/data/bwrite")
	public void batchInsertOrUpdateData() throws Exception {
		dataService.batchInsertOrUpdateData();
		return ;
	}
	@RequestMapping("/data/pwrite")
	public void pageInsertOrUpdateData() throws Exception {
		dataService.pageInsertOrUpdateData();
		return ;
	}
//	@RequestMapping("/data/write/periodical")
//	public void insertDataPeriodically() throws Exception {
//		// 默认现在起每 3600 秒执行一次
//		dataService.insertDataPeriodically(3600);
//		return ;
//	}
//	@RequestMapping("/data/write/scheduled")
//	public void insertDataOnSchedule() throws Exception {
//		// 默认指定时间后每 3600 秒执行一次
//		dataService.insertDataOnSchedule(3600);
//		return ;
//	}
	@RequestMapping("/data/delete")
	public void deleteData(String id) throws Exception {
		dataService.deleteData(id);
		return ;
	}
	@RequestMapping("/data/bdelete")
	public void batchDeleteData(String ids) throws Exception {
		dataService.batchDeleteData(ids);
		return ;
	}
	@RequestMapping("/data/pdelete")
	public void pageDeleteData(String ids) throws Exception {
		dataService.pageDeleteData(ids);
		return ;
	}


//	@RequestMapping("/data/test")
//	public String testData(HttpServletRequest request) throws Exception {
//		// request.getParameterMap() 返回类型为 Map<String,String[]> 的请求参数
//		return dataService.queryData(request.getParameterMap());
//	}
}
