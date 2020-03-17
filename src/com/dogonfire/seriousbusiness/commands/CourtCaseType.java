package com.dogonfire.seriousbusiness.commands;


public enum CourtCaseType
{
	FreeForm,			   // Any freeform accusation with 
	Spamming,		 	   // Company has chatted an sentence amount exceeding policy max
	SalesTaxFraud,		   // Company has moved money between lands to avoid sales taxes
	StockManipulation,     // Company has hoarded and instantly sold/bought more items exceeding policy max in order to manipulate stock value
	LoanSharking,   	   // Company has issued loans with rates exceeding policy max		
	TaxAvoidance,   	   // Company has invested in cryptocurrency/items in order to avoid taxes		
	TradingIllegalItems,   // Company has bought or sold illegal items in a land
	//IllegalTrademarks    // Company has issued too many active patents
}
