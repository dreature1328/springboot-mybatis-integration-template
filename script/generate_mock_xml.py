import uuid
import random
import xml.etree.ElementTree as ET
from xml.dom import minidom

def generate_random_str(randomlength=16):
    base_str = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    return ''.join(random.choices(base_str, k=randomlength))

def generate_mock_xml():
    # 创建根元素 <response>
    root = ET.Element("response")
    
    # 添加固定字段
    ET.SubElement(root, "success").text = "true"
    ET.SubElement(root, "code").text = "SUCCESS"
    ET.SubElement(root, "message").text = "操作成功"
    
    # 创建数据容器 <data>
    data_element = ET.SubElement(root, "data")
    
    # 生成<item>元素
    for _ in range(500):
        item = ET.SubElement(data_element, "item")
        ET.SubElement(item, "id").text = str(uuid.uuid4())
        ET.SubElement(item, "key1").text = generate_random_str()
        ET.SubElement(item, "key2").text = generate_random_str()
    
    # 生成带缩进的XML字符串
    rough_xml = ET.tostring(root, encoding='utf-8')
    parsed = minidom.parseString(rough_xml)
    return parsed.toprettyxml(indent="    ")

# 写入XML文件
with open('mock_data.xml', 'w', encoding='utf-8') as f:
    f.write(generate_mock_xml())