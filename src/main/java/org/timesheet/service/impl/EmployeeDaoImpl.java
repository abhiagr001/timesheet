package org.timesheet.service.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.timesheet.domain.Employee;
import org.timesheet.service.dao.EmployeeDao;

@Repository
public class EmployeeDaoImpl extends HibernateDao<Employee, Long> implements EmployeeDao{

	@Override
	public boolean removeEmployee(Employee employee) {
		Query employeeTaskQuery=currentSession().createQuery(
				"from Task t where :id in elments(t.assignedEmployees)");
		employeeTaskQuery.setParameter("id", employee.getId());
		
		//employee must not be assigned to any task
		if(!employeeTaskQuery.list().isEmpty())
			return false;
		
		Query employeeTimesheetQuery=currentSession().createQuery(
				"from Timesheet t where t.who.id=:id");
		employeeTimesheetQuery.setParameter("id", employee.getId());
		
		//employee must not be assigned to any timesheet
		if(!employeeTimesheetQuery.list().isEmpty())
			return false;
		
		//ok, remove as usual
		remove(employee);
		return true;
	}

	
}