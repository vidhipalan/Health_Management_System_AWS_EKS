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
public class PatientDao implements IPatientDao {
	

	// TODO
	@Inject
	@ClinicDomain
	private EntityManager em;
	
	// TODO
	@Inject
	private ITreatmentDao treatmentDao;


	private static final Logger logger = Logger.getLogger(PatientDao.class.getCanonicalName());

	@Override
	public void addPatient(Patient patient) throws PatientExn {
		/*
		 * UUID is supported by Jakarta EE 10, but JDBC driver does not use PSQL uuid type.
		 */
		UUID pid = patient.getPatientId();
		Query query = em.createNamedQuery("CountPatientByPatientId").setParameter("patientId", pid);
		Long numExisting = (Long) query.getSingleResult();
		
		logger.info(String.format("Adding patient with id %s, found %d existing records", pid, numExisting));
		
		if (numExisting < 1) {
			
			// Add to database, and initialize the patient aggregate with a treatment DAO.
			em.persist(patient);
			patient.setTreatmentDao(this.treatmentDao);
			
		} else {
			
			throw new PatientExn("Insertion: Patient with patient id (" + pid + ") already exists.");
		}
	}
	
	@Override
	/*
	 * The Boolean flag indicates if related treatments should be loaded eagerly.
	 */
	public Patient getPatient(UUID id, boolean includeTreatments) throws PatientExn {
		/*
		 * Retrieve patient using external key
		 */
		// String queryName = includeTreatments ? "SearchPatientWithTreatmentsByPatientId" : "SearchPatientByPatientId";
		String queryName = "SearchPatientByPatientId";
		TypedQuery<Patient> query = em.createNamedQuery(queryName, Patient.class).setParameter("patientId",id);
		List<Patient> patients = query.getResultList();
		
		if (patients.size() > 1) {
			throw new PatientExn("Duplicate patient records: patient id = " + id);
		} else if (patients.size() < 1) {
			throw new PatientExn("Patient not found: patient id = " + id);
		} else {
			Patient p = patients.get(0);
			/*
			 * Refresh from database or we will never see new treatments.
			 */
			em.refresh(p);
			p.setTreatmentDao(this.treatmentDao);
			return p;
		}
	}

	@Override
	public Patient getPatient(UUID id) throws PatientExn {
		return getPatient(id, true);
	}

	@Override
	public List<Patient> getPatients() {
		/*
		 * Return a list of all patients (remember to set treatmentDAO)
		 */
		TypedQuery<Patient> query = em.createNamedQuery("SearchAllPatients", Patient.class);
		List<Patient> patients = query.getResultList();
		
		for (Patient p : patients) {
			p.setTreatmentDao(treatmentDao);
		}

		return patients;
	}
	
	@Override
	public void deletePatients() {
		Query update = em.createNamedQuery("RemoveAllPatients");
		update.executeUpdate();
	}

}
