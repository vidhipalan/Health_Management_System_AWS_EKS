package edu.stevens.cs548.clinic.domain;

public interface IPatientFactory {
	
	public Patient createPatient () throws IPatientDao.PatientExn;

}
