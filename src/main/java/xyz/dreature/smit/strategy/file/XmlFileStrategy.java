package xyz.dreature.smit.strategy.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import xyz.dreature.smit.service.FileService;
import xyz.dreature.smit.strategy.ExtractStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class XmlFileStrategy implements ExtractStrategy<Document> {
    @Autowired
    @Qualifier("xmlFileService")
    private FileService<Document> fileService;

    @Override
    public String getStrategyName() {
        return "file:xml";
    }

    @Override
    public List<Document> extract(Map<String, ?> fileParams) {
        List<Document> result = new ArrayList<>();
        String filePath = (String) fileParams.get("filePath");
        result.add(fileService.read(filePath));
        return result;
    }

    @Override
    public List<Document> extractBatch(List<? extends Map<String, ?>> filesParams) {
        List<String> filePaths = new ArrayList<>();
        for (Map<String, ?> fileParams : filesParams) {
            String filePath = (String) fileParams.get("filePath");
            filePaths.add(filePath);
        }
        return fileService.readBatch(filePaths);
    }

//    @Override
//    public List<Document> extractBatch(List<? extends Map<String, ?>> filesParams) {
//        return filesParams.parallelStream()
//                .flatMap(fileParams -> extract(fileParams).stream())
//                .collect(Collectors.toList());
//    }
}
