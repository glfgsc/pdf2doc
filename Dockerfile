#基础镜像
FROM anapsix/alpine-java:8_server-jre_unlimited

#时区配置
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

#环境配置
ENV SERVER_PORT=8081
ENV MYPATH /usr/local

WORKDIR $MYPATH

ADD ./coin-cat-manager-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-jar", "app.jar"]