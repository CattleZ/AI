#!/usr/bin/env python3
"""
简化版MCP通信日志记录器

该脚本作为MCP服务器的代理，记录所有stdin/stdout通信。
使用方法：
  python mcp_logger_simple.py <MCP服务器启动命令...>
"""

import json
import logging
import subprocess
import sys
import threading
from datetime import datetime
from pathlib import Path


class ThreadSafeLogger:
    """线程安全的日志记录器"""
    
    def __init__(self, log_file: Path):
        self.log_file = log_file
        self.lock = threading.Lock()
        self.setup_logging()
    
    def setup_logging(self) -> None:
        """设置日志配置"""
        self.log_file.parent.mkdir(parents=True, exist_ok=True)
        
        # 创建logger
        self.logger = logging.getLogger('mcp_logger')
        self.logger.setLevel(logging.INFO)
        
        # 清除已有的处理器
        self.logger.handlers.clear()
        
        # 文件处理器
        file_handler = logging.FileHandler(self.log_file, encoding='utf-8')
        file_handler.setLevel(logging.INFO)
        
        # 控制台处理器
        console_handler = logging.StreamHandler(sys.stderr)
        console_handler.setLevel(logging.INFO)
        
        # 设置格式
        formatter = logging.Formatter(
            '%(asctime)s - %(levelname)s - %(message)s'
        )
        file_handler.setFormatter(formatter)
        console_handler.setFormatter(formatter)
        
        # 添加处理器
        self.logger.addHandler(file_handler)
        self.logger.addHandler(console_handler)
    
    def log_message(self, direction: str, data: str) -> None:
        """线程安全的消息记录"""
        with self.lock:
            timestamp = datetime.now().isoformat()
            
            # 尝试解析JSON消息
            try:
                if data.strip():
                    json_data = json.loads(data.strip())
                    formatted_data = json.dumps(
                        json_data, indent=2, ensure_ascii=False
                    )
                else:
                    formatted_data = data
            except (json.JSONDecodeError, ValueError):
                formatted_data = data
                
            log_entry = f"""
{'='*80}
时间: {timestamp}
方向: {direction}
消息内容:
{formatted_data}
{'='*80}
"""
            
            self.logger.info(log_entry)
            # 强制刷新以确保立即写入
            for handler in self.logger.handlers:
                handler.flush()
    
    def info(self, message: str) -> None:
        """记录信息日志"""
        with self.lock:
            self.logger.info(message)
            for handler in self.logger.handlers:
                handler.flush()
    
    def error(self, message: str) -> None:
        """记录错误日志"""
        with self.lock:
            self.logger.error(message)
            for handler in self.logger.handlers:
                handler.flush()


def handle_pipe(source, target, logger, direction):
    """处理管道数据传输和日志记录"""
    try:
        while True:
            line = source.readline()
            if not line:
                break
            
            # 立即记录原始消息（调试用）
            logger.info(f"[{direction}] 收到数据: {repr(line)}")
                
            # 记录格式化消息
            logger.log_message(direction, line.rstrip('\n\r'))
            
            # 转发消息
            target.write(line)
            target.flush()
            
    except Exception as e:
        logger.error(f"处理{direction}消息时出错: {e}")
        import traceback
        logger.error(f"详细错误信息: {traceback.format_exc()}")


def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("错误: 请提供MCP服务器启动命令", file=sys.stderr)
        print("使用方法:", file=sys.stderr)
        print("  python mcp_logger_simple.py <命令> [参数...]", file=sys.stderr)
        print("示例:", file=sys.stderr)
        print("  python mcp_logger_simple.py uv --directory /path run "
              "script.py", file=sys.stderr)
        sys.exit(1)
    
    # 获取项目根目录: tools -> MCP -> src -> 项目根目录
    script_dir = Path(__file__).parent
    project_root = script_dir.parent.parent.parent
    log_file = project_root / "logs" / "mcp_communication.log"
    
    # 设置日志
    logger = ThreadSafeLogger(log_file)
    
    # 解析服务器命令
    server_cmd = sys.argv[1:]
    
    try:
        logger.info("启动MCP通信日志记录器...")
        logger.info(f"日志文件路径: {log_file}")
        logger.info(f"启动MCP服务器: {' '.join(server_cmd)}")
        
        # 启动MCP服务器进程
        process = subprocess.Popen(
            server_cmd,
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=0
        )
        
        logger.info(f"MCP服务器已启动，PID: {process.pid}")
        logger.info("准备启动监听线程...")
        
        # 创建线程处理输入输出
        threads = [
            threading.Thread(
                target=handle_pipe,
                args=(sys.stdin, process.stdin, logger, "CLIENT_TO_SERVER"),
                daemon=True,
                name="ClientToServer"
            ),
            threading.Thread(
                target=handle_pipe,
                args=(process.stdout, sys.stdout, logger, "SERVER_TO_CLIENT"),
                daemon=True,
                name="ServerToClient"
            ),
            threading.Thread(
                target=handle_pipe,
                args=(process.stderr, sys.stderr, logger, "SERVER_ERROR"),
                daemon=True,
                name="ServerError"
            )
        ]
        
        # 启动所有线程
        for thread in threads:
            logger.info(f"启动线程: {thread.name}")
            thread.start()
        
        logger.info("所有监听线程已启动，等待MCP通信...")
        
        # 等待进程结束
        process.wait()
        logger.info(f"MCP服务器进程已结束，退出码: {process.returncode}")
        
    except KeyboardInterrupt:
        logger.info("收到中断信号，正在关闭...")
        if 'process' in locals():
            process.terminate()
    except Exception as e:
        logger.error(f"运行出错: {e}")
        import traceback
        logger.error(f"详细错误: {traceback.format_exc()}")
        sys.exit(1)


if __name__ == "__main__":
    main()
