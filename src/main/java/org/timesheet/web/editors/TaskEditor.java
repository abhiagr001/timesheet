package org.timesheet.web.editors;

import java.beans.PropertyEditorSupport;

import org.timesheet.domain.Task;
import org.timesheet.service.dao.TaskDao;

public class TaskEditor extends PropertyEditorSupport{

	private TaskDao taskDao;
	
	public TaskEditor(TaskDao taskDao) {
		this.taskDao=taskDao;
	}
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException{
		long id=Long.parseLong(text);
		Task task =taskDao.find(id);
		setValue(task);
	}
}
