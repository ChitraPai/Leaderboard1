package com.revature.biz.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.biz.StudentProjectService;
import com.revature.biz.exception.BusinessServiceException;
import com.revature.data.StudentProjectDAO;
import com.revature.data.exception.DataServiceException;
import com.revature.model.DTO.StudentProjectActivityDetailsDTO;
import com.revature.model.DTO.StudentProjectPercentageDTO;
import com.revature.model.DTO.StudentProjectSkillPointsDTO;
import com.revature.model.DTO.StudentprojectDetailsDTO;

@Service
public class StudentProjectServiceImpl implements StudentProjectService {
	private static Logger logger = Logger.getLogger(StudentProjectServiceImpl.class);

	@Autowired
	private StudentProjectDAO studentProjectDAO;

	
	
	@Override
	public List<StudentProjectSkillPointsDTO> getStudentProjectSkillPoints(Integer studentId) throws BusinessServiceException {
		List<StudentProjectSkillPointsDTO> studentProjects = null;
		try {
			studentProjects = studentProjectDAO.getStudentProjectSkillPoints(studentId);
			logger.info("Student Project Skill Points retrieved successfully");
		} catch (DataServiceException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessServiceException(e.getMessage(), e);
		}
		return studentProjects;
	}

	

	@Override
	public List<StudentProjectPercentageDTO> getStudentProjectPercentageCompleted(Integer studentId)
			throws BusinessServiceException {
		List<StudentProjectPercentageDTO> studentProjectPercentage = null;
		try {
			studentProjectPercentage = studentProjectDAO.getStudentProjectPercentageCompleted(studentId);
			logger.info("Student Project Activity Points retrieved successfully");
		} catch (DataServiceException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessServiceException(e.getMessage(), e);
		}
		return studentProjectPercentage;
	}
	
	@Override
	public List<StudentprojectDetailsDTO> getStudentProjectDetails(String projectName)
			throws BusinessServiceException {
		List<StudentprojectDetailsDTO> studentProjectDetails = null;
		try {
			studentProjectDetails = studentProjectDAO.getStudentProjectDetails(projectName);
			logger.info("Student Project Activity Points retrieved successfully");
		} catch (DataServiceException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessServiceException(e.getMessage(), e);
		}
		return studentProjectDetails;
	}
	@Override
	public List<StudentProjectActivityDetailsDTO> getStudentProjectActivityDetails(String projectName)
			throws BusinessServiceException {
		List<StudentProjectActivityDetailsDTO> studentProjectActivityDetails = null;
		try {
			studentProjectActivityDetails = studentProjectDAO.getStudentProjectActivityDetails(projectName);
			logger.info("Student Project Activity Points retrieved successfully");
		} catch (DataServiceException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessServiceException(e.getMessage(), e);
		}
		return studentProjectActivityDetails;
	}
}
