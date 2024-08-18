FROM gradle:jdk17

WORKDIR /app

COPY . /app

RUN gradle build

CMD ["java", "-jar", "/app/build/libs/dota-stats-0.0.1-SNAPSHOT.jar"]