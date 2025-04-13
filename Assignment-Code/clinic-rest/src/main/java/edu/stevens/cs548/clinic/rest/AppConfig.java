package edu.stevens.cs548.clinic.rest;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.gson.JsonGsonFeature;
import org.glassfish.jersey.server.ResourceConfig;


@ApplicationPath("/")

// TODO define database connection
@DataSourceDefinition( name="java:global/jdbc/cs548",
		className="org.postgresql.ds.PGSimpleDataSource",
		user="cs548user",
		password="dbuserpw",
		databaseName="cs548",
		serverName="cs548db.c30gwcuam2rb.us-east-2.rds.amazonaws.com", portNumber=5432)
public class AppConfig extends ResourceConfig {
	/*
	 * ResourceConfig is a subclass of Application defined by the Jersey framework
	 * that supports the ability to register extensions as "features".  We add the
	 * JsonGsonFeature feature (see the jersey-media-json-gson dependency in the POM file).
	 */
	public AppConfig() {
		packages("edu.stevens.cs548.clinic.rest").register(JsonGsonFeature.class);
	}

}
