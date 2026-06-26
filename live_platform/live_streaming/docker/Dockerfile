FROM base_graal_jdk
# 这里定义的是容器内部的工作空间
ENV workdir=/home/
RUN yum install -y  telnet


# WORKDIR 指定工作目录;也指定了进入容器时的目录
WORKDIR $workdir

# 将Dockerfile所在目录的文件拷贝到docker容器内的工作空间里
COPY *.jar $workdir

# 默认暴露端口，可docker run -p 指定
#EXPOSE 8079
#以上是 build 要执行的
# docker run 会执行这里，
#CMD ["java", "--enable-preview", "-jar", "live_account-0.0.1-SNAPSHOT.jar"]
