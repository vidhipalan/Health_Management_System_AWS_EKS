package edu.stevens.cs548.clinic.service.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public abstract class TreatmentDto {
	
	private UUID id;
	
	@SerializedName("patient-id")
	private UUID patientId;
	
	/* 
	 * Use this to display patient name in list of treatments without N+1 problem.
	 */
	@SerializedName("patient-name")
	private String patientName;
	
	@SerializedName("provider-id")
	private UUID providerId;
	
	/* 
	 * Use this to display provider name in list of treatments without N+1 problem.
	 */
	@SerializedName("provider-name")
	private String providerName;
	
	private String diagnosis;
	
	@SerializedName("followup-treatments")
	private Collection<TreatmentDto> followupTreatments;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public Collection<TreatmentDto> getFollowupTreatments() {
		return followupTreatments;
	}
	
	public void setFollowupTreatments(Collection<TreatmentDto> treatments) {
		this.followupTreatments = treatments;
	}
	
	public TreatmentDto() {
		this.followupTreatments = new ArrayList<>();
	}

}
