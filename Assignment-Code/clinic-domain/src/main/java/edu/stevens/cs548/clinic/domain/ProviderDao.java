package edu.stevens.cs548.clinic.domain;

import edu.stevens.cs548.clinic.domain.ClinicDomainProducer.ClinicDomain;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

// TODO
@RequestScoped
@Transactional
public class ProviderDao implements IProviderDao {

	// TODO
	@Inject
	@ClinicDomain
	private EntityManager em;
	
	// TODO
	@Inject
	private ITreatmentDao treatmentDao;

	private Logger logger = Logger.getLogger(ProviderDao.class.getCanonicalName());

	@Override
	public void addProvider(Provider provider) throws ProviderExn {
		// Add to database, and initialize the provider aggregate with a treatment DAO.
		 UUID id = provider.getProviderId();
		Query query = em.createNamedQuery("CountProviderByProviderId").setParameter("providerId", id);
		Long numExisting = (Long) query.getSingleResult();
		
		logger.info(String.format("Adding provider with id %s, found %d existing records", id, numExisting));
		
		if (numExisting < 1) {
			
			em.persist(provider);
			provider.setTreatmentDao(this.treatmentDao);
			
		} else {
			
			throw new ProviderExn("Insertion: Provider with Provider id (" + id + ") already exists.");

		}
	}

	@Override
	/*
	 * The boolean flag indicates if related treatments should be loaded eagerly.
	 */
	public Provider getProvider(UUID id, boolean includeTreatments) throws ProviderExn {
		String queryName = "SearchProviderByProviderId";
		TypedQuery<Provider> query = em.createNamedQuery(queryName, Provider.class).setParameter("providerId", id);
		List<Provider> providers = query.getResultList();
		if (providers.size() > 1) {
			throw new ProviderExn("Duplicate provider records: provider id = " + id);
		} else if (providers.size() < 1) {
			throw new ProviderExn("Provider not found: provider id = " + id);
		} else {
			Provider provider = providers.get(0);
			/*
			 * Refresh from the database or we will never see new treatments.
			 */
			em.refresh(provider);
			provider.setTreatmentDao(this.treatmentDao);
			return provider;
		}
	}
	
	@Override
	/*
	 * By default, we eagerly load related treatments with a provider record.
	 */
	public Provider getProvider(UUID id) throws ProviderExn {
		return getProvider(id, true);
	}
	
	@Override
	public List<Provider> getProviders() {
		TypedQuery<Provider> query = em.createNamedQuery("SearchAllProviders", Provider.class);
		List<Provider> providers = query.getResultList();

		for (Provider provider : providers) {
			provider.setTreatmentDao(treatmentDao);
		}

		return providers;
	}
	
	@Override
	public void deleteProviders() {
		Query update = em.createNamedQuery("RemoveAllTreatments");
		update.executeUpdate();
		update = em.createNamedQuery("RemoveAllProviders");
		update.executeUpdate();
	}

}
