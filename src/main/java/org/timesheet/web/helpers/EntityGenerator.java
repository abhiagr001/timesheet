package org.timesheet.web.helpers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.stereotype.Service;
import org.timesheet.domain.Employee;
import org.timesheet.domain.Manager;
import org.timesheet.domain.Task;
import org.timesheet.domain.Timesheet;
import org.timesheet.service.GenericDao;
import org.timesheet.service.dao.EmployeeDao;
import org.timesheet.service.dao.ManagerDao;
import org.timesheet.service.dao.TaskDao;
import org.timesheet.service.dao.TimesheetDao;

/**
 * Small util helper for generating entities to simulate real system.
 */
@Service
public class EntityGenerator {

	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private ManagerDao managerDao;
	
	@Autowired
	private TaskDao taskDao;
	
	@Autowired
	private TimesheetDao timesheetDao;
	
	public void generateDomain() {
		Employee steve=new Employee("Steve","Design");
		Employee bill=new Employee("Bill","Marketing");
		Employee linus=new Employee("Linus","Programming");
		
		//free employees(no tasks/timesheets)
		Employee john=new Employee("John","Beatles");
		Employee george=new Employee("George","Beatles");
		Employee ringo=new Employee("Ringo","Beatles");
		Employee paul=new Employee("Paul","Beatles");
		
		Manager eric=new Manager("Eric");
		Manager larry=new Manager("Larry");
		
		//free managers
		Manager simon=new Manager("Simon");
		Manager garfunkel=new Manager("Garfunkel");
		
		addAll(employeeDao,steve,bill,linus,john,george,ringo,paul);
		addAll(managerDao,eric,larry,simon,garfunkel);
		
		Task springTask=new Task("migration to spring",eric,steve,linus);
		Task tomcatTask=new Task("optimizing tomcat",eric,bill);
		Task centosTask=new Task("deploying to CentOS",larry,linus);
		
		addAll(taskDao,springTask,tomcatTask,centosTask);
		
		Timesheet linusOnSpring= new Timesheet(linus,springTask,42);
		Timesheet billOnTomcat= new Timesheet(bill,tomcatTask,30);
		
		addAll(timesheetDao,linusOnSpring,billOnTomcat);
	}
	
	public void deleteDomain() {
		List<Timesheet> timesheets=timesheetDao.list();
		for(Timesheet timesheet:timesheets)
			timesheetDao.remove(timesheet);
		
		List<Task> tasks=taskDao.list();
		for(Task task:tasks)
			taskDao.remove(task);
		
		List<Employee> employees=employeeDao.list();
		for(Employee employee:employees)
			employeeDao.remove(employee);
		
		List<Manager> managers=managerDao.list();
		for(Manager manager:managers)
			managerDao.remove(manager);
	}
	
	private <T> void addAll(GenericDao<T,Long> dao, T... entities) {
		for(T o:entities)
			dao.add(o);
	}
}
