FROM ubuntu

USER root
RUN echo "alias cls='clear; pwd; ls'" >> /root/.bashrc

RUN yes | unminimize

RUN apt-get update && apt-get clean

RUN apt-get install -y openjdk-8-jdk && \
    apt-get install -y inetutils-ping && \
    apt-get install -y wget && \
    apt-get install -y man-db

WORKDIR /home/public
