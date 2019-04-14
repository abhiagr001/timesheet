package org.timesheet.web.exceptions;

import org.timesheet.domain.Employee;

/*
 * when employee can't be deleted
*/
public class EmployeeDeleteException extends Exception{

	private Employee employee;
	
	public EmployeeDeleteException(Employee employee) {
		this.employee=employee;
	}
	
	public Employee getEmployee() {
		return employee;
	}
}
