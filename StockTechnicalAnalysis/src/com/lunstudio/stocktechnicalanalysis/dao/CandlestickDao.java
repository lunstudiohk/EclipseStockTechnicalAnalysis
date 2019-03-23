package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;

public interface CandlestickDao extends BaseDao {

	public List<CandlestickEntity> getCandlestickList(String stockCode) throws Exception;
	
	public void deleteCandlestick(String stockCode, Date tradeDate) throws Exception;
	
	public List<CandlestickEntity> getCandlestickListFromDate(Date tradeDate) throws Exception;

}
