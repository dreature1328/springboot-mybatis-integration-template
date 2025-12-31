# 基于 Spring Boot + MyBatis 的轻量级数据集成模板

本项目是针对数据集成场景的模板工程，基于 Spring Boot + MyBatis，提供**轻量级**可扩展的 ETL 解决方案，支持多数据源（本地文件 / API / 数据库 / 缓存 / 消息队列等），可处理单任务**千万级**数据量，**不涉及**分布式框架（Hadoop / Spark / Flink 等），便于初学者快速上手。

## 关联项目

由于此前在频繁集成数据，因而写了以下两套模板，后者可以说是前者的扩充。

> 💬相关
>
> 基于 Spring Boot + MyBatis 的基础级数据增删改查模板
>
> - 代码仓库：https://github.com/dreature1328/springboot-mybatis-crud-template
> 
>
>基于 Spring Boot + MyBatis 的轻量级数据集成模板（本项目）
> 
>- 代码仓库：https://github.com/dreature1328/springboot-mybatis-integration-template

## 架构设计

### 核心业务层级

采用“自上而下 + 分层调用”的驱动流程，结合“策略 + 模板方法“的设计模式，支持扩展和多数据源适配。

| 层级           | 包路径          | 核心职责                                                     | 设计特点                     |
| -------------- | --------------- | ------------------------------------------------------------ | ---------------------------- |
| **编排调度层** | `orchestration` | 组装 ETL 组件、管理流程上下文、监控指标、任务调度            | 模板方法模式、门面模式       |
| **ETL 组件层** | `component`     | 实现抽取、转换、加载的核心逻辑，控制事务边界                 | 策略模式、注册模式、条件分发 |
| **策略算法层** | `strategy`      | 实现不同数据源和场景的具体算法，包括业务逻辑、参数解析和场景优化 | 策略模式                     |
| **数据服务层** | `service`       | 封装多数据源的基础操作，提供统一的数据读写接口               | 数据多源、批量优化           |
| **数据访问层** | `mapper`        | 直接与数据库交互，执行 SQL 操作和对象关系映射                | ORM 框架                     |

### 辅助支撑层级

| 层级       | 包路径          | 核心职责                                               | 设计特点       |
| ---------- | --------------- | ------------------------------------------------------ | -------------- |
| **控制层** | `controller`    | 提供 HTTP 测试接口，包括请求解析、参数验证、响应封装等 | REST 设计      |
| **配置层** | `common.config` | 管理 Bean 定义、条件装配等                             | 配置类显式声明 |
| **工具层** | `common.util`   | 提供通用工具方法、辅助函数等                           | 工具类静态化   |
| **模型层** | `common.model`  | 定义上下文、数据模型等                                 | 分层模型设计   |

## 数据集成

### ETL 流程骨架

ETL 流程的固定骨架（抽取 → 转换 → 加载）被定义为模板，具体步骤由 ETL 组件实现。

- 抽取器（Extractor）：采用策略模式（策略接口 + 组合，强调"算法"的选择，通常以策略名称作为键注册，如 `"db:ids"`, `"db:page"`），抽取并返回结构化中间数据（如 `JsonNode`、`Document` 等）
- 转换器（Transformer）采用注册模式（注册中心，强调"组件"的查找，通常以类型信息作为键注册，如 `"JsonNode->Data"`），将结构化数据映射为实体对象（如 `Data.java`）
- 加载器（Loader）：采用条件分发，主要涉及持久化，将实体对象映射到数据表记录

### 执行方式概览

为适应不同数据量级和场景，部分方法有多种执行方式，通过重载形式或函数名进行区分。

ETL 组件实现“单项”和“单批”（若有）原子化操作，而“逐项”与“分批”交由编排器在外部实现。

| 类型 | 命名逻辑                                                     | 实现逻辑                                                     |
| ---- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 单项 | `execute(Object obj)`                                        | 直接处理单个对象                                             |
| 逐项 | `execute(Object... objArray)`<br />`execute(List<Object> objList)` | 遍历数组（可变参数）或列表，对每个元素依次单项处理           |
| 单批 | `executeBatch(List<Object> objList)`                         | 将整个列表视作单个批次，通过批量优化机制，系统调用或资源开销 |
| 分批 | `executeBatch(List<Object> objList, int batchSize)`          | 将整个列表分割成多个子批次，对每批依次单批处理，规避内存溢出风险 |

### 批量优化策略

- **文件读取**：采用**并行流**读取，降低磁盘 I/O 开销
- **API 请求**：采用**异步并发**发送批量请求，避免同步阻塞
- **数据库交互**：通过**动态拼接**长 SQL 合并操作（如 `INSERT INTO ... VALUES (...), (...), ...`），降低网络 I/O 开销
- **消息队列**：采用**批量消费**模式，提高吞吐量

### 多源数据支持

| 数据源       | 支持类型    | 特点              |
| ------------ | ----------- | ----------------- |
| **本地文件** | JSON / XML  | 支持并行流读取    |
| **API 接口** | RESTful API | 支持并发请求      |
| **数据库**   | MySQL       | 支持批量 SQL 优化 |
| **消息队列** | RabbitMQ    | 支持批量消费      |

### 字段映射示例


| 实体字段     | 表字段        | 类型映射              |
| ------------ | ------------- | --------------------- |
| id           | id            | Long ↔ BIGINT         |
| numericValue | numeric_value | Integer ↔ INT         |
| decimalValue | decimal_value | Double ↔ DOUBLE       |
| textContent  | text_content  | String ↔ VARCHAR(255) |
| activeFlag   | active_flag   | Boolean ↔ TINYINT(1)  |

## 启动流程

1. **数据源配置**
   - 编辑 `application.properties` 文件，配置数据源参数
   - 执行 `data_table.sql` 脚本，初始化数据库表结构
2. **注入配置**：按需调整 `common.config` 中涉及服务类 / ETL 组件 / 编排器的配置
3. **项目启动**：运行 `Application.java` 主类，启动 Spring Boot 应用
4. **接口测试**：发起请求调用控制层接口，直接调用数据服务（`<data source>Controller.java`）或运行 ETL 流程（`EtlController.java`），或者注册任务并定时调度（`JobController.java`）

## 项目结构

```text
├── orchestration/        # 编排调度层
├── component/            # ETL 组件层
│   ├── extractor/        # 抽取器
│   ├── transformer/      # 转换器
│   └── loader/           # 加载器
├── strategy/             # 策略算法层
├── service/              # 数据服务层
├── mapper/               # 数据访问层
├── controller/           # 控制层
└── common/
    ├── config/           # 配置层
    ├── util/             # 工具层
    └── model/            # 模型层
```

## 相关脚本

脚本位于 `scripts/` 目录

- 数据表结构定义脚本：`data_table.sql` 
- 随机数据生成脚本：`generate_mock_json.py` 与 `generate_mock_xml.py`

> 注：旧版模板式代码段生成脚本已被移除
