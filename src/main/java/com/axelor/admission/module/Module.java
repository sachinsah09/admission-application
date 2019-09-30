package com.axelor.admission.module;

import com.axelor.admission.service.AdmissionProcessService;
import com.axelor.admission.service.AdmissionProcessServiceImp;
import com.axelor.app.AxelorModule;

public class Module extends AxelorModule {
  protected void configure() {
    bind(AdmissionProcessService.class).to(AdmissionProcessServiceImp.class);
  }
}
