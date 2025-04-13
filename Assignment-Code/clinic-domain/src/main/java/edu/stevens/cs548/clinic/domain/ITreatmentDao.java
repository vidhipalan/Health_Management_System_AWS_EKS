package edu.stevens.cs548.clinic.domain;

import java.util.UUID;

public interface ITreatmentDao {
	
	public static class TreatmentExn extends Exception {
		private static final long serialVersionUID = 1L;
		public TreatmentExn (String msg) {
			super(msg);
		}
	}
	
	/**
	 * Retrieve a treatment based on external key.
	 */
	public Treatment getTreatment (UUID id) throws TreatmentExn;
	
	/**
	 * Insert into database.
	 */
	public void addTreatment (Treatment t);
	
}
