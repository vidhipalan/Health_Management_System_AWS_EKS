package edu.stevens.cs548.clinic.micro.domain.stub;

import edu.stevens.cs548.clinic.micro.domain.IPatientMicroService;
import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;

// TODO
@RequestScoped
public class PatientService implements IPatientService {
	
	private Logger logger = Logger.getLogger(PatientService.class.getCanonicalName());
	
	private static final String LOCATION = "Location";
	
	// TODO
	@Inject
	@RestClient
	IPatientMicroService patientMicroService;

	@Override
	public UUID addPatient(PatientDto dto) throws PatientServiceExn {
		logger.info(String.format("addPatient: Adding patient %s in microservice client!", dto.getName()));
		try {
			Response response = patientMicroService.addPatient(dto);
			if (response.getStatus() >= 300) {
				Exception e = new WebApplicationException(response);
				throw new PatientServiceExn("Failed to add patient "+dto.getId(), e);
			}
			String location = response.getHeaderString(LOCATION);
			if (location == null) {
				throw new IllegalStateException("Missing location response header!");
			}
			String[] uriSegments = URI.create(location).getPath().split("/");
			return UUID.fromString(uriSegments[uriSegments.length-1]);
		} catch (WebApplicationException e) {
			throw new PatientServiceExn("Web service failure.", e);
		}
	}

	@Override
	public List<PatientDto> getPatients() throws PatientServiceExn {
		logger.info(String.format("getPatients: Getting all patients in microservice client!"));
		try {
			return patientMicroService.getPatients();
		} catch (WebApplicationException e) {
			throw new PatientServiceExn("Web service failure.", e);
		}
	}

	@Override
	public PatientDto getPatient(UUID id, boolean includeTreatments) throws PatientServiceExn {
		logger.info(String.format("getPatient: Getting patient %s in microservice client!", id.toString()));
		try {
			return patientMicroService.getPatient(id.toString(), Boolean.toString(includeTreatments));
		} catch (WebApplicationException e) {
			throw new PatientServiceExn("Web service failure.", e);
		}
	}

	@Override
	public PatientDto getPatient(UUID id) throws PatientServiceExn {
		return getPatient(id, true);
	}

	@Override
	public TreatmentDto getTreatment(UUID patientId, UUID treatmentId)
			throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn {
		logger.info(String.format("getTreatment: Getting treatment %s in microservice client!", treatmentId.toString()));
		try {
			return patientMicroService.getTreatment(patientId.toString(), treatmentId.toString());
		} catch (WebApplicationException e) {
			throw new PatientServiceExn("Web service failure.", e);
		}
	}

	@Override
	public void removeAll() throws PatientServiceExn {
		logger.info(String.format("deletePatients: Deleting all patients in microservice client!"));
		try {
			patientMicroService.removeAll();
		} catch (WebApplicationException e) {
			throw new PatientServiceExn("Web service failure.", e);
		}
	}

}
