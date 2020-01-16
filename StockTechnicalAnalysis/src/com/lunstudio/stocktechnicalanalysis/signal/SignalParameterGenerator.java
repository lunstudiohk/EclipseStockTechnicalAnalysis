package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;

public class SignalParameterGenerator {

	public static List<StockSignalEntity> getEmptyParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		parameterList.add(new StockSignalEntity());
		return parameterList;
	}
	
	public static List<StockSignalEntity> getRsiRangeParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(int rsi=10; rsi<=90; rsi+=5) {
			parameter = new StockSignalEntity();
			parameter.setLowerDailyRsi(BigDecimal.valueOf(rsi));
			parameter.setUpperDailyRsi(BigDecimal.valueOf(rsi+5));
			parameterList.add(parameter);
		}
		/*
		for(int rsi=10; rsi<=90; rsi+=5) {
			parameter = new StockSignalEntity();
			parameter.setLowerDailyRsi(BigDecimal.valueOf(rsi));
			parameter.setUpperDailyRsi(BigDecimal.valueOf(rsi+10));
			parameterList.add(parameter);
		}
		*/
		return parameterList;
	}
	
	public static List<StockSignalEntity> getLowerSmaParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(BigDecimal sma : StockSignalEntity.SMA) {
			parameter = new StockSignalEntity();
			parameter.setLowerDailySma(sma);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<StockSignalEntity> getRsiTypeParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = new StockSignalEntity();
		parameter.setRsiType(StockSignalEntity.RSI_ABOVE);
		parameterList.add(parameter);
		parameter = new StockSignalEntity();
		parameter.setRsiType(StockSignalEntity.RSI_BELOW);
		parameterList.add(parameter);
		return parameterList;
	}
	
	public static List<StockSignalEntity> getSmaParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(BigDecimal sma : StockSignalEntity.SMA) {
			parameter = new StockSignalEntity();
			parameter.setUpperDailySma(sma);
			parameterList.add(parameter);
		}
		
		for(BigDecimal sma : StockSignalEntity.SMA) {
			parameter = new StockSignalEntity();
			parameter.setLowerDailySma(sma);
			parameterList.add(parameter);
		}
		return parameterList;
	}

	public static List<StockSignalEntity> getCandlestickTypeParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		Integer[] type = { 
				StockSignalEntity.CANDLESTICK_FILLED, StockSignalEntity.CANDLESTICK_HALLOW, 
				StockSignalEntity.CANDLESTICK_FILLED_GAPUP, StockSignalEntity.CANDLESTICK_FILLED_GAPDOWN,
				StockSignalEntity.CANDLESTICK_HALLOW_GAPUP, StockSignalEntity.CANDLESTICK_HALLOW_GAPDOWN
				};
		for(Integer val : type) {
			parameter = new StockSignalEntity();
			parameter.setCandlestickType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	/*
	public static List<SignalParameterEntity> getDownCandlestickTypeParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		Integer[] type = { SignalParameterEntity.CANDLESTICK_FILLED, SignalParameterEntity.CANDLESTICK_HALLOW, SignalParameterEntity.CANDLESTICK_OPENGAPDOWN, SignalParameterEntity.CANDLESTICK_HIGHGAPDOWN };
		for(Integer val : type) {
			parameter = new SignalParameterEntity();
			parameter.setCandlestickType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	*/
	public static List<StockSignalEntity> getSmaTypeParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		Integer[] type = { StockSignalEntity.SMA_SHORT_MEDIUM_LONG, StockSignalEntity.SMA_SHORT_LONG_MEDIUM, 
				StockSignalEntity.SMA_MEDIUM_SHORT_LONG, StockSignalEntity.SMA_MEDIUM_LONG_SHORT,
				StockSignalEntity.SMA_LONG_SHORT_MEDIUM, StockSignalEntity.SMA_LONG_MEDIUM_SHORT };
		for(Integer val : type) {
			parameter = new StockSignalEntity();
			parameter.setSmaType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<StockSignalEntity> getMacdTypeParameterList(Integer[] type) throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		//Integer[] type = { SignalParameterEntity.MACD_ABOVE_ZERO, SignalParameterEntity.MACD_BELOW_ZERO, SignalParameterEntity.MACD_CROSS_ZERO };
		for(Integer val : type) {
			parameter = new StockSignalEntity();
			parameter.setMacdType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<StockSignalEntity> getMacdTypeAboveDailyParameter() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = new StockSignalEntity();
		parameter.setMacdType(StockSignalEntity.MACD_ABOVE_DAILY);
		parameterList.add(parameter);
		return parameterList;
	}
	
	
	public static List<StockSignalEntity> getMacdRelativeParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		Integer[] type = { StockSignalEntity.MACD_HIGHER, StockSignalEntity.MACD_LOWER };
		for(Integer val : type) {
			parameter = new StockSignalEntity();
			parameter.setMacdType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<StockSignalEntity> getPriceNegativeDiffParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(int i=-3; i>=-20; i--) {
			parameter = new StockSignalEntity();
			parameter.setLowerPriceDiff(BigDecimal.valueOf(i));
			parameterList.add(parameter);
		}
		return parameterList;
	}

	public static List<StockSignalEntity> getPricePositiveDiffParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(int i=3; i<=20; i++) {
			parameter = new StockSignalEntity();
			parameter.setUpperPriceDiff(BigDecimal.valueOf(i));
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<StockSignalEntity> getMacdPeriodParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(int i=5; i<50; i+=5) {
			parameter = new StockSignalEntity();
			parameter.setLowerPeriod(i);
			parameterList.add(parameter);
		}
		return parameterList;
	}

	public static List<StockSignalEntity> getSmaPeriodParameterList() throws Exception {
		List<StockSignalEntity> parameterList = new ArrayList<StockSignalEntity>();
		StockSignalEntity parameter = null;
		for(int i=5; i<100; i+=5) {
			parameter = new StockSignalEntity();
			parameter.setLowerPeriod(i);
			parameterList.add(parameter);
		}
		return parameterList;
	}
}
