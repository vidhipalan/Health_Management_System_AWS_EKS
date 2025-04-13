package edu.stevens.cs548.clinic.service.dto;

import java.time.LocalDate;

import com.google.gson.annotations.SerializedName;

public class DrugTreatmentDto extends TreatmentDto {
	
	private String drug;
	
	private float dosage;
	
	@SerializedName("start-date")
	private LocalDate startDate;
	
	@SerializedName("end-date")
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

	
}
