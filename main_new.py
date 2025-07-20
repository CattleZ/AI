#!/usr/bin/env python3
"""
PythonTest - 主程序入口
"""
from typing import List
from sentence_transformers import SentenceTransformer
import chromadb

embedding_model = SentenceTransformer("shibing624/text2vec-base-chinese")

# 创建一个chromdb的客户端(内存型的客户端)
client = chromadb.EphemeralClient()
chromadb_collection = client.get_or_create_collection(name="default")


# 第一步分片
def split_into_chunks(doc_file: str) -> List[str]:
    """
    将文档分割成片段
    
    Args:
        doc_file: 文档文件路径
        
    Returns:
        文档片段列表
    """
    with open(doc_file, "r", encoding="utf-8") as f:
        text = f.read()
    return [chunk for chunk in text.split("\n\n") if chunk.strip()]


# 第二步嵌入生成向量
def generate_embeddings(chunk: str) -> List[float]:
    """
    为文档片段生成嵌入向量
    
    Args:
        chunk: 文档片段
        
    Returns:
        嵌入向量列表
    """
    embedding = embedding_model.encode(chunk)
    return embedding.tolist()


# 第三步：向ChromaDB添加向量
def save_to_chromadb(chunks: List[str], embeddings: List[List[float]]) -> None:
    """
    将文档片段和对应的嵌入向量保存到ChromaDB
    
    Args:
        chunks: 文档片段列表
        embeddings: 对应的嵌入向量列表
    """
    ids = [str(i) for i in range(len(chunks))]
    chromadb_collection.add(
        documents=chunks,
        embeddings=embeddings,
        ids=ids
    )
    print(f"向ChromaDB添加向量成功，向量数量: {len(chunks)}")


def main() -> None:
    """
    主程序入口函数
    
    该函数执行文档分片和嵌入生成的完整流程。
    """
    print("开始处理文档...")
    
    # 第一步：分片
    chunks = split_into_chunks("doc.md")
    print(f"文档分片数量: {len(chunks)}")
    
    # 第二步：生成嵌入向量
    print("正在生成嵌入向量...")
    embeddings = [generate_embeddings(chunk) for chunk in chunks]
    print(f"嵌入向量数量: {len(embeddings)}")

    # 第三步：保存到ChromaDB
    print("正在保存向量到ChromaDB...")
    save_to_chromadb(chunks, embeddings)
    print("处理完成！")


if __name__ == "__main__":
    main()
