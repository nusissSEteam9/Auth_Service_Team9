# 构建阶段
FROM ubuntu:latest AS builder

# 更新并安装必要工具
RUN apt-get update && apt-get install -y --no-install-recommends openjdk-17-jdk findutils && \
    rm -rf /var/lib/apt/lists/*  # 清理缓存减少镜像大小

# 设置 JAVA_HOME 环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

# 设置工作目录
WORKDIR /app

# 复制项目文件到构建镜像
COPY . .

# 授予 gradlew 脚本执行权限
RUN chmod +x ./gradlew

# 使用 Gradle Wrapper 构建项目，并禁用文件系统监视功能
RUN ./gradlew clean build --no-daemon -Dorg.gradle.vfs.watch=false -x test

# 使用更小的运行时基础镜像
FROM openjdk:17-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到运行时镜像
COPY application-jar/*.jar /app/app.jar

# 暴露 8080 端口
EXPOSE 8080

# 运行应用程序
CMD ["java", "-jar", "/app/app.jar"]