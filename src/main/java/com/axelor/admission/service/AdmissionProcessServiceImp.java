package com.axelor.admission.service;

import com.axelor.admission.db.AdmissionEntry;
import com.axelor.admission.db.AdmissionProcess;
import com.axelor.admission.db.CollegeEntry;
import com.axelor.admission.db.Faculty;
import com.axelor.admission.db.FacultyEntry;
import com.axelor.admission.db.repo.AdmissionEntryRepository;
import com.axelor.admission.db.repo.FacultyEntryRepository;
import com.axelor.admission.db.repo.FacultyRepository;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;
import java.time.LocalDate;
import java.util.Collections;
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
			//Comparator<AdmissionEntry> merit = Comparator.comparing(AdmissionEntry::getMerit);
			//admissionEntryList.sort(merit);
			Comparator<AdmissionEntry> comparator =  new Comparator<AdmissionEntry>() {
				@Override
				public int compare(AdmissionEntry o1, AdmissionEntry o2) {
					int result = o1.getMerit().compareTo(o2.getMerit());
					if(result == 0) {
						return o1.getRegistrationDate().compareTo(o2.getRegistrationDate());
					}
					return result == 0 ? 0 : result > 0 ? -1 : 1;
				}
			};
		Collections.sort(admissionEntryList, comparator);
			for (AdmissionEntry admissionEntry : admissionEntryList) {
				for (CollegeEntry collegeEntry : admissionEntry.getCollegeList()) {
					FacultyEntry facultyEntry = Beans.get(FacultyEntryRepository.class).all()
							.filter("self.faculty=? AND self.college= ?", faculty.getId(), collegeEntry.getCollege())
							.fetchOne();
					int seatVailable = facultyEntry.getSeats();
					if (seatVailable > 0) {
						admissionEntry.setCollegeSelected(collegeEntry.getCollege());
						admissionEntry.setValidationDate(LocalDate.now());
						admissionEntry.setStatus(3);
						seatVailable--;
						facultyEntry.setSeats(seatVailable);
						break;
					}
				}
				if (admissionEntry.getStatus() == 2) {
					admissionEntry.setStatus(4);
					admissionEntry.setCollegeSelected(null);
					admissionEntry.setValidationDate(null);
				}
			}
		}
	}
}
