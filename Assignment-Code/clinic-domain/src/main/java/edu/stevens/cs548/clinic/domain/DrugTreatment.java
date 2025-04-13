package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.Entity;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity implementation class for Entity: DrugTreatment
 * 
 */
// TODO JPA annotations
	@Entity
public class DrugTreatment extends Treatment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String drug;
	
	private float dosage;

	private LocalDate startDate;

	private LocalDate endDate;
	
	private int frequency;

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public float getDosage() {
		return dosage;
	}

	public void setDosage(float dosage) {
		this.dosage = dosage;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	

	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportDrugTreatment(treatmentId,
										   patient.getPatientId(),
										   patient.getName(),
										   provider.getProviderId(),
										   provider.getName(),
								   		   diagnosis,
								   		   drug, 
								   		   dosage,
								   		   startDate,
								   		   endDate,
								   		   frequency,
								   		   () -> exportFollowupTreatments(visitor));
	}

	public DrugTreatment() {
		super();
	}

}
