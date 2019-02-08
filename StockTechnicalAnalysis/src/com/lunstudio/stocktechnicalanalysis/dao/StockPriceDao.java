package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;


public interface StockPriceDao extends BaseDao{

	public StockPriceEntity getStockPrice(String stockCode, Date tradeDate, String priceType);
	
	public StockPriceEntity getPreviousStockPrice(String stockCode, Date tradeDate, String priceType);

	public List<StockPriceEntity> getLastStockPriceList(String stockCode, Integer period, String priceType);
	
	public List<StockPriceEntity> getStockPriceEntityListInDate(String stockCode, Date startDate, Date endDate, String priceType);

	public List<StockPriceEntity> getStockPriceList(Date tradeDate, String priceType);

}
