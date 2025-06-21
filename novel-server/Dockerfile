# 使用 OpenJDK 17 作为基础镜像
#FROM openjdk:17-jdk-slim
#FROM eclipse-temurin:17-jdk-alpine
## AdoptOpenJDK 停止发布 OpenJDK 二进制，而 Eclipse Temurin 是它的延伸，提供更好的稳定性

FROM eclipse-temurin:21-jre

# 设置工作目录
WORKDIR /app

# 复制 jar 包到容器中
ADD ./target/novel-server.jar /app/novel-server.jar

# 暴露端口（确保与你的应用程序端口一致）
EXPOSE 30000

# 启动命令
CMD ["java", "-jar", "/app/novel-server.jar"]