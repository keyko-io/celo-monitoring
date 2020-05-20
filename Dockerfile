FROM maven:3.6.3-jdk-11

ARG NODE_VERSION=11
ARG NEWMAN_VERSION=4.6.0

RUN if [ ! $(echo $NEWMAN_VERSION | grep -oE "^[0-9]+\.[0-9]+\.[0-9]+$") ]; then \
        echo "\033[0;31mA valid semver Newman version is required in the NEWMAN_VERSION build-arg\033[0m"; \
        exit 1; \
    fi

ADD https://deb.nodesource.com/setup_$NODE_VERSION.x /opt/install_node.sh

RUN apt-get update -y && \
    apt-get upgrade -y && \
    apt-get install -y gettext-base && \
    apt-get install -y gnupg && \
    bash /opt/install_node.sh && \
    apt-get install -y nodejs && \
    npm install -g newman@${NEWMAN_VERSION} && \
    rm /opt/install_node.sh && \
    apt-get purge -y gnupg;

ENV LC_ALL="en_US.UTF-8" LANG="en_US.UTF-8" LANGUAGE="en_US.UTF-8"


WORKDIR /

COPY pom.xml /
COPY bin /celo-bin
COPY config /config
COPY docs /docs
COPY src /src

RUN mvn package

RUN mv target/celo-monitoring-engine.jar celo-monitoring-engine.jar

CMD /bin/bash -x celo-bin/run-monitoring.bash config && java -jar celo-monitoring-engine.jar
