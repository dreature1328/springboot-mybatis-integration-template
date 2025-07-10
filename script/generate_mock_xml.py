import os
import uuid
import random
import time
import xml.etree.ElementTree as ET
from xml.dom import minidom
from datetime import datetime

def generate_mock_xml(count):
    # 创建根元素 <response>
    root = ET.Element("response")
    
    # 添加固定字段
    ET.SubElement(root, "success").text = "true"
    ET.SubElement(root, "code").text = "SUCCESS"
    ET.SubElement(root, "message").text = "操作成功"
    ET.SubElement(root, "timestamp").text = str(int(time.time() * 1000))  # 毫秒级时间戳
    
    # 创建数据容器 <data>
    data_element = ET.SubElement(root, "data")
    
    # 生成<item>元素
    for _ in range(count):
        item = ET.SubElement(data_element, "item")
        
        # 生成与JSON一致的ID（UUID高64位转非负长整型）
        uuid_value = uuid.uuid4()
        uuid_bytes = uuid_value.bytes
        msb_bytes = uuid_bytes[:8]
        data_id = abs(int.from_bytes(msb_bytes, byteorder='big', signed=True))
        
        # 添加各字段
        ET.SubElement(item, "id").text = str(data_id)
        ET.SubElement(item, "numericValue").text = str(random.randint(0, 10000))
        ET.SubElement(item, "decimalValue").text = str(round(random.uniform(0.0, 100.0) * 100) / 100.0)
        
        # 生成16位随机字符串
        characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
        ET.SubElement(item, "textContent").text = ''.join(random.choices(characters, k=16))
        
        # 随机布尔值
        ET.SubElement(item, "activeFlag").text = str(random.choice([True, False])).lower()
    
    # 生成带缩进的XML字符串
    rough_xml = ET.tostring(root, encoding='utf-8')
    parsed = minidom.parseString(rough_xml)
    return parsed.toprettyxml(indent="    ")

def save_to_file(data, filename):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(current_dir, filename)
    
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(xml_data)
    
    print(f"保存文件: {os.path.abspath(output_path)}")
    print(f"生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"数据条目: 500")

if __name__ == '__main__':
    # 生成XML数据
    xml_data = generate_mock_xml(500)
    # 文件保存
    save_to_file(xml_data, 'mock_data.xml')
