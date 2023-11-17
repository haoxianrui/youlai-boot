# 使用 openjdk 17-jdk-alpine 作为基础镜像
FROM openjdk:17-jdk-alpine

# 安装 tini，用于容器的进程初始化和管理
RUN apk --update --no-cache add tini

# 时区修改
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

# 修改 Alpine Linux 的软件包源为国内镜像源（这里使用了中国科技大学的镜像源）
RUN echo -e https://mirrors.ustc.edu.cn/alpine/v3.7/main/ > /etc/apk/repositories

# 安装 DejaVu 字体和 fontconfig，可用于支持中文等字体渲染
RUN apk --no-cache add ttf-dejavu fontconfig

# 在运行时自动挂载 /tmp 目录为匿名卷，提高可移植性
VOLUME /tmp

# 将构建的 Spring Boot 可执行 JAR 复制到容器中，重命名为 app.jar
ADD target/youlai-boot.jar app.jar

# 指定容器启动时执行的命令
CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /app.jar

# 暴露容器的端口
EXPOSE 8989
