package edu.stevens.cs548.clinic.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProviderDto {
	
	private UUID id;
	
	private String npi;
	
	private String name;
	
	private List<TreatmentDto> treatments;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public ProviderDto() {
		this.treatments = new ArrayList<>();
	}
	
}
