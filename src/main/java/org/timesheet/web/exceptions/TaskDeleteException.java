package org.timesheet.web.exceptions;

import org.timesheet.domain.Task;

/*
 * when task can't be deleted
*/
public class TaskDeleteException extends Exception{

	private Task task;
	
	public TaskDeleteException(Task task) {
		this.task=task;
	}
	
	public Task getTask() {
		return task;
	}
}
