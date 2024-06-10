FROM docker.io/gitpod/workspace-java-21

COPY install-az-cli.sh install-az-cli.sh

RUN sudo chmod +x ./install-az-cli.sh && ./install-az-cli.sh
