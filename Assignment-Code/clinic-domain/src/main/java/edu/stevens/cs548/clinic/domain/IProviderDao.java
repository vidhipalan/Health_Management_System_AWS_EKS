package edu.stevens.cs548.clinic.domain;

import java.util.Collection;
import java.util.UUID;

public interface IProviderDao {
	
	public static class ProviderExn extends Exception {
		private static final long serialVersionUID = 1L;
		public ProviderExn (String msg) {
			super(msg);
		}
	}

	public void addProvider (Provider provider) throws ProviderExn;
		
	public Provider getProvider (UUID id, boolean includeTreatments) throws ProviderExn;

	public Provider getProvider (UUID id) throws ProviderExn;

	public Collection<Provider> getProviders();
	
	public void deleteProviders();
	
}
