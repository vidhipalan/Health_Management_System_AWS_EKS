package edu.stevens.cs548.clinic.micro.domain.rest;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.gson.JsonGsonFeature;
import org.glassfish.jersey.server.ResourceConfig;


// TODO
@ApplicationPath("/")
@DataSourceDefinition( name="java:global/jdbc/cs548",
		className="org.postgresql.ds.PGSimpleDataSource",
		user="cs548user",
		password="dbuserpw",
		databaseName="cs548",
		serverName="cs548db.c30gwcuam2rb.us-east-2.rds.amazonaws.com", portNumber=5432)
public class AppConfig extends ResourceConfig {

	public AppConfig() {
		packages("edu.stevens.cs548.clinic.micro.domain.rest").register(JsonGsonFeature.class);
	}

}
