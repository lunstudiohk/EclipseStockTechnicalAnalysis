package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;

public interface CandlestickDao extends BaseDao {

	public List<CandlestickEntity> getCandlestickList(String stockCode, String priceType) throws Exception;
	
	public List<CandlestickEntity> getCandlestickListByType(String stockCode, String type) throws Exception;
	
	public List<CandlestickEntity> getCandlestickList(String stockCode, String priceType, String type, Integer candlestickType) throws Exception;
	
	public void deleteCandlestick(String stockCode, Date tradeDate, String priceType) throws Exception;
	
	public List<CandlestickEntity> getCandlestickListFromDate(Date tradeDate, String priceType) throws Exception;
	
	public List<CandlestickEntity> getCandlestickListFromDate(String stockCode, Date tradeDate, String priceType) throws Exception;

}
