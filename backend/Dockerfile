FROM openjdk:17

RUN mkdir /api
COPY . /api
RUN mkdir -p /api/target/logs/

# Java Environment path sets
ENV PATH=$PATH:/api/target/
ENV JAVA_OPTS="-XX:+UseNUMA -XX:+UseCondCardMark -XX:-UseBiasedLocking -Xms128M -Xmx400M -Xss1M -XX:+UseParallelGC -XX:MaxGCPauseMillis=100 -XX:GCTimeRatio=19"
EXPOSE 8080

# Set execute permission for binary folders
RUN chmod +x /api/target/idw-signage-backend-0.0.1-SNAPSHOT.jar
#RUN chmod +x /api/target/logs/

# Health Check
HEALTHCHECK --interval=10s --timeout=10s --retries=3 CMD curl -f http://localhost:8080/saml || exit 1

# Default Command
CMD ["java","-jar","-Dspring.profiles.active=uat","./api/target/idw-signage-backend-0.0.1-SNAPSHOT.jar", "-Dhttp.address=0.0.0.0", "-Dhttp.port=8080"]
