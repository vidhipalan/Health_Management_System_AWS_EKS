package edu.stevens.cs548.clinic.rest;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("patient")
@RequestScoped
@Transactional
public class PatientResource extends ResourceBase {
	
	private static final Logger logger = Logger.getLogger(PatientResource.class.getCanonicalName());

	// TODO
	@Context
	private UriInfo uriInfo;
	
	// TODO

	@Inject
	private IPatientService patientService;
	
	@GET
	@Path("{id}")
	@Produces("application/json")
	/*
	 * Return a provider DTO including the list of treatments they are administering.
	 */
	public Response getPatient(@PathParam("id") String id) {
		try {
			UUID patientId = UUID.fromString(id);
			PatientDto patient = patientService.getPatient(patientId, true);
			ResponseBuilder responseBuilder = Response.ok(patient);
			/* 
			 * Add links for treatments in response headers.
			 */
			for (TreatmentDto treatment : patient.getTreatments()) {
				responseBuilder.link(getTreatmentUri(uriInfo, treatment.getProviderId(), treatment.getId()), TREATMENT);
			}
			return responseBuilder.build();
		} catch (PatientNotFoundExn e) {
			logger.info("Failed to find patient with id "+id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (PatientServiceExn e) {
			logger.log(Level.SEVERE, "Patient service request (getPatient) failed! ", e);
			return Response.status(Status.BAD_REQUEST).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed patient id: "+id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Consumes("application/json")
	public Response addPatient(PatientDto patientDto) {
		try {
			UUID id = patientService.addPatient(patientDto);
			URI patientUri = getPatientUri(uriInfo, id);
			return Response.created(patientUri).build();
		} catch (PatientServiceExn e) {
			logger.log(Level.SEVERE, "Patient service request (addPatient) failed! ", e);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
}
