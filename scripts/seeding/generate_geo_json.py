import os
import json
import random
import time
import string
from datetime import datetime, timedelta

class GeoJsonMockDataGenerator:
    def __init__(self):
        # 中国大致经纬度范围
        self.lng_min, self.lng_max = 73.0, 135.0
        self.lat_min, self.lat_max = 3.0, 54.0

        # 地址池
        self.address_pool = [
            "北京市东城区长安街1号",
            "上海市浦东新区世纪大道100号",
            "广州市天河区天河路385号",
            "深圳市南山区科技园南区科苑路15号",
            "杭州市西湖区文三路18号",
            "成都市武侯区天府大道北段1288号",
            "武汉市江汉区解放大道1234号",
            "南京市玄武区中山路321号",
            "重庆市渝中区解放碑步行街",
            "西安市雁塔区雁塔南路88号",
            "长沙市岳麓区岳麓大道233号",
            "郑州市金水区金水路115号",
            "青岛市市南区香港中路78号",
            "大连市中山区人民路60号",
            "厦门市思明区湖滨南路99号"
        ]
    # 生成要素编码的前缀池
    def generate_feature_id(self, index):
        """生成要素编码，例如 GEO-240101-000001-AB"""
        prefixes = ["GEO", "FEAT", "MAP", "GIS", "LAND"]
        prefix = random.choice(prefixes)
        date_str = datetime.now().strftime("%y%m%d")
        seq = str(index).zfill(6)
        suffix = ''.join(random.choices(string.ascii_uppercase, k=2))
        return f"{prefix}-{date_str}-{seq}-{suffix}"
    # 生成要素名称
    def generate_feature_name(self):
        prefixes = ["road", "building", "park", "water", "bridge", "tunnel", 
                    "square", "tower", "station", "airport", "harbor", "dam"]
        descriptors = ["north", "south", "east", "west", "central", "new", "old",
                       "main", "branch", "elevated", "underground", "scenic"]
        suffixes = ["segment", "section", "area", "zone", "gate", "terminal"]
        pattern = random.choice([1, 2, 3])
        if pattern == 1:
            return f"{random.choice(prefixes)}-{random.choice(descriptors)}"
        elif pattern == 2:
            return f"{random.choice(descriptors)}-{random.choice(suffixes)}"
        else:
            return f"{random.choice(prefixes)}-{random.choice(descriptors)}-{random.choice(suffixes)}"

    def generate_address(self):
        return random.choice(self.address_pool)

    def generate_timestamp(self, base_date=None, days_back=365):
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

    # 生成随机点坐标
    def generate_random_point(self):
        lng = random.uniform(self.lng_min, self.lng_max)
        lat = random.uniform(self.lat_min, self.lat_max)
        return [round(lng, 6), round(lat, 6)]
    
    # 生成随机线坐标数组
    def generate_random_linestring(self, num_points=5):
        points = []
        for _ in range(max(2, num_points)):
            points.append(self.generate_random_point())
        return points

    # 生成随机多边形坐标
    def generate_random_polygon(self, num_vertices=5):
        points = []
        for _ in range(max(3, num_vertices)):
            points.append(self.generate_random_point())
        points.append(points[0])
        return [points]

    def generate_random_geometry(self):
        # 随机生成一种几何类型（点、线、面）
        geom_type = random.choices(['Point', 'LineString', 'Polygon'], weights=[0.6, 0.3, 0.1])[0]
        if geom_type == 'Point':
            coords = self.generate_random_point()
        elif geom_type == 'LineString':
            coords = self.generate_random_linestring(random.randint(3, 8))
        else:  # Polygon
            coords = self.generate_random_polygon(random.randint(4, 8))
        return {"type": geom_type, "coordinates": coords}

    # 根据几何类型计算中心点
    def compute_center_point(self, geometry):
        geom_type = geometry["type"]
        coords = geometry["coordinates"]
        if geom_type == "Point":
            return coords
        elif geom_type == "LineString":
            lng_sum = sum(p[0] for p in coords)
            lat_sum = sum(p[1] for p in coords)
            return [round(lng_sum / len(coords), 6), round(lat_sum / len(coords), 6)]
        elif geom_type == "Polygon":
            ring = coords[0]
            if ring[0] == ring[-1]:
                ring = ring[:-1]
            lng_sum = sum(p[0] for p in ring)
            lat_sum = sum(p[1] for p in ring)
            return [round(lng_sum / len(ring), 6), round(lat_sum / len(ring), 6)]
        else:
            return [0, 0]

    def generate_single_feature(self, index, feature_id=None, record_id=None):
        """
        生成一个GeoJSON Feature，包含顶层 id 字段
        :param index: 序号（用于生成 feature_id）
        :param feature_id: 可选，若为None则自动生成
        :param record_id: 可选，若为None则自动生成（正随机数）
        """
        if feature_id is None:
            feature_id = self.generate_feature_id(index)
        if record_id is None:
            record_id = random.getrandbits(63)
            if record_id == 0:
                record_id = 1

        created_at = self.generate_timestamp()
        updated_at_delta = timedelta(
            days=random.randint(0, 30),
            hours=random.randint(0, 23),
            minutes=random.randint(0, 59)
        )
        updated_at = (datetime.fromisoformat(created_at) + updated_at_delta).isoformat()

        geometry = self.generate_random_geometry()
        geom_type = geometry["type"]
        center_point = self.compute_center_point(geometry)

        properties = {
            "feature_id": feature_id,
            "feature_name": self.generate_feature_name(),
            "address": self.generate_address(),
            "geom_type": geom_type,
            "center_point": center_point,
            "created_at": created_at,
            "updated_at": updated_at
        }

        feature = {
            "id": record_id,          # 顶层 id，供数据库主键使用
            "type": "Feature",
            "geometry": geometry,
            "properties": properties
        }
        return feature

    def generate_batch_data(self, count):
        data = []
        used_feature_ids = set()
        used_record_ids = set()
        for i in range(count):
            # 生成唯一的 feature_id
            while True:
                fid = self.generate_feature_id(i)
                if fid not in used_feature_ids:
                    used_feature_ids.add(fid)
                    break
            # 生成唯一的 record_id
            while True:
                rid = random.getrandbits(63)
                if rid == 0:
                    continue
                if rid not in used_record_ids:
                    used_record_ids.add(rid)
                    break
            feature = self.generate_single_feature(i, fid, rid)
            data.append(feature)
            if (i + 1) % 100 == 0:
                print(f"已生成 {i + 1}/{count} 个要素")
        return data

    def create_geojson(self, data):
        return {
            "type": "FeatureCollection",
            "data": data
        }

    def save_to_file(self, geojson_data, filename=None):
        if filename is None:
            filename = "geo_data.geojson"
        current_dir = os.path.dirname(os.path.abspath(__file__))
        output_path = os.path.join(current_dir, filename)
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(geojson_data, f, ensure_ascii=False, indent=2)
        print("\n" + "="*60)
        print(f"GeoJSON 文件保存位置: {output_path}")
        print(f"生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"要素数量: {len(geojson_data['data'])}")
        print(f"文件大小: {os.path.getsize(output_path) / 1024:.2f} KB")
        print("="*60)
        return output_path

def main():
    try:
        count = 500
        print(f"\n正在生成 {count} 个 GeoJSON 要素...")
        generator = GeoJsonMockDataGenerator()
        data = generator.generate_batch_data(count)
        geojson = generator.create_geojson(data)
        output_file = generator.save_to_file(geojson)
        print(f"\n✓ GeoJSON 数据生成完成!")
        print(f"✓ 文件已保存至: {output_file}")
        # 预览第一个要素
        if data:
            first = data[0]
            print("\n示例要素预览:")
            print(json.dumps(first, indent=2, ensure_ascii=False)[:600] + "...")
    except Exception as e:
        print(f"\n✗ 生成数据时发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    main()