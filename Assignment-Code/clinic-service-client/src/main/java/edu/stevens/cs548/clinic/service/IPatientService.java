package edu.stevens.cs548.clinic.service;

import java.util.List;
import java.util.UUID;

import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

public interface IPatientService {
	
	public class PatientServiceExn extends Exception {
		private static final long serialVersionUID = 1L;
		public PatientServiceExn (String m, Exception e) {
			super(m, e);
		}
	}
	public class PatientNotFoundExn extends PatientServiceExn {
		private static final long serialVersionUID = 1L;
		public PatientNotFoundExn (String m, Exception e) {
			super(m, e);
		}
	}
	public class TreatmentNotFoundExn extends PatientServiceExn {
		private static final long serialVersionUID = 1L;
		public TreatmentNotFoundExn (String m, Exception e) {
			super(m, e);
		}
	}
	
	public UUID addPatient(PatientDto dto) throws PatientServiceExn;

	public List<PatientDto> getPatients() throws PatientServiceExn;
	
	public PatientDto getPatient(UUID id, boolean includeTreatments) throws PatientServiceExn;
	
	public PatientDto getPatient(UUID id) throws PatientServiceExn;
	
	public TreatmentDto getTreatment(UUID patientId, UUID treatmentId) throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn;

	public void removeAll() throws PatientServiceExn;
	
}
