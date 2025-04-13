package edu.stevens.cs548.clinic.webapp.provider;

import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.webapp.BaseBacking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("providerBacking")
@RequestScoped
public class ProviderBacking extends BaseBacking {

	private static final long serialVersionUID = -6498472821445783075L;

	private static Logger logger = Logger.getLogger(ProviderBacking.class.getCanonicalName());

	/**
	 * The value of this property is provided as a query string parameter and
	 * set by a metadata annotation in the page.
	 */
	private String id;
	
	private ProviderDto provider;
		
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		if (provider == null) {
			return null;
		}
		return provider.getName();
	}

	public String getNpi() {
		if (provider == null) {
			return null;
		}
		return provider.getNpi();
	}

	public List<TreatmentDto> getTreatments() {
		if (provider == null) {
			return new ArrayList<>();
		}
		return provider.getTreatments();
	}

	// TODO
	@Inject
	private IProviderService providerService;
	
	/**
	 * Triggered by receipt of a parameter value identifying the provider.
	 */
	public void load() {
		try {
			if (id == null) {
				throw new IllegalArgumentException("No value specified for provider id!");
			}
			provider = providerService.getProvider(UUID.fromString(id));
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Invalid id specified for provider: "+id, e);
			addMessage(MESSAGE_PROVIDER_ID_INVALID);
		} catch (ProviderServiceExn e) {
			logger.log(Level.SEVERE, "Failed to load provider record with id: "+id, e);
			addMessage(MESSAGE_PROVIDER_ID_INVALID);
		}
	}

}
