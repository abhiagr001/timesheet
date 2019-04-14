package org.timesheet.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.timesheet.DomainAwareBase;
import org.timesheet.domain.Employee;
import org.timesheet.domain.Manager;
import org.timesheet.domain.Task;
import org.timesheet.service.dao.EmployeeDao;
import org.timesheet.service.dao.ManagerDao;
import org.timesheet.service.dao.TaskDao;
import org.timesheet.web.exceptions.TaskDeleteException;

@ContextConfiguration(locations= {"/persistence-beans.xml", "/controllers.xml"})
public class TaskControllerTest extends DomainAwareBase{

	@Autowired
	private TaskDao taskDao;
	
	@Autowired
	private ManagerDao managerDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private TaskController controller;
	
	private Model model; //used for controller
	
	@Before
	public void setUp() {
		model= new ExtendedModelMap();
	}
	
	@After
	public void cleanUp() {
		List<Task> tasks= taskDao.list();
		for(Task task: tasks)
			taskDao.remove(task);
	}
	
	@Test
	public void testShowTask() {
		
		//prepare some data
		Task task = sampleTask();
		
		//use controller
		String view = controller.showTasks(model);
		assertEquals("tasks/list", view);
		
		List<Task> listFromDao = taskDao.list();
		Collection<?> listFromModel = (Collection<?>) model.asMap().get("tasks");
		
		assertTrue(listFromModel.contains(task));
		assertTrue(listFromDao.containsAll(listFromModel));
	}
	
	@Test
	public void testDeleteTaskOk() throws TaskDeleteException{
		
		//prepare ID to delete
		Task task = sampleTask();
		long id=task.getId();
		
		//delete and assert
		String view = controller.deleteTask(id);
		assertEquals("redirect:/tasks",view);
		assertNull(taskDao.find(id));
	}
	
	@Test(expected=TaskDeleteException.class)
	public void testDeleteTaskThrowsException() throws TaskDeleteException{
		
		//prepare ID to delete
		Task task = sampleTask();
		long id=task.getId();
		
		//mock DAO for this call
		TaskDao mockedDao=mock(TaskDao.class);
		when(mockedDao.removeTask(task)).thenReturn(false);
		
		TaskDao originalDao= controller.getTaskDao();
		try {
			//delete & expect exception
			controller.setTaskDao(mockedDao);
			controller.deleteTask(id);
		}
		finally {
			controller.setTaskDao(originalDao);
		}
	}
	
	@Test
	public void testHandleDeleteException() {
		Task task = sampleTask();
		TaskDeleteException e =new TaskDeleteException(task);
		ModelAndView modelAndView = controller.handleDeleteException(e);
		
		assertEquals("tasks/delete-error",modelAndView.getViewName());
		assertTrue(modelAndView.getModelMap().containsValue(task));
	}
	
	@Test
	public void testGetTask() {
		
		//prepare Task
		Task task = sampleTask();
		long id=task.getId();
		
		//get & assert
		String view = controller.getTask(id, model);
		assertEquals("tasks/view", view);
		assertEquals(task, model.asMap().get("task"));
	}
	
	@Test
	public void testRemoveEmployee() {
		
		//prepare task
		Task task = sampleTask();
		long id=task.getAssignedEmployees().get(0).getId();
		controller.removeEmployee(task.getId(), id);
		
		//task was updated inside controller in other transaction
		task=taskDao.find(task.getId());
		
		//get employee and assert
		Employee employee=employeeDao.find(id);
		assertFalse(task.getAssignedEmployees().contains(employee));
	}
	
	@Test
	public void testAddEmployee() {
		Task task = sampleTask();
		Employee cassidy=new Employee("Butch Cassidy", "Cowboy");
		employeeDao.add(cassidy);
		controller.addEmployee(task.getId(), cassidy.getId());
		
		//task was updated inside controller in other transaction
		task=taskDao.find(task.getId());
		
		//get employee and assert
		Employee employee =employeeDao.find(cassidy.getId());
		assertTrue(task.getAssignedEmployees().contains(employee));
	}
	
	@Test
	public void testAddTask() {
		
		//prepare task
		Task task = sampleTask();
		
		//save but via controller
		String view=controller.addTask(task);
		assertEquals("redirect:/tasks",view);
		
		//task is stored in DB
		assertEquals(task,taskDao.find(task.getId()));
	}
	
	private Task sampleTask() {
		Manager manager = new Manager("Jesse James");
		managerDao.add(manager);
		
		Employee terrence= new Employee("Terrence", "Cowboys");
		Employee kid= new Employee("Sundance Kid", "Cowboys");
		employeeDao.add(terrence);
		employeeDao.add(kid);
		
		Task task = new Task ("Wild West", manager, terrence, kid);
		taskDao.add(task);
		
		return task;
	}
}
