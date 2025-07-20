<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Copilot 自定义指令

## 项目概述
这是一个Python项目模板，包含了标准的项目结构和工具配置。

## 代码风格和约定

### Python代码规范
- 使用Black进行代码格式化
- 遵循PEP 8编码规范
- 使用类型提示 (Type Hints)
- 函数和类必须有详细的文档字符串
- 变量和函数名使用snake_case
- 类名使用PascalCase
- 常量使用UPPER_CASE

### 文档字符串格式
使用Google风格的文档字符串:
```python
def function_name(param1: int, param2: str) -> bool:
    """
    函数的简短描述
    
    Args:
        param1: 参数1的描述
        param2: 参数2的描述
        
    Returns:
        返回值描述
        
    Raises:
        异常类型: 异常描述
    """
```

### 测试约定
- 所有新功能都需要编写测试
- 测试文件命名为 `test_*.py`
- 测试类命名为 `Test*`
- 测试方法命名为 `test_*`
- 使用pytest框架

### 导入顺序
1. 标准库导入
2. 第三方库导入
3. 本地模块导入

每组导入之间用空行分隔。

### 异常处理
- 优先使用具体的异常类型而不是通用的Exception
- 在异常信息中提供有用的上下文
- 使用logging记录异常信息

### 性能考虑
- 优先使用列表推导式而不是循环
- 对于大数据集考虑使用生成器
- 避免不必要的字符串拼接操作

## 项目特定指令
- 在src/目录下添加新的模块
- 所有工具函数放在src/utils.py中
- 配置相关的代码应该支持JSON格式
- 添加适当的类型检查和输入验证
- 新增功能时更新相应的测试和文档
