package com.health.service;

import com.health.common.PatientDetails;
import com.health.common.PatientDetailsRequest;
import com.health.common.PatientRegistrationRequest;
import com.health.common.PatientRegistrationResponse;
import com.health.common.PatientServiceGrpc;
import com.health.repository.PatientRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class PatientService extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository patientRepository;


    @Override
    public void registerPatient(PatientRegistrationRequest request, StreamObserver<PatientRegistrationResponse> responseObserver) {
    }

    @Override
    public void getPatientDetails(PatientDetailsRequest request, StreamObserver<PatientDetails> responseObserver) {
    }
}
