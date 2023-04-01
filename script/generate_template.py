#-*- coding:utf-8 -*-
from string import Template
import configparser
import json
from case_converter import *

config = configparser.ConfigParser()
# 读取配置文件
with open('config.properties', 'r', encoding='utf-8') as f:
    config.read_file(f)
# 读取 JSON 文件
with open('input.json', 'r') as f:
    data = json.load(f)

# 将参数项存储到字典中，后续替换进字符串中
params = dict(config.items(config.default_section))

data_name_camel = to_camel_case(params['data_name'])
data_name_snake = to_snake_case(params['data_name'])

default_params = {
    'project_name': data_name_camel,
    'project_name_pascal': capitalize_first_letter(data_name_camel),
    'url_name': data_name_camel,
    'class_name': capitalize_first_letter(data_name_camel),
    'object_name': uncapitalize_first_letter(data_name_camel),
    'table_name': data_name_snake,
    'json_keys': list(data.keys()),
    'json_keys_num': len(data.keys()),
    'java_attrs': [to_camel_case(key) for key in data.keys()],
    'java_attr_types': ['String' for _ in data.keys()],
    'sql_fields': [to_snake_case(key) for key in data.keys()],
    'primary_key' : next(iter(data.keys())),
    'primary_attr' : to_camel_case(next(iter(data.keys()))),
    'primary_attr_type' : 'String',
    'primary_field': to_snake_case(next(iter(data.keys()))),
    'page_size': 300,
    'page_data_size': 12000
}

if config.has_option('DEFAULT', 'java_attrs'):
    params['java_attrs'] = [v.strip() for v in config.get('DEFAULT', 'java_attrs').split(',')]
if config.has_option('DEFAULT', 'sql_fields'):
    params['sql_fields'] = [v.strip() for v in config.get('DEFAULT', 'sql_fields').split(',')]


for key, default_value in default_params.items():
    params.setdefault(key, default_value)
    
locals().update(params)

# --- Common 层 ---

# 属性
text11 = f'''public class {class_name} {{
    // 属性'''

# 无参构造函数
text12 = f'''    // 无参构造函数
    public {class_name}() {{
    }}'''

# 成员列表构造函数
text13 = f'''    // 成员列表构造函数
    public {class_name}('''

text14 = f''' {{'''

# 复制构造函数
text15 = f'''    // 复制构造函数
    public {class_name}({class_name} {object_name}) {{'''

# Getter 方法
text16 = f'''    // Getter 方法'''

# Setter 方法
text17 = f'''    // Setter 方法'''

# 重写 toString 方法
text18 = f'''    // 重写 toString 方法
    @Override
    public String toString() {{
        return
            "{class_name}["'''

# --- Common 层 ---

# --- Controller 层 ---
text2 = f'''
	@Autowired
	private {project_name_pascal}Service {project_name}Service;
 
    // 迁移
    @RequestMapping("/{url_name}/migrate")
    public void migrate{class_name}() throws Exception {{
        {project_name}Service.migrate{class_name}();
        return ;
    }}

    // 迁移优化
    @RequestMapping("/{url_name}/migratex")
    public void migrate{class_name}Optimized() throws Exception {{
        {project_name}Service.migrate{class_name}Optimized();
        return ;
    }}
'''
# --- Controller 层 ---

# --- Service 层 ---

text301 = ''
text302 = ''

text3 = f'''
    @Autowired
    private DataMapper dataMapper;

    // 请求头示例
    private static Map<String, String> headers = HTTPUtils.headers;
    static {{
        final String key="";
        final String value="";
        // 添加自定义请求头，key 和 value 是你需要添加的头信息的键与值，如用于鉴权
        headers.put(key, value);

    }}

    public <T> void pageHandle(List<T> list, int pageSize, Consumer<List<T>> handleFunction){{
        int count = 0;
        while (!list.isEmpty()) {{
            // 取出当前页数据
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            // 执行插入或更新操作
            handleFunction.accept(subList);
            // 统计插入或更新的记录数
            count += subList.size();
            // 从列表中移除已处理的数据
            list.subList(0, subList.size()).clear();
        }}
    }}

    public <T, R> List<R> pageHandle(List<T> list, int pageSize, Function<List<T>, List<R>> handleFunction) {{
        List<R> resultList = new ArrayList<>();
        int count = 0;
        while (!list.isEmpty()) {{
            // 取出当前页数据
            List<T> subList = list.subList(0, Math.min(pageSize, list.size()));
            // 执行查询操作
            List<R> subResultList = handleFunction.apply(subList);
            // 将结果添加到总结果列表中
            resultList.addAll(subResultList);
            // 统计查询的记录数
            count += subList.size();
            // 从列表中移除已处理的数据
            list.subList(0, subList.size()).clear();
        }}
        return resultList;
    }}

     // 迁移数据
    public void migrate{class_name}() throws Exception {{

        // 自己按需求生成自定义参数列表
        List<Map<String, String>> paramList = getParams();

        // 同步请求并获取响应内容
        List<String> responses = request{class_name}(paramList);

        // 依次加工数据，将响应内容加工成对象列表
        List<{class_name}> {object_name}List = process{class_name}(responses);

        // 将对象依次插入或更新进数据库
        insertOrUpdate{class_name}({object_name}List);

        return ;
    }}

    // 优化迁移数据
    public void migrate{class_name}Optimized() throws Exception {{

        // 自己按需求生成自定义参数列表
        List<Map<String, String>> paramList = getParams();

        // 异步请求并获取响应内容
        List<String> responses = pageRequest{class_name}(paramList);

        // 流水线加工数据，将响应内容加工成对象列表
        List<{class_name}> {object_name}List = pielineProcess{class_name}(responses);

        // 将对象分页插入或更新进数据库
        pageInsertOrUpdate{class_name}({object_name}List);

        return ;
    }}

    public List<Map<String, String>> getParams() {{
        // 总请求数
        int totalRequests = 1000;
        // 自己按需求生成自定义参数列表
        List<Map<String, String>> paramsList = new ArrayList<>();

        for(int i = 0; i < totalRequests; i++) {{
            // key 和 value 是你需要添加的参数名与参数值
            String key = "";
            String value = "";
            paramsList.add(new HashMap<String, String>(){{{{
                put(key, value);
            }}}});
        }}
        return paramsList;
    }}

    // 单次请求
    public String request{class_name}(Map<String, ?> params) throws Exception {{
        String strURL = "http://www.example.com";
        String method = "GET";

        String response = requestHTTPContent(strURL, method, headers, params);
        return response;
    }}

    // 依次同步请求
    public List<String> request{class_name}(List<? extends Map<String,?>> paramList) throws Exception {{
        List<String> responses = new ArrayList<>();
        for(Map<String, ?> params : paramList) {{
            responses.add(request{class_name}(params));
        }}
        return responses;
    }}

    // 批量异步请求
    public List<String> batchRequest{class_name}(List<? extends Map<String,?>> paramList) {{
        String strURL = "http://www.example.com";
        String method = "GET";
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // 添加异步请求任务
        for (Map<String, ?> params : paramList) {{
            CompletableFuture<String> future = asyncHTTPRequest(strURL, method, headers, params);
            futures.add(future);
        }}

        // 等待异步任务完成，超时时间为 30 分钟
        try {{
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1800, TimeUnit.SECONDS);
        }} catch (InterruptedException | ExecutionException e) {{
            e.printStackTrace();
        }} catch (TimeoutException e) {{
            System.out.println("请求超时");
            e.printStackTrace();
            return Collections.emptyList();
        }}

        // 将所有异步请求的结果获取为 List<String>
        List<String> responses = new ArrayList<>();
        for (CompletableFuture<String> future : futures) {{
            try {{
                responses.add(future.get());
            }} catch (InterruptedException | ExecutionException e) {{
                e.printStackTrace();
            }}
        }}

        return responses;
    }}

    // 分页异步请求
    public List<String> pageRequest{class_name}(List<? extends Map<String,?>> paramList) {{
        int pageSize = {page_size};
        return pageHandle(paramList, pageSize, this::batchRequest{class_name});
    }}
'''
# --- Service 层 ---

# --- Mapper 层 ---
text4 = f'''
    // 依次插入或更新
    public void insertOrUpdate{class_name}({class_name} {object_name});
    // 批量插入或更新
    public void batchInsertOrUpdate{class_name}(List<{class_name}> {object_name}List);
    // 清空
    public void clear{class_name}();
'''
# --- Mapper 层 ---

# --- MyBatis 层 ---
text501 = '(' # 如 (`id`,`attr1`,`attr2`)
text502 = '(' # 如 (#{id},#{attr1},#{attr2}) 
text503 = '(' # 如 (#{item.id},222#{item.attr1},222#{item.attr2}) 
text504 = '' # 如 `id` = #{id}, `attr1` = #{attr1}, `attr2` = #{attr2}
text505 = '    <!-- WHEN ... THEN ... 语句相当于编程语言中的 switch 语句 -->' # 如 `id` = #{item.id}, `attr1` = #{item.attr1}, `attr2` = #{item.attr2}
text506 = '' # 如 `id` = VALUES(`id`), `attr1` = VALUES(`attr1`), `attr2` = VALUES(`attr2`)
# --- MyBatis 层 ---


# --- 数据库建表的 SQL 语句 ---
text61 = f'''DROP TABLE IF EXISTS `{table_name}`;
CREATE TABLE `{table_name}` ('''

text62 = ''
# --- 数据库建表的 SQL 语句 ---

for i in range(json_keys_num):
    json_key = params['json_keys'][i]
    java_attr = params['java_attrs'][i]
    java_attr_pascal = capitalize_first_letter(java_attr)
    java_attr_type = params['java_attr_types'][i]
    sql_field = params['sql_fields'][i]
    
    is_first = is_last = False
    if(i == 0): is_first = True
    if(i == json_keys_num - 1): is_last = True    
    
    text11 += f'''
    private {java_attr_type} {java_attr};'''

    text13 += f'''
        {java_attr_type} {java_attr}'''

    text14 += f'''
        this.{java_attr} = {java_attr};'''

    text15 += f'''
        this.{java_attr} = {object_name}.get{java_attr_pascal}();'''

    text16 += f'''
    public {java_attr_type} get{java_attr_pascal}() {{
        return {java_attr};
    }}'''
    

    text17 += f'''
    public void set{java_attr_pascal}(String {java_attr}) {{
        this.{java_attr} = {java_attr};
    }}
    '''

    text18 += f'''
            + "{java_attr}=" + {java_attr} + ", "'''

    text301 += f'''
                            jsonDetailInfo.getString("{json_key}")'''
    text302 += f'''
                        ((JSONObject) jsonDetailInfo).getString("{json_key}")'''
    text501 += f'`{sql_field}`'
    text502 += f'#{{{java_attr}}}'
    text503 += f'#{{item.{java_attr}}}'
    text504 += f'`{sql_field}` = #{{{java_attr}}}'

    text505 += f'''
            <trim prefix=" `{sql_field}` = CASE " suffix=" END, ">
                <foreach collection="list" item="item">
                    WHEN `{primary_attr}` = #{{item.{primary_attr}}} THEN #{{item.{java_attr}}}
                </foreach>
            </trim>'''

    text506 += f'`{sql_field}` = VALUES(`{sql_field}`)'
    text61 += f'''
    `{sql_field}` VARCHAR(255) DEFAULT NULL'''
    
    if(is_last):
        text13 += '''
    )'''
        text14 += '''
    }'''

        text15 += '''
    }'''
        text18 += '''
            + "]";
    }
}'''
        text501 += ')'
        text502 += ')'
        text503 += ')'   
        text61 += '''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;'''
    else:
        text13 += ','
        text301 += ','
        text302 += ','
        text501 += ','
        text502 += ','
        text503 += ','
        text504 += ', '
        text506 += ', '
        text61 += ','
        
        
text3 += f'''
    // 单项加工
    public List<{class_name}> process{class_name}(String response) {{
        List<{class_name}> {object_name}List = new ArrayList<>();
        JSONObject jsonObj = JSON.parseObject(response);
        if (jsonObj != null) {{
            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组
            if (jsonInfo != null) {{
                for (int i = 0; i < jsonInfo.size(); i++) {{// 遍历 JSON 数组依次取出 JSON 对象
                    JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);
                    {object_name}List.add(new {class_name}({text301}
                    ));
                }}
            }}
        }}
        return {object_name}List;
    }}

    // 依次加工
    public List<{class_name}> process{class_name}(List<String> responses) {{
        List<{class_name}> {object_name}List = new ArrayList<>();
        for (String response : responses) {{
            {object_name}List.addAll(process{class_name}(response));
        }}
        return {object_name}List;
    }}

    // 流水线加工
    public List<{class_name}> pielineProcess{class_name}(List<String> responses) {{
        return responses.stream()
                .map(JSON::parseObject)
                .filter(Objects::nonNull)
                .flatMap(jsonObj -> jsonObj.getJSONArray("data").stream())
                .map(jsonDetailInfo -> new {class_name}({text302}
                ))
                .collect(Collectors.toList());
    }}

    // 依次插入或更新
    public void insertOrUpdate{class_name}(List<{class_name}> {object_name}List) {{
        for({class_name} {object_name} : {object_name}List) {{
            {project_name}Mapper.insertOrUpdate{class_name}({object_name});
        }}
        return ;
    }}

    // 分页插入或更新
    public void pageInsertOrUpdate{class_name}(List<{class_name}> {object_name}List) throws Exception {{
        int pageDataSize = {page_data_size}; // 页面数据量大小，即每页记录数 × 字段数，可自行设置
        int totalFields = {class_name}.class.getDeclaredFields().length; // 总字段数，即数据表中的列数
        int pageSize = pageDataSize / totalFields; // 页面大小，即每页记录数

        pageHandle({object_name}List, pageSize, {project_name}Mapper::batchInsertOrUpdate{class_name});
        return ;
    }}

    // 清空
    public void clear{class_name}() {{
        {project_name}Mapper.clear{class_name}();
        return ;
    }}

'''

# 依次插入或更新
text551 = f'''
    <!-- 依次插入或更新 -->
    <insert id="insertOrUpdate{class_name}" parameterType="{class_name}">
        INSERT INTO `{table_name}` {text501}
        VALUES {text502}
        ON DUPLICATE KEY UPDATE {text506}
    </insert>
'''

# 批量插入或更新
text552 = f'''
    <!-- 批量插入或更新 -->
    <insert id="batchInsertOrUpdate{class_name}" parameterType="java.util.List">
        INSERT INTO `{table_name}` {text501}
        VALUES
        <foreach collection="list" item="item" separator=",">
            {text503}
        </foreach>
        ON DUPLICATE KEY UPDATE {text506}
    </insert>
'''

# 清空
text571 = f'''
    <!-- 清空 -->
    <update id="clear{class_name}">
        TRUNCATE TABLE `{table_name}`
    </update>
'''

with open(params['output_name_1'],"w",encoding='utf-8') as f:

    f.write('\n\n'.join([
        text11,
        text12,
        text13 + text14,
        text15,
        text16,
        text17,
        text18
    ]))

    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_2'],"w",encoding='utf-8') as f:

    f.write(''.join([
        text2
    ]))

    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_3'],"w",encoding='utf-8') as f:

    f.write(''.join([
        text3
    ]))

    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_4'],"w",encoding='utf-8') as f:

    f.write(''.join([
        text4
    ]))

    f.flush() # 写入硬盘            
    f.close() # 关闭文件


with open(params['output_name_5'],"w",encoding='utf-8') as f:
    f.write(''.join([
        text551,
        text552,
        text571
    ]))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_6'],"w",encoding='utf-8') as f:
    f.write(''.join([
        text61,
    ]))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件