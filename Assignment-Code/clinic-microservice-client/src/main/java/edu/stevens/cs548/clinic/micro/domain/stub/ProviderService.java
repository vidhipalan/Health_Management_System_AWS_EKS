package edu.stevens.cs548.clinic.micro.domain.stub;

import edu.stevens.cs548.clinic.micro.domain.IProviderMicroService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;

// TODO
@RequestScoped
public class ProviderService implements IProviderService {
	
	private Logger logger = Logger.getLogger(ProviderService.class.getCanonicalName());

	private static final String LOCATION = "location";
	
	// TODO
	@Inject
	@RestClient
	IProviderMicroService providerMicroService;
	
	@Override
	public UUID addProvider(ProviderDto dto) throws ProviderServiceExn {
		logger.info(String.format("addProvider: Adding provider %s in microservice client!", dto.getName()));
		try {
			Response response = providerMicroService.addProvider(dto);
			if (response.getStatus() >= 300) {
				Exception e = new WebApplicationException(response);
				throw new ProviderServiceExn("Failed to add patient "+dto.getId(), e);
			}
			String location = response.getHeaderString(LOCATION);
			if (location == null) {
				throw new IllegalStateException("Missing location response header!");
			}
			String[] uriSegments = URI.create(location).getPath().split("/");
			return UUID.fromString(uriSegments[uriSegments.length-1]);
		} catch (WebApplicationException e) {
			throw new ProviderServiceExn("Web service failure.", e);
		}
	}

	@Override
	public List<ProviderDto> getProviders() throws ProviderServiceExn {
		logger.info(String.format("getProviders: Getting all providers in microservice client!"));
		try {
			return providerMicroService.getProviders();
		} catch (WebApplicationException e) {
			throw new ProviderServiceExn("Web service failure.", e);
		}
	}

	@Override
	public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn {
		logger.info(String.format("getProvider: Getting provider %s in microservice client!", id.toString()));
		try {
			return providerMicroService.getProvider(id.toString(), Boolean.toString(includeTreatments));
		} catch (WebApplicationException e) {
			throw new ProviderServiceExn("Web service failure.", e);
		}
	}

	@Override
	public ProviderDto getProvider(UUID id) throws ProviderServiceExn {
		return getProvider(id, true);
	}

	@Override
	public UUID addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn {
		logger.info(String.format("addTreatment: Adding treatment for %s in microservice client!", dto.getPatientName()));
		try {
			Response response = providerMicroService.addTreatment(dto.getProviderId().toString(), dto);
			String location = response.getHeaderString(LOCATION);
			if (location == null) {
				throw new IllegalStateException("Missing location header after adding treatment!");
			}
			String[] uriSegments = URI.create(location).getPath().split("/");
			return UUID.fromString(uriSegments[uriSegments.length-1]);
		} catch (WebApplicationException e) {
			throw new ProviderServiceExn("Web service failure.", e);
		}
	}

	@Override
	public TreatmentDto getTreatment(UUID providerId, UUID treatmentId)
			throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn {
		logger.info(String.format("getTreatment: Getting treatment %s in microservice client!", treatmentId.toString()));
		try {
			return providerMicroService.getTreatment(providerId.toString(), treatmentId.toString());
		} catch (WebApplicationException e) {
			throw new ProviderServiceExn("Web service failure.", e);
		}
	}

	@Override
	public void removeAll() throws ProviderServiceExn {
		logger.info(String.format("deleteProviders: Deleting all providers in microservice client!"));
		try {
			providerMicroService.removeAll();
		} catch (WebApplicationException e) {
			throw new ProviderServiceExn("Web service failure.", e);
		}
	}

}
