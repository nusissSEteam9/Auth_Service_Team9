# 使用支持多架构的 Ubuntu 基础镜像
FROM ubuntu:latest

# 更新并安装必要的工具，包括 OpenJDK 21 和 findutils
RUN apt-get update && apt-get install -y findutils openjdk-21-jdk

# 获取架构并设置 JAVA_HOME
RUN arch=$(dpkg --print-architecture) && \
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-$arch && \
    export PATH="$JAVA_HOME/bin:$PATH"

# 设置工作目录
WORKDIR /app

# 复制项目的所有文件到容器中
COPY . .

# 授予 gradlew 脚本执行权限
RUN chmod +x ./gradlew

# 使用 Gradle Wrapper 构建项目，并禁用文件系统监视功能
RUN ./gradlew clean build --info --stacktrace -Dorg.gradle.vfs.watch=false -x test

# 暴露 8080 端口
EXPOSE 8080

# 运行应用程序
CMD ["java", "-jar", "build/libs/Auth_Service_Team9-0.0.1-SNAPSHOT.jar"]