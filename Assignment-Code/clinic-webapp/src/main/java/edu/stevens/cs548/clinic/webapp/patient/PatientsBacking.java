package edu.stevens.cs548.clinic.webapp.patient;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.webapp.BaseBacking;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named("patientsBacking")
@RequestScoped
public class PatientsBacking extends BaseBacking {

	private static final long serialVersionUID = -733113325524128462L;
	
	// TODO
	@Inject
	IPatientService patientService;

	/*
	 * The list of patients, from which the cursor is selected.
	 */
	private List<PatientDto> patients = new ArrayList<>();

	public List<PatientDto> getPatients() {
		return patients;
	}

	// TODO
	@PostConstruct
	private void load() {
		try {
			patients = patientService.getPatients();
		} catch (PatientServiceExn e) {
			throw new IllegalStateException("Failed to get list of patients.", e);
		}
	}


}
