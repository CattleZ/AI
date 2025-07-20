"""
测试主程序模块
"""

import sys
import os
import pytest
from unittest.mock import patch

# 添加项目根目录到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

import main


class TestMain:
    """测试主程序类"""
    
    def test_main_function_exists(self):
        """测试主函数是否存在"""
        assert hasattr(main, 'main')
        assert callable(main.main)
    
    @patch('builtins.print')
    def test_main_output(self, mock_print):
        """测试主函数输出"""
        main.main()
        
        # 验证是否调用了print
        assert mock_print.called
        
        # 获取所有print调用的参数
        call_args = [str(call[0][0]) for call in mock_print.call_args_list]
        
        # 验证输出内容
        assert "Hello, World!" in call_args
        assert "欢迎使用 PythonTest 项目!" in call_args
    
    def test_main_execution(self):
        """测试主函数执行不会抛出异常"""
        try:
            main.main()
        except Exception as e:
            pytest.fail(f"main() raised {e} unexpectedly!")
