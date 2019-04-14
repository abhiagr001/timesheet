<%--@elvariable id="task" type="org.timesheet.domain.Task"--%>
 
<html>
<head>
    <title>Cannot delete task</title>
</head>
<body>
    Oops! Resource <a href="${task.id}">${task.description}</a> can not be deleted.
        Make sure there are no timesheets assigned on task.
 
 
<a href="../welcome">Back to main page.</a>
</body>
</html>