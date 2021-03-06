package com.revature.biz;

import java.util.List;

import com.revature.biz.exception.BusinessServiceException;
import com.revature.model.DTO.StudentActivityPointsDTO;
import com.revature.model.DTO.StudentLoginHoursDTO;
import com.revature.model.DTO.StudentRankDTO;

public interface StudentService {
	/**
	 * Used to get student ranks.
	 * 
	 * @throws BusinessServiceException
	 *             if any business error occurs
	 */
	List<StudentRankDTO> getStudentGlobalRanks() throws BusinessServiceException;

	List<StudentRankDTO> getStudentUniversityRanks(String universityName) throws BusinessServiceException;

	StudentActivityPointsDTO getStudentActivityPointsByStudentId(Integer studentId) throws BusinessServiceException;

	List<StudentLoginHoursDTO> getStudentLoginDetails(Integer studentId) throws BusinessServiceException;

}
