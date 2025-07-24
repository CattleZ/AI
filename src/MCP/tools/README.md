# MCP通信日志记录器

## 概述

`mcp_logger.py` 是一个用于记录MCP服务器与客户端（如Cline）之间通信的中间代理工具。它可以捕获、记录和转发所有的输入输出消息，方便调试和分析MCP通信过程。

## 功能特点

- **双向通信代理**: 作为MCP服务器和客户端之间的中间代理
- **完整日志记录**: 记录所有客户端到服务器和服务器到客户端的消息
- **JSON格式化**: 自动格式化JSON消息，提高可读性
- **错误处理**: 捕获并记录服务器错误输出
- **时间戳**: 为每条消息添加详细的时间戳
- **异步处理**: 使用异步I/O确保高性能通信

## 使用方法

### 1. 直接运行

```bash
cd /Users/zhanghe5/Desktop/LiXiang/学习/PythonTest
python src/MCP/tools/mcp_logger.py
```

### 2. 通过uv运行

```bash
cd /Users/zhanghe5/Desktop/LiXiang/学习/PythonTest
uv run src/MCP/tools/mcp_logger.py
```

### 3. 作为MCP服务器配置

在Claude Desktop或其他MCP客户端的配置中，将原来的服务器命令：

```json
{
  "command": "uv",
  "args": ["--directory", "/path/to/project", "run", "src/MCP/mcpserve-weather.py"]
}
```

替换为：

```json
{
  "command": "python",
  "args": ["/path/to/project/src/MCP/tools/mcp_logger.py"]
}
```

## 日志输出

日志文件会保存在 `logs/mcp_communication.log`，包含以下信息：

- **时间戳**: 消息发生的精确时间
- **方向**: 消息流向（CLIENT_TO_SERVER、SERVER_TO_CLIENT、SERVER_ERROR）
- **消息内容**: 格式化的JSON消息或原始文本

### 日志格式示例

```
================================================================================
时间: 2024-01-20T10:30:45.123456
方向: CLIENT_TO_SERVER
消息内容:
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "get_forecast",
    "arguments": {
      "latitude": 40.7128,
      "longitude": -74.0060
    }
  }
}
================================================================================
```

## 项目结构

```
src/MCP/tools/
├── mcp_logger.py          # 主日志记录脚本
├── README.md             # 使用说明（本文件）
└── logs/                 # 日志文件目录
    └── mcp_communication.log  # 通信日志文件
```

## 技术实现

### 核心组件

1. **MCPLogger类**: 主要的日志记录器类
   - 处理进程管理
   - 消息格式化和记录
   - 异步通信处理

2. **异步任务处理**:
   - `handle_client_to_server()`: 处理客户端到服务器的消息
   - `handle_server_to_client()`: 处理服务器到客户端的消息
   - `handle_server_errors()`: 处理服务器错误输出

3. **进程管理**:
   - 启动和监控MCP服务器进程
   - 优雅关闭和资源清理

### 依赖要求

- Python 3.11+
- asyncio（标准库）
- subprocess（标准库）
- json（标准库）
- logging（标准库）

## 调试和故障排除

### 常见问题

1. **服务器启动失败**
   - 检查项目路径是否正确
   - 确认uv和项目依赖已正确安装
   - 查看错误日志获取详细信息

2. **日志文件权限问题**
   - 确保logs目录存在且有写权限
   - 检查磁盘空间是否充足

3. **通信中断**
   - 检查MCP服务器是否正常运行
   - 确认客户端连接配置正确

### 调试模式

可以通过修改日志级别来获取更详细的调试信息：

```python
logging.basicConfig(level=logging.DEBUG)
```

## 扩展功能

可以根据需要扩展以下功能：

1. **消息过滤**: 添加消息类型过滤器，只记录特定类型的消息
2. **统计分析**: 添加通信统计和性能分析功能
3. **实时监控**: 实现Web界面或命令行界面实时查看通信状态
4. **消息重放**: 实现消息重放功能，用于测试和调试

## 注意事项

- 日志文件可能会变得很大，建议定期清理或实现日志轮转
- 在生产环境中使用时，考虑性能影响和存储空间
- 确保敏感信息不会被记录到日志中

## 许可证

本工具遵循项目的整体许可证协议。
