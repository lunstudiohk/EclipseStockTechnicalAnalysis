package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;

public interface CandlestickPattern {

	public boolean isValid(Date tradeDate) throws Exception;
	
	public CandlestickEntity getCandlestickEntity() throws Exception;
	
}
