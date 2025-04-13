package edu.stevens.cs548.clinic.domain;


public interface ITreatmentFactory {
	
	public DrugTreatment createDrugTreatment ();
	public RadiologyTreatment createRadiologyTreatment ();

	public SurgeryTreatment createSurgeryTreatment();

	public PhysiotherapyTreatment createPhysiotherapyTreatment();
	
	/*
	 * TODO add methods for Radiology, Surgery, Physiotherapy
	 */

	
}
