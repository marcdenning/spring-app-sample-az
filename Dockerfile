FROM eclipse-temurin:17.0.6_10-jre
VOLUME /tmp
RUN addgroup --system app && adduser --system --no-create-home --ingroup app --disabled-password app
ADD --chown=app:app ./build/libs/ROOT.war /ROOT.war
USER app
RUN touch /ROOT.war
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/ROOT.war"]
