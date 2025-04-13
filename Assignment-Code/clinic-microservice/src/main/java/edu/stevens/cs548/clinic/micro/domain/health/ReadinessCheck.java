package edu.stevens.cs548.clinic.micro.domain.health;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

// TODO
@ApplicationScoped
@Readiness
public class ReadinessCheck implements HealthCheck {
	
	private static final Logger logger = Logger.getLogger(ReadinessCheck.class.getCanonicalName());
	
	private static final String DATABASE_HOST_PROPERTY = "DATABASE_HOST";
	
	private static final int DATABASE_PORT = 5432;
	
	private static final String READINESS_CHECK_NAME = "Database Readiness Check";
	
	private static final String ERROR_KEY = "error";

	@Override
	public HealthCheckResponse call() {
		HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named(READINESS_CHECK_NAME);
		try {
			
			pingServer();
			
			logger.info("Readiness check for database succeeded!");
			return responseBuilder.up().build();
			
		} catch (IOException e) {
			
			return responseBuilder.down().withData(ERROR_KEY, e.getMessage()).build();
			
		}
	}
	
	private void pingServer() throws UnknownHostException, IOException {
		String host = System.getenv(DATABASE_HOST_PROPERTY);
		Socket socket = new Socket(host,DATABASE_PORT);
		socket.close();
	}

}
