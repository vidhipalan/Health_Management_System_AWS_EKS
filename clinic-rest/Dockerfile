FROM payara/micro:6.2025.1-jdk21
COPY --chown=payara:payara clinic-rest.war ${DEPLOY_DIR}
CMD ["--contextroot", "api", "--deploy", "/opt/payara/deployments/clinic-rest.war"]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
