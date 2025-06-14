package dreature.smit.controller;

import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.common.vo.Result;
import dreature.smit.entity.Data;
import dreature.smit.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static dreature.smit.common.util.BatchUtils.mapEach;

@RestController
@RequestMapping("/data")
public class DataController {
	@Autowired
	private DataService dataService;

	// 生成数据（测试用）
	@RequestMapping("/generate")
	public ResponseEntity<Result> generate() {
		List<Data> result = dataService.generate(10);
		int count = result.size();
		String message = String.format("生成 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 解析 JSON 数据（测试用）
	@RequestMapping ("/parse-json")
	public ResponseEntity<Result> parseFromJson() {
		List<Data> result = dataService.parseFromJson("script/mock_data.json");
		int count = result.size();
		String message = String.format("解析 %d 条 JSON 数据", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 解析 XML 数据（测试用）
	@RequestMapping("/parse-xml")
	public ResponseEntity<Result> parseFromXml() {
		List<Data> result = dataService.parseFromXml("script/mock_data.xml");
		int count = result.size();
		String message = String.format("解析 %d 条 XML 数据", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 生成请求参数（测试用）
	@RequestMapping("/params")
	public ResponseEntity<Result> generateParams() {
		List<Map<String, String>> result = dataService.generateParams(10);
		int count = result.size();
		String message = String.format("生成 %d 组参数", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 依次同步请求
	@RequestMapping("/request")
	public ResponseEntity<Result> request() {
		List<Map<String, String>> paramsList = dataService.generateParams(10);
		List<String> responses = mapEach(paramsList, dataService::request);
		int count1 = paramsList.size();
		int count2 = responses.size();
		String message = String.format("应发 %d 次请求，收到 %d 条响应", count1, count2);
		return ResponseEntity.ok().body(Result.success(message, responses));
	}

	// 批量异步请求
	@RequestMapping("/request-b")
	public ResponseEntity<Result> requestBatch() {
		List<Map<String, String>> paramsList = dataService.generateParams(1000);
		List<String> responses = dataService.requestBatch(paramsList);
		int count1 = paramsList.size();
		int count2 = responses.size();
		String message = String.format("应发 %d 次请求，收到 %d 条响应", count1, count2);
		return ResponseEntity.ok().body(Result.success(message, responses));
	}

	// 分页异步请求
	@RequestMapping("/request-p")
	public ResponseEntity<Result> requestPage() {
		List<Map<String, String>> paramsList = dataService.generateParams(1000);
		List<String> responses = dataService.requestPage(paramsList);
		int count1 = paramsList.size();
		int count2 = responses.size();
		String message = String.format("应发 %d 次请求，收到 %d 条响应", count1, count2);
		return ResponseEntity.ok().body(Result.success(message, responses));
	}

	// 生成响应内容（测试用）
	@RequestMapping("/responses")
	public ResponseEntity<Result> generateResponses() {
		List<String> result = dataService.generateResponses(10);
		int count = result.size();
		String message = String.format("生成 %d 条响应", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 传统转换
	@RequestMapping("/transform")
	public ResponseEntity<Result> transform() {
//		List<Map<String, String>> paramsList = dataService.generateParams();
//		List<String> responses = dataService.request(paramsList);
		List<String> responses = dataService.generateResponses(1000);
		List<Data> dataList = mapEach(responses, dataService::transform);
		int count = dataList.size();
		String message = String.format("转换 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, dataList));
	}

	// 流水线转换
	@RequestMapping("/transform-p")
	public ResponseEntity<Result> transformPipeline() {
//		List<Map<String, String>> paramsList = dataService.generateParams();
//		List<String> responses = dataService.requestPage(paramsList);
		List<String> responses = dataService.generateResponses(1000);
		List<Data> dataList = dataService.transformPipeline(responses);
		int count = dataList.size();
		String message = String.format("转换 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, dataList));
	}

	// 集成
	@RequestMapping("/integrate")
	public ResponseEntity<Result> integrate() {
		List<Map<String, String>> paramsList = dataService.generateParams(10);
		IntegrationStats stats = dataService.integrate(paramsList);
		return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
	}

	// 优化集成
	@RequestMapping("/integrate-x")
	public ResponseEntity<Result> integrateOptimized() {
		List<Map<String, String>> paramsList = dataService.generateParams(1000);
		List<IntegrationStats> result = dataService.integrateOptimized(paramsList);
		return ResponseEntity.ok().body(Result.success(result.get(0).generateReport(), null));
	}

	// 分页优化集成
	@RequestMapping("/integrate-px")
	public ResponseEntity<Result> integratePageOptimized() {
		List<Map<String, String>> paramsList = dataService.generateParams(1000);
		List<IntegrationStats> statsList = dataService.integratePageOptimized(paramsList);

		// 合并多个报告
		StringBuilder combinedReport = new StringBuilder();
		for (int i = 0; i < statsList.size(); i++) {
			combinedReport.append("第").append(i + 1).append("页: ")
					.append(statsList.get(i).generateReport());
			if (i < statsList.size() - 1) {
				combinedReport.append("\n");
			}
		}

		return ResponseEntity.ok().body(Result.success(combinedReport.toString(), null));
	}

	// 查询总数
	@RequestMapping("/count")
	public ResponseEntity<Result> countAll() {
		int count = dataService.countAll();
		String message = String.format("查询到 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, count));
	}

	// 查询 n 条
	@RequestMapping("/find")
	public ResponseEntity<Result> findTopN(int n) {
		List<Data> result = dataService.findTopN(n);
		int count = result.size();
		String message = String.format("查询到 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 分页查询
	@RequestMapping("/select-p")
	public ResponseEntity<Result> selectPage(String ids) {
		List<Data> result = dataService.selectPageByIds(Arrays.asList(ids.split(",")));
		int count = result.size();
		String message = String.format("分页查询到 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 分页插入
	@RequestMapping("/insert-p")
	public ResponseEntity<Result> insertPage() {
		int affectedRows = dataService.insertPage(dataService.generate(100000));
		String message = String.format("分页插入 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message,null));
	}

	// 分页更新
	@RequestMapping("/update-p")
	public ResponseEntity<Result> updatePage() {
		int affectedRows = dataService.updatePage(dataService.generate(100000));
		String message = String.format("分页更新 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message, null));
	}
	// 分页插入或更新
	@RequestMapping("/upsert-p")
	public ResponseEntity<Result> upsertPage() {
		int affectedRows = dataService.upsertPage(dataService.generate(100000));
		String message = String.format("分页插入或更新 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message, null));
	}

	// 分页删除
	@RequestMapping("/delete-p")
	public ResponseEntity<Result> deletePageByIds(String ids) {
		int affectedRows = dataService.deletePageByIds(Arrays.asList(ids.split(",")));
		String message = String.format("分页删除 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message, null));
	}

	// 清空
	@RequestMapping("/truncate")
	public ResponseEntity<Result> truncate() {
		int count = dataService.countAll();
		dataService.truncate();
		String message = String.format("清空 %d 条数据", count);
		return ResponseEntity.ok().body(Result.success(message, null));
	}
}
