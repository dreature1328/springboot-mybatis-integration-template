package dreature.smit.controller;

import dreature.smit.common.stats.IntegrationStats;
import dreature.smit.common.vo.Result;
import dreature.smit.entity.Data;
import dreature.smit.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
	public ResponseEntity<Result> generate(
			@RequestParam(name = "count", defaultValue = "10") int count
	) {
		List<Data> result = dataService.generate(count);
		int resultCount = result.size();
		String message = String.format("生成 %d 条数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 解析 JSON 数据（测试用）
	@RequestMapping ("/parse-json")
	public ResponseEntity<Result> parseFromJson() {
		List<Data> result = dataService.parseFromJson("script/mock_data.json");
		int resultCount = result.size();
		String message = String.format("解析 %d 条 JSON 数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 解析 XML 数据（测试用）
	@RequestMapping("/parse-xml")
	public ResponseEntity<Result> parseFromXml() {
		List<Data> result = dataService.parseFromXml("script/mock_data.xml");
		int resultCount = result.size();
		String message = String.format("解析 %d 条 XML 数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 生成请求参数（测试用）
	@RequestMapping("/params")
	public ResponseEntity<Result> generateParams(
			@RequestParam(name = "count", defaultValue = "10") int count
	) {
		List<Map<String, String>> result = dataService.generateParams(count);
		int resultCount = result.size();
		String message = String.format("生成 %d 组参数", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 依次同步请求
	@RequestMapping("/request")
	public ResponseEntity<Result> request(
			@RequestParam(name = "total", defaultValue = "10") int total
	) {
		List<Map<String, String>> paramsList = dataService.generateParams(total);
		List<String> responses = mapEach(paramsList, dataService::request);
		int resultCount = responses.size();
		String message = String.format("依次同步发送 %d 次请求，收到 %d 条响应", total, resultCount);
		return ResponseEntity.ok().body(Result.success(message, responses));
	}

	// 分批异步请求
	@RequestMapping("/request-batch")
	public ResponseEntity<Result> requestBatch(
			@RequestParam(name = "total", defaultValue = "1000") int total,
			@RequestParam(name = "batch-size", defaultValue = "200") int batchSize
	) {
		List<Map<String, String>> paramsList = dataService.generateParams(total);
		List<String> responses = dataService.requestBatch(paramsList, batchSize);
		int resultCount = responses.size();
		String message = String.format("分批异步发送 %d 次请求，收到 %d 条响应", total, resultCount);
		return ResponseEntity.ok().body(Result.success(message, responses));
	}

	// 生成响应内容（测试用）
	@RequestMapping("/responses")
	public ResponseEntity<Result> generateResponses(
			@RequestParam(name = "count", defaultValue = "10") int count
	) {
		List<String> result = dataService.generateResponses(count);
		int resultCount = result.size();
		String message = String.format("生成 %d 条响应", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 传统转换
	@RequestMapping("/transform")
	public ResponseEntity<Result> transform(
			@RequestParam(name = "total", defaultValue = "1000") int total
	) {
//		List<Map<String, String>> paramsList = dataService.generateParams();
//		List<String> responses = dataService.request(paramsList);
		List<String> responses = dataService.generateResponses(total);
		List<Data> dataList = dataService.transform(responses.toArray(new String[0]));
		int resultCount = dataList.size();
		String message = String.format("传统转换 %d 条数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, dataList));
	}

	// 流水线转换
	@RequestMapping("/transform-pipeline")
	public ResponseEntity<Result> transformPipeline(
			@RequestParam(name = "total", defaultValue = "1000") int total
	) {
//		List<Map<String, String>> paramsList = dataService.generateParams();
//		List<String> responses = dataService.requestBatch(paramsList);
		List<String> responses = dataService.generateResponses(total);
		List<Data> dataList = dataService.transformPipeline(responses);
		int resultCount = dataList.size();
		String message = String.format("流水线转换 %d 条数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, dataList));
	}

	// 集成
	@RequestMapping("/integrate")
	public ResponseEntity<Result> integrate(
			@RequestParam(name = "total", defaultValue = "10") int total
	) {
		List<Map<String, String>> paramsList = dataService.generateParams(total);
		IntegrationStats stats = dataService.integrate(paramsList);
		return ResponseEntity.ok().body(Result.success(stats.generateReport(), null));
	}

	// 优化集成
	@RequestMapping("/integrate-x")
	public ResponseEntity<Result> integrateOptimized(
			@RequestParam(name = "total", defaultValue = "1000") int total, // 数据总数
			@RequestParam(name = "req-batch-size", defaultValue = "200") int reqBatchSize, // 请求批大小
			@RequestParam(name = "persist-batch-size", defaultValue = "1000") int persistBatchSize // 持久化批大小
	) {
		List<Map<String, String>> paramsList = dataService.generateParams(total);
		List<IntegrationStats> result = dataService.integrateOptimized(paramsList, reqBatchSize, persistBatchSize);
		return ResponseEntity.ok().body(Result.success(result.get(0).generateReport(), null));
	}

	// 分批优化集成
	@RequestMapping("/integrate-batch-x")
	public ResponseEntity<Result> integrateBatchOptimized(
			@RequestParam(name = "total", defaultValue = "1000") int total, // 数据总数
			@RequestParam(name = "int-batch-size", defaultValue = "200") int intBatchSize, // 集成批大小
			@RequestParam(name = "req-batch-size", defaultValue = "200") int reqBatchSize, // 请求批大小
			@RequestParam(name = "persist-batch-size", defaultValue = "1000") int persistBatchSize // 持久化批大小
	) {
		List<Map<String, String>> paramsList = dataService.generateParams(total);
		List<IntegrationStats> statsList = dataService.integrateBatchOptimized(paramsList, intBatchSize, reqBatchSize, persistBatchSize);

		// 合并多个报告
		StringBuilder combinedReport = new StringBuilder();
		for (int i = 0; i < statsList.size(); i++) {
			combinedReport.append("第").append(i + 1).append("批: ")
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
		String message = String.format("查询总数为 %d 条", count);
		return ResponseEntity.ok().body(Result.success(message, count));
	}

	// 查询数据
	@RequestMapping("/find")
	public ResponseEntity<Result> find(
			@RequestParam(name = "count", required = false) Integer count
	) {
		List<Data> result = (count == null ? dataService.findAll() : dataService.findRandomN(count));
		int resultCount = result.size();
		String message = String.format("查询 %d 条数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 分批查询
	@RequestMapping("/select-batch")
	public ResponseEntity<Result> selectBatchByIds(
			@RequestParam(name = "ids") String ids,
			@RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
	) {
		List<Data> result = dataService.selectBatchByIds(new ArrayList<>(Arrays.asList(ids.split(","))), batchSize);
		int resultCount = result.size();
		String message = String.format("分批查询 %d 条数据", resultCount);
		return ResponseEntity.ok().body(Result.success(message, result));
	}

	// 分批插入
	@RequestMapping("/insert-batch")
	public ResponseEntity<Result> insertBatch(
			@RequestParam(name = "total", defaultValue = "1000") int total,
			@RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
	) {
		int affectedRows = dataService.insertBatch(dataService.generate(total), batchSize);
		String message = String.format("分批插入 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message, null));
	}

	// 分批更新
	@RequestMapping("/update-batch")
	public ResponseEntity<Result> updateBatch(
			@RequestParam(name = "total", defaultValue = "1000") int total,
			@RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
	) {
		int affectedRows = dataService.updateBatch(dataService.generate(total), batchSize);
		String message = String.format("分批更新 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message, null));
	}

	// 分批插入或更新
	@RequestMapping("/upsert-batch")
	public ResponseEntity<Result> upsertBatch(
			@RequestParam(name = "total", defaultValue = "1000") int total,
			@RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
	) {
		int affectedRows = dataService.upsertBatch(dataService.generate(total), batchSize);
		String message = String.format("分批插入或更新 %d 条数据", affectedRows);
		return ResponseEntity.ok().body(Result.success(message, null));
	}

	// 分批删除
	@RequestMapping("/delete-batch")
	public ResponseEntity<Result> deleteBatchByIds(
			@RequestParam(name = "ids") String ids,
			@RequestParam(name = "batch-size", defaultValue = "1000") int batchSize
	) {
		int affectedRows = dataService.deleteBatchByIds(new ArrayList<>(Arrays.asList(ids.split(","))), batchSize);
		String message = String.format("分批删除 %d 条数据", affectedRows);
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
