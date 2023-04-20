# SpringBoot单体应用部署Dockerfile
FROM openjdk:17-jdk-alpine

RUN apk --update --no-cache add tini
ENTRYPOINT ["tini"]

# /tmp 目录就会在运行时自动挂载为匿名卷，任何向 /tmp 中写入的信息都不会记录进容器存储层
VOLUME /tmp

ADD target/youlai-boot.jar app.jar

CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /app.jar

EXPOSE 8989
# 时区修改
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone

RUN echo -e https://mirrors.ustc.edu.cn/alpine/v3.7/main/ > /etc/apk/repositories

RUN apk --no-cache add ttf-dejavu fontconfig