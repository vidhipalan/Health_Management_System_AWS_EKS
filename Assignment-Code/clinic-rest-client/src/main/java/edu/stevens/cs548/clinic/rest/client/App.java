package edu.stevens.cs548.clinic.rest.client;

import edu.stevens.cs548.clinic.rest.client.stub.WebClient;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
	
	public static final String APP_PROPERTIES = "/app.properties";
	
	public static final String SERVER_URI_PROPERTY = "server.uri";
	
	public static final String DATABASE_FILE_PROPERTY = "database.file";

	public static final String PATIENTS = "patients";

	public static final String PROVIDERS = "providers";

	public static final String TREATMENTS = "treatments";

	private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private final PatientDtoFactory patientFactory = new PatientDtoFactory();

	private final ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private final TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	private final WebClient client;

	private URI serverUri;

	public void severe(String s) {
		logger.severe(s);
	}

	public void severe(Exception e) {
		logger.log(Level.SEVERE, "Error during processing!", e);
	}

	public void warning(String s) {
		logger.info(s);
	}

	public void info(String s) {
		logger.info(s);
	}

	/*
	 * Main program
	 */
	public static void main(String[] args) {
		new App(args);
	}

	static void msg(String m) {
		System.out.print(m);
	}

	static void msgln(String m) {
		System.out.println(m);
	}

	static void err(String s) {
		System.err.println("** " + s);
	}
	
	protected void loadProperties() {
		/*
		 * Load default properties.
		 */
		try {
			Properties props = new Properties();
			InputStream propsIn = getClass().getResourceAsStream(APP_PROPERTIES);
			props.load(propsIn);
			propsIn.close();
			serverUri = URI.create(props.getProperty(SERVER_URI_PROPERTY));
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load properties from "+APP_PROPERTIES, e);
		}
	}
	
	protected List<String> processArgs(String[] args) {
		/*
		 * Default properties may be overridden on the command line.
		 */
		List<String> commandLineArgs = new ArrayList<String>();
		int ix = 0;
		Hashtable<String, String> opts = new Hashtable<String, String>();

		while (ix < args.length) {
			if (args[ix].startsWith("--")) {
				String option = args[ix++].substring(2);
				if (ix == args.length || args[ix].startsWith("--"))
					severe("Missing argument for --" + option + " option.");
				else if (opts.containsKey(option))
					severe("Option \"" + option + "\" already set.");
				else
					opts.put(option, args[ix++]);
			} else {
				commandLineArgs.add(args[ix++]);
			}
		}
		/*
		 * Overrides of values from configuration file.
		 */
		Enumeration<String> keys = opts.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement();
			if ("server".equals(k))
				serverUri = URI.create(opts.get("server"));
			else
				severe("Unrecognized option: --" + k);
		}

		return commandLineArgs;
	}


	public App(String[] args) {
		
		loadProperties();
		
		processArgs(args);
		
		client = new WebClient(serverUri);
		
		// Main command-line interface loop

		while (true) {
			try {
				msg("cs548> ");
				String line = in.readLine();
				if (line == null) {
					return;
				}
				String[] inputs = line.split("\\s+");
				if (inputs.length > 0) {
					String cmd = inputs[0];
					if (cmd.length() == 0)
						;
					else if ("addpatient".equals(cmd))
						addPatient();
					else if ("addprovider".equals(cmd))
						addProvider();
					else if ("addtreatment".equals(cmd))
						addOneTreatment();
					else if ("help".equals(cmd))
						help(inputs);
					else if ("quit".equals(cmd))
						return;
					else
						msgln("Bad input.  Type \"help\" for more information.");
				}
			} catch (Exception e) {
				severe(e);
			}
		}
	}


	public void addPatient() throws IOException {
		PatientDto patient = patientFactory.createPatientDto();
		patient.setId(UUID.randomUUID());
		msg("Name: ");
		patient.setName(in.readLine());
		patient.setDob(readDate("Patient DOB"));
		msgln("Added patient: "+client.addPatient(patient));
	}

	public void addProvider() throws IOException {
		ProviderDto provider = providerFactory.createProviderDto();
		provider.setId(UUID.randomUUID());
		msg("NPI: ");
		provider.setNpi(in.readLine());
		msg("Name: ");
		provider.setName(in.readLine());
		msgln("Added provider: "+client.addProvider(provider));
	}


	public void addOneTreatment() throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		if (treatment != null) {
			msgln("Added treatment: "+client.addTreatment(treatment));
		}
	}

	public TreatmentDto addTreatment() throws IOException, ParseException {
		msg("What form of treatment: [D]rug, [S]urgery, [R]adiology, [P]hysiotherapy? ");
		String line = in.readLine().toUpperCase();
		TreatmentDto treatment = null;
		if ("D".equals(line)) {
			treatment = addDrugTreatment();
		} else if ("S".equals(line)) {
			treatment = addSurgeryTreatment();
		} else if ("R".equals(line)) {
			treatment = addRadiologyTreatment();
		} else if ("P".equals(line)) {
			treatment = addPhysiotherapyTreatment();
		}

		if (treatment != null) {
			msgln("Adding follow-up treatments...");
			addTreatmentList(treatment);
			msgln("...finished follow-up treatments");
		}
		return treatment;
	}

	/*
	 * Use this to add a list of follow-up treatments.
	 */
	public void addTreatmentList(TreatmentDto parent) throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		while (treatment != null) {
			parent.getFollowupTreatments().add(treatment);
			treatment = addTreatment();
		}
	}

	private DrugTreatmentDto addDrugTreatment() throws IOException, ParseException {
		DrugTreatmentDto treatment = treatmentFactory.createDrugTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Drug: ");
		treatment.setDrug(in.readLine());
		msg("Dosage: ");
		treatment.setDosage(Float.parseFloat(in.readLine()));
		treatment.setStartDate(readDate("Start date"));
		treatment.setEndDate(readDate("End date"));
		msg("Frequency: ");
		treatment.setFrequency(Integer.parseInt(in.readLine()));

		return treatment;
	}

	private SurgeryTreatmentDto addSurgeryTreatment() throws IOException, ParseException {
		SurgeryTreatmentDto treatment = treatmentFactory.createSurgeryTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
		msg("Discharge instructions: ");
		treatment.setDischargeInstructions(in.readLine());
		treatment.setSurgeryDate(readDate("Surgery date"));



		return treatment;
	}

	private RadiologyTreatmentDto addRadiologyTreatment() throws IOException, ParseException {
		RadiologyTreatmentDto treatment = treatmentFactory.createRadiologyTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.getTreatmentDates().add(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}

	private PhysiotherapyTreatmentDto addPhysiotherapyTreatment() throws IOException, ParseException {
		PhysiotherapyTreatmentDto treatment = treatmentFactory.createPhysiotherapyTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.getTreatmentDates().add(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}


	private LocalDate readDate(String field) throws IOException {
		msg(String.format("%s (MM/dd/yyyy): ", field));
		String line = in.readLine();
		if (line == null || line.isBlank()) {
			return null;
		}
		return LocalDate.parse(line, dateFormatter);
	}


	public void help(String[] inputs) {
		if (inputs.length == 1) {
			msgln("Commands are:");
			msgln("  addpatient: add a patient");
			msgln("  addprovider: add a provider");
			msgln("  addtreatment: add a treatment");
			msgln("  quit: exit the app");
		}
	}

}
