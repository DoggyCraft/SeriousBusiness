package com.dogonfire.seriousbusiness;

import java.util.Date;
import java.util.UUID;


public class Employee
{
	private final UUID employeeId;
	private final Date lastWorked;

	public Employee(UUID employeeId, Date lastWorked)
	{
		this.employeeId = employeeId;
		this.lastWorked = lastWorked;
	}
	
	public UUID getEmployeeId()
	{
		return this.employeeId;
	}

	public Date getLastWorked()
	{
		return this.lastWorked;
	}
}