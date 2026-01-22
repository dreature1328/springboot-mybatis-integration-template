package xyz.dreature.smit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.dreature.smit.common.model.vo.Result;
import xyz.dreature.smit.component.transformer.Transformer;
import xyz.dreature.smit.component.transformer.registry.TransformerRegistry;
import xyz.dreature.smit.service.FileService;
import xyz.dreature.smit.service.registry.FileServiceRegistry;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    // 上传抽取
    @RequestMapping("/upload")
    public <S, T> ResponseEntity<Result<List<T>>> readByUpload(
            @RequestParam(name = "file")
            @NotNull(message = "文件不能为空")
            MultipartFile file,

            @RequestParam(name = "service-key", defaultValue = "file11")
            @NotBlank(message = "服务键不能为空")
            String serviceKey,

            @RequestParam(name = "transformer-key", defaultValue = "JsonNode->StandardEntity")
            @NotBlank(message = "转换器键不能为空")
            String transformerKey
    ) {
        FileService<S> fileService = fileServiceRegistry.getService(serviceKey);
        Transformer<S, T> transformer = transformerRegistry.get(transformerKey);

        List<T> result = transformer.transform(null, fileService.read(file.getResource()));
        int resultCount = result.size();
        String message = String.format("解析 %d 条数据", resultCount);
        log.info("文件解析完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }

    // 定位抽取
    @RequestMapping("/location")
    public <S, T> ResponseEntity<Result<List<T>>> readByPath(
            @RequestParam(name = "location", defaultValue = "scripts/seeding/standard_data.json")
            @NotBlank(message = "资源定位不能为空")
            String location,

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

        List<T> result = transformer.transformStream(null, fileService.readBatch(fileService.collect(location)));
        int resultCount = result.size();
        String message = String.format("解析 %d 条数据", resultCount);
        log.info("文件解析完成，条数：{}", resultCount);
        return ResponseEntity.ok().body(Result.success(message, result));
    }
}
