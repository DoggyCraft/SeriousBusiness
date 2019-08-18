package com.dogonfire.seriousbusiness;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;


public class EmployeesComparator implements Comparator<Employee>
{
	public EmployeesComparator()
	{
	}

	public int compare(Employee object1, Employee object2)
	{
		if(object1==null)
		{
			return 1; 
		}
		
		if(object2==null)
		{
			return -1; 
		}

		Employee b1 = (Employee) object1;
		Employee b2 = (Employee) object2;

		return (int) (b2.getLastWorked().getTime() - b1.getLastWorked().getTime());
	}
}