package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PatientDTO;

import java.util.List;


public interface PatientService {
    Long addPatient(PatientDTO patientDTO) ;
    PatientDTO getPatientById(Long id) ;

    PatientDTO updatePatient(PatientDTO patientDTO);

    Boolean patientExists(Long id);

    List<PatientDTO> getAllPatients();

    List<DoctorDropdown> getPatientsById(List<Long> ids);

    List<PatientDTO> findAllByIds(List<Long> ids);

}

