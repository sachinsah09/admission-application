package com.axelor.admission.service;

import com.axelor.admission.db.AdmissionProcess;

public interface AdmissionProcessService {
  public void completeAdmissionOnAdmissionEntry(AdmissionProcess admissionProcess);
}
