# 基于 Spring Boot + MyBatis 的轻量级数据集成模板

本项目是针对数据集成场景的模板工程，基于 Spring Boot + MyBatis，提供**轻量级**可扩展的 ETL 解决方案，支持多数据源（本地文件 / API / 数据库 / 缓存 / 消息队列等），可处理单任务**千万级**数据量，**不涉及**分布式框架（Hadoop / Spark / Flink 等），便于初学者快速上手。

## 关联项目

由于此前在频繁集成数据，因而写了以下两套模板，后者可以说是前者的扩充。

> 💬相关
>
> 基于 Spring Boot + MyBatis 的基础级数据增删改查模板
>
> - 技术博客：https://blog.csdn.net/weixin_42077074/article/details/128868655
> - GitHub 源码：https://github.com/dreature1328/springboot-mybatis-crud-template
>
>
> 基于 Spring Boot + MyBatis 的轻量级数据集成模板（本项目）
>
> - 技术博客：https://blog.csdn.net/weixin_42077074/article/details/129802650
> - GitHub 源码：https://github.com/dreature1328/springboot-mybatis-integration-template

## 架构设计

| 主要层级       | 包路径          | 核心职责                                                     | 设计特点                                                     |
| -------------- | --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **编排调度层** | `orchestration` | 组装 ETL 组件并编排流程，管理上下文并监控指标，注册进任务并调度 | 对于**编排器类**，完全采用泛型，通过统一接口驱动 ETL 流程，结合配置类注入（ETL 组件类实现等） |
| **ETL 组件层** | `component`     | 实现抽取 / 转换 / 加载等逻辑                                 | 对于 **ETL 组件类**，完全采用泛型，将接口与实现分离，可能涉及抽象类，主要调用服务类方法，结合配置类注入（服务类实现等） |
| **数据服务层** | `service`       | 提供多源数据的基础读写操作                                   | 对于**服务类**，基本采用泛型，将接口与实现分离，一般不涉及抽象类，主要调用工具类、模板类方法，结合配置类注入（内部解析器，类型，模板工具类等） |
| **通用层**     | `common`        | 提供工具 / 配置 / 模型等支持                                 | 对于**工具类**，较少采用泛型，以静态成员和方法为主，避免被继承或实例化 |

## 数据服务

结合泛型数据服务类与静态工具类，支持数据源包括本地文件（JSON/XML 文件）、API（REST 规范）、数据库（MySQL）、缓存（Redis）、消息队列（RabbitMQ）。

## ETL 流程

- 抽取器（Extractor）抽取并返回结构化中间数据（`JsonNode`、`Document` 等）
- 转换器（Transformer）将结构化数据映射为实体对象（如 `Data.java`）
- 加载器（Loader）主要涉及持久化，将实体对象映射到数据表记录


| 实体属性     | 表字段        | 类型映射              |
| ------------ | ------------- | --------------------- |
| id           | id            | Long ↔ BIGINT         |
| numericValue | numeric_value | Integer ↔ INT         |
| decimalValue | decimal_value | Double ↔ DOUBLE       |
| textContent  | text_content  | String ↔ VARCHAR(255) |
| activeFlag   | active_flag   | Boolean ↔ TINYINT(1)  |

- 编排器（Orchestrator）组装具体的 ETL 组件，根据上下文（`EtlContext.java`）运行流程，并生成指标监控结果（`EtlMetrics.java`）

## 执行方式

为适应不同数据量级和场景，部分方法有多种执行方式，通过重载形式或函数名进行区分。

| 类型 | 命名逻辑                                                     | 实现逻辑                                                     |
| ---- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 单项 | `execute(Object obj)`                                        | 直接处理单个对象                                             |
| 逐项 | `execute(Object... objArray)`<br />`execute(List<Object> objList)` | 遍历数组（可变参数）或列表，对每个元素依次单项处理           |
| 单批 | `executeBatch(List<Object> objList)`                         | 将整个列表视作单个批次，通过批量优化机制，系统调用或资源开销 |
| 分批 | `executeBatch(List<Object> objList, int batchSize)`          | 将整个列表分割成多个子批次，对每批依次单批处理，规避内存溢出风险 |

不同场景的批量优化策略各异。例如，对于文件读取，采用**并行流**读取，降低磁盘 I/O 开销；对于 HTTP 请求 API，采用**异步并发**发送批量请求，避免同步阻塞；对于数据库操作，通过**动态拼接**长 SQL 合并操作（如 `INSERT INTO ... VALUES (...), (...), ...`），降低网络 I/O 开销。

也并非所有场景都有批量优化策略，如数据转换本质上需要逐项处理，但可结合**流水线处理**。

ETL 组件内部仅实现“单项”和“单批”（若有）这些原子化操作，而“逐项”与“分批”交由编排器在外部实现。

## 启动流程

1. **数据源配置**
   - 编辑 `application.properties` 文件，配置数据源参数
   - 执行 `data_table.sql` 脚本，初始化数据库表结构
2. **注入配置**：按需调整 `common.config` 中涉及服务类 / ETL 组件 / 编排器的配置
3. **项目启动**：运行 `Application.java` 主类，启动 Spring Boot 应用
4. **接口测试**：发起请求调用 Controller 层接口，直接调用数据服务或运行 ETL 流程，或者注册任务并定时调度

## 相关脚本

脚本位于 `script/` 目录

- 数据表结构定义脚本：`data_table.sql` 
- 随机数据生成脚本：`generate_mock_json.py` 与 `generate_mock_xml.py`

> 注：旧版模板式代码段生成脚本已被移除
