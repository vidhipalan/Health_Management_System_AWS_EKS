package edu.stevens.cs548.clinic.rest.client.stub;

import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/*
 * The API for the clinic REST server.
 */
public interface IServerApi {

    @POST("patient")
    public Call<Void> addPatient(@Body PatientDto patientDto);

    @POST("provider")
    public Call<Void> addProvider(@Body ProviderDto providerDto);

    @POST("provider/{id}/treatment")
    public Call<Void> addTreatment(@Path("id") String providerId, @Body TreatmentDto treatmentDto);

}
