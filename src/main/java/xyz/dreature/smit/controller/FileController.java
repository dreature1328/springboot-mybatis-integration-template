package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.component.transformer.Transformer;
import xyz.dreature.smit.service.FileService;

import javax.validation.constraints.NotBlank;
import java.util.List;

// 测试接口（文件操作）
@Slf4j
@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    @Autowired
    private FileService<JsonNode> jsonFileService;

    @Autowired
    private FileService<Document> xmlFileService;

    @Autowired
    private Transformer<JsonNode, Data> jsonTransformer;

    @Autowired
    private Transformer<Document, Data> xmlTransformer;

    // ===== 文件抽取 =====
    // 解析 JSON 文件
    @RequestMapping("/parse-json")
    public ResponseEntity<Result<List<Data>>> parseDataFromJsonFile(
            @RequestParam(name = "file-path", defaultValue = "scripts/mock_data.json")
            @NotBlank(message = "文件路径不能为空")
            String filePath
    ) {
        JsonNode jsonNode = jsonFileService.read(filePath);
        List<Data> result = jsonTransformer.transform(null, jsonNode);
        int resultCount = result.size();
        String message = String.format("解析 %d 条 JSON 数据", resultCount);
        log.info("JSON 文件解析完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 解析 XML 文件
    @RequestMapping("/parse-xml")
    public ResponseEntity<Result<List<Data>>> parseDataFromXmlFile(
            @RequestParam(name = "file-path", defaultValue = "scripts/mock_data.xml")
            @NotBlank(message = "文件路径不能为空")
            String filePath
    ) {
        Document document = xmlFileService.read(filePath);
        List<Data> result = xmlTransformer.transform(null, document);
        int resultCount = result.size();
        String message = String.format("解析 %d 条 XML 数据", resultCount);
        log.info("XML 文件解析完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}
