package com.revature.data.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.revature.data.access.DataModifier;
import com.revature.data.access.DataRetriver;
import com.revature.data.access.exception.DataAccessException;
import com.revature.data.exception.DataServiceException;
import com.revature.data.utils.DataUtils;
import com.revature.model.StudentAccount;
import com.revature.model.DTO.StudentAccountDTO;

@Repository
public class StudentAccountDAOImpl implements com.revature.data.StudentAccountDAO {
	private static Logger logger = Logger.getLogger(StudentAccountDAOImpl.class);
	@Autowired
	private DataRetriver dataRetriver;

	public DataRetriver getDataRetriver() {
		return dataRetriver;
	}

	public void setDataRetriver(DataRetriver dataRetriver) {
		this.dataRetriver = dataRetriver;
	}

	@Autowired
	private DataModifier dataModifier;

	public DataModifier getDataModifier() {
		return dataModifier;
	}

	public void setDataModifier(DataModifier dataModifier) {
		this.dataModifier = dataModifier;
	}

	@Override
	public StudentAccountDTO getId(String emailId, String password) throws DataServiceException {

		StudentAccountDTO studentAccount = null;
		try {
			StringBuilder sb = new StringBuilder(
					"SELECT s.`ID`stuId,s.`EMAIL`username,s.`NAME`name,u.`NAME`collegeName,s.`DEPARTMENT`department,\r\n"
							+ "IFNULL(sa.`TOTAL_ACTIVITY_POINTS`,0) actPts,\r\n"
							+ "IFNULL(ss.`TOTAL_SKILL_POINTS`,0) skillPts\r\n"
							+ "FROM students s JOIN universities u ON s.`UNIVERSITY_ID`=u.`ID`\r\n"
							+ "JOIN student_account sacc ON s.`ID`=sacc.`STUDENT_ID`\r\n" + "JOIN (SELECT a.id, \r\n"
							+ "ROUND (SUM(IFNULL(a.actpts,0))*(SELECT WEIGHTAGE FROM seed_points_weightage WHERE id=1)/100)AS TOTAL_ACTIVITY_POINTS\r\n"
							+ "FROM\r\n"
							+ "((SELECT s.`ID`,SUM(c.`ENROLLMENT_POINTS`)+SUM(IF(IFNULL(sc.`COMPLETED_ON`,FALSE),c.`COMPLETION_POINTS`,0)) actpts \r\n"
							+ "FROM students s\r\n" + "JOIN student_courses sc ON s.`ID`=sc.`STUDENT_ID` \r\n"
							+ "JOIN courses c ON c.`ID`=sc.`COURSE_ID` GROUP BY s.`ID`)\r\n" + "\r\n" + "UNION ALL\r\n"
							+ "(SELECT s.`ID`,SUM(p.`ENROLLMENT_POINTS`)+SUM(IF(IFNULL(sp.`COMPLETED_ON`,FALSE),p.`COMPLETION_POINTS`,0)) actpts \r\n"
							+ "FROM students s \r\n" + "JOIN student_projects sp ON s.`ID`=sp.`STUDENT_ID`\r\n"
							+ "JOIN projects p ON sp.`PROJECT_ID`=p.`ID`GROUP BY s.`ID`)\r\n" + "\r\n" + "UNION ALL\r\n"
							+ "(SELECT `student_id` id,COUNT(DISTINCT(DATE(activity_timestamp)))*(SELECT points FROM point_settings WHERE login_activity_id=1) actpts\r\n"
							+ " FROM student_audit_details\r\n"
							+ "WHERE login_activity_id=1 GROUP BY student_id ))a\r\n" + "GROUP BY a.id)sa\r\n"
							+ "ON s.`ID`=sa.id\r\n" + "LEFT JOIN\r\n" + "(\r\n" + "SELECT b.id,\r\n"
							+ "ROUND (SUM(IFNULL(b.skillpts,0))*(SELECT WEIGHTAGE FROM seed_points_weightage WHERE id=2)/100)AS TOTAL_SKILL_POINTS\r\n"
							+ " FROM\r\n" + "((SELECT sp.`STUDENT_ID` id,SUM(SKILL_POINTS) skillpts\r\n"
							+ "FROM student_project_sprint_activities spsa\r\n"
							+ "JOIN project_sprint_activities psa\r\n"
							+ "ON psa.`ID`=spsa.`PROJECT_SPRINT_ACTIVITY_ID`\r\n"
							+ "RIGHT JOIN student_project_sprints sps\r\n"
							+ "ON sps.`ID`=spsa.`STUDENT_PROJECTS_SPRINT_ID`\r\n" + "RIGHT JOIN student_projects sp\r\n"
							+ "ON sp.`ID`=sps.`STUDENT_PROJECT_ID`\r\n" + "RIGHT JOIN projects p \r\n"
							+ "ON p.`ID`=sp.`PROJECT_ID`\r\n" + "WHERE spsa.`STATUS_ID`= 2\r\n"
							+ "GROUP BY sp.STUDENT_ID)\r\n" + "UNION ALL\r\n"
							+ "(SELECT sc.`STUDENT_ID` id,SUM(cc.`SKILL_POINTS`) skillpts\r\n"
							+ "FROM student_courses sc\r\n" + "LEFT JOIN student_course_contents scc\r\n"
							+ "ON sc.`ID` = scc.`STUDENT_COURSE_ID`\r\n" + "JOIN course_contents cc\r\n"
							+ "ON cc.`ID` = scc.`COURSE_CONTENT_ID`\r\n" + "JOIN courses c\r\n"
							+ "ON c.`ID`=cc.`COURSE_ID`\r\n" + "WHERE scc.`STATUS_ID` = 2\r\n"
							+ "GROUP BY sc.`STUDENT_ID`)\r\n" + "UNION ALL\r\n"
							+ "(SELECT sq.`STUDENT_ID`,SUM(q.`SKILL_POINTS`) skillpts\r\n"
							+ "FROM student_quizes sq\r\n" + "JOIN quizzes q ON q.`ID`=sq.`QUIZ_ID`\r\n"
							+ "RIGHT JOIN students s\r\n" + "ON s.`ID`=sq.`STUDENT_ID`\r\n"
							+ "WHERE sq.`STATUS_ID`=2\r\n" + "GROUP BY sq.`STUDENT_ID`\r\n" + "))b\r\n"
							+ "GROUP BY b.id)ss\r\n" + "ON sa.id=ss.id\r\n" + "WHERE email='" + emailId + "'"
							+ " and PASSWORD='" + password + "'");
			studentAccount = (StudentAccountDTO) dataRetriver.retrieveBySQLAsObject(sb.toString(),
					StudentAccountDTO.class);
			logger.info("Login data retrieval success..");
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			throw new DataServiceException(DataUtils.getPropertyMessage("data_retrieval_fail"), e);
		}
		return studentAccount;

	}
	// @Override
	// public StudentAccountDTO getUserByLogin(String emailId, String password)
	// throws DataServiceException {
	// StudentAccountDTO studentAccountDTOObj = null;
	//
	// try {
	// StringBuilder sb = new StringBuilder(
	// "SELECT PASSWORD password FROM student_account WHERE USERNAME='" +
	// emailId + "'");
	// studentAccountDTOObj = (StudentAccountDTO)
	// dataRetriver.retrieveBySQLAsObject(sb.toString(),
	// StudentAccountDTO.class);
	// System.out.println(studentAccountDTOObj);
	// if (studentAccountDTOObj == null)
	// logger.info("User not exists");
	// else {
	// String dbPassword = studentAccountDTOObj.getPassword();
	// logger.info("Pass-db " + dbPassword);
	// if (DataUtils.checkPassword(password, dbPassword)) {
	// logger.info("User login success...");
	// StringBuilder sb1 = new StringBuilder(
	// "SELECT student_Id stuId,NAME name,`USERNAME` username ,`PASSWORD`
	// password,`DEPARTMENT` department,COLLEGE_NAME collegeName,Skill_Points
	// skillPts,Activity_Points actPts FROM `vw_student_details` WHERE
	// USERNAME='"
	// + emailId + "'" + " and PASSWORD='" + dbPassword + "'");
	// studentAccountDTOObj = (StudentAccountDTO)
	// dataRetriver.retrieveBySQLAsObject(sb1.toString(),
	// StudentAccountDTO.class);
	// } else
	// logger.info("User login failure..." + studentAccountDTOObj);
	// logger.info("Users data retrieval success..");
	// }
	// } catch (DataAccessException e) {
	// logger.error(e.getMessage(), e);
	// throw new
	// DataServiceException(DataUtils.getPropertyMessage("data_retrieval_fail"),
	// e);
	// }
	// return studentAccountDTOObj;
	// }
	//
	// @Override
	// public String insertUserPassword(String password, String emailId) throws
	// DataServiceException {
	// String hashedPassword = null;
	// int rows = 0;
	// String msg = null;
	// try {
	// hashedPassword = DataUtils.encryptPassword(password);
	// System.out.println(hashedPassword);
	// StringBuilder sb = new StringBuilder("UPDATE student_account SET
	// PASSWORD='" + hashedPassword + "'"
	// + " WHERE USERNAME='" + emailId + "'");
	// rows = dataModifier.modifyBySQL(sb.toString());
	// if (rows == 0)
	// msg = "Password not inserted...";
	// else
	// msg = "Password inserted...";
	// } catch (DataAccessException e) {
	// logger.error(e.getMessage(), e);
	// throw new
	// DataServiceException(DataUtils.getPropertyMessage("data_modifier_fail"),
	// e);
	// }
	// logger.info(msg);
	// return msg;
	// }

	@Override
	public List<StudentAccount> getActivityPointsByStudentId(Integer studentId) throws DataServiceException {
		List<StudentAccount> studentAccount = null;
		try {
			StringBuilder sb = new StringBuilder(
					"select course_enrollment_points,course_completion_points,project_enrollment_points,project_completion_points,login_points from vw_student_activity_points where student_id="
							+ studentId);
			studentAccount = dataRetriver.retrieveBySQL(sb.toString());
			logger.info("Student activity points data retrieval success..");
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			throw new DataServiceException(DataUtils.getPropertyMessage("data_retrieval_fail"), e);
		}
		return studentAccount;
	}

	@Override
	public String updatePassword(String emailId, String oldPassword, String newPassword) throws DataServiceException {
		StudentAccountDTO studentAccountDTO = null;
		int rows = 0;
		String msg = null;
		try {
			StringBuilder sb = new StringBuilder("select student_id stuId from student_account where USERNAME='"
					+ emailId + "'" + " and PASSWORD='" + oldPassword + "'");
			studentAccountDTO = (StudentAccountDTO) dataRetriver.retrieveBySQLAsObject(sb.toString(),
					StudentAccountDTO.class);
			if (studentAccountDTO == null)
				logger.info("User not exists");
			else {
				StringBuilder sb1 = new StringBuilder("UPDATE student_account SET PASSWORD='" + newPassword + "'"
						+ " WHERE USERNAME='" + emailId + "'");
				rows = dataModifier.modifyBySQL(sb1.toString());
				if (rows == 0)
					msg = "Password not updated...";
				else
					msg = "Password updated...";

			}
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			throw new DataServiceException(DataUtils.getPropertyMessage("data_modifier_fail"), e);
		}
		logger.info(msg);
		return msg;
	}

}
