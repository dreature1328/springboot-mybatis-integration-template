




import random

def generate_random_str(randomlength = 16):
  # randomlength 是生成值的长度
  random_str =''
  base_str ='ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz0123456789'
  length =len(base_str) -1
  for i in range(randomlength):
    random_str += base_str[random.randint(0, length)]
  return random_str

def generate_mock_json(id):
    return '        {"id":"' + str(id).zfill(6) + '","key1":"' + generate_random_str() + '","key2":"' + generate_random_str() + '"}';

with open('mock_data.json',"w",encoding='utf-8') as f:
    mock_data = '\n'.join([
        '{',
        '    "code" : "200",',
        '    "msg" : "success",',
        '    "data" : [\n'                                                           
        ])
    length = 500
    for i in range(0, length):
       mock_data += generate_mock_json(i+1)
       if(i != length -1): mock_data += ',\n'
    mock_data = '\n'.join([mock_data,
        '    ]',
        '}'
        ])
    f.write(mock_data)
    f.flush() # 写入硬盘            
    f.close() # 关闭文件