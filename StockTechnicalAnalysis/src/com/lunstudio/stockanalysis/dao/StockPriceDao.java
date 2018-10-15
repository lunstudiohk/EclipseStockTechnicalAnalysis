package com.lunstudio.stockanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stockanalysis.entity.StockPriceEntity;
import com.lunstudio.stockanalysis.entity.StockTradeEntity;

public interface StockPriceDao extends BaseDao{


	public StockPriceEntity findByStockTradeDate(String stockCode, Date tradeDate);
	public StockPriceEntity findByStockTradeDate(String stockCode, Date tradeDate, String priceType);
	
	public void deleteByStockCode(String stockCode);
	
	/**
	 * Get the last stock price entity
	 * @param stockCode
	 * @return
	 */
	public StockPriceEntity getLatestStockPriceEntity(String stockCode);
	public StockPriceEntity getLatestStockPriceEntity(String stockCode, String priceType);
	
	public List<StockPriceEntity> getStockPriceAfterTradeDate(String stockCode, Date tradeDate);
	public List<StockPriceEntity> getStockPriceAfterTradeDate(String stockCode, Date tradeDate, String priceType);
	
	/**
	 * Get stock price list in descending order
	 * @param stockCode
	 * @param count
	 * @return
	 */
	public List<StockPriceEntity> getStockPriceListInDesc(String stockCode, Integer count);
	public List<StockPriceEntity> getStockPriceListInDesc(String stockCode, Integer count, String priceType);
	
	/**
	 * Get stock price list in ascending order
	 * @param stockCode
	 * @param count
	 * @return
	 */
	public List<StockPriceEntity> getStockPriceListInAsc(String stockCode, Integer count);
	public List<StockPriceEntity> getStockPriceListInAsc(String stockCode, Integer count, String priceType);	

	/**
	 * Get the nth stock price in desc 
	 * @param stockCode
	 * @param period nth stock price
	 * @return
	 */
	public StockPriceEntity getLastStockPriceInPeriod(String stockCode, Integer period);
	public StockPriceEntity getLastStockPriceInPeriod(String stockCode, Integer period, String priceType);
	
	/**
	 * Get last stock price list in ascending order 
	 * @param stockCode
	 * @param period nth stock price
	 * @return
	 */
	public List<StockPriceEntity> getLastStockPriceListInAsc(String stockCode, Integer period);
	public List<StockPriceEntity> getLastStockPriceListInAsc(String stockCode, Integer period, String priceType);
	
	/**
	 * Get number of trade date within the specified date
	 * @param stockCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Long getTradeDateCount(String stockCode, Date startDate, Date endDate);
	public Long getTradeDateCount(String stockCode, Date startDate, Date endDate, String priceType);

	public List<Date> getLastTradeDateList(Date endDate, Integer count);
	public List<Date> getLastTradeDateList(Date endDate, Integer count, String priceType);
	
	public List<StockPriceEntity> getStockPriceEntityListInDate(Date startDate, Date endDate);
	public List<StockPriceEntity> getStockPriceEntityListInDate(Date startDate, Date endDate, String priceType);
	public List<StockPriceEntity> getStockPriceEntityListInDate(String stockCode, Date startDate, Date endDate, String priceType);
	
	public List<StockPriceEntity> getZeroStockPriceList();
}
