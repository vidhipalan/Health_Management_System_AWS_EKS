package edu.stevens.cs548.clinic.domain;

import java.util.Collection;
import java.util.UUID;

public interface IPatientDao {
	
	public static class PatientExn extends Exception {
		private static final long serialVersionUID = 1L;
		public PatientExn (String msg) {
			super(msg);
		}
	}

	public void addPatient (Patient pat) throws PatientExn;
		
	/*
	 * By default, treatments are loaded only lazily
	 */
	public Patient getPatient (UUID id) throws PatientExn;
	
	public Patient getPatient (UUID id, boolean includeTreatments) throws PatientExn;

	public Collection<Patient> getPatients();
	
	public void deletePatients();
	
}
