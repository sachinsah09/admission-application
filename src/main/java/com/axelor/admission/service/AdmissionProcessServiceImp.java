package com.axelor.admission.service;

import com.axelor.admission.db.AdmissionEntry;
import com.axelor.admission.db.AdmissionProcess;
import com.axelor.admission.db.CollegeEntry;
import com.axelor.admission.db.Faculty;
import com.axelor.admission.db.repo.AdmissionEntryRepository;
import com.axelor.admission.db.repo.FacultyRepository;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class AdmissionProcessServiceImp implements AdmissionProcessService {

	@Override
	@Transactional
	public void completeAdmissionOnAdmissionEntry(AdmissionProcess admissionProcess) {
		LocalDate fromDate = admissionProcess.getFromDate();
		LocalDate toDate = admissionProcess.getToDate();

		List<Faculty> facultyList = Beans.get(FacultyRepository.class).all().fetch();

		for (Faculty faculty : facultyList) {
			List<AdmissionEntry> admissionEntryList = Beans.get(AdmissionEntryRepository.class).all().filter(
					"self.status = 2 AND self.faculty = ? AND self.registrationDate >= ? AND self.registrationDate <= ?",
					faculty.getId(), fromDate, toDate).fetch();
			Comparator<AdmissionEntry> merit = Comparator.comparing(AdmissionEntry::getMerit);
			admissionEntryList.sort(merit);

			for (AdmissionEntry admissionEntry : admissionEntryList) {
				for (CollegeEntry collegeEntry : admissionEntry.getCollegeList()) {

					admissionEntry.setCollegeSelected(collegeEntry.getCollege());
					admissionEntry.setValidationDate(LocalDate.now());
				}
			}
		}
	}
}
