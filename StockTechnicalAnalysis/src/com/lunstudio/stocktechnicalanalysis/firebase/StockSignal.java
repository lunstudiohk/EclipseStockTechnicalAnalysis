package com.lunstudio.stocktechnicalanalysis.firebase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;

public class StockSignal extends FirebaseData {
	
	private Map<String, Object> data = null;
	
	public Map<String, Object> getData() {
		return this.data;
	}

	public StockSignal(String stockCode, Date date) {
		data = new HashMap<String, Object>();
		data.put("stock", stockCode);
		data.put("date", date.toString());
		data.put("signal", new ArrayList<Object>());
		return;
	}

	@SuppressWarnings("unchecked")
	public void addSignal(StockSignalEntity entity) {
		Map<String, Object> signal = new HashMap<String, Object>();
		signal.put("tradeType", entity.getSignalType());
		signal.put("signalType", entity.getType());
		signal.put("target", entity.getTargetReturn().doubleValue());
		signal.put("confident", entity.getConfident().doubleValue());
		signal.put("count", entity.getCount());
		
		Map<String, Object> up = new HashMap<String, Object>();
		up.put("min", entity.getUpperMin().doubleValue());
		up.put("median", entity.getUpperMedian().doubleValue());
		up.put("max", entity.getUpperMax().doubleValue());
		up.put("day", entity.getUpperDayMedian());
		signal.put("up", up);
		
		Map<String, Object> low = new HashMap<String, Object>();
		low.put("min", entity.getLowerMax().doubleValue());
		low.put("median", entity.getLowerMedian().doubleValue());
		low.put("max", entity.getLowerMin().doubleValue());
		low.put("day", entity.getLowerDayMedian().doubleValue());
		signal.put("low", low);
		
		Map<String, Object> rsi = new HashMap<String, Object>();
		if( entity.getUpperDailyRsi() != null ) {
			rsi.put("up", entity.getUpperDailyRsi().doubleValue());
		}
		if( entity.getLowerDailyRsi() != null ) {
			rsi.put("low", entity.getLowerDailyRsi().doubleValue());
		}
		signal.put("rsi", rsi);
		
		Map<String, Object> sma = new HashMap<String, Object>();
		if( entity.getUpperDailySma() != null ) {
			sma.put("up", entity.getUpperDailySma().intValue());
		}
		if( entity.getLowerDailySma() != null ) {
			sma.put("low", entity.getLowerDailySma().intValue());
		}
		signal.put("sma", sma);
		
		Map<String, Object> period = new HashMap<String, Object>();
		if( entity.getUpperPeriod() != null ) {
			period.put("up", entity.getUpperPeriod().intValue());
		}
		if( entity.getLowerPeriod() != null ) {
			period.put("low", entity.getLowerPeriod().intValue());
		}
		signal.put("period", period);
		
		Map<String, Object> type = new HashMap<String, Object>();
		type.put("macd", entity.getMacdType());
		type.put("sma", entity.getSmaType());
		type.put("rsi", entity.getRsiType());
		type.put("candlestick", entity.getCandlestickType());
		signal.put("type", type);
		
		Map<String, Object> price = new HashMap<String, Object>();
		if( entity.getUpperPriceDiff() != null ) {
			price.put("up", entity.getUpperPriceDiff().doubleValue());
		}
		if( entity.getLowerPriceDiff() != null ) {
			price.put("low", entity.getLowerPriceDiff().doubleValue());
		}
		signal.put("price", price);
		
		List<Object> signalDateList = new ArrayList<Object>();
		
		for(StockSignalDateEntity signalDate : entity.getStockSignalDateList()) {
			Map<String, Object> dateDetail = new HashMap<String, Object>();
			dateDetail.put("tradeDate", signalDate.getSignalDate().toString());
			dateDetail.put("price", signalDate.getSignalPrice().doubleValue());
			if( signalDate.getHighReturn() != null ) {
				dateDetail.put("highreturn", signalDate.getHighReturn().doubleValue());
			}
			if( signalDate.getHighDay() != null ) {
				dateDetail.put("highday", signalDate.getHighDay());
			}
			if( signalDate.getLowReturn() != null ) {
				dateDetail.put("lowreturn", signalDate.getLowReturn().doubleValue());
			}
			if( signalDate.getLowDay() != null ) {
				dateDetail.put("lowday", signalDate.getLowDay());
			}
			signalDateList.add(dateDetail);
		}
		signal.put("signalDate", signalDateList);		
		((ArrayList<Object>)data.get("signal")).add(signal);
		return;
	}
	
}
