package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.dao.CandlestickDao;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

@Service
public class CandleStickSrv {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private CandlestickDao candlestickDao;
	
	public void deleteCandleStick(String stockCode, Date tradeDate, String priceType) throws Exception {
		this.candlestickDao.deleteCandlestick(stockCode, tradeDate, priceType);
		return;
	}
	
	public List<CandlestickEntity> generateBullishCandleStick(List<StockPriceEntity> stockPriceList) throws Exception {
		List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
		int startIndex = 2;	//Max. 3-days Candle stick pattern
		BullishCandlestickPatterns bullishPatterns = new BullishCandlestickPatterns(stockPriceList);
		for(int i=startIndex; i<stockPriceList.size(); i++) {
			candlestickList.addAll(bullishPatterns.getBullishCandlestickEntityList(stockPriceList.get(i).getTradeDate()));
		}
		return candlestickList;
	}
	
	public List<CandlestickEntity> generateBearishCandleStick(List<StockPriceEntity> stockPriceList) throws Exception {
		List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
		int startIndex = 2;	//Max. 3-days Candle stick pattern
		BearishCandlestickPatterns bearishPatterns = new BearishCandlestickPatterns(stockPriceList);
		for(int i=startIndex; i<stockPriceList.size(); i++) {
			candlestickList.addAll(bearishPatterns.getBearishCandlestickEntityList(stockPriceList.get(i).getTradeDate()));
		}
		return candlestickList;
	}
	
	public Map<Date, List<CandlestickEntity>> getCandlestickDateMapFromDate(String stockCode, Date tradeDate, String priceType) throws Exception {
		Map<Date, List<CandlestickEntity>> dateMap = new HashMap<Date, List<CandlestickEntity>>();
		List<CandlestickEntity> dataList = this.candlestickDao.getCandlestickListFromDate(stockCode, tradeDate, priceType);
		List<CandlestickEntity> tmpList = null;
		for(CandlestickEntity candlestick : dataList) {
			tmpList = dateMap.get(candlestick.getTradeDate());
			if( tmpList == null ) {
				tmpList = new ArrayList<CandlestickEntity>();
				dateMap.put(candlestick.getTradeDate(), tmpList);
			}
			tmpList.add(candlestick);
		}
		return dateMap;
	}
	
	public List<CandlestickEntity> getCandlestickListFromDate(Date tradeDate, String priceType) throws Exception {
		return this.candlestickDao.getCandlestickListFromDate(tradeDate, priceType);
	}
	
	public List<CandlestickEntity> getCandlestickList(String stockCode, String priceType) throws Exception {
		return this.candlestickDao.getCandlestickList(stockCode, priceType);
	}
	
	public void saveCandlestickList(List<CandlestickEntity> candlestickList) throws Exception {
		this.candlestickDao.save(candlestickList, candlestickList.size());
		return;
	}
	
	public Map<Date, CandlestickEntity> getCandlestickDateMap(String stockCode, String priceType, String type, Integer candlestickType) throws Exception {
		Map<Date, CandlestickEntity> dateMap = new HashMap<Date, CandlestickEntity>();
		List<CandlestickEntity> lists = this.candlestickDao.getCandlestickList(stockCode, priceType, type, candlestickType);
		for(CandlestickEntity entity : lists) {
			dateMap.put(entity.getTradeDate(), entity);
		}
		return dateMap;
	}
	
}
