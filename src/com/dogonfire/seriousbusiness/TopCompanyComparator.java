package com.dogonfire.seriousbusiness;

import java.util.Comparator;


public class TopCompanyComparator implements Comparator<CompanyStockValue>
{
	public TopCompanyComparator()
	{
	}

	public int compare(CompanyStockValue object1, CompanyStockValue object2)
	{
		CompanyStockValue g1 = (CompanyStockValue) object1;
		CompanyStockValue g2 = (CompanyStockValue) object2;

		return (int) (g2.stockValue - g1.stockValue);
	}
}