package edu.stevens.cs548.clinic.rest;

import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;

public abstract class ResourceBase {
	
	/*
	 * Relationships for links in representations
	 */
	protected static final String RELATIONSHIP_BASE = "http://cs548.stevens.edu/clinic/relationship/";
	
	public static final String PATIENT = RELATIONSHIP_BASE + "patient";
	
	public static final String PROVIDER = RELATIONSHIP_BASE + "provider";
			
	public static final String TREATMENT = RELATIONSHIP_BASE + "treatment";
	
	/*
	 * Helpful functions shared by resource classes.
	 */
	protected URI getPatientUri(UriInfo uriInfo, UUID id) {
		return uriInfo.getBaseUriBuilder().path("patient").path("{id}").build(id);
	}
	
	protected URI getProviderUri(UriInfo uriInfo, UUID id) {
		return uriInfo.getBaseUriBuilder().path("provider").path("{id}").build(id);
	}
	
	protected URI getTreatmentUri(UriInfo uriInfo, UUID id, UUID tid) {
		return uriInfo.getBaseUriBuilder().path("provider").path("{id}").path("treatment").path("{tid}").build(id, tid);
	}
	
}
