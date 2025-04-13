package edu.stevens.cs548.clinic.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Use this pattern to import a treatment into the domain model without violating the aggregate
 * pattern.  The provider provides an implementation of ITreatmentImporter to an external
 * service, and the external service calls one of the operations to input a treatment into the
 * domain model.
 * 
 * The difficult part is how to handle follow-up treatments.  Each of these operations returns
 * a consumer that adds any follow-up treatments to the follow-ups for the new treatment, and
 * this is passed in recursive calls to add follow-up treatments.
 * 
 * @author dduggan
 *
 */
public interface ITreatmentImporter {

	public Consumer<Treatment> importtDrugTreatment(UUID tid, Patient patient, Provider provider, String diagnosis, String drug,
			float dosage, LocalDate start, LocalDate end, int frequency, Consumer<Treatment> consumer);

	public Consumer<Treatment> importRadiology(UUID tid, Patient patient, Provider provider, String diagnosis, List<LocalDate> dates,
			Consumer<Treatment> consumer);

	public Consumer<Treatment> importSurgery(UUID tid, Patient patient, Provider provider, String diagnosis, LocalDate date,
			String dischargeInstructions, Consumer<Treatment> consumer);

	public Consumer<Treatment> importPhysiotherapy(UUID tid, Patient patient, Provider provider, String diagnosis, List<LocalDate> dates,
			Consumer<Treatment> consumer);

}
