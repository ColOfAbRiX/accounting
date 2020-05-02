FROM       openjdk:11.0-jre
COPY       / /
RUN        [ "chmod", "+x", "/entrypoint.sh" ]
EXPOSE     8008
ENTRYPOINT [ "/entrypoint.sh" ]
