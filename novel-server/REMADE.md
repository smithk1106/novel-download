需要外网环境
```bash
mvn clean package -Dmaven.test.skip=true


docker build -t novel-download -f Dockerfile .

docker images

```
```bash
# --name指定容器名字 -v目录挂载 -p指定端口映射  -e设置mysql参数 -d后台运行
docker run -p 30006:30000 --name novel-download \
-v /Users/wangpenglong/dockerMounts/novel-download/download:/app/download \
-v /Users/wangpenglong/dockerMounts/novel-download/db:/app/db \
-v /Users/wangpenglong/dockerMounts/novel-download/logs:/app/logs \
-d novel-download:latest

docker run -p 30000:30000 --name novel-download \
-d biliw/novel-download:1.0.0
####
-v 将对应文件挂载到主机
-e 初始化对应
-p 容器端口映射到主机的端口
-d images名字 我们可以过 -d 指定容器的后台运行模式。
```




# 推送镜像

```bash
docker images
docker tag novel-download:latest ideaflow1/novel-download:1.0.1

docker push ideaflow1/novel-download:1.0.1
```

#使用

```bash
docker pull ideaflow1/novel-download:1.0.1



docker run -p 30000:30000 --name ideaflow1-novel-download \
-v /ideaflowNovelDownload/download:/app/download \
-v /ideaflowNovelDownload/db:/app/db \
-v /ideaflowNovelDownload/logs:/app/logs \
-d ideaflow1/novel-download:1.0.1

```

/ideaflowNovelDownload/download 请更改成 你的下载小说存储的路径
/ideaflowNovelDownload/db  请更改成 你的页面数据存储的路径
/ideaflowNovelDownload/logs  请更改成 你的程序运行日志存储的路径


MacOS的 Docker Desktop都支持buildx
```bash
docker buildx build --platform linux/amd64,linux/arm64 -t ideaflow1/novel-download:1.0.2  --push .
```

