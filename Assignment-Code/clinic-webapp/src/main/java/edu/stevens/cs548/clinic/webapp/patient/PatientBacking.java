package edu.stevens.cs548.clinic.webapp.patient;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.webapp.BaseBacking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("patientBacking")
@RequestScoped
public class PatientBacking extends BaseBacking {

	private static final long serialVersionUID = -6498472821445783075L;

	private static Logger logger = Logger.getLogger(PatientBacking.class.getCanonicalName());

	/**
	 * The value of this property is provided as a query string parameter and
	 * set by a metadata annotation in the page.
	 */
	private String id;
	
	private PatientDto patient;
		
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		if (patient == null) {
			return null;
		}
		return patient.getName();
	}

	public LocalDate getDob() {
		if (patient == null) {
			return null;
		}
		return patient.getDob();
	}

	public List<TreatmentDto> getTreatments() {
		if (patient == null) {
			return new ArrayList<>();
		}
		return patient.getTreatments();
	}

	// TODO
	@Inject
	private IPatientService patientService;
	
	/**
	 * Triggered by receipt of a parameter value identifying the patient.
	 */
	public void load() {
		try {
			if (id == null) {
				throw new IllegalArgumentException("No value specified for patient id!");
			}
			patient = patientService.getPatient(UUID.fromString(id), true);
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Invalid id specified for patient: "+id, e);
			addMessage(MESSAGE_PATIENT_ID_INVALID);
		} catch (PatientServiceExn e) {
			logger.log(Level.SEVERE, "Failed to load patient record with id: "+id, e);
			addMessage(MESSAGE_PATIENT_ID_INVALID);
		}
	}

}
