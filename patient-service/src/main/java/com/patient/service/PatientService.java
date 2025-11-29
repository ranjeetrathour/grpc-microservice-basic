package com.health.service;

import com.health.common.PatientDetails;
import com.health.common.PatientDetailsRequest;
import com.health.common.PatientRegistrationRequest;
import com.health.common.PatientRegistrationResponse;
import com.health.common.PatientServiceGrpc;
import com.health.entity.Patient;
import com.health.repository.PatientRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver; // StreamObserver is used to send responses back to the client
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService // Marks this class as a gRPC service so Spring Boot can detect it
@RequiredArgsConstructor
@Slf4j
public class PatientService extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository patientRepository; // Repository to interact with the database

    /**
     * Method to register a new patient
     * @param request - contains patient data sent by client
     * @param responseObserver - used to send response back to client
     */
    @Override
    public void registerPatient(PatientRegistrationRequest request, StreamObserver<PatientRegistrationResponse> responseObserver) {
        if (request == null) { // Check if request is null
            log.info("request can not be null");
            return;
        }

        // Save patient data to database
        Patient save = patientRepository.save(Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build());

        // StreamObserver sends the response back to the client
        responseObserver.onNext(
                PatientRegistrationResponse.newBuilder()
                        .setPatientId(save.getId()) // send generated patient ID
                        .setMessage("patient save successfully") // send success message
                        .build()
        );

        responseObserver.onCompleted(); // Marks the response as complete
    }

    /**
     * Method to fetch patient details
     * @param request - contains patient_id sent by client
     * @param responseObserver - used to send patient details back to client
     */
    @Override
    public void getPatientDetails(PatientDetailsRequest request, StreamObserver<PatientDetails> responseObserver) {
        // Convert the incoming patient_id string to UUID
        UUID patientId = UUID.fromString(request.getPatientId());

        // Fetch patient from database using repository
        var exitingPatient = patientRepository.findById(String.valueOf(patientId));

        if (exitingPatient.isPresent()){
            var patient = exitingPatient.get();

            // StreamObserver sends patient details back to client
            // Note: If email is null, we send empty string because proto3 doesn't allow null
            responseObserver.onNext(PatientDetails.newBuilder()
                    .setPatientId(patient.getId())
                    .setFirstName(patient.getFirstName())
                    .setLastName(patient.getLastName())
                    .setEmail(patient.getEmail() == null ? "" : patient.getEmail())
                    .setPhone(patient.getPhone())
                    .setAddress(patient.getAddress())
                    .build()
            );
        } else {
            // If patient not found, send error back to client
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Patient not found")
                            .asRuntimeException()
            );
        }

        responseObserver.onCompleted(); // Marks the response as complete
    }
}
