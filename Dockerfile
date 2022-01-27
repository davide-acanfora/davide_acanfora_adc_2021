FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/davide-acanfora/davide_acanfora_adc_2021.git

FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY --from=0 /app/davide_acanfora_adc_2021 /app
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
ENV ID=0
ENV MASTER=127.0.0.1
COPY --from=1 /app/target/sudoku-1.0-jar-with-dependencies.jar /app

CMD /usr/bin/java -jar sudoku-1.0-jar-with-dependencies.jar -i $ID -m $MASTER