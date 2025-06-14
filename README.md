## 基于 Spring Boot + MyBatis 的数据集成模板

本项目是针对数据库操作的模板工程，基于 Spring Boot + MyBatis，提供 ETL 集成数据的基础实现方案，便于初学者理解流程。

### 关联项目

由于此前在频繁集成数据，因而写了增删改查模板与集成模板（本项目）这两套模板，后者可以说是前者的扩充，辅之博客文章进行介绍。

> 💬相关
>
> 基于 Spring Boot + MyBatis 的数据增删改查模板
>
> https://blog.csdn.net/weixin_42077074/article/details/128868655
>
> https://github.com/dreature1328/springboot-mybatis-crud-template
>
> 基于 Spring Boot + MyBatis 的数据集成模板
>
> https://blog.csdn.net/weixin_42077074/article/details/129802650
>
> https://github.com/dreature1328/springboot-mybatis-integration-template

### 相关说明

启动整个项目就是常规 Spring Boot 的操作，在 `application.properties` 进行相应配置后，运行启动类 `Application.java`，后续借助测试接口调用函数即可。

虽然 MyBatis-Plus 可很大程度简化开发，但本项目基于原生 MyBatis 实现，便于理解原理以及灵活处理 SQL。

> 💬相关
>
> 博客文章《基于MyBatis依次、批量、分页增删改查》
>
> https://blog.csdn.net/weixin_42077074/article/details/129405833

在 `script` 文件夹下，目前已移除此前版本的模板式代码段生成脚本，仅留有供测试的随机数据生成脚本。