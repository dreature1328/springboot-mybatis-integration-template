import json
import uuid
import random

def generate_random_str(randomlength=16):
    base_str = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    return ''.join(random.choices(base_str, k=randomlength))

def generate_mock_json():
    return {
        "id": str(uuid.uuid4()),
        "key1": generate_random_str(),
        "key2": generate_random_str()
    }

# 利用 json.dump() 函数将生成的数据直接写入文件
data = {"success": True, "code": "SUCCESS", "message": "操作成功", "data": [generate_mock_json() for _ in range(500)]}

with open('mock_data.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)