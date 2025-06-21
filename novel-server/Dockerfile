

FROM eclipse-temurin:21-jre

# 设置工作目录
WORKDIR /app

# 复制 jar 包到容器中
ADD ./target/novel-server.jar /app/novel-server.jar

# 暴露端口（确保与你的应用程序端口一致）
EXPOSE 30000

# 启动命令
CMD ["java", "-jar", "/app/novel-server.jar"]
