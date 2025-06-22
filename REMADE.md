

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
