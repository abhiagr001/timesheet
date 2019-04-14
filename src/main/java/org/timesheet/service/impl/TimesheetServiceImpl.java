package org.timesheet.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.timesheet.domain.Employee;
import org.timesheet.domain.Manager;
import org.timesheet.domain.Task;
import org.timesheet.service.TimesheetService;
import org.timesheet.service.dao.TaskDao;

@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
@Service("timsheetService")
public class TimesheetServiceImpl implements TimesheetService{

	//dependencies
	private SessionFactory sessionFactory;
	private TaskDao taskDao;
	
	private Random random=new Random();

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public TaskDao getTaskDao() {
		return taskDao;
	}

	@Autowired
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Task busiestTask() {
		List<Task> tasks=taskDao.list();
		if(tasks.isEmpty())
			return null;
		
		Task busiest =tasks.get(0);
		for(Task task:tasks) {
			if(task.getAssignedEmployees().size()>busiest.getAssignedEmployees().size())
				busiest=task;
		}
		return busiest;
	}

	@Override
	public List<Task> tasksForEmployee(Employee employee) {
		List<Task> allTasks=taskDao.list();
		List<Task> tasksForEmployee=new ArrayList<Task>();
		
		for(Task task:allTasks) {
			if(task.getAssignedEmployees().contains(employee))
				tasksForEmployee.add(task);
		}
			
		return tasksForEmployee;
	}

	@Override
	public List<Task> tasksForManager(Manager manager) {
		
		Query query = currentSession()
                .createQuery("from Task t where t.manager.id = :id");
		query.setParameter("id", manager.getId());
		return query.list();
	}

	
}
