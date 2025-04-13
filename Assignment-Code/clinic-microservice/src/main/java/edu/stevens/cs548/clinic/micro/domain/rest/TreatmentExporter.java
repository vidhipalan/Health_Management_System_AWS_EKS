package edu.stevens.cs548.clinic.micro.domain.rest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import edu.stevens.cs548.clinic.domain.ITreatmentExporter;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;

public class TreatmentExporter implements ITreatmentExporter<TreatmentDto> {
	
	/*
	 * Whether to process follow-up treatments.
	 */
	private boolean includeFollowups;

	private TreatmentDtoFactory factory = new TreatmentDtoFactory();

	private TreatmentExporter(boolean includeFollowups) {
		this.includeFollowups = includeFollowups;
	}

	public static TreatmentExporter exportWithFollowups() {
		return new TreatmentExporter(true);
	}

	public static TreatmentExporter exportWithoutFollowups() {
		return new TreatmentExporter(false);
	}

	@Override
	public TreatmentDto exportDrugTreatment(UUID tid, UUID patientId, String patientName, UUID providerId,
			String providerName, String diagnosis, String drug, float dosage, LocalDate start, LocalDate end,
			int frequency, Supplier<Collection<TreatmentDto>> followups) {
		
		DrugTreatmentDto dto = factory.createDrugTreatmentDto();

		dto.setId(tid);
		dto.setPatientId(patientId);
		dto.setPatientName(patientName);
		dto.setProviderId(providerId);
		dto.setProviderName(providerName);
		dto.setDiagnosis(diagnosis);
		dto.setDrug(drug);
		dto.setDosage(dosage);
		dto.setStartDate(start);
		dto.setEndDate(end);
		dto.setFrequency(frequency);
		
		if (includeFollowups) {
			dto.setFollowupTreatments(followups.get());
		}

		return dto;
	}

	@Override
	public TreatmentDto exportRadiology(UUID tid, UUID patientId, String patientName, UUID providerId,
			String providerName, String diagnosis, List<LocalDate> dates, Supplier<Collection<TreatmentDto>> followups) {

		RadiologyTreatmentDto dto = factory.createRadiologyTreatmentDto();

		dto.setId(tid);
		dto.setPatientId(patientId);
		dto.setPatientName(patientName);
		dto.setProviderId(providerId);
		dto.setProviderName(providerName);
		dto.setDiagnosis(diagnosis);
		dto.setTreatmentDates(dates);
		
		if (includeFollowups) {
			dto.setFollowupTreatments(followups.get());
		}
		
		return dto;
	}

	@Override
	public TreatmentDto exportSurgery(UUID tid, UUID patientId, String patientName, UUID providerId,
			String providerName, String diagnosis, LocalDate date, String dischargeInstructions, 
			Supplier<Collection<TreatmentDto>> followups) {
		SurgeryTreatmentDto dto = factory.createSurgeryTreatmentDto();
		dto.setId(tid);
		dto.setPatientId(patientId);
		dto.setPatientName(patientName);
		dto.setProviderId(providerId);
		dto.setProviderName(providerName);
		dto.setDiagnosis(diagnosis);
		dto.setSurgeryDate(date);
		dto.setDischargeInstructions(dischargeInstructions);

		if (includeFollowups) {
			dto.setFollowupTreatments(followups.get());
		}
		return dto;
	}

	@Override
	public TreatmentDto exportPhysiotherapy(UUID tid, UUID patientId, String patientName, UUID providerId,
			String providerName, String diagnosis, List<LocalDate> dates, Supplier<Collection<TreatmentDto>> followups) {
		PhysiotherapyTreatmentDto dto = factory.createPhysiotherapyTreatmentDto();

		dto.setId(tid);
		dto.setPatientId(patientId);
		dto.setPatientName(patientName);
		dto.setProviderId(providerId);
		dto.setProviderName(providerName);
		dto.setDiagnosis(diagnosis);
		dto.setTreatmentDates(dates);

		if(includeFollowups) {
			dto.setFollowupTreatments(followups.get());
		}

		return dto;	}

}