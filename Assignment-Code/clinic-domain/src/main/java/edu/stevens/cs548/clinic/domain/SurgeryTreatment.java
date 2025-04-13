package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class SurgeryTreatment extends Treatment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4173146640306267418L;
	
	private LocalDate surgeryDate;
	
	private String dischargeInstructions;
	
	public LocalDate getSurgeryDate() {
		return surgeryDate;
	}

	public void setSurgeryDate(LocalDate surgeryDate) {
		this.surgeryDate = surgeryDate;
	}

	public String getDischargeInstructions() {
		return dischargeInstructions;
	}

	public void setDischargeInstructions(String dischargeInstructions) {
		this.dischargeInstructions = dischargeInstructions;
	}
	
	@Override
	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportSurgery(
				treatmentId,
				patient.getPatientId(),
				patient.getName(),
				provider.getProviderId(),
				provider.getName(),
				diagnosis,
				surgeryDate,
				dischargeInstructions,
				() -> exportFollowupTreatments(visitor));

	}
	
	public SurgeryTreatment() {
		super();
	}

}
