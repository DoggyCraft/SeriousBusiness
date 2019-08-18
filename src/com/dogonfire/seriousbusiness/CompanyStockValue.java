package com.dogonfire.seriousbusiness;

import java.util.UUID;


public class CompanyStockValue
{
	private final double stockValue;
	private final double stockChange;
	private final UUID companyId;
	private final int numberOfEmployees;

	public CompanyStockValue(UUID companyId, double stockValue, double stockChange, int numberOfEmployees)
	{
		this.stockValue = stockValue;
		this.stockChange = stockChange;
		this.companyId = companyId;
		this.numberOfEmployees = numberOfEmployees;
	}
	
	public UUID getCompanyId()
	{
		return this.companyId;
	}

	public double getStockValue()
	{
		return this.stockValue;
	}

	public double getStockChange()
	{
		return this.stockChange;
	}

	public double getNumberOfEmployees()
	{
		return this.numberOfEmployees;
	}
}