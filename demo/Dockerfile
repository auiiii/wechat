FROM hub.iot.chinamobile.com/runtime/jre:8-fonts
RUN apk add -U tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata
COPY ./shz-npn/target/shz-npn-1.0-SNAPSHOT.jar /data/www/java/work/webapps/ROOT.jar
WORKDIR /data/www/java/work/webapps/
CMD [ "java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Dserver.port=8080", "-Dspring.config.location=/data/app/application.yml", "-Dlogging.config=/data/app/logback-cicd.xml", "-Denv=FAT", "-jar", "ROOT.jar","-Dspring.application.name=shz-npn"]
