需要外网环境
```bash
mvn clean package -Dmaven.test.skip=true


docker build -t novel-download -f Dockerfile .

docker images

```
```bash
# --name指定容器名字 -v目录挂载 -p指定端口映射  -e设置mysql参数 -d后台运行
docker run -p 30005:30000 --name novel-download \
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


docker images

# 推送镜像

```bash
docker tag novel-download:latest ideaflow1/novel-download:1.0.0

docker push ideaflow1/novel-download:1.0.0
```

#使用



```bash
docker pull ideaflow1/novel-download:1.0.0



docker run -p 30000:30000 --name ideaflow1-novel-download \
-v /Users/download:/app/download \
-v /Users/novel-download/db:/app/db \
-d ideaflow1/novel-download:1.0.0

```

/Users/download 请更改成 你的下载小说存储的路径
/Users/novel-download/db  请更改成 你的页面数据存储的路径