package com.axelor.admission.web;

import com.axelor.admission.db.AdmissionProcess;
import com.axelor.admission.service.AdmissionProcessService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class AdmissionProcessController {

  @Inject AdmissionProcessService service;

  public void completeAdmissionOnAdmissionEntry(ActionRequest request, ActionResponse response) {
    AdmissionProcess admissionProcess = request.getContext().asType(AdmissionProcess.class);
    try {
      service.completeAdmissionOnAdmissionEntry(admissionProcess);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
