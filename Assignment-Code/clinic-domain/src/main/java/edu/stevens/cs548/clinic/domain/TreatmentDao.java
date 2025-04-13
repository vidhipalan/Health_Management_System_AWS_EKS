package edu.stevens.cs548.clinic.domain;

import edu.stevens.cs548.clinic.domain.ClinicDomainProducer.ClinicDomain;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

// TODO
@RequestScoped
@Transactional
public class TreatmentDao implements ITreatmentDao {
	
	// TODO
	@Inject
	@ClinicDomain
	private EntityManager em;

	@Override
	public Treatment getTreatment(UUID id) throws TreatmentExn {
		/*
		 * Retrieve treatment using external key
		 */
		TypedQuery<Treatment> query = em.createNamedQuery("SearchTreatmentByTreatmentId", Treatment.class).setParameter("treatmentId",id);
		List<Treatment> treatments = query.getResultList();
		
		if (treatments.size() > 1) {
			throw new TreatmentExn("Duplicate treatment records: treatment id = " + id);
		} else if (treatments.size() < 1) {
			throw new TreatmentExn("Treatment not found: treatment id = " + id);
		} else {
			Treatment t = treatments.get(0);
			/*
			 * Refresh from database or we will never see new follow-up treatments.
			 */
			em.refresh(t);
			return t;
		}
	}

	@Override
	public void addTreatment(Treatment t) {
		em.persist(t);
	}
	
}
