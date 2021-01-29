FROM mcr.microsoft.com/java/jre:11u9-zulu-alpine
VOLUME /tmp
RUN addgroup -S app && adduser -S -H -D app -G app
ADD --chown=app:app ./build/libs/ROOT.war /ROOT.war
USER app
RUN touch /ROOT.war
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/ROOT.war"]
