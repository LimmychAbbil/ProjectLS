FROM jetty:11.0.15-jdk11
USER root

#Install maven
ARG MAVEN_VERSION=3.9.4
RUN mkdir /maven
RUN cd /maven && wget https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
RUN cd /maven && tar xzvf apache-maven-${MAVEN_VERSION}-bin.tar.gz && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

RUN cd /usr/bin && ln -s /maven/apache-maven-${MAVEN_VERSION}/bin/mvn mvn

RUN mkdir /lserver

COPY src /lserver/src
COPY pom.xml /lserver/pom.xml

RUN cd /lserver && mvn clean package -DskipTests
RUN cp /lserver/target/*.war /var/lib/jetty/webapps/root.war
