package edu.stevens.cs548.clinic.micro.domain.rest;

import edu.stevens.cs548.clinic.domain.IPatientDao;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDao;
import edu.stevens.cs548.clinic.domain.IProviderDao.ProviderExn;
import edu.stevens.cs548.clinic.domain.IProviderFactory;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.domain.ProviderFactory;
import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Microservice for provider aggregate
 */
// TODO
@RequestScoped
@Transactional
@Path("provider")
public class ProviderMicroService {

	private Logger logger = Logger.getLogger(ProviderMicroService.class.getCanonicalName());

	private IProviderFactory providerFactory;

	private ProviderDtoFactory providerDtoFactory;
	
	public ProviderMicroService() {
		// Initialize factories
		providerFactory = new ProviderFactory();
		providerDtoFactory = new ProviderDtoFactory();
	}
	

	// TODO
	@Context
	UriInfo uriInfo;
	
	// TODO
	@Inject
	private IProviderDao providerDao;

	// TODO
	@Inject
	private IPatientDao patientDao;

	// TODO
	@POST
	@Consumes("application/json")
	public Response addProvider(ProviderDto dto) {
		// Use factory to create Provider entity, and persist with DAO
		try {
			logger.info(String.format("addProvider: Adding provider %s in microservice!", dto.getName()));
			Provider provider = providerFactory.createProvider();
			if (dto.getId() == null) {
				provider.setProviderId(UUID.randomUUID());
			} else {
				provider.setProviderId(dto.getId());
			}
			provider.setName(dto.getName());
			provider.setNpi(dto.getNpi());
			providerDao.addProvider(provider);
			URI location = uriInfo.getBaseUriBuilder().path(provider.getProviderId().toString()).build();
			return Response.created(location).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to add provider!", e);
			return Response.serverError().build();
		}
	}
	
	// TODO
	@GET
	@Produces("application/json")
	public List<ProviderDto> getProviders() {
		try {
			logger.info(String.format("getProviders: Getting all providers in microservice!"));
			Collection<Provider> providers = providerDao.getProviders();
			List<ProviderDto> dtos = new ArrayList<>();
			for (Provider provider : providers) {
				dtos.add(providerToDto(provider, false));
			}
			return dtos;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to get patients!", e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	private ProviderDto providerToDto(Provider provider, boolean includeTreatments) throws TreatmentExn {
		ProviderDto dto = providerDtoFactory.createProviderDto();
		dto.setId(provider.getProviderId());
		dto.setName(provider.getName());
		dto.setNpi(provider.getNpi());
		if (includeTreatments) {
			dto.setTreatments(provider.exportTreatments(TreatmentExporter.exportWithoutFollowups()));
		}
		return dto;
	}
	
	// TODO
	@GET
	@Path("{id}")
	@Produces("application/json")
	public ProviderDto getProvider(@PathParam("id") String id, @QueryParam("treatments") @DefaultValue("true")String treatments) {
		try {
			logger.info(String.format("getProvider: Getting provider %s in microservice!", id));
			UUID providerId = UUID.fromString(id);
			boolean includeTreatments = Boolean.parseBoolean(treatments);

			Provider provider = providerDao.getProvider(providerId);
			return providerToDto(provider, includeTreatments);

		} catch (ProviderExn e) {
			logger.info("Failed to find provider with id "+id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (TreatmentExn e) {
			logger.log(Level.SEVERE, String.format("Failed to get provider %s!", id), e);
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	// TODO
	@POST
	@Path("{id}/treatment")
	@Consumes("application/json")
	public Response addTreatment(@PathParam("id") String id, TreatmentDto dto) {
		try {
			logger.info(String.format("addTreatment: Adding treatment for %s in microservice!", dto.getPatientName()));
			UUID providerId = UUID.fromString(id);
			if (!providerId.equals(dto.getProviderId())) {
				throw new IllegalArgumentException(String.format("Provider id %s in URI is not the same as the treatment provider id %s", id, dto.getProviderId().toString()));
			}
			UUID tid = addTreatmentImpl(dto, null);
			URI location = uriInfo.getBaseUriBuilder().path(dto.getProviderId().toString()).path("treatment").path(tid.toString()).build();
			return Response.created(location).build();
		} catch (PatientExn | ProviderExn e) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed to add treatment %s!", id), e);
			return Response.serverError().build();
		}
	}

	private UUID addTreatmentImpl(TreatmentDto dto, Consumer<Treatment> parentFollowUps)
			throws PatientExn, ProviderExn {
		/*
		 * Set the external key here.
		 */
		if (dto.getId() == null) {
			dto.setId(UUID.randomUUID());
		}

		Provider provider = providerDao.getProvider(dto.getProviderId());
		Patient patient = patientDao.getPatient(dto.getPatientId());

		Consumer<Treatment> followUpsConsumer = null;

		if (dto instanceof DrugTreatmentDto) {

			DrugTreatmentDto drugTreatmentDto = (DrugTreatmentDto) dto;
			followUpsConsumer = provider.importtDrugTreatment(dto.getId(), patient, provider, dto.getDiagnosis(),
					drugTreatmentDto.getDrug(), drugTreatmentDto.getDosage(), drugTreatmentDto.getStartDate(),
					drugTreatmentDto.getEndDate(), drugTreatmentDto.getFrequency(), parentFollowUps);

			/*
			 * TODO Handle the other cases
			 */


		} else if (dto instanceof RadiologyTreatmentDto) {
			RadiologyTreatmentDto radiologyTreatmentDto = (RadiologyTreatmentDto) dto;
			followUpsConsumer = provider.importRadiology(dto.getId(), patient, provider, dto.getDiagnosis(),
					radiologyTreatmentDto.getTreatmentDates(),parentFollowUps);
		} else if (dto instanceof  PhysiotherapyTreatmentDto) {
			PhysiotherapyTreatmentDto physiotherapyTreatmentDto = (PhysiotherapyTreatmentDto) dto;
			followUpsConsumer = provider.importPhysiotherapy(dto.getId(), patient, provider, dto.getDiagnosis(),
					physiotherapyTreatmentDto.getTreatmentDates(), parentFollowUps);
		} else if (dto instanceof SurgeryTreatmentDto) {
			SurgeryTreatmentDto surgeryTreatmentDto = (SurgeryTreatmentDto) dto;
			followUpsConsumer = provider.importSurgery(dto.getId(), patient, provider, dto.getDiagnosis(),
					surgeryTreatmentDto.getSurgeryDate() ,surgeryTreatmentDto.getDischargeInstructions(), parentFollowUps);
		} else {
			throw new IllegalArgumentException("No treatment-specific info provided.");
		}
		for (TreatmentDto followUp : dto.getFollowupTreatments()) {
			addTreatmentImpl(followUp, followUpsConsumer);
		}
		return dto.getId();

	}
	
	// TODO
	@GET
	@Path("{id}/treatment/{tid}")
	@Produces("application/json")
	public TreatmentDto getTreatment(@PathParam("id") String id, @PathParam("tid") String tid) {
		try {
			logger.info(String.format("getTreatment: Getting treatment %s in microservice!", tid));
			// Export treatment DTO from Provider aggregate
			UUID providerId = UUID.fromString(id);
			UUID treatmentId = UUID.fromString(tid);
			Provider provider = providerDao.getProvider(providerId);
			return provider.exportTreatment(treatmentId, TreatmentExporter.exportWithFollowups());
		} catch (ProviderExn e) {
			logger.info("Failed to find provider with id "+id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (TreatmentExn e) {
			logger.info(String.format("Failed to find treatment %s for provider %s", tid, id));
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed to get treatment %s!", tid), e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO
	@DELETE
	public void removeAll() {
		logger.info(String.format("deleteProviders: Deleting all providers in microservice!"));
		providerDao.deleteProviders();
	}


}
