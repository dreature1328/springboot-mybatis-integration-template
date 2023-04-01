#-*- coding:utf-8 -*-

import re

# 分割单词
def to_words(name):
    words = []                  # 用于存储单词的列表
    word = ''                   # 用于存储正在构建的单词

    if(len(name) <= 1):
        words.append(name)
        return words

    # 按照常见分隔符进行分割
    # name_parts = re.split('[ _\-/\\\\]+', name)
    # 按照非数字字母字符进行分割
    name_parts = re.split('[^0-9a-zA-Z]', name)
    for part in name_parts:
        part_len = len(part)        # 字符串的长度
        word = ''
        # 如果子串为空，继续循环
        if not part:
            continue   
        for index, char in enumerate(part):
            # “小|大无”
            if(index == part_len - 1):
                if(char.isupper() and part[index-1].islower()):
                    if(word): words.append(word)
                    words.append(char)
                    word = ''
                    continue

            # “有|大小”或“小|大有”
            elif(index != 0 and char.isupper()):
                if((part[index-1].islower() and part[index+1].isalpha()) or (part[index-1].isalpha() and part[index+1].islower())):
                    if(word): words.append(word)
                    word = ''
            word += char
        if(len(word) > 0): words.append(word)
    # 去除空单词
    return [word for word in words if word != '']

# 分割成全小写单词
def to_lower_words(name):
    words = to_words(name)
    return [word.lower() for word in words]

# 分割成全大写单词
def to_upper_words(name):
    words = to_words(name)
    return [word.upper() for word in words]

# 分割成首大写、其余小写单词
def to_capital_words(name):
    words = to_words(name)
    return [word.capitalize() for word in words]

# 转短横线命名法
def to_kebab_case(name):
    words = to_lower_words(name)
    to_kebab_case = '-'.join(words)
    return to_kebab_case

# 转小蛇式命名法
def to_snake_case(name):
    words = to_lower_words(name)
    snake_case_name = '_'.join(words)
    return snake_case_name

# 转大蛇式命名法
def to_macro_case(name):
    words = to_upper_words(name)
    snake_case_name = '_'.join(words)
    return snake_case_name

# 转小驼峰命名法
def to_camel_case(name):
    words = to_words(name)
    camel_case_words = []
    for word in words:
        if len(word) <= 1:
            camel_case_words.append(word.upper())
        else:
            camel_case_words.append(word[0].upper() + word[1:])

    camel_case = ''.join(camel_case_words)
    if len(camel_case) <= 1:
        camel_case = camel_case.lower()
    else:
        camel_case = ''.join(camel_case[0].lower() + camel_case[1:])
    return camel_case

# 转大驼峰命名法
def to_pascal_case(name):
    words = to_words(name)
    pascal_case_words = []
    for word in words:
        if len(word) <= 1:
            pascal_case_words.append(word.upper())
        else:
            pascal_case_words.append(word[0].upper() + word[1:])
    pascal_case = ''.join(pascal_case_words)
    return pascal_case

# 仅首字母大写，其余不变
def capitalize_first_letter(name):
    return name[:1].upper() + name[1:]

# 仅首字母小写，其余不变
def uncapitalize_first_letter(name):
    return name[:1].lower() + name[1:]