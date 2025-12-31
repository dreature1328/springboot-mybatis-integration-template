package xyz.dreature.smit.component.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.EtlContext;
import xyz.dreature.smit.service.DbService;

import java.io.Serializable;
import java.util.List;

// 数据库加载器
@Component
public class DbLoader<T, ID extends Serializable> implements Loader<T, ID> {
    @Autowired
    private DbService<T, ID> dbService;

    // 单项加载
    @Override
    public int load(EtlContext context, T targetData) {
        String strategy = context.getLoadStrategy();
        int affectedRows;
        switch (strategy.toLowerCase()) {
            case "db:insert":
                affectedRows = dbService.insert(targetData);
                break;
            case "db:update":
                affectedRows = dbService.update(targetData);
                break;
            case "db:upsert":
                affectedRows = dbService.upsert(targetData);
                break;
            default:
                throw new IllegalArgumentException("不支持的数据库加载策略: " + strategy);
        }
        return affectedRows;
    }

    // 分批加载
    @Override
    public int loadBatch(EtlContext context, List<T> targetData) {
        String strategy = context.getLoadStrategy();
        int affectedRows;
        switch (strategy.toLowerCase()) {
            case "db:insert":
                affectedRows = dbService.insertBatch(targetData);
                break;
            case "db:update":
                affectedRows = dbService.updateBatch(targetData);
                break;
            case "db:upsert":
                affectedRows = dbService.upsertBatch(targetData);
                break;
            default:
                throw new IllegalArgumentException("不支持的数据库加载策略: " + strategy);
        }
        return affectedRows;
    }
}
