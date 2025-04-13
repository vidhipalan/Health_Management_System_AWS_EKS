package edu.stevens.cs548.clinic.micro.domain.rest;

import edu.stevens.cs548.clinic.domain.IPatientDao;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IPatientFactory;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientFactory;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST version of Patient Service
 */
// TODO
@RequestScoped
@Transactional
@Path("patient")
public class PatientMicroService {
	
	private Logger logger = Logger.getLogger(PatientMicroService.class.getCanonicalName());	

	// TODO
	@Context
	private UriInfo uriInfo;
	
	// TODO
	@Inject
	private IPatientDao patientDao;
	
	private IPatientFactory patientFactory;
	
	private PatientDtoFactory patientDtoFactory;

	public PatientMicroService() {
		// Initialize factories
		patientFactory = new PatientFactory();
		patientDtoFactory = new PatientDtoFactory();
	}
	
	// TODO
	@POST
	@Consumes("application/json")
	public Response addPatient(PatientDto dto) {
		try {
			logger.info(String.format("addPatient: Adding patient %s in microservice!", dto.getName()));
			Patient patient = patientFactory.createPatient();
			if (dto.getId() == null) {
				patient.setPatientId(UUID.randomUUID());
			} else {
				patient.setPatientId(dto.getId());
			}
			patient.setName(dto.getName());
			patient.setDob(dto.getDob());
			patientDao.addPatient(patient);
			UUID id = patient.getPatientId();
			URI uri = uriInfo.getBaseUriBuilder().path(id.toString()).build();
			return Response.created(uri).build();
		} catch (PatientExn e) {
			logger.log(Level.SEVERE, "Failed to add patient!", e);
			return Response.serverError().build();
		}
	}

	// TODO
	@GET
	@Produces("application/json")
	public List<PatientDto> getPatients() {
		try {
			logger.info(String.format("getPatients: Getting all patients in microservice!"));
			Collection<Patient> patients = patientDao.getPatients();
			List<PatientDto> dtos = new ArrayList<>();
			for (Patient p : patients) {
				dtos.add(patientToDto(p, false));
			}
			return dtos;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to get patients!", e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	private PatientDto patientToDto(Patient patient, boolean includeTreatments) throws TreatmentExn {
		PatientDto dto = patientDtoFactory.createPatientDto();
		dto.setId(patient.getPatientId());
		dto.setName(patient.getName());
		dto.setDob(patient.getDob());
		if (includeTreatments) {
			dto.setTreatments(patient.exportTreatments(TreatmentExporter.exportWithoutFollowups()));
		}
		return dto;
	}
	
	// TODO
	@GET
	@Path("{id}")
	@Produces("application/json")
	public PatientDto getPatient(@PathParam("id") String id, @QueryParam("treatments") @DefaultValue("true") String treatments) {
		try {
			logger.info(String.format("getPatient: Getting patient %s in microservice!", id));
			UUID patientId = UUID.fromString(id);
			boolean includeTreatments = Boolean.parseBoolean(treatments);

			// TODO use DAO to get patient by external key, create DTO that includes treatments
			Patient patient = patientDao.getPatient(patientId);
			return patientToDto(patient, includeTreatments);
			
		} catch (PatientExn e) {
			logger.info("Failed to find patient with id "+id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed to get patient %s!", id), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO
	@GET
	@Path("{id}/treatment/{tid}")
	@Produces("application/json")
	public TreatmentDto getTreatment(@PathParam("id") String id, @PathParam("tid") String tid) {
		// Export treatment DTO from patient aggregate
		try {
			logger.info(String.format("getTreatment: Getting treatment %s in microservice!", tid));
			UUID patientId = UUID.fromString(id);
			UUID treatmentId = UUID.fromString(tid);
			Patient patient = patientDao.getPatient(patientId);
			return patient.exportTreatment(treatmentId, TreatmentExporter.exportWithFollowups());
		} catch (PatientExn e) {
			logger.info("Failed to find patient with id "+id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (TreatmentExn e) {
			logger.info(String.format("Failed to find treatment %s for patient %s", tid, id));
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed to get treatment %s!", tid), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	// TODO
	@DELETE
	public void removeAll() {
		logger.info(String.format("deletePatients: Deleting all patients in microservice!"));
		patientDao.deletePatients();
	}

}
