import os
import json
import random
import time
import string
import uuid
import xml.etree.ElementTree as ET
from xml.dom import minidom
from datetime import datetime, timedelta

class AdvancedMockXMLGenerator:
    def __init__(self):
        self.status_options = list(range(11))  # 0-10的状态值
        
        # 标签池
        self.tag_pool = [
            "active", "pending", "completed", "archived", "flagged",
            "featured", "verified", "test", "development", "production",
            "high-priority", "medium-priority", "low-priority", "bug",
            "enhancement", "documentation", "security", "performance"
        ]
        
        # 分类
        self.categories = [
            "system", "user", "data", "network", "security",
            "application", "service", "resource", "configuration",
            "monitoring", "analytics", "storage", "computation"
        ]
        
        # 通用环境
        self.environments = ["dev", "test", "staging", "production", "sandbox"]
    
    def generate_code(self, index):
        """生成编码"""
        prefixes = ["ENT", "OBJ", "RES", "SRC", "TMP", "CFG"]
        prefix = random.choice(prefixes)
        date_str = datetime.now().strftime("%y%m%d")  # 短年份格式
        seq = str(index).zfill(6)
        suffix = ''.join(random.choices(string.ascii_uppercase, k=2))
        return f"{prefix}-{date_str}-{seq}-{suffix}"
    
    def generate_name(self):
        """生成名称"""
        prefixes = ["auto", "sys", "data", "net", "app", "web", "api", "db"]
        descriptors = [
            "monitor", "analyzer", "processor", "validator", "generator",
            "scheduler", "notifier", "collector", "aggregator", "dispatcher"
        ]
        suffixes = ["service", "component", "module", "unit", "agent", "daemon"]
        
        pattern = random.choice([1, 2, 3])
        if pattern == 1:
            return f"{random.choice(prefixes)}-{random.choice(descriptors)}"
        elif pattern == 2:
            return f"{random.choice(descriptors)}-{random.choice(suffixes)}"
        else:
            return f"{random.choice(prefixes)}-{random.choice(descriptors)}-{random.choice(suffixes)}"
    
    def generate_jsonb_attributes(self):
        """生成动态属性"""
        attribute_types = random.choice(["system", "data", "config", "monitoring", "custom"])
        
        if attribute_types == "system":
            attributes = {
                "category": random.choice(self.categories),
                "subcategory": f"subcategory-{random.randint(1, 20)}",
                "version": f"{random.randint(1, 5)}.{random.randint(0, 9)}.{random.randint(0, 99)}",
                "environment": random.choice(self.environments),
                "specs": {
                    "concurrency": str(random.randint(1, 100)),
                    "timeout": str(random.randint(1, 300)),
                    "retry_attempts": str(random.randint(0, 5)),
                    "memory_limit": f"{random.randint(128, 8192)}MB",
                    "cpu_cores": str(random.choice([1, 2, 4, 8, 16]))
                }
            }
        elif attribute_types == "data":
            attributes = {
                "data_type": random.choice(["structured", "unstructured", "semi-structured"]),
                "format": random.choice(["json", "xml", "csv", "binary", "text"]),
                "volume": f"{random.randint(1, 1000)}{random.choice(['MB', 'GB', 'TB'])}",
                "sensitivity": random.choice(["public", "internal", "confidential", "restricted"]),
                "retention_days": str(random.randint(1, 3650)),
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
                        str(random.randint(1, 100)),
                        random.choice(["true", "false"]),
                        f"value_{random.randint(1, 100)}"
                    ]) for i in range(random.randint(1, 5))
                }
            }
        elif attribute_types == "monitoring":
            attributes = {
                "monitoring_enabled": random.choice(["true", "false"]),
                "metrics": random.sample([
                    "cpu_usage", "memory_usage", "disk_io", "network_throughput",
                    "request_rate", "error_rate", "latency", "queue_size"
                ], k=random.randint(1, 5)),
                "alerts": {
                    "level": random.choice(["info", "warning", "error", "critical"]),
                    "threshold": str(random.randint(1, 100)),
                    "notifications": random.choice(["email", "webhook", "slack", "sms"])
                },
                "dashboard_url": f"https://monitor.example.com/dashboards/{''.join(random.choices(string.ascii_lowercase + string.digits, k=8))}"
            }
        else:  # custom
            attributes = {
                "metadata": {
                    "created_by_system": random.choice(["system-a", "system-b", "system-c"]),
                    "last_modified_by": f"user-{random.randint(1000, 9999)}",
                    "priority": str(random.randint(1, 5)),
                    "expiry_date": (datetime.now() + timedelta(days=random.randint(1, 365))).strftime("%Y-%m-%d")
                },
                "settings": {
                    "auto_update": random.choice(["true", "false"]),
                    "logging_level": random.choice(["debug", "info", "warn", "error"]),
                    "backup_enabled": random.choice(["true", "false"]),
                    "performance_mode": random.choice(["standard", "optimized", "debug"])
                }
            }
        
        # 添加通用属性
        attributes["created_timestamp"] = str(int(time.time() * 1000) - random.randint(0, 86400000))
        attributes["modification_count"] = str(random.randint(0, 100))
        
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
        
        tags = random.sample(self.tag_pool, min(num_tags, len(self.tag_pool)))
        
        if random.random() < 0.3:
            custom_prefixes = ["custom", "user-defined", "project-specific", "temporary"]
            custom_tags = [f"{random.choice(custom_prefixes)}-{random.randint(1, 100)}" for _ in range(random.randint(1, 3))]
            tags.extend(custom_tags)
        
        return list(set(tags))
    
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
    
    def generate_id(self):
        """生成 ID"""
        # 使用UUID的高64位并确保为正数
        uuid_value = uuid.uuid4()
        uuid_bytes = uuid_value.bytes
        msb_bytes = uuid_bytes[:8]
        return abs(int.from_bytes(msb_bytes, byteorder='big', signed=True))
    
    def dict_to_xml_element(self, parent, dict_obj, element_name=None):
        """递归将字典转换为 XML元素"""
        if isinstance(dict_obj, dict):
            if element_name:
                elem = ET.SubElement(parent, element_name)
            else:
                elem = parent
                
            for key, value in dict_obj.items():
                if isinstance(value, dict):
                    child_elem = ET.SubElement(elem, key)
                    self.dict_to_xml_element(child_elem, value)
                elif isinstance(value, list):
                    child_elem = ET.SubElement(elem, key)
                    for item in value:
                        if isinstance(item, dict):
                            item_elem = ET.SubElement(child_elem, "item")
                            self.dict_to_xml_element(item_elem, item)
                        else:
                            ET.SubElement(child_elem, "item").text = str(item)
                else:
                    ET.SubElement(elem, key).text = str(value)
            return elem
        else:
            if element_name:
                elem = ET.SubElement(parent, element_name)
                elem.text = str(dict_obj)
                return elem
            else:
                parent.text = str(dict_obj)
                return parent
    
    def generate_single_item(self, index):
        """生成单条 XML 记录"""
        record_id = self.generate_id()
        created_at = self.generate_timestamp()
        
        # 更新时间通常在创建时间之后（0-30天内）
        updated_at_delta = timedelta(
            days=random.randint(0, 30),
            hours=random.randint(0, 23),
            minutes=random.randint(0, 59)
        )
        updated_at = (datetime.fromisoformat(created_at) + updated_at_delta).isoformat()
        
        # 创建 item 元素
        item = ET.Element("item")
        
        # 添加基本字段
        ET.SubElement(item, "id").text = str(record_id)
        ET.SubElement(item, "code").text = self.generate_code(index)
        ET.SubElement(item, "name").text = self.generate_name()
        ET.SubElement(item, "status").text = str(random.choice(self.status_options))
        ET.SubElement(item, "createdAt").text = created_at
        ET.SubElement(item, "updatedAt").text = updated_at
        
        # 添加 attributes
        attributes = self.generate_jsonb_attributes()
        attr_elem = ET.SubElement(item, "attributes")
        self.dict_to_xml_element(attr_elem, attributes)
        
        # 添加 tags
        tags = self.generate_tags_array()
        tags_elem = ET.SubElement(item, "tags")
        for tag in tags:
            ET.SubElement(tags_elem, "tag").text = tag
        
        # 添加可选字段
        if random.random() > 0.7:
            related_elem = ET.SubElement(item, "relatedEntities")
            for _ in range(random.randint(1, 3)):
                ET.SubElement(related_elem, "entity").text = f"related-{random.randint(1000, 9999)}"
        
        return item
    
    def generate_advanced_xml(self, count):
        """生成高级 XML 数据"""
        # 创建根元素
        root = ET.Element("response")
        
        # 添加响应信息
        ET.SubElement(root, "success").text = "true"
        ET.SubElement(root, "code").text = "SUCCESS"
        ET.SubElement(root, "message").text = "操作成功"
        ET.SubElement(root, "timestamp").text = str(int(time.time() * 1000))
        
        # 创建 data 元素
        data_elem = ET.SubElement(root, "data")
        
        used_codes = set()
        
        for i in range(count):
            if (i + 1) % 100 == 0:
                print(f"已生成 {i + 1}/{count} 条记录")
            
            item = self.generate_single_item(i)
            
            # 确保 code 唯一
            code_elem = item.find("code")
            while code_elem.text in used_codes:
                code_elem.text = self.generate_code(i)
            used_codes.add(code_elem.text)
            
            data_elem.append(item)
        
        return root
    
    def save_to_file(self, xml_root, filename=None):
        """保存 XML 数据到文件"""
        if filename is None:
            filename = "advanced_data.xml"
        
        current_dir = os.path.dirname(os.path.abspath(__file__))
        output_path = os.path.join(current_dir, filename)
        
        # 生成格式化的XML
        rough_string = ET.tostring(xml_root, encoding='utf-8')
        reparsed = minidom.parseString(rough_string)
        pretty_xml = reparsed.toprettyxml(indent="    ", encoding='utf-8')
        
        with open(output_path, 'wb') as f:
            f.write(pretty_xml)
        
        print("\n" + "="*60)
        print(f"文件保存位置: {output_path}")
        print(f"生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        # 统计记录数
        record_count = len(xml_root.find(".//data"))
        print(f"数据条目: {record_count}")
        print(f"文件大小: {os.path.getsize(output_path) / 1024:.2f} KB")
        print("="*60)
        
        return output_path

def main():
    try:
        # 设置生成数量
        count = 500
        print(f"\n正在生成 {count} 条数据...")
        
        # 初始化生成器
        generator = AdvancedMockXMLGenerator()
        
        # 生成XML数据
        xml_root = generator.generate_advanced_xml(count)
        
        # 保存文件
        output_file = generator.save_to_file(xml_root)
        
        print(f"\n✓ 数据生成完成!")
        print(f"✓ 文件已保存至: {output_file}")
        
        # 显示一些示例
        print("\n示例记录预览:")
        data_elem = xml_root.find(".//data")
        if data_elem is not None:
            items = list(data_elem.findall("item"))[:2]
            for i, item in enumerate(items):
                print(f"\n示例 {i+1}:")
                id_elem = item.find("id")
                code_elem = item.find("code")
                name_elem = item.find("name")
                tags_elem = item.find("tags")
                created_elem = item.find("createdAt")
                
                if id_elem is not None:
                    print(f"  ID: {id_elem.text}")
                if code_elem is not None:
                    print(f"  编码: {code_elem.text}")
                if name_elem is not None:
                    print(f"  名称: {name_elem.text}")
                if tags_elem is not None:
                    tags = [tag.text for tag in tags_elem.findall("tag")]
                    print(f"  标签数: {len(tags)}")
                if created_elem is not None:
                    print(f"  创建时间: {created_elem.text[:10]}")
        
    except Exception as e:
        print(f"\n✗ 生成数据时发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    main()