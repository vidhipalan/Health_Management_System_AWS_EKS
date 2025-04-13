package edu.stevens.cs548.clinic.service.dto;

public class TreatmentDtoFactory {
	
	public DrugTreatmentDto createDrugTreatmentDto() {
		return new DrugTreatmentDto();
	}
	
	/*
	 * TODO: Repeat for other treatments.
	 */
	public SurgeryTreatmentDto createSurgeryTreatmentDto() { return new SurgeryTreatmentDto();}
	public PhysiotherapyTreatmentDto createPhysiotherapyTreatmentDto() {
		return new PhysiotherapyTreatmentDto();
	}
	public RadiologyTreatmentDto createRadiologyTreatmentDto() {
		return new RadiologyTreatmentDto();
	}
}
