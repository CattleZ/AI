#!/usr/bin/env python3
"""
PythonTest - 主程序入口
"""
from typing import List
from sentence_transformers import SentenceTransformer, CrossEncoder
import chromadb
from dotenv import load_dotenv
import os
from google import genai

embedding_model = SentenceTransformer("shibing624/text2vec-base-chinese")

# 加载环境变量
load_dotenv()

# 初始化Google GenAI客户端
api_key = os.getenv('GEMINI_API_KEY')
if not api_key:
    raise ValueError("请在.env文件中设置GEMINI_API_KEY")

google_client = genai.Client(api_key=api_key)

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


# 第四步 召回
def recall_from_chromadb(query: str, top_k: int = 5) -> List[str]:
    """
    从ChromaDB中召回相关文档片段
    
    Args:
        query: 查询文本
        top_k: 返回的文档片段数量
        
    Returns:
        相关文档片段列表
    """
    query_embedding = embedding_model.encode(query)
    results = chromadb_collection.query(
        query_embeddings=[query_embedding.tolist()],
        n_results=top_k
    )
    return results['documents'][0] if results['documents'] else []


# 第五步 重排
def rerank_results(
    query: str,
    retrieved_chunks: List[str],
    topk: int
) -> List[str]:
    """
    使用交叉编码器对检索结果进行重排序
    
    Args:
        query: 查询文本
        retrieved_chunks: 检索到的文档片段
        topk: 返回的前k个结果
        
    Returns:
        重排序后的文档片段列表
    """
    cross_encoder = CrossEncoder("cross-encoder/mmarco-mMiniLMv2-L12-H384-v1")
    pairs = [(query, chunk) for chunk in retrieved_chunks]
    scores = cross_encoder.predict(pairs)
    
    # 将召回的片段及分数组成列表
    chunk_with_scores = [
        (chunk, score)
        for chunk, score in zip(retrieved_chunks, scores)
    ]
    chunk_with_scores.sort(key=lambda x: x[1], reverse=True)
    return [chunk for chunk, _ in chunk_with_scores[:topk]]


# 第六步：生成答案
def generate_answer(query: str, chunks: List[str]) -> str:
    """
    使用Google Gemini生成答案
    
    Args:
        query: 用户问题
        chunks: 相关文档片段
        
    Returns:
        生成的答案
    """
    context = (chr(10)*2).join(chunks)
    prompt = (
        f"你是一个知识助手，请根据用户的问题和下列片段生成准确的答案：\n\n"
        f"相关片段：{context}\n\n"
        f"用户问题：{query}\n\n"
        f"请基于上述内容回答，不要生成无关内容。"
    )
    
    try:
        response = google_client.models.generate_content(
            model="gemini-2.0-flash-exp",
            contents=prompt
        )
        return response.text if response and response.text else "无法生成答案"
    except Exception as e:
        print(f"生成答案时出错: {e}")
        return f"生成答案失败: {str(e)}"


# 主程序入口
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

    # 第四步：召回
    query = "哆啦A梦使用的3个秘密道具分别是什么?"
    results = recall_from_chromadb(query)
    reranked_results = rerank_results(query, results, topk=3)

    # 遍历打印results
    for i, result in enumerate(reranked_results):
        print(f"结果 {i}: {result}")
    
    # 第五步：生成
    answer = generate_answer(query, reranked_results)
    print(f"答案: {answer}")


if __name__ == "__main__":
    main()
