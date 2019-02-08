package com.lunstudio.stocktechnicalanalysis.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.dao.CandlestickDao;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

@Service
public class CandleStickSrv {

	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private CandlestickDao candlestickDao;
	
	public List<CandlestickEntity> generateCandleStick(List<StockPriceEntity> stockPriceList) throws Exception {
		List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
		int startIndex = 3;	//Now only generate 1-day CandleStick => Start index can be 0; if 2-day CandleStick => Start Index is 1;
		BullishCandlestickPatterns bullishPatterns = new BullishCandlestickPatterns(stockPriceList);
		for(int i=startIndex; i<stockPriceList.size(); i++) {
			candlestickList.addAll(bullishPatterns.getBullishCandlestickEntityList(stockPriceList.get(i).getTradeDate()));
		}		
		return candlestickList;
	}
	
	public List<CandlestickEntity> getCandlestickList(String stockCode) throws Exception {
		return this.candlestickDao.getCandlestickList(stockCode);
	}
	
	public void saveCandlestickList(List<CandlestickEntity> candlestickList) throws Exception {
		this.candlestickDao.save(candlestickList, candlestickList.size());
		return;
	}
	
}
