import os
import json
import random
import time
import string
from datetime import datetime, timedelta

class AdvancedMockDataGenerator:
    def __init__(self):
        self.status_options = list(range(11))  # 0-10的状态值
        
        # 通用的标签池
        self.tag_pool = [
            "active", "pending", "completed", "archived", "flagged",
            "featured", "verified", "test", "development", "production",
            "high-priority", "medium-priority", "low-priority", "bug",
            "enhancement", "documentation", "security", "performance"
        ]
        
        # 通用分类
        self.categories = [
            "system", "user", "data", "network", "security",
            "application", "service", "resource", "configuration",
            "monitoring", "analytics", "storage", "computation"
        ]
        
        # 通用环境
        self.environments = ["dev", "test", "staging", "production", "sandbox"]
    
    def generate_code(self, index):
        """生成实体编码"""
        prefixes = ["ENT", "OBJ", "RES", "SRC", "TMP", "CFG"]
        prefix = random.choice(prefixes)
        date_str = datetime.now().strftime("%y%m%d")  # 短年份格式
        seq = str(index).zfill(6)
        suffix = ''.join(random.choices(string.ascii_uppercase, k=2))
        return f"{prefix}-{date_str}-{seq}-{suffix}"
    
    def generate_name(self):
        """生成通用实体名称"""
        # 格式: 前缀 + 描述 + 类型
        prefixes = ["auto", "sys", "data", "net", "app", "web", "api", "db"]
        descriptors = [
            "monitor", "analyzer", "processor", "validator", "generator",
            "scheduler", "notifier", "collector", "aggregator", "dispatcher"
        ]
        suffixes = ["service", "component", "module", "unit", "agent", "daemon"]
        
        # 随机组合模式
        pattern = random.choice([1, 2, 3])
        if pattern == 1:
            return f"{random.choice(prefixes)}-{random.choice(descriptors)}"
        elif pattern == 2:
            return f"{random.choice(descriptors)}-{random.choice(suffixes)}"
        else:
            return f"{random.choice(prefixes)}-{random.choice(descriptors)}-{random.choice(suffixes)}"
    
    def generate_jsonb_attributes(self):
        """生成JSONB动态属性 - 更通用化"""
        attribute_types = random.choice(["system", "data", "config", "monitoring", "custom"])
        
        if attribute_types == "system":
            attributes = {
                "category": random.choice(self.categories),
                "subcategory": f"subcategory-{random.randint(1, 20)}",
                "version": f"{random.randint(1, 5)}.{random.randint(0, 9)}.{random.randint(0, 99)}",
                "environment": random.choice(self.environments),
                "specs": {
                    "concurrency": random.randint(1, 100),
                    "timeout": random.randint(1, 300),
                    "retry_attempts": random.randint(0, 5),
                    "memory_limit": f"{random.randint(128, 8192)}MB",
                    "cpu_cores": random.choice([1, 2, 4, 8, 16])
                }
            }
        elif attribute_types == "data":
            attributes = {
                "data_type": random.choice(["structured", "unstructured", "semi-structured"]),
                "format": random.choice(["json", "xml", "csv", "binary", "text"]),
                "volume": f"{random.randint(1, 1000)}{random.choice(['MB', 'GB', 'TB'])}",
                "sensitivity": random.choice(["public", "internal", "confidential", "restricted"]),
                "retention_days": random.randint(1, 3650),
                "compression": random.choice(["none", "gzip", "lz4", "snappy", "zstd"])
            }
        elif attribute_types == "config":
            attributes = {
                "config_type": random.choice(["static", "dynamic", "templated", "inherited"]),
                "source": random.choice(["file", "database", "api", "environment"]),
                "validation": random.choice(["enabled", "disabled"]),
                "refresh_interval": f"{random.randint(1, 3600)} seconds",
                "parameters": {
                    f"param_{i}": random.choice([
                        random.randint(1, 100),
                        random.choice(["true", "false"]),
                        f"value_{random.randint(1, 100)}"
                    ]) for i in range(random.randint(1, 5))
                }
            }
        elif attribute_types == "monitoring":
            attributes = {
                "monitoring_enabled": random.choice([True, False]),
                "metrics": random.sample([
                    "cpu_usage", "memory_usage", "disk_io", "network_throughput",
                    "request_rate", "error_rate", "latency", "queue_size"
                ], k=random.randint(1, 5)),
                "alerts": {
                    "level": random.choice(["info", "warning", "error", "critical"]),
                    "threshold": random.randint(1, 100),
                    "notifications": random.choice(["email", "webhook", "slack", "sms"])
                },
                "dashboard_url": f"https://monitor.example.com/dashboards/{''.join(random.choices(string.ascii_lowercase + string.digits, k=8))}"
            }
        else:  # custom
            attributes = {
                "metadata": {
                    "created_by_system": random.choice(["system-a", "system-b", "system-c"]),
                    "last_modified_by": f"user-{random.randint(1000, 9999)}",
                    "priority": random.randint(1, 5),
                    "expiry_date": (datetime.now() + timedelta(days=random.randint(1, 365))).strftime("%Y-%m-%d")
                },
                "settings": {
                    "auto_update": random.choice([True, False]),
                    "logging_level": random.choice(["debug", "info", "warn", "error"]),
                    "backup_enabled": random.choice([True, False]),
                    "performance_mode": random.choice(["standard", "optimized", "debug"])
                }
            }
        
        # 添加通用属性
        attributes["created_timestamp"] = int(time.time() * 1000) - random.randint(0, 86400000)  # 0-24小时内的随机时间
        attributes["modification_count"] = random.randint(0, 100)
        
        # 随机添加扩展属性
        if random.random() > 0.3:
            extensions = ["ext_a", "ext_b", "ext_c", "ext_d", "ext_e"]
            attributes["extensions"] = random.sample(extensions, k=random.randint(1, 3))
        
        return attributes
    
    def generate_tags_array(self):
        """生成标签数组"""
        num_tags = random.randint(0, 8)
        if num_tags == 0:
            return []
        
        # 从标签池中选择
        tags = random.sample(self.tag_pool, min(num_tags, len(self.tag_pool)))
        
        # 30%的概率添加自定义标签
        if random.random() < 0.3:
            custom_prefixes = ["custom", "user-defined", "project-specific", "temporary"]
            custom_tags = [f"{random.choice(custom_prefixes)}-{random.randint(1, 100)}" for _ in range(random.randint(1, 3))]
            tags.extend(custom_tags)
        
        return list(set(tags))  # 去重
    
    def generate_timestamp(self, base_date=None, days_back=365):
        """生成随机时间戳"""
        if base_date is None:
            base_date = datetime.now()
        
        random_days = random.randint(0, days_back)
        random_hours = random.randint(0, 23)
        random_minutes = random.randint(0, 59)
        random_seconds = random.randint(0, 59)
        random_microseconds = random.randint(0, 999999)
        
        past_date = base_date - timedelta(
            days=random_days, 
            hours=random_hours,
            minutes=random_minutes,
            seconds=random_seconds,
            microseconds=random_microseconds
        )
        
        return past_date.isoformat()
    
    def generate_single_record(self, index, record_id=None):
        """生成单条记录"""
        if record_id is None:
            # 生成一个64位的随机ID，但确保为正数
            record_id = random.getrandbits(63)
        
        created_at = self.generate_timestamp()
        
        # 更新时间通常在创建时间之后（0-30天内）
        updated_at_delta = timedelta(
            days=random.randint(0, 30),
            hours=random.randint(0, 23),
            minutes=random.randint(0, 59)
        )
        updated_at = (datetime.fromisoformat(created_at) + updated_at_delta).isoformat()
        
        record = {
            "id": record_id,
            "code": self.generate_code(index),
            "name": self.generate_name(),
            "status": random.choice(self.status_options),
            "attributes": self.generate_jsonb_attributes(),
            "tags": self.generate_tags_array(),
            "createdAt": created_at,
            "updatedAt": updated_at
        }
        
        # 添加可选字段
        if random.random() > 0.7:
            record["relatedEntities"] = [
                f"related-{random.randint(1000, 9999)}" 
                for _ in range(random.randint(1, 3))
            ]
        
        return record
    
    def generate_batch_records(self, count):
        """批量生成记录"""
        records = []
        used_codes = set()
        used_ids = set()
        
        for i in range(count):
            # 确保ID唯一
            while True:
                record_id = random.getrandbits(63)
                if record_id not in used_ids:
                    used_ids.add(record_id)
                    break
            
            record = self.generate_single_record(i, record_id)
            
            # 确保code唯一
            while record["code"] in used_codes:
                record["code"] = self.generate_code(i)
            used_codes.add(record["code"])
            
            records.append(record)
            
            # 每生成100条记录打印进度
            if (i + 1) % 100 == 0:
                print(f"已生成 {i + 1}/{count} 条记录")
        
        return records
    
    def create_response_structure(self, records):
        """创建响应数据结构"""
        return {
            "success": True,
            "code": "SUCCESS",
            "message": "操作成功",
            "data": records,
            "timestamp": int(time.time() * 1000)  # 毫秒级时间戳
        }
    
    def save_to_file(self, data, filename=None):
        """保存数据到文件"""
        if filename is None:
            # timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            # filename = f"generic_advanced_data_{timestamp}.json"
            filename = "mock_advanced_data.json"
        
        current_dir = os.path.dirname(os.path.abspath(__file__))
        output_path = os.path.join(current_dir, filename)
        
        # 确保输出目录存在
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        
        print("\n" + "="*60)
        print(f"文件保存位置: {output_path}")
        print(f"生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"数据条目: {len(data['data'])}")
        print(f"文件大小: {os.path.getsize(output_path) / 1024:.2f} KB")
        print("="*60)
        
        return output_path

def main():
    try:
        # 设置生成数量
        count = 500
        print(f"\n正在生成 {count} 条通用高级实体记录...")
        
        # 初始化生成器
        generator = AdvancedMockDataGenerator()
        
        # 生成数据
        records = generator.generate_batch_records(count)
        
        # 创建响应结构
        response = generator.create_response_structure(records)
        
        # 保存文件
        output_file = generator.save_to_file(response)
        
        print(f"\n✓ 数据生成完成!")
        print(f"✓ 文件已保存至: {output_file}")
        
        # 显示一些示例
        print("\n示例记录预览:")
        for i in range(min(2, len(records))):
            record = records[i]
            print(f"\n示例 {i+1}:")
            print(f"  ID: {record['id']}")
            print(f"  编码: {record['code']}")
            print(f"  名称: {record['name']}")
            print(f"  标签数: {len(record['tags'])}")
            print(f"  创建时间: {record['createdAt'][:10]}")
        
    except Exception as e:
        print(f"\n✗ 生成数据时发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    main()