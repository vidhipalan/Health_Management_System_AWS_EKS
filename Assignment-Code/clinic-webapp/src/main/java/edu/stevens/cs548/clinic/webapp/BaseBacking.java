package edu.stevens.cs548.clinic.webapp;

import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public class BaseBacking implements Serializable {
	
	private static final long serialVersionUID = 1819802919748985743L;
	
	public static final String CHARSET = "utf-8";
	
	private static final String MISSING_TREATMENT_TYPE = "treatment.type.missing";
	
	private static final String DRUG_TREATMENT_TYPE = "treatment.type.drug";
	
	private static final String RADIOLOGY_TREATMENT_TYPE = "treatment.type.radiology";
	
	private static final String SURGERY_TREATMENT_TYPE = "treatment.type.surgery";
	
	private static final String PHYSIOTHERAPY_TREATMENT_TYPE = "treatment.type.physiotherapy";
	
	private static final String RESOURCE_BUNDLE = "Messages";
	
	protected static final String MESSAGE_PATIENT_ID_MISSING = "patient.id.missing";

	protected static final String MESSAGE_PATIENT_ID_INVALID = "patient.id.invalid";
	
	protected static final String MESSAGE_PROVIDER_ID_MISSING = "provider.id.missing";

	protected static final String MESSAGE_PROVIDER_ID_INVALID = "provider.id.invalid";

	protected static final String MESSAGE_TREATMENT_ID_INVALID = "treatment.id.invalid";
	
	protected static final String MESSAGE_TREATMENT_ID_MISSING = "treatment.id.missing";
	
	protected static final String MESSAGE_TREATMENT_RELATED_MISSING = "treatment.related.missing";

	
	@Inject
	private FacesContext facesContext;
	
	protected FacesContext getFacesContext() {
		// return FacesContext.getCurrentInstance();
		return facesContext;
	}

	protected ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	protected HttpServletRequest getWebRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}
	
	protected HttpServletResponse getWebResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}
	
	/*
	 * Locale-specific feedback, optionally associated with a UI component.
	 */
	private static ResourceBundle bundle;
	private static synchronized ResourceBundle getBundle(FacesContext context) {
		if (bundle == null) {
			Locale locale = context.getViewRoot().getLocale();
			bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
		}
		return bundle;
	}

	protected static void addMessageToContext(FacesContext context, String clientId, String key) {
		ResourceBundle res = getBundle(context);
		context.addMessage(clientId,  new FacesMessage(res.getString(key)));
	}
	
	protected void addMessage(String clientId, String key) {
		addMessageToContext(getFacesContext(), clientId, key);
	}
	
	protected String getClientId(String id) {
		return getFacesContext().getViewRoot().findComponent(id).getClientId(getFacesContext());
	}
	
	protected void addMessage(String key) {
		addMessage((String)null, key);
	} 
	
	public String getDisplayString(String key) {
		return key == null ? null : getBundle(getFacesContext()).getString(key);
	}
	
	protected static void reportValidationError(FacesContext context, UIComponent component, String key) {
		((UIInput) component).setValid(false);
		addMessageToContext(context, component.getClientId(context), key);
	}
	
	protected static String getTreatmentTypeKey(TreatmentDto treatment) {
		if (treatment == null) {
			return MISSING_TREATMENT_TYPE;
		} else if (treatment instanceof DrugTreatmentDto) {
			return DRUG_TREATMENT_TYPE;
		} else if (treatment instanceof RadiologyTreatmentDto) {
			return RADIOLOGY_TREATMENT_TYPE;
		} else if (treatment instanceof SurgeryTreatmentDto) {
			return SURGERY_TREATMENT_TYPE;
		} else if (treatment instanceof PhysiotherapyTreatmentDto) {
			return PHYSIOTHERAPY_TREATMENT_TYPE;
		} else {
			throw new IllegalStateException("Unrecognized type of treatment: "+treatment.getClass().getSimpleName());
		}
	}
	
	public String getTreatmentType(TreatmentDto treatment) {
		return getDisplayString(getTreatmentTypeKey(treatment));
	}
		
}
