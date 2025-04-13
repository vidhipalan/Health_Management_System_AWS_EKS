package edu.stevens.cs548.clinic.service;

import java.util.List;
import java.util.UUID;

import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

public interface IProviderService {
	
	public class ProviderServiceExn extends Exception {
		private static final long serialVersionUID = 1L;
		public ProviderServiceExn (String m, Exception e) {
			super(m, e);
		}
	}
	public class ProviderNotFoundExn extends ProviderServiceExn {
		private static final long serialVersionUID = 1L;
		public ProviderNotFoundExn (String m, Exception e) {
			super(m, e);
		}
	}
	public class TreatmentNotFoundExn extends ProviderServiceExn {
		private static final long serialVersionUID = 1L;
		public TreatmentNotFoundExn (String m, Exception e) {
			super(m, e);
		}
	}
	
	public UUID addProvider(ProviderDto dto) throws ProviderServiceExn;
	
	public List<ProviderDto> getProviders() throws ProviderServiceExn;

	public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn;
	
	public ProviderDto getProvider(UUID id) throws ProviderServiceExn;
	
	public UUID addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn;
	
	public TreatmentDto getTreatment(UUID providerId, UUID treatmentId) throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn;

	public void removeAll() throws ProviderServiceExn;
		
}
