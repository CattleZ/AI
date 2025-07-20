# API 文档

## 主程序模块 (main.py)

### main()
主函数，程序入口点。

**功能**: 打印欢迎信息

**参数**: 无

**返回值**: 无

**示例**:
```python
from main import main
main()
```

## 工具函数模块 (src/utils.py)

### load_config(config_path: str) -> Dict[str, Any]
加载JSON配置文件。

**参数**:
- `config_path` (str): 配置文件路径

**返回值**: 
- `Dict[str, Any]`: 配置字典

**异常**:
- `FileNotFoundError`: 配置文件不存在时抛出

**示例**:
```python
from src.utils import load_config
config = load_config("config.json")
```

### save_config(config: Dict[str, Any], config_path: str) -> None
保存配置到JSON文件。

**参数**:
- `config` (Dict[str, Any]): 配置字典
- `config_path` (str): 配置文件路径

**返回值**: 无

**示例**:
```python
from src.utils import save_config
save_config({"key": "value"}, "config.json")
```

### get_timestamp() -> str
获取当前时间戳字符串。

**返回值**: 
- `str`: 格式为 "YYYY-MM-DD HH:MM:SS" 的时间戳

**示例**:
```python
from src.utils import get_timestamp
timestamp = get_timestamp()  # "2025-07-18 10:30:45"
```

### validate_email(email: str) -> bool
验证邮箱地址格式。

**参数**:
- `email` (str): 邮箱地址

**返回值**: 
- `bool`: 邮箱格式是否有效

**示例**:
```python
from src.utils import validate_email
is_valid = validate_email("test@example.com")  # True
```

### format_file_size(size_bytes: int) -> str
格式化文件大小为易读的字符串。

**参数**:
- `size_bytes` (int): 文件大小（字节）

**返回值**: 
- `str`: 格式化后的大小字符串

**示例**:
```python
from src.utils import format_file_size
size = format_file_size(1024)  # "1.0KB"
```

### chunk_list(lst: List[Any], chunk_size: int) -> List[List[Any]]
将列表分割成指定大小的块。

**参数**:
- `lst` (List[Any]): 原始列表
- `chunk_size` (int): 每个块的大小

**返回值**: 
- `List[List[Any]]`: 分块后的列表

**示例**:
```python
from src.utils import chunk_list
chunks = chunk_list([1, 2, 3, 4, 5], 2)  # [[1, 2], [3, 4], [5]]
```
