# PythonTest

一个简单的Python项目模板。

## 项目结构

```
PythonTest/
├── main.py              # 主程序入口
├── src/                 # 源代码目录
│   ├── __init__.py
│   └── utils.py         # 工具函数
├── tests/               # 测试目录
│   ├── __init__.py
│   └── test_main.py     # 测试文件
├── docs/                # 文档目录
├── requirements.txt     # 项目依赖
├── .gitignore          # Git忽略文件
├── .env.example        # 环境变量示例
└── README.md           # 项目说明
```

## 安装和运行

1. **创建虚拟环境**:
   ```bash
   python -m venv venv
   ```

2. **激活虚拟环境**:
   - Windows: `venv\Scripts\activate`
   - macOS/Linux: `source venv/bin/activate`

3. **安装依赖**:
   ```bash
   pip install -r requirements.txt
   ```

4. **运行项目**:
   ```bash
   python main.py
   ```

## 开发

### 运行测试
```bash
python -m pytest tests/
```

### 代码格式化
```bash
black .
```

### 代码检查
```bash
flake8 .
```

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
