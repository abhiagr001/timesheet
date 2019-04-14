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
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.timesheet.DomainAwareBase;
import org.timesheet.domain.Employee;
import org.timesheet.domain.Manager;
import org.timesheet.domain.Task;
import org.timesheet.domain.Timesheet;
import org.timesheet.service.dao.EmployeeDao;
import org.timesheet.service.dao.ManagerDao;
import org.timesheet.service.dao.TaskDao;
import org.timesheet.service.dao.TimesheetDao;
import org.timesheet.web.commands.TimesheetCommand;
import org.timesheet.web.exceptions.TaskDeleteException;

@ContextConfiguration(locations= {"/persistence-beans.xml", "/controllers.xml"})
public class TimesheetControllerTest extends DomainAwareBase{

	@Autowired
	private TaskDao taskDao;
	
	@Autowired
	private ManagerDao managerDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private TimesheetController controller;
	
	@Autowired
	private TimesheetDao timesheetDao;
	
	private Model model; //used for controller
	
	@Before
	public void setUp() {
		model= new ExtendedModelMap();
	}
		
	@Test
	public void testShowTimesheets() {
		
		//prepare some data
		Timesheet timesheet = sampleTimesheet();
		
		//use controller
		String view = controller.showTimesheets(model);
		assertEquals("timesheets/list", view);
		
		List<Timesheet> listFromDao = timesheetDao.list();
		Collection<?> listFromModel = (Collection<?>) model.asMap().get("timesheets");
		
		assertTrue(listFromModel.contains(timesheet));
		assertTrue(listFromDao.containsAll(listFromModel));
	}
	
	@Test
	public void testDeleteTimesheet() {
		
		//prepare ID to delete
		Timesheet timesheet = sampleTimesheet();
		timesheetDao.add(timesheet);
		long id=timesheet.getId();
		
		//delete and assert
		String view = controller.deleteTimesheet(id);
		assertEquals("redirect:/timesheets",view);
		assertNull(timesheetDao.find(id));
	}
		
	@Test
	public void testGetTimesheet() {
		
		//prepare Timesheet
		Timesheet timesheet = sampleTimesheet();
		timesheetDao.add(timesheet);
		long id=timesheet.getId();
		TimesheetCommand tsCommand=new TimesheetCommand(timesheet);

		//get & assert
		String view = controller.getTimesheet(id, model);
		assertEquals("timesheets/view", view);
		assertEquals(timesheet, model.asMap().get("tsCommand"));
	}
	
	@Test
	public void testUpdateTimesheetValid() {
		
		//prepare ID to delete
		Timesheet timesheet=sampleTimesheet();
		timesheetDao.add(timesheet);
		long id=timesheet.getId();
		TimesheetCommand tsCommand=new TimesheetCommand(timesheet);
		
		//user alters Timesheet hours in HTML form with valid value
		tsCommand.setHours(1337);
		BindingResult result=mock(BindingResult.class);
		when(result.hasErrors()).thenReturn(false);
		
		//update and assert
		String view=controller.updateTimesheet(id, tsCommand, result);
		assertEquals("redirect:/timesheets", view);
		assertTrue(1337==timesheetDao.find(id).getHours());
	}
	
	@Test
	public void testUpdateTimesheetInValid() {
		
		//prepare ID to delete
		Timesheet timesheet=sampleTimesheet();
		timesheetDao.add(timesheet);
		long id=timesheet.getId();
		TimesheetCommand tsCommand=new TimesheetCommand(timesheet);
		Integer originalHours=tsCommand.getHours();
		
		//user alters Timesheet hours in HTML form with invalid value
		tsCommand.setHours(-1);
		BindingResult result=mock(BindingResult.class);
		when(result.hasErrors()).thenReturn(true);
		
		//update and assert
		String view=controller.updateTimesheet(id, tsCommand, result);
		assertEquals("redirect:/timesheets", view);
		assertEquals(originalHours,timesheetDao.find(id).getHours());
	}
			
	@Test
	public void testAddTimesheet() {
		
		//prepare timesheet
		Timesheet timesheet = sampleTimesheet();
		
		//save but via controller
		String view=controller.addTimesheet(timesheet);
		assertEquals("redirect:/timesheets",view);
		
		//timesheet is stored in DB
		assertEquals(timesheet,timesheetDao.find(timesheet.getId()));
	}
	
	private Timesheet sampleTimesheet() {
		Manager jeremy = new Manager("Jeremy");
		managerDao.add(jeremy);
		
		Employee marty= new Employee("Martin Brodeur", "NHL");
		employeeDao.add(marty);
		
		Task winStanleyCup=new Task("NHL finals", jeremy, marty);
		taskDao.add(winStanleyCup);
		
		Timesheet stanleyCupSheet= new Timesheet (marty, winStanleyCup, 100);
		timesheetDao.add(stanleyCupSheet);
		
		return stanleyCupSheet;
	}
}
