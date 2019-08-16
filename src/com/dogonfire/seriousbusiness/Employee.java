package com.dogonfire.seriousbusiness;

import java.util.Date;
import java.util.UUID;


public class Employee
{
	public UUID employeeId;
	public Date lastWorked;

	public Employee(UUID believerId, Date lastWorked)
	{
		this.employeeId = believerId;
		this.lastWorked = lastWorked;
	}
}