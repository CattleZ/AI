"""
测试工具函数模块
"""

import os
import tempfile
import json
import pytest
from datetime import datetime

import sys
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from src.utils import (
    load_config,
    save_config,
    get_timestamp,
    validate_email,
    format_file_size,
    chunk_list
)


class TestUtils:
    """测试工具函数类"""
    
    def test_load_config_success(self):
        """测试成功加载配置文件"""
        with tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False) as f:
            test_config = {"test": "value", "number": 42}
            json.dump(test_config, f)
            temp_path = f.name
        
        try:
            result = load_config(temp_path)
            assert result == test_config
        finally:
            os.unlink(temp_path)
    
    def test_load_config_file_not_found(self):
        """测试加载不存在的配置文件"""
        with pytest.raises(FileNotFoundError):
            load_config("nonexistent_file.json")
    
    def test_save_config(self):
        """测试保存配置文件"""
        with tempfile.TemporaryDirectory() as temp_dir:
            config_path = os.path.join(temp_dir, "test_config.json")
            test_config = {"key": "value", "list": [1, 2, 3]}
            
            save_config(test_config, config_path)
            
            # 验证文件是否创建
            assert os.path.exists(config_path)
            
            # 验证内容是否正确
            with open(config_path, 'r', encoding='utf-8') as f:
                loaded_config = json.load(f)
            assert loaded_config == test_config
    
    def test_get_timestamp(self):
        """测试获取时间戳"""
        timestamp = get_timestamp()
        
        # 验证格式
        assert isinstance(timestamp, str)
        assert len(timestamp) == 19  # "YYYY-MM-DD HH:MM:SS"
        
        # 验证能够解析
        parsed = datetime.strptime(timestamp, "%Y-%m-%d %H:%M:%S")
        assert isinstance(parsed, datetime)
    
    def test_validate_email(self):
        """测试邮箱验证"""
        # 有效邮箱
        valid_emails = [
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org"
        ]
        
        for email in valid_emails:
            assert validate_email(email), f"应该是有效邮箱: {email}"
        
        # 无效邮箱
        invalid_emails = [
            "invalid.email",
            "@example.com",
            "user@",
            "user name@example.com",
            ""
        ]
        
        for email in invalid_emails:
            assert not validate_email(email), f"应该是无效邮箱: {email}"
    
    def test_format_file_size(self):
        """测试文件大小格式化"""
        test_cases = [
            (0, "0B"),
            (512, "512.0B"),
            (1024, "1.0KB"),
            (1536, "1.5KB"),
            (1048576, "1.0MB"),
            (1073741824, "1.0GB")
        ]
        
        for size_bytes, expected in test_cases:
            result = format_file_size(size_bytes)
            assert result == expected, f"输入 {size_bytes} 应该返回 {expected}，但得到 {result}"
    
    def test_chunk_list(self):
        """测试列表分块"""
        # 测试正常分块
        lst = [1, 2, 3, 4, 5, 6, 7, 8, 9]
        result = chunk_list(lst, 3)
        expected = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
        assert result == expected
        
        # 测试不能整除的情况
        result2 = chunk_list(lst, 4)
        expected2 = [[1, 2, 3, 4], [5, 6, 7, 8], [9]]
        assert result2 == expected2
        
        # 测试空列表
        result3 = chunk_list([], 3)
        assert result3 == []
        
        # 测试块大小大于列表长度
        result4 = chunk_list([1, 2], 5)
        assert result4 == [[1, 2]]
