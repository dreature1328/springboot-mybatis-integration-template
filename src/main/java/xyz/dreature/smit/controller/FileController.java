package xyz.dreature.smit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.component.transformer.Transformer;
import xyz.dreature.smit.component.transformer.registry.TransformerRegistry;
import xyz.dreature.smit.service.FileService;
import xyz.dreature.smit.service.registry.FileServiceRegistry;

import javax.validation.constraints.NotBlank;
import java.util.List;

// 测试接口（文件操作）
@Slf4j
@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    @Autowired
    private FileServiceRegistry fileServiceRegistry;

    @Autowired
    private TransformerRegistry transformerRegistry;

    // ===== 文件抽取 =====
    // 解析文件
    @RequestMapping("/parse")
    public <S, T> ResponseEntity<Result<List<T>>> parseFromFile(
            @RequestParam(name = "file-path", defaultValue = "scripts/seeding/standard_data.json")
            @NotBlank(message = "文件路径不能为空")
            String filePath,

            @RequestParam(name = "service-key", defaultValue = "file11")
            @NotBlank(message = "服务键不能为空")
            String serviceKey,

            @RequestParam(name = "transformer-key", defaultValue = "JsonNode->StandardEntity")
            @NotBlank(message = "转换器键不能为空")
            String transformerKey
            // 替换例子： "scripts/seeding/advanced_data.json", "file21", "JsonNode->AdvancedEntity"
    ) {
        FileService<S> fileService = fileServiceRegistry.getService(serviceKey);
        Transformer<S, T> transformer = transformerRegistry.get(transformerKey);

        List<T> result = transformer.transform(null, fileService.read(filePath));
        int resultCount = result.size();
        String message = String.format("解析 %d 条数据", resultCount);
        log.info("文件解析完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}
