package dreature.smit.service.impl;

import dreature.smit.common.util.BatchUtils;
import dreature.smit.common.util.MqUtils;
import dreature.smit.entity.Data;
import dreature.smit.service.LoadService;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static dreature.smit.common.util.BatchUtils.reduceBatch;

@Service
public class DataLoadServiceImpl extends BaseServiceImpl<Data> implements LoadService<Data> {
    // ----- 数据库持久化 -----
    // 分批插入
    public int insertBatch(List<Data> dataList, int batchSize) {
        return reduceBatch(dataList, batchSize, baseMapper::insertBatch);
    }

    // 分批更新
    public int updateBatch(List<Data> dataList, int batchSize) {
        return reduceBatch(dataList, batchSize, baseMapper::updateBatch);
    }

    // 分批插入或更新
    public int upsertBatch(List<Data> dataList, int batchSize) {
        return reduceBatch(dataList, batchSize, baseMapper::upsertBatch);
    }

    // 分批删除
    public int deleteBatchByIds(List<String> ids, int batchSize) {
        return reduceBatch(ids, batchSize, baseMapper::deleteBatchByIds);
    }

    // 清空
    public void truncate() {
        baseMapper.truncate();
    }

    // ----- 消息发布（中转） -----
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private MessageProperties messageProperties;

    @PostConstruct
    public void init() {
        messageProperties = MqUtils.createDefaultMessageProperties();
    }

    // 逐项发送（异步回调）
    public int send(List<Data> dataList) {
        int successCount = 0;
        // 需要设置 spring.rabbitmq.publisher-confirm-type=correlated
        List<CorrelationData> correlationDataList = new ArrayList<>();
        try {
            for (Data data : dataList) {
                CorrelationData correlationData = new CorrelationData(data.getId());
                correlationDataList.add(correlationData);
                MqUtils.send(rabbitTemplate, data, messageProperties, correlationData);
            }

            for (CorrelationData cd : correlationDataList) {
                try {
                    // 等待确认结果，最多等待5秒
                    CorrelationData.Confirm confirm = cd.getFuture().get(5, TimeUnit.SECONDS);
                    if (confirm.isAck()) {
                        successCount++;
                    } else {
                        // 记录失败原因
                        System.err.println("消息发送失败: " + confirm.getReason());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("等待消息确认时被中断");
                } catch (ExecutionException | TimeoutException e) {
                    System.err.println("获取消息确认结果时发生异常" + e);
                }
            }
            return successCount;
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("消息发送异常", e);
        }
    }

    // 单批发送（异步回调）
    public int sendBatch(List<Data> dataList) {

        // 以批大小作为一条消息的数据量（列表大小）
        try {
            CorrelationData cd = new CorrelationData("BATCH_" + UUID.randomUUID());
            MqUtils.send(rabbitTemplate, dataList, messageProperties, cd);

            try {
                // 等待确认结果，最多等待5秒
                CorrelationData.Confirm confirm = cd.getFuture().get(5, TimeUnit.SECONDS);
                if (confirm.isAck()) {
                    return 1;
                } else {
                    // 记录失败原因
                    System.err.println("批量消息发送失败: " + confirm.getReason());
                    return 0;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("等待批量消息确认时被中断");
                return 0;
            } catch (ExecutionException | TimeoutException e) {
                System.err.println("获取批量消息确认结果时发生异常:" + e);
                return 0;
            }
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    // 分批发送（异步回调）
    public int sendBatch(List<Data> dataList, int batchSize) {
        return BatchUtils.reduceBatch(dataList, batchSize, this::sendBatch);
    }

}
