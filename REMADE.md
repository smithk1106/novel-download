# 项目简介
在线小说下载网站 | 小说下载工具  | 免费小说 | 网络小说 | 免费下载 | 网文下载

这是一款专为追求极致阅读体验的书友打造的小说下载工具，支持 docker 多平台，打造在线小说下载网站，无需复杂配置。项目内置多书源，能够将全网热门连载与完本小说一键下载为 EPUB、TXT、HTML、PDF 等主流格式，方便导入各类专业阅读器或设备，实现全设备离线阅读。无论你是 iOS 用户、电脑用户，还是电子书阅读器爱好者，都能轻松获取高质量小说资源，享受自由、便捷的阅读体验。项目专注于下载核心功能，界面简洁高效，开箱即用，助你随时随地畅读心仪好书。

# 项目使用技术

本项目主要技术栈如下：

- 前端：Vue3
- 后端：Java17、Spring Boot3、MyBatis-plus
- 数据库：H2（可扩展为其他数据库）,本地文件系统
- 构建工具：Maven
- WebSocket 实时通信
- 容器化部署：Docker

# 使用

```bash
docker pull ideaflow1/novel-download:latest

docker run -p 30000:30000 --name ideaflow1-novel-download \
-v /ideaflowNovelDownload/download:/app/download \
-v /ideaflowNovelDownload/db:/app/db \
-v /ideaflowNovelDownload/logs:/app/logs \
-d ideaflow1/novel-download:latest

```

/ideaflowNovelDownload/download 请更改成 你的下载小说存储的路径
/ideaflowNovelDownload/db  请更改成 你的页面数据存储的路径
/ideaflowNovelDownload/logs  请更改成 你的程序运行日志存储的路径

# 文档部署教程
[部署文档地址](https://www.ideaflow.top/article/82)


# 参考开源项目
 [so-novel](https://github.com/freeok/so-novel)
