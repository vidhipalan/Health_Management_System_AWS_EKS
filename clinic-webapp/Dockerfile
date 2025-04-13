FROM payara/micro:6.2025.1-jdk21
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD ["--contextroot", "clinic", "--deploy", "/opt/payara/deployments/clinic-webapp.war"]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"
