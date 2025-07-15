package xyz.dreature.smit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import xyz.dreature.smit.common.model.entity.Data;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.common.util.JsonUtils;
import xyz.dreature.smit.common.util.XmlUtils;
import xyz.dreature.smit.component.transformer.Transformer;

import java.util.List;

// 测试接口（文件操作）
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    @Qualifier("jsonEntityTransformer")
    Transformer jsonTransformer;

    @Autowired
    @Qualifier("xmlEntityTransformer")
    Transformer xmlTransformer;

    // ===== 文件抽取 =====
    // 解析 JSON 文件
    @RequestMapping("/parse-json")
    public ResponseEntity<Result<List<Data>>> parseDataFromJsonFile(
            @RequestParam(name = "file-path", defaultValue = "script/mock_data.json") String filePath
    ) {
        JsonNode jsonNode = JsonUtils.parseFile(filePath);
        List<Data> result = jsonTransformer.transform(null, jsonNode);
        int resultCount = result.size();
        String message = String.format("解析 %d 条 JSON 数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 解析 XML 文件
    @RequestMapping("/parse-xml")
    public ResponseEntity<Result<List<Data>>> parseDataFromXmlFile(
            @RequestParam(name = "file-path", defaultValue = "script/mock_data.xml") String filePath
    ) {
        Document document = XmlUtils.parseFile(filePath);
        List<Data> result = xmlTransformer.transform(null, document);
        int resultCount = result.size();
        String message = String.format("解析 %d 条 XML 数据", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}
