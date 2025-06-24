## 基于 Spring Boot + MyBatis 的轻量级数据集成模板

本项目是针对数据集成场景的模板工程，基于 Spring Boot + MyBatis，提供 ETL 流程的**轻量级**实现方案，**不涉及**分布式框架（Hadoop、Spark、Flink 等），支持多数据源（本地文件、数据库、API、消息队列等），可处理单任务**千万级**数据量，便于初学者理解流程。

### 关联项目

由于此前在频繁集成数据，因而写了增删改查模板与集成模板（本项目）这两套模板，后者可以说是前者的扩充，辅之博客文章进行介绍。

> 💬相关
>
> 基于 Spring Boot + MyBatis 的基础数据增删改查模板
>
> https://blog.csdn.net/weixin_42077074/article/details/128868655
>
> https://github.com/dreature1328/springboot-mybatis-crud-template
>
> 基于 Spring Boot + MyBatis 的轻量级数据集成模板
>
> https://blog.csdn.net/weixin_42077074/article/details/129802650
>
> https://github.com/dreature1328/springboot-mybatis-integration-template

### 架构设计

项目采用 MVC 分层架构，服务层基于 ETL 功能划分。

- 抽取服务（Extract）：支持多源数据接入，包括本地文件（JSON/XML 文件）、数据库（MySQL）、API（REST 规范）、消息队列（RabbitMQ）。
- 转换服务（Transform）：负责数据格式转换（JSON/XML 与实体类的映射）。
- 加载服务（Load）：结合持久层，基于原生 MyBatis 实现（未采用 MyBatis-Plus），便于理解逻辑和灵活处理 SQL。

### 启动流程

启动整个项目就是常规 Spring Boot 的操作，在 `application.properties` 进行相应配置后，运行启动类 `Application.java`，后续借助测试接口触发 ETL 流程。

启用异步监听消息队列的功能需将对应的函数（`@RabbitListener`）注释还原并实现逻辑。

### 相关脚本

在 `script` 文件夹下，目前已移除此前版本的模板式代码段生成脚本，仅留有供测试的随机数据生成脚本。
