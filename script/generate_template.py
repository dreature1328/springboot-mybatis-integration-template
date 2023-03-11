#-*- coding:utf-8 -*-
from string import Template
import re

seps=[' ','-','_','/','\\','\'','\"']

# 是否为分隔符
def issep(char):
    if(len(char)>1):
        return False
    for sep in seps:
        if(char == sep):
            return True
    return False

# 转下划线命名法
def to_under_score_case(original_name):
    under_score_name = []

    for index, char in enumerate(original_name):
        is_the_first = is_the_last = False
        if(index == 0):
            is_the_first = True
        elif(index == len(original_name) - 1):
            is_the_last = True

        if issep(char):
            under_score_name.append("_")
            continue
        elif char.isupper():
            if(is_the_first):
                pass
            elif(is_the_last):
                if(original_name[index-1].islower()):
                     under_score_name.append("_")
            else: #非首非尾
                if((original_name[index-1].islower() and original_name[index+1].isalpha()) or (original_name[index-1].isalpha() and original_name[index+1].islower())):
                    under_score_name.append("_")
        under_score_name.append(char)
    return "".join(under_score_name).lower()

# 转驼峰命名法
def to_camel_case(original_name):
    under_score_name = []

    for index, char in enumerate(original_name):
        if issep(char):
            continue
        if(index == 0):
            under_score_name.append(char)
            continue       
        pre_char = original_name[index-1]
        if issep(pre_char):
            under_score_name.append(char.upper())
            continue
        under_score_name.append(char)
    return "".join(under_score_name)

#　读取配置文件
def read_config(config_file_name):
    params = {}
    content=open(config_file_name,'r',encoding='utf-8')
    for line in content:
        comment_index = line.find('#')
        if(comment_index != -1): line = line[:comment_index]
        if(line.isspace()): continue
        entry = line.split("=",2)
        if(len(entry)<2): continue
        params[entry[0].strip()] = entry[1].strip()
    return params

params = read_config('config.properties')

data_name_camel = to_camel_case(params['data_name'])
data_name_under_score = to_under_score_case(params['data_name'])

if(not 'url_name' in params): params['url_name'] = data_name_camel.lower()
if(not 'class_name' in params): params['class_name'] = data_name_camel[:1].upper() + data_name_camel[1:]
if(not 'object_name' in params): params['object_name'] = data_name_camel[:1].lower() + data_name_camel[1:]
if(not 'table_name' in params): params['table_name'] = data_name_under_score

action_name_list = ['request','select','insert','update','insertOrUpdate','delete','clear']
for index, action_name in enumerate(action_name_list):
    action_param_lower_camel_name = 'action_name_' + str(index + 1) + '_lower_camel'
    action_param_upper_camel_name = 'action_name_' + str(index + 1) + '_upper_camel'
    action_param_lower_name = 'action_name_' + str(index + 1) + '_lower'
    if(not action_param_lower_camel_name in params): params[action_param_lower_camel_name] = action_name_list[index][:1].lower() + action_name_list[index][1:]
    if(not action_param_upper_camel_name in params): params[action_param_upper_camel_name] = action_name_list[index][:1].upper() + action_name_list[index][1:]
    if(not action_param_lower_name in params): params[action_param_lower_name] = action_name_list[index].lower()

# Common 层

# 类的属性
text11 = '\n'.join([
    'public class ${class_name} {',
    '    // 类的属性',
    ])

# 类的成员列表构造函数
text12 = '\n'.join([
    '    // 类的成员列表构造函数',
    '    public ${class_name}('
    ])

text13 = '\n'.join(['{']) 
text14 = '\n'.join([ # 类的复制构造函数
    '    // 类的复制构造函数',
    '    public ${class_name}(${class_name} ${object_name}) {'
    ])
text15 = '\n'.join(['    // 类的 Getter 方法']) # 类的 Getter 方法
text16 = '\n'.join(['    // 类的 Setter 方法']) # 类的 Setter 方法
# 重写类的 toString 方法
text17 = '\n'.join([
    '    // 重写类的 toString 方法',
    '    @Override',
    '    public String toString() {',
    '        return',
    '            "${class_name}["'
    ])

# 生成对象
text405 = 'new ${class_name}('

# MyBatis 层
text501 = '(' # 如 (`id`,`attr1`,`attr2`)
text502 = '(' # 如 (#{id},#{attr1},#{attr2}) 
text503 = '(' # 如 (#{item.id},222#{item.attr1},222#{item.attr2}) 
text504 = '' # 如 `id` = #{id}, `attr1` = #{attr1}, `attr2` = #{attr2}
text505 = '            <!-- WHEN ... THEN ... 语句相当于编程语言中的 switch 语句 -->' # 如 `id` = #{item.id}, `attr1` = #{item.attr1}, `attr2` = #{item.attr2}
text506 = '' # 如 `id` = VALUES(`id`), `attr1` = VALUES(`attr1`), `attr2` = VALUES(`attr2`)

# 数据库建表的 SQL 语句
text61 = '\n'.join([
    'DROP TABLE IF EXISTS `${table_name}`;',
    'CREATE TABLE `${table_name}` ('
    ])

text62 = ''

text701 = '${protocal}://${domain}:${port}/${url_name}/${action_name_lower}';
text702 = '${protocal}://${domain}:${port}/${url_name}/b${action_name_lower}';
text703 = '${protocal}://${domain}:${port}/${url_name}/p${action_name_lower}';

text711 = '\n'.join([
    text701.replace('${action_name','${action_name_1'),
    text701.replace('${action_name','${action_name_2'),
    text702.replace('${action_name','${action_name_2'),
    text703.replace('${action_name','${action_name_2'),
    text701.replace('${action_name','${action_name_3'),
    text702.replace('${action_name','${action_name_3'),
    text703.replace('${action_name','${action_name_3'),
    text701.replace('${action_name','${action_name_4'),
    text702.replace('${action_name','${action_name_4'),
    text703.replace('${action_name','${action_name_4'),
    text701.replace('${action_name','${action_name_5'),
    text702.replace('${action_name','${action_name_5'),
    text703.replace('${action_name','${action_name_5'),
    text701.replace('${action_name','${action_name_6'),
    text702.replace('${action_name','${action_name_6'),
    text703.replace('${action_name','${action_name_6'),
    text701.replace('${action_name','${action_name_7') 
    ])

isInputJSON = False
json_key_list = []
sql_comment_list = []

content=open(params['input_name'],'r',encoding='utf-8')
for line in content:

    isInputJSON = False
    isInputDoc = False

    if (re.findall('".*".*:.*".*"', line)): isInputJSON = True
    elif (re.findall('\S+ +\S+ +\S+ +\S+', line)): isInputDoc = True

    pattern = re.compile('[\{\}"]|:.*')

    line = pattern.sub('',line).strip()

    if(line == ''): continue

    words = line.split(" ",1)
    json_key_list.append(words[0]) # 将 JSON 属性名依次加进列表

    if(isInputDoc): sql_comment_list.append(words[1]) # 将注释依次加进列表
    else: sql_comment_list.append('')

content.close()# 关闭文件

has_primary_key = False
json_key_num = len(json_key_list)
if('primary_key_name' in params): has_primary_key = True
elif(json_key_num >= 1 and not 'primary_key_name' in params): params['primary_key_name'] = json_key_list[1]
if(not 'primary_key_attr_name' in params): params['primary_key_attr_name'] = to_camel_case(params['primary_key_name'])
if(not 'primary_key_field_name' in params): params['primary_key_field_name'] = to_under_score_case(params['primary_key_name'])

for index, json_key in enumerate(json_key_list):

    java_attr = to_camel_case(json_key.replace(".","p"))
    sql_field = to_under_score_case(json_key)


    sql_comment = sql_comment_list[index]

    str_index = str(index+1)
    json_key_param_name = 'json_key_' + str_index
    java_attr_param_name = 'java_attr_' + str_index
    sql_field_param_name = 'sql_field_' + str_index
    sql_comment_param_name = 'sql_comment_' + str_index

    if(not json_key_param_name in params): params[json_key_param_name] = json_key
    if(not java_attr_param_name in params): params[java_attr_param_name] = java_attr
    if(not sql_field_param_name in params): params[sql_field_param_name] = sql_field
    if(not sql_comment_param_name in params): params[sql_comment_param_name] = sql_comment


    
    if(index == json_key_num - 1): is_the_last = True
    else: is_the_last = False 
 
    text11 = '\n'.join([text11, '    private String ${' + java_attr_param_name + '};'])
    text12 = '\n'.join([text12, '        String ${' + java_attr_param_name + '}'])
    text13 = '\n'.join([text13, '        this.${' + java_attr_param_name + '} = ${' + java_attr_param_name + '};'])
    text14 = '\n'.join([text14, '        this.${' + java_attr_param_name + '} = ${object_name}.get' + java_attr.capitalize() + '();'])
    text15 = '\n'.join([text15,
        '    public String get' + java_attr.capitalize() + '() {',
        '        return ${' + java_attr_param_name + '};',
        '    }'
        ])
    text16 = '\n'.join([text16,
        '    public void set' + java_attr.capitalize() + '(String ${' + java_attr_param_name + '}) {',
        '        this.${' + java_attr_param_name + '} = ${' + java_attr_param_name + '};',
        '    }'
        ])

    text17 = '\n'.join([text17, '            + "${' + java_attr_param_name + '}=" + ${' + java_attr_param_name + '} + ", "'])
    text405 = '\n'.join([text405, '                    jsonDetailInfo.getString("${' + json_key_param_name + '}")'])
    text501 += '`${' + sql_field_param_name + '}`'
    text502 += '#{${' + java_attr_param_name + '}}'
    text503 += '#{item.${' + java_attr_param_name + '}}'
    text504 += '`${' + sql_field_param_name + '}` = #{${' + java_attr_param_name + '}}'

    text505 = "\n".join([text505,
        '            <trim prefix=" `${' + sql_field_param_name + '}` = CASE " suffix=" END, ">',
        '                <foreach collection="list" item="item">',
        '                    WHEN `${primary_key_field_name}` = #{item.${primary_key_field_name}} THEN #{item.${' + java_attr_param_name + '}}',
        '                </foreach>',
        '            </trim>'
        ])

    text506 += '`${' + sql_field_param_name + '}` = VALUES(`${' + sql_field_param_name + '}`)'
    if(has_primary_key and sql_field == params['primary_key_field_name']): text61 = '\n'.join([text61, '`${' + sql_field_param_name + '}` VARCHAR(255) NOT NULL PRIMARY KEY'])
    else: text61 = '\n'.join([text61, '`${' + sql_field_param_name + '}` VARCHAR(255) DEFAULT NULL'])
    text62 = '\n'.join([text62, "ALTER TABLE ${table_name} MODIFY COLUMN `${" + sql_field_param_name + "}` VARCHAR(255)" +" comment '${" + sql_comment_param_name + "}';"])


    if(is_the_last):
        text12 = '\n'.join([text12,
            '    )'
            ])
        text13 = '\n'.join([text13,
            '    }'
            ])
        text14 = '\n'.join([text14,
            '    }'
            ])
        text17 = '\n'.join([text17,
            '            + "]";',
            '    }',
            '}'
            ])
        text405 = '\n'.join([text405, '                )'])        
        text501 += ')'
        text502 += ')'
        text503 += ')'   
        text61 = '\n'.join([text61,
            ') ENGINE=InnoDB DEFAULT CHARSET=utf8;'
            ])
    
    else:
        text12 += ','
        text405 += ','
        text501 += ','
        text502 += ','
        text503 += ','
        text504 += ', '
        text506 += ', '
        text61 += ','

# Controller 层

text201 = '\n'.join([
    '	@RequestMapping("/${url_name}/${action_name_lower}")',
    '	public void ${action_name_lower_camel}${class_name}() throws Exception {',
    '		${project_name}Service.${action_name_lower_camel}${class_name}();',
    '		return ;',
    '	}'
    ])
text202 = '\n'.join([
    '	@RequestMapping("/${url_name}/b${action_name_lower}")',
    '	public void batch${action_name_upper_camel}${class_name}() throws Exception {',
    '		${project_name}Service.batch${action_name_upper_camel}${class_name}();',
    '		return ;',
    '	}'
    ])
text203 = '\n'.join([
    '	@RequestMapping("/${url_name}/p${action_name_lower}")',
    '	public void page${action_name_upper_camel}${class_name}() throws Exception {',
    '		${project_name}Service.page${action_name_upper_camel}${class_name}();',
    '		return ;',
    '	}'
    ])

text211 = '\n'.join([
    '    // 发送请求',
    '    @RequestMapping("/${url_name}/${action_name_1_lower}")',
    '    public String ${action_name_1_lower_camel}${class_name}(HttpServletRequest request) throws Exception {',
    '        return ${project_name}Service.${action_name_1_lower_camel}${class_name}(request.getParameterMap());',
    '    }'
    ])
text221 = '\n'.join([
    '    // 依次查询',
    '	@RequestMapping("/${url_name}/${action_name_2_lower}")',
    '	public JSONObject ${action_name_2_lower_camel}${class_name}(String ${primary_key_attr_name}) throws Exception {',
    '		return (JSONObject) JSON.toJSON(${project_name}Service.${action_name_2_lower_camel}${class_name}(${primary_key_attr_name}));',
    '	}'
    ])
text222 = '\n'.join([
    '    // 批量查询',
    '	@RequestMapping("/${url_name}/b${action_name_2_lower}")',
    '	public JSONArray batch${action_name_2_upper_camel}${class_name}(String ${primary_key_attr_name}s) throws Exception {',
    '		return JSONArray.parseArray(JSON.toJSONString(${project_name}Service.batch${action_name_2_upper_camel}${class_name}(${primary_key_attr_name}s)));',
    '	}'
    ])
text223 = '\n'.join([
    '    // 分页查询',
    '	@RequestMapping("/${url_name}/p${action_name_2_lower}")',
    '	public JSONArray page${action_name_2_upper_camel}${class_name}(String ${primary_key_attr_name}s) throws Exception {',
    '		return JSONArray.parseArray(JSON.toJSONString(${project_name}Service.page${action_name_2_upper_camel}${class_name}(${primary_key_attr_name}s)));',
    '	}'
    ])
text231 = '\n'.join(['    // 依次插入',text201.replace('${action_name','${action_name_3')])
text232 = '\n'.join(['    // 批量插入',text202.replace('${action_name','${action_name_3')])
text233 = '\n'.join(['    // 分页插入',text203.replace('${action_name','${action_name_3')])
text241 = '\n'.join(['    // 依次更新',text201.replace('${action_name','${action_name_4')])
text242 = '\n'.join(['    // 批量更新',text202.replace('${action_name','${action_name_4')])
text243 = '\n'.join(['    // 分页更新',text203.replace('${action_name','${action_name_4')])
text251 = '\n'.join(['    // 依次插入或更新',text201.replace('${action_name','${action_name_5')])
text252 = '\n'.join(['    // 批量插入或更新',text202.replace('${action_name','${action_name_5')])
text253 = '\n'.join(['    // 分页插入或更新',text203.replace('${action_name','${action_name_5')])
text261 = '\n'.join([
    '    // 依次删除',
    '	@RequestMapping("/${url_name}/${action_name_6_lower}")',
    '	public void ${action_name_6_lower_camel}${class_name}(String ${primary_key_attr_name}) throws Exception {',
    '		${project_name}Service.${action_name_6_lower_camel}${class_name}(${primary_key_attr_name});',
    '		return ;',
    '	}'
    ])
text262 = '\n'.join([
    '    // 批量删除',
    '	@RequestMapping("/${url_name}/b${action_name_6_lower}")',
    '	public void batch${action_name_6_upper_camel}${class_name}(String ${primary_key_attr_name}s) throws Exception {',
    '		${project_name}Service.batch${action_name_6_upper_camel}${class_name}(${primary_key_attr_name}s);',
    '		return ;',
    '	}'
    ])
text263 = '\n'.join([
    '    // 分页删除',
    '	@RequestMapping("/${url_name}/p${action_name_6_lower}")',
    '	public void page${action_name_6_upper_camel}${class_name}(String ${primary_key_attr_name}s) throws Exception {',
    '		${project_name}Service.page${action_name_6_upper_camel}${class_name}(${primary_key_attr_name}s);',
    '		return ;',
    '	}'
    ])
text271 = '\n'.join([
    '    // 清空',
    '	@RequestMapping("/${url_name}/${action_name_7_lower}")',
    '	public void ${action_name_7_lower_camel}${class_name}() throws Exception {',
    '		${project_name}Service.${action_name_7_lower_camel}${class_name}();',
    '		return ;',
    '	}'
    ])



# Mapper 层
text321 = '\n'.join([
    '    // 依次查询',
    '    public ${class_name} ${action_name_2_lower_camel}${class_name}(String ${primary_key_attr_name});'
    ])
text322 = '\n'.join([
    '    // 批量查询',
    '    public List<${class_name}> batch${action_name_2_upper_camel}${class_name}(List<String> ${primary_key_attr_name}List);'
    ])
text331 = '\n'.join([
    '    // 依次插入',
    '    public void ${action_name_3_lower_camel}${class_name}(${class_name} ${object_name});'
    ])
text332 = '\n'.join([
    '    // 批量插入',
    '    public void batch${action_name_3_upper_camel}${class_name}(List<${class_name}> ${object_name}List);'
    ])
text341 = '\n'.join([
    '    // 依次更新',
    '    public void ${action_name_4_lower_camel}${class_name}(${class_name} ${object_name});'
    ])
text342 = '\n'.join([
    '    // 批量更新',
    '    public void batch${action_name_4_upper_camel}${class_name}(List<${class_name}> ${object_name}List);'
    ])
text351 = '\n'.join([
    '    // 依次插入或更新',
    '    public void ${action_name_5_lower_camel}${class_name}(${class_name} ${object_name});'
    ])
text352 = '\n'.join([
    '    // 批量插入或更新',
    '    public void batch${action_name_5_upper_camel}${class_name}(List<${class_name}> ${object_name}List);'
    ])
text361 = '\n'.join([
    '    // 依次删除',
    '    public void ${action_name_6_lower_camel}${class_name}(String ${primary_key_attr_name});'
    ])
text362 = '\n'.join([
    '    // 批量删除',
    '    public void batch${action_name_6_upper_camel}${class_name}(List<String> ${primary_key_attr_name}List);'
    ])
text371 = '\n'.join([
    '    // 清空',
    '    public void clear${class_name}();'
    ])


# 发送请求并获取返回的 JSON 字符串
text401 = '\n'.join([
    '        // 添加自定义参数，key 和 value 是你需要添加的参数名与参数值',
    '        final String key="";',
    '        final String value="";',
    '        String jsonStr = request${class_name}(new HashMap<String, String>() {{',
    '            put(key, value);',
    '        }});'
    ])

# 取出 JSON 数据并遍历
text402 = '\n'.join([
    '        JSONObject jsonObj = JSON.parseObject(jsonStr);// 将 JSON 字符串解析成 JSON 对象',
    '        if (jsonObj != null){',
    '            JSONArray jsonInfo = jsonObj.getJSONArray("data");//解析成 JSON 数组',
    '            if (jsonInfo != null) for (int i = 0; i < jsonInfo.size(); i++) {// 遍历 JSON 数组依次取出 JSON 对象',
    '                JSONObject jsonDetailInfo = jsonInfo.getJSONObject(i);',
    ])

# 批量处理前小部分
text403 = '\n'.join([   
    '        List<${class_name}> ${object_name}List = new ArrayList<>();'
    ])

# 分页处理前小部分
text404 = '\n'.join([   
    '        int pageSize = 12000/${class_name}.class.getDeclaredFields().length; // 页面大小，此处意为每次分页写入的数据量定在 12000',
    '        int curSize = 0; // 当前大小，当达到页面大小后就重置为 0',
    '        int cumSize = 0; // 累积大小，一直累积，不进行重置',
    '        List<${class_name}> ${object_name}List = new ArrayList<>();'
    ])

# 参数列表
text405 = text405

# 依次处理后半部分
text406 = '\n'.join([
    '                ${project_name}Mapper.${action_name_lower_camel}${class_name}(' + text405 + ');',
    '           }',
    '        }',
    '        return ;'
    ])

# 批量处理后半部分
text407 = '\n'.join([
    '                ${object_name}List.add(' + text405 + ');'
    '           }',
    '        }',
    '        ${project_name}Mapper.batch${action_name_upper_camel}${class_name}(${object_name}List);',
    '        return ;'
    ])

# 分页处理后半部分
text408 = '\n'.join([
    '                ${object_name}List.add(' + text405 + ');',
    '                curSize ++;',
    '                cumSize ++;',
    '                if(curSize == pageSize){',
    '                    ${project_name}Mapper.batch${action_name_upper_camel}${class_name}(${object_name}List);',
    '                    ${object_name}List.clear();',
    '                    curSize = 0;',
    '                }',
    '            }',
    '        }',
    '        if(cumSize > 0) ${project_name}Mapper.batch${action_name_upper_camel}${class_name}(${object_name}List);',
    '        return ;'
    ])

# 发送请求
text411 = '\n'.join([
    '    // 发起请求',
    '    public String ${action_name_1_lower_camel}${class_name}(Map params) throws Exception {',
    '        String strURL = "";',
    '        String method = "GET";',
    '        final String key="";',
    '        final String value="";',
    '        // 添加自定义请求头，key 和 value 是你需要添加的头信息的键与值，如用于鉴权',
    '        Map<String, String> headers = new HashMap<String, String>(){{',
    '            put(key, value);',
    '        }};',
    '        return getResponseContent(strURL, method, headers, params);',
    '    }'
    ])

# 依次查询
text421 = '\n'.join([
    '    // 依次查询',
    '    public ${class_name} ${action_name_2_lower_camel}${class_name}(String ${primary_key_attr_name}){',
    '        return ${project_name}Mapper.${action_name_2_lower_camel}${class_name}(${primary_key_attr_name});',
    '    }'
    ])

# 批量查询
text422 = '\n'.join([
    '    // 批量查询',
    '    public List<${class_name}> batch${action_name_2_upper_camel}${class_name}(String ${primary_key_attr_name}s){',
    '        return ${project_name}Mapper.batch${action_name_2_upper_camel}${class_name}(Arrays.asList(${primary_key_attr_name}s.split(",")));',
    '    }'
    ])

# 分页查询
text423 = '\n'.join([
    '    // 分页查询',
    '    public List<${class_name}> page${action_name_2_upper_camel}${class_name}(String ${primary_key_attr_name}s){',
    '        List<${class_name}> ${object_name}List = new ArrayList<>();',
    '        List<String> ${primary_key_attr_name}List = Arrays.asList(${primary_key_attr_name}s.split(","));',
    '        int ${primary_key_attr_name}Size = ${primary_key_attr_name}List.size();',
    '',
    '        int pageSize = 12000/1; // pageSize 给定页面大小，页面大小为 12000/参数种类',
    '        int pageNum = (${primary_key_attr_name}Size % pageSize == 0 ? ${primary_key_attr_name}Size / pageSize : ${primary_key_attr_name}Size / pageSize + 1); //页数向上取整',
    '',
    '        int start, end;',
    '        for(int i = 1; i <= pageNum; i++){',
    '            start = (i - 1) * pageSize;',
    '            if(i == pageNum) end = ${primary_key_attr_name}Size;',
    '            else end = i * pageSize;',
    '            ${object_name}List.addAll(${project_name}Mapper.batch${action_name_2_upper_camel}${class_name}(${primary_key_attr_name}List.subList(start, end)));',
    '        }',
    '        return ${object_name}List;',
    '    }'
    ])

# 依次插入
text431 = '\n'.join([
    '    // 依次插入',
    '    public void ${action_name_lower_camel}${class_name}() throws Exception {',
    text401,
    text402,
    text406,
    '    }'
    ]).replace('${action_name','${action_name_3')

# 批量插入
text432 = '\n'.join([
    '    // 批量插入',
    '    public void batch${action_name_upper_camel}${class_name}() throws Exception {',
    text401,
    text403,
    text402,
    text407,
    '    }'
    ]).replace('${action_name','${action_name_3')

# 分页插入
text433 = '\n'.join([
    '    // 分页插入',
    '    public void page${action_name_upper_camel}${class_name}() throws Exception {',
    text401,
    text404,
    text402,
    text408,
    '    }'
    ]).replace('${action_name','${action_name_3')

# 依次更新
text441 = '\n'.join([
    '    // 依次更新',
    '    public void update${class_name}() throws Exception {',
    text401,
    text402,
    text406,
    '    }'
    ]).replace('${action_name','${action_name_4')

# 批量更新
text442 = '\n'.join([
    '    // 批量更新',
    '    public void batchUpdate${class_name}() throws Exception {',
    text401,
    text403,
    text402,
    text407,
    '    }'
    ]).replace('${action_name','${action_name_4')

# 分页更新
text443 = '\n'.join([
    '    // 分页更新',
    '    public void pageUpdate${class_name}() throws Exception {',
    text401,
    text404,
    text402,
    text408,
    '    }'
    ]).replace('${action_name','${action_name_4')

# 依次插入或更新
text451 = '\n'.join([
    '    // 依次插入或更新',
    '    public void insertOrUpdate${class_name}() throws Exception {',
    text401,
    text402,
    text406,
    '    }'
    ]).replace('${action_name','${action_name_5')

# 批量插入或更新
text452 = '\n'.join([
    '    // 批量插入或更新',
    '    public void batchInsertOrUpdate${class_name}() throws Exception {',
    text401,
    text403,
    text402,
    text407,
    '    }'
    ]).replace('${action_name','${action_name_5')

# 分页插入或更新
text453 = '\n'.join([
    '    // 分页插入或更新',
    '    public void pageInsertOrUpdate${class_name}() throws Exception {',
    text401,
    text404,
    text402,
    text408,
    '    }'
    ]).replace('${action_name','${action_name_5')

# 依次删除
text461 = '\n'.join([
    '    // 依次删除',
    '    public void ${action_name_6_lower_camel}${class_name}(String ${primary_key_attr_name}){',
    '        ${project_name}Mapper.${action_name_6_lower_camel}${class_name}(${primary_key_attr_name});'
    '        return ;',
    '    }'
    ])

# 批量删除
text462 = '\n'.join([
    '    // 批量删除',
    '    public void batch${action_name_6_upper_camel}${class_name}(String ${primary_key_attr_name}s){',
    '        ${project_name}Mapper.batch${action_name_6_upper_camel}${class_name}(Arrays.asList(${primary_key_attr_name}s.split(",")));',
    '        return ;',
    '    }'
    ])

# 依次删除
text463 = '\n'.join([
    '    // 依次删除',
    '    public void page${action_name_6_upper_camel}${class_name}(String ${primary_key_attr_name}s){',
    '        List<String> ${primary_key_attr_name}List = Arrays.asList(${primary_key_attr_name}s.split(","));',
    '        int ${primary_key_attr_name}Size = ${primary_key_attr_name}List.size();',
    '',
    '        int pageSize = 12000/1; // pageSize 给定页面大小，页面大小为 12000/参数种类',
    '        int pageNum = (${primary_key_attr_name}Size % pageSize == 0 ? ${primary_key_attr_name}Size / pageSize : ${primary_key_attr_name}Size / pageSize + 1); //页数向上取整',
    '',
    '        int start, end;',
    '        for(int i = 1; i <= pageNum; i++){',
    '            start = (i - 1) * pageSize;',
    '            if(i == pageNum) end = ${primary_key_attr_name}Size;',
    '            else end = i * pageSize;',
    '            ${project_name}Mapper.${action_name_6_upper_camel}${class_name}(${primary_key_attr_name}List.subList(start, end));',
    '        }',
    '        return ;',
    '    }'
    ])

# 清空
text471 = '\n'.join([
    '    // 清空',
    '   public void ${action_name_7_lower_camel}${class_name}(){',
    '        ${project_name}Mapper.${action_name_7_lower_camel}${class_name}();',
    '        return ;',
    '    }'
    ])

# 依次查询
text521 = '\n'.join([
    '    <!-- 依次查询 -->',
    '    <select id="select${class_name}" parameterType="String" resultType="${class_name}">',
    '        SELECT * FROM `${table_name}`',
    '        WHERE `${primary_key_field_name}` = #{id}',
    '    </select>'
    ])

# 批量查询
text522 = '\n'.join([
    '    <!-- 批量查询 -->',
    '    <select id="batchSelect${class_name}" resultType="${class_name}">',
    '        SELECT * FROM `${table_name}`',
    '        WHERE `${primary_key_field_name}` IN',
    '        <foreach collection="list" item="item" open="(" close=")" separator=",">',
    '            #{item}',
    '        </foreach>',
    '    </select>'
    ])

# 依次插入
text531 = '\n'.join([
    '    <!-- 依次插入 -->',
    '    <insert id="insert${class_name}" parameterType="${class_name}">',
    '        INSERT INTO `${table_name}` ' + text501,
    '        VALUES ' + text502,
    '    </insert>'
    ])

# 批量插入
text532 = '\n'.join([
    '    <!-- 批量插入 -->',
    '    <insert id="batchInsert${class_name}" parameterType="java.util.List">',
    '        INSERT INTO `${table_name}` ' + text501,
    '        VALUES',
    '        <foreach collection="list" item="item" index="index" separator="," >',
    '            ' + text503,
    '        </foreach>',
    '    </insert>'
    ])

# 依次更新
text541 = '\n'.join([
    '    <!-- 依次更新 -->',
    '    <update id="update${class_name}" parameterType="${class_name}">',
    '        UPDATE `${table_name}`',
    '        SET '+ text504,
    '        WHERE `${primary_key_field_name}` = #{id}',
    '    </update>'
    ])

# 批量更新
text542 = '\n'.join([
    '    <!-- 批量更新 -->',
    '    <update id="batchUpdate${class_name}" parameterType="java.util.List">',
    '        UPDATE `${table_name}`',
    '        <trim prefix="SET" suffixOverrides=",">',
    text505,
    '        </trim>',
    '        WHERE `${primary_key_field_name}` IN',
    '        <foreach collection="list" item="item" open="(" close=")" separator=",">',
    '            #{item.${primary_key_field_name}}',
    '        </foreach>',
    '    </update>'
    ])

# 依次插入或更新
text551 = '\n'.join([
    '    <!-- 依次插入或更新 -->',
    '    <insert id="insertOrUpdate${class_name}" parameterType="${class_name}">',
    '        INSERT INTO `${table_name}` ' + text501,
    '        VALUES ' + text502,
    '        ON DUPLICATE KEY UPDATE ' + text506,
    '    </insert>'
    ])

# 批量插入或更新
text552 = '\n'.join([
    '    <!-- 批量插入或更新 -->',
    '    <insert id="batchInsertOrUpdate${class_name}" parameterType="java.util.List">',
    '        INSERT INTO `${table_name}` ' + text501,
    '        VALUES',
    '        <foreach collection="list" item="item" separator=",">',
    '            ' + text503,
    '        </foreach>',
    '        ON DUPLICATE KEY UPDATE ' + text506,
    '    </insert>'
    ])

# 依次删除
text561 = '\n'.join([
    '    <!-- 依次删除 -->',
    '    <delete id="delete${class_name}" parameterType="String">',
    '        DELETE FROM `${table_name}`',
    '        WHERE `${primary_key_field_name}` = #{id}',
    '    </delete>'
    ])

# 批量删除
text562 = '\n'.join([
    '    <!-- 批量删除 -->',
    '    <delete id="batchDelete${class_name}" parameterType="java.util.List">',
    '        DELETE FROM `${table_name}`',
    '        WHERE `${primary_key_field_name}` IN',
    '        <foreach collection="list" item="item" open="(" close=")" separator=",">',
    '            #{item}',
    '        </foreach>',
    '    </delete>'
    ])

# 清空
text571 = '\n'.join([
    '    <!-- 清空 -->',
    '    <update id="clear${class_name}">',
    '        TRUNCATE TABLE `${table_name}`',
    '    </update>'
    ])

for index, json_key in enumerate(json_key_list):
    if(index == json_key_num-1): is_the_last = True
    else: is_the_last = False 

with open(params['output_name_1'],"w",encoding='utf-8') as f:

    f.write(Template('\n\n'.join([
        text11,
        text12 + text13,
        text14,
        text15,
        text16,
        text17
    ])).substitute(params))

    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_2'],"w",encoding='utf-8') as f:
    f.write(Template('\n\n'.join([
        text211,
        text221,
        text222,
        text223,
        text231,
        text232,
        text233,
        text241,
        text242,
        text243,
        text251,
        text252,
        text253,
        text261,
        text262,
        text263,
        text271
    ])).substitute(params))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_3'],"w",encoding='utf-8') as f:
    f.write(Template('\n'.join([
        text321,
        text322,
        text331,
        text332,
        text341,
        text342,
        text351,
        text352,
        text361,
        text362,
        text371
    ])).substitute(params))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_4'],"w",encoding='utf-8') as f:
    f.write(Template('\n\n'.join([
        text411,
        text421,
        text422,
        text423,
        text431,
        text432,
        text433,
        text441,
        text442,
        text443,
        text451,
        text452,
        text453,
        text461,
        text462,
        text463,
        text471
    ])).substitute(params))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件


with open(params['output_name_5'],"w",encoding='utf-8') as f:
    f.write(Template('\n\n'.join([
        text521,
        text522,
        text531,
        text532,
        text541,
        text542,
        text551,
        text552,
        text561,
        text562,
        text571
    ])).substitute(params))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_6'],"w",encoding='utf-8') as f:
    f.write(Template('\n'.join([
        text61,
        text62
    ])).substitute(params))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件

with open(params['output_name_7'],"w",encoding='utf-8') as f:
    f.write(Template('\n'.join([
        text711
    ])).substitute(params))
    f.flush() # 写入硬盘            
    f.close() # 关闭文件