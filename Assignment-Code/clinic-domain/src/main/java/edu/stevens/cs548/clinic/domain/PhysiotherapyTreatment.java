package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OrderBy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO
@Entity
public class PhysiotherapyTreatment extends Treatment {

	private static final long serialVersionUID = 5602950140629148756L;

	// Order by date
	@ElementCollection
	@OrderBy
	protected List<LocalDate> treatmentDates;

	public void addTreatmentDate(LocalDate date) {
		treatmentDates.add(date);
	}

	@Override
	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportPhysiotherapy(treatmentId,
				   patient.getPatientId(), 
				   patient.getName(),
				   provider.getProviderId(), 
				   provider.getName(),
				   diagnosis, 
				   treatmentDates,
				   () -> exportFollowupTreatments(visitor));	
	}
	
	public PhysiotherapyTreatment() {
		super();
		treatmentDates = new ArrayList<>();
	}

}
