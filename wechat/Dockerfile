FROM openjdk:8-jre
LABEL maintainer="wechat.zj"

ARG jarName=wechat.jar
ARG jarSrcPath=target/wechat.jar
ARG workdic=/home/zj/demo/wechat-server
ARG volumeName=logs/wechat-server
ARG port=8185
ARG profile=dev
ENV jarName=$jarName
ENV port=$port
ENV profile=$profile

RUN mkdir -p $workdic
WORKDIR $workdic
COPY $jarSrcPath $jarName
RUN bash -c "touch $jarName"
VOLUME ["$workdic/$volumeName"]
EXPOSE $port
CMD ["sh", "-c", "java -Dfile.encoding=utf-8 -jar $jarName --server.port=$port --spring.profiles.active=$profile"]
# build && run
# docker build -t ccteg/biz_job:0.0.1 .
# docker run -di -p 8077:8077 -e profile=dev --name biz_job_0.0.1 ccteg/biz_job:0.0.1
# docker logs biz_job_0.0.1