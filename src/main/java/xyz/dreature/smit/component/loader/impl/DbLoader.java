package xyz.dreature.smit.component.loader.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.dreature.smit.common.model.context.Context;
import xyz.dreature.smit.component.loader.Loader;
import xyz.dreature.smit.service.DbService;
import xyz.dreature.smit.service.registry.DbServiceRegistry;

import java.io.Serializable;
import java.util.List;

// 数据库加载器
@Component
@Lazy
public class DbLoader<T, ID extends Serializable> implements Loader<T, ID> {
    @Autowired
    private DbServiceRegistry registry;

    // 单项加载
    @Override
    public int load(Context context, T targetData) {
        String dataSource = context.getTargetDataSource();
        DbService<T, ID> service = registry.getService(dataSource);

        String strategy = context.getLoadStrategy();
        int affectedRows;
        switch (strategy) {
            case "db:insert":
                affectedRows = service.insert(targetData);
                break;
            case "db:update":
                affectedRows = service.update(targetData);
                break;
            case "db:upsert":
                affectedRows = service.upsert(targetData);
                break;
            default:
                throw new IllegalArgumentException("不支持的数据库加载策略: " + strategy);
        }
        return affectedRows;
    }

    // 分批加载
    @Override
    public int loadBatch(Context context, List<T> targetData) {
        String dataSource = context.getTargetDataSource();
        DbService<T, ID> service = registry.getService(dataSource);

        String strategy = context.getLoadStrategy();
        int affectedRows;
        switch (strategy) {
            case "db:insert":
                affectedRows = service.insertBatch(targetData);
                break;
            case "db:update":
                affectedRows = service.updateBatch(targetData);
                break;
            case "db:upsert":
                affectedRows = service.upsertBatch(targetData);
                break;
            default:
                throw new IllegalArgumentException("不支持的数据库加载策略: " + strategy);
        }
        return affectedRows;
    }
}
