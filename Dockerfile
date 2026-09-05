# ---- build stage: produce the WAR, fully offline ----
# Dependencies are vendored in .m2repo, so Maven runs with -o (offline) and never
# contacts Maven Central. Requires only the two base images to be present locally.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY .m2repo /m2repo
COPY pom.xml .
COPY src ./src
RUN mvn -B -o -Dmaven.repo.local=/m2repo clean package

# ---- runtime stage: Tomcat 10.1 (Jakarta EE 10 namespace) ----
FROM tomcat:10.1-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/suggestionbox.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
