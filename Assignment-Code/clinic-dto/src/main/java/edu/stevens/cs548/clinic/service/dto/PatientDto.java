package edu.stevens.cs548.clinic.service.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientDto {
	
	private UUID id;
		
	private String name;
	
	private LocalDate dob;
	
	private List<TreatmentDto> treatments;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public List<TreatmentDto> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<TreatmentDto> treatments) {
		this.treatments = treatments;
	}
	
	public void addTreatment(TreatmentDto treatment) {
		this.treatments.add(treatment);
	}
	
	public PatientDto() {
		this.treatments = new ArrayList<>();
	}
	
}
