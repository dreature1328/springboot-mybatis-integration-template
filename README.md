由于作者最近频繁在迁移数据，因而写了一个基于  Spring Boot + MyBatis 的数据迁移模板，包含各种增删改查数据的方法，而且针对不同的数据量级，还分为依次、批量、分页三类操作。

启动整个项目就是常规 Spring Boot 的操作，运行启动类 `StarterDataCenter.java`，其后通过发起请求使用相应功能（请求 URL 都在 Controller 层的 `DataController.java` 里）。

在这篇博客文章有关于项目整体的简单介绍。

> 💬相关
>
> 博客文章《基于SpringBoot的数据迁移模板》
>
> https://blog.csdn.net/weixin_42077074/article/details/128868655

在另一篇博客文章中有详细介绍本项目中依次、批量、分页处理数据的思路，以及对应的 MyBatis 实现。

> 💬相关
>
> 博客文章《基于MyBatis依次、批量、分页增删改查》
>
> https://blog.csdn.net/weixin_42077074/article/details/129405833

其中，如果嫌麻烦，可以借助 Python 根据 JSON 快速生成生成相关代码。

具体使用过程为：在 `script` 文件夹下，在 `config.properties` 里填写相应参数，随后在 `demo.json` 里加上你的 JSON 数据（暂不支持嵌套），其后运行 `generate_template.py` 即可生成模板相关的代码段（目前是 7 个 txt 文件），将代码段替换项目中对应位置的示例代码即可。

此外，还可以运行 `script` 文件夹下的 `generate_mock_data.py` 生成随机数据 `mock_data.json` 供测试。