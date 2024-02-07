FROM sonarqube:9.9.1-community

USER root

RUN rm -rf /opt/sonarqube/lib/extensions/sonar-javascript-plugin-*

USER sonarqube

ARG sonar_plugin_name

COPY sonar-plugin/sonar-javascript-plugin/target/$sonar_plugin_name /opt/sonarqube/lib/extensions
