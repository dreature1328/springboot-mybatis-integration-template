import os
import json
import uuid
import random
import time
from datetime import datetime

def generate_mock_data(count):
    data = []
    characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    
    for _ in range(count):
        # 取 UUID 的高 64 位并转换为非负长整型值
        uuid_value = uuid.uuid4()
        uuid_bytes = uuid_value.bytes
        msb_bytes = uuid_bytes[:8]
        data_id = abs(int.from_bytes(msb_bytes, byteorder='big', signed=True))
        
        # 取 0-10000 之间的随机整数
        numeric_value = random.randint(0, 10000)

        # 取 0.0-100.0 之间的随机小数（保留两位）
        decimal_value = round(random.uniform(0.0, 100.0) * 100) / 100.0
        
        # 取 16 位随机字符串（大小写字母及数字）
        text_content = ''.join(random.choices(characters, k=16))
        
        # 取随机布尔值
        active_flag = random.choice([True, False])
        
        data.append({
            "id": data_id,
            "numericValue": numeric_value,
            "decimalValue": decimal_value,
            "textContent": text_content,
            "activeFlag": active_flag
        })
    
    return data

def create_response(data):
    return {
        "success": True,
        "code": "SUCCESS",
        "message": "操作成功",
        "data": data,
        "timestamp": int(time.time() * 1000)  # 毫秒级时间戳
    }

def save_to_file(data, filename):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(current_dir, filename)
    
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print(f"保存文件: {output_path}")
    print(f"生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"数据条目: {len(data['data'])}")

if __name__ == '__main__':
    # 数据生成
    mock_data = generate_mock_data(500)
    # 响应结构
    response = create_response(mock_data)
    # 文件保存
    save_to_file(response, 'mock_data.json')