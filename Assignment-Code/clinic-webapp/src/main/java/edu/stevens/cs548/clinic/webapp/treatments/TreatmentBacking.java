package edu.stevens.cs548.clinic.webapp.treatments;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.webapp.BaseBacking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("treatmentBacking")
@RequestScoped
public class TreatmentBacking extends BaseBacking {

	protected static final long serialVersionUID = -6498472821445783075L;

	protected static Logger logger = Logger.getLogger(TreatmentBacking.class.getCanonicalName());
		
	protected String type;
	
	protected boolean drugTreatment = false;
	
	protected boolean surgeryTreatment = false;
	
	protected boolean radiologyTreatment = false;
	
	protected boolean physiotherapyTreatment = false;

	protected UUID id;
		
	protected UUID patientId;
	
	protected String patientName;
	
	protected UUID providerId;
	
	protected String providerName;
	
	protected String diagnosis;
	
	protected Collection<TreatmentDto> followupTreatments;
		
	/*
	 * Drug treatment
	 */
	protected String drug;
	
	protected float dosage;
	
	protected LocalDate startDate;
	
	protected LocalDate endDate;
	
	protected int frequency;
	
	/*
	 * Surgery
	 */
	protected LocalDate surgeryDate;
	
	protected String dischargeInstructions;
	
	/*
	 * Radiology or Physiotherapy
	 */
	protected List<LocalDate> treatmentDates;


	/**
	 * These property are provided as query string parameters and
	 * set by a metadata annotation in the page.
	 */
	public void setId(String id) {
		if (id != null) {
			this.id = UUID.fromString(id);
		}
	}
	
	public void setPatientId(String patientId) {
		if (patientId != null) {
			this.patientId = UUID.fromString(patientId);
		}
	}

	public void setProviderId(String providerId) {
		if (providerId != null) {
			this.providerId = UUID.fromString(providerId);
		}
	}


	/*
	 * The type of the treatment determines what is rendered on the web page.
	 */
	public boolean isDrugTreatment() {
		return drugTreatment;
	}
	
	public boolean isSurgeryTreatment() {
		return surgeryTreatment;
	}
	
	public boolean isRadiologyTreatment() {
		return radiologyTreatment;
	}
	
	public boolean isPhysiotherapyTreatment() {
		return physiotherapyTreatment;
	}
	
	public boolean hasFollowupTreatments() {
		return followupTreatments != null && !followupTreatments.isEmpty();
	}
	
	public String getType() {
		return type;
	}
	
	/*
	 * Properties for the backing store
	 */
	
	public String getId() {
		return id == null ? "" : id.toString();
	}
	
	public String getPatientId() {
		return patientId == null ? "" : patientId.toString();
	}
	
	public String getPatientName() {
		return patientName;
	}

	public String getProviderId() {
		return providerId == null ? "" : providerId.toString();
	}
	
	public String getProviderName() {
		return providerName;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public String getDrug() {
		return drug;
	}

	public float getDosage() {
		return dosage;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public int getFrequency() {
		return frequency;
	}

	public LocalDate getSurgeryDate() {
		return surgeryDate;
	}

	public String getDischargeInstructions() {
		return dischargeInstructions;
	}

	public List<LocalDate> getTreatmentDates() {
		return treatmentDates == null ? new ArrayList<>() : treatmentDates;
	}

	public Collection<TreatmentDto> getFollowupTreatments() {
		return followupTreatments == null ? new ArrayList<>() : followupTreatments;
	}


	// TODO
	@Inject
	private IPatientService patientService;

    // TODO
	@Inject
	private IProviderService providerService;
	
	
	private TreatmentDto loadTreatment() {
		if (id == null) {
			addMessage(MESSAGE_TREATMENT_ID_MISSING);
			return null;
		}
		if (patientId == null && providerId == null) {
			addMessage(MESSAGE_TREATMENT_RELATED_MISSING);
			return null;
		}
		try {
			if (patientId != null) {
				return patientService.getTreatment(patientId, id);
			} else {
				return providerService.getTreatment(providerId, id);
			}
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Invalid id specified for treatment: "+id, e);
			addMessage(MESSAGE_TREATMENT_ID_INVALID);
		} catch (PatientServiceExn | ProviderServiceExn e) {
			logger.log(Level.SEVERE, "Failed to load treatment record with id: "+id, e);
			addMessage(MESSAGE_TREATMENT_ID_INVALID);
		}
		return null;
		
	}
	
	/**
	 * Triggered by receipt of a parameter value identifying the treatment.
	 */
	public void load() {
		TreatmentDto treatment = loadTreatment();
		if (treatment != null) {
			this.id = treatment.getId();
			this.patientId = treatment.getPatientId();
			this.providerId = treatment.getProviderId();
			this.patientName = treatment.getPatientName();
			this.providerName = treatment.getProviderName();
			this.diagnosis = treatment.getDiagnosis();
			this.followupTreatments = treatment.getFollowupTreatments();
			
			this.type = getTreatmentType(treatment);
			
			if (treatment instanceof DrugTreatmentDto) {
				DrugTreatmentDto ddto = (DrugTreatmentDto)treatment;
				this.drug =ddto.getDrug();
				this.dosage = ddto.getDosage();
				this.startDate = ddto.getStartDate();
				this.endDate = ddto.getEndDate();
				this.frequency = ddto.getFrequency();
				this.drugTreatment = true;
			} else if (treatment instanceof SurgeryTreatmentDto) {
				// TODO
				SurgeryTreatmentDto sdto = (SurgeryTreatmentDto)treatment;
				this.surgeryDate = sdto.getSurgeryDate();
				this.dischargeInstructions = sdto.getDischargeInstructions();
				this.surgeryTreatment = true;
			} else if (treatment instanceof RadiologyTreatmentDto) {
				RadiologyTreatmentDto rdto = (RadiologyTreatmentDto)treatment;
				this.treatmentDates = rdto.getTreatmentDates();
				this.radiologyTreatment = true;
			} else if (treatment instanceof PhysiotherapyTreatmentDto) {
				PhysiotherapyTreatmentDto pdto = (PhysiotherapyTreatmentDto)treatment;
				this.treatmentDates = pdto.getTreatmentDates();
				this.physiotherapyTreatment = true;
			} 
		}

	}

}
