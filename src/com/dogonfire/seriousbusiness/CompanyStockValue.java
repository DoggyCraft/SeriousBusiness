package com.dogonfire.seriousbusiness;

import java.util.UUID;


public class CompanyStockValue
{
	public double stockValue;
	public double stockChange;
	public UUID companyId;
	public int numberOfEmployees;

	public CompanyStockValue(UUID companyId, double stockValue, double stockChange, int numberOfEmployees)
	{
		this.stockValue = stockValue;
		this.stockChange = stockChange;
		this.companyId = companyId;
		this.numberOfEmployees = numberOfEmployees;
	}
}