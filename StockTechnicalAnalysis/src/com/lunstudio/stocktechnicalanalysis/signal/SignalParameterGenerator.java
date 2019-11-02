package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.SignalParameterEntity;

public class SignalParameterGenerator {

	public static List<SignalParameterEntity> getEmptyParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		parameterList.add(new SignalParameterEntity());
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getRsiRangeParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(int rsi=10; rsi<=90; rsi+=5) {
			parameter = new SignalParameterEntity();
			parameter.setLowerDailyRsi(BigDecimal.valueOf(rsi));
			parameter.setUpperDailyRsi(BigDecimal.valueOf(rsi+5));
			parameterList.add(parameter);
		}
		for(int rsi=10; rsi<=90; rsi+=5) {
			parameter = new SignalParameterEntity();
			parameter.setLowerDailyRsi(BigDecimal.valueOf(rsi));
			parameter.setUpperDailyRsi(BigDecimal.valueOf(rsi+10));
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getLowerSmaParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(BigDecimal sma : SignalParameterEntity.SMA) {
			parameter = new SignalParameterEntity();
			parameter.setLowerDailySma(sma);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getRsiTypeParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = new SignalParameterEntity();
		parameter.setRsiType(SignalParameterEntity.RSI_ABOVE);
		parameterList.add(parameter);
		parameter = new SignalParameterEntity();
		parameter.setRsiType(SignalParameterEntity.RSI_BELOW);
		parameterList.add(parameter);
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getSmaParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(BigDecimal sma : SignalParameterEntity.SMA) {
			parameter = new SignalParameterEntity();
			parameter.setUpperDailySma(sma);
			parameterList.add(parameter);
		}
		
		for(BigDecimal sma : SignalParameterEntity.SMA) {
			parameter = new SignalParameterEntity();
			parameter.setLowerDailySma(sma);
			parameterList.add(parameter);
		}
		return parameterList;
	}

	public static List<SignalParameterEntity> getCandlestickTypeParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		Integer[] type = { 
				SignalParameterEntity.CANDLESTICK_FILLED, SignalParameterEntity.CANDLESTICK_HALLOW, 
				SignalParameterEntity.CANDLESTICK_FILLED_GAPUP, SignalParameterEntity.CANDLESTICK_FILLED_GAPDOWN,
				SignalParameterEntity.CANDLESTICK_HALLOW_GAPUP, SignalParameterEntity.CANDLESTICK_HALLOW_GAPDOWN
				};
		for(Integer val : type) {
			parameter = new SignalParameterEntity();
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
	public static List<SignalParameterEntity> getSmaTypeParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		Integer[] type = { SignalParameterEntity.SMA_SHORT_MEDIUM_LONG, SignalParameterEntity.SMA_SHORT_LONG_MEDIUM, 
				SignalParameterEntity.SMA_MEDIUM_SHORT_LONG, SignalParameterEntity.SMA_MEDIUM_LONG_SHORT,
				SignalParameterEntity.SMA_LONG_SHORT_MEDIUM, SignalParameterEntity.SMA_LONG_MEDIUM_SHORT };
		for(Integer val : type) {
			parameter = new SignalParameterEntity();
			parameter.setSmaType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getMacdTypeParameterList(Integer[] type) throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		//Integer[] type = { SignalParameterEntity.MACD_ABOVE_ZERO, SignalParameterEntity.MACD_BELOW_ZERO, SignalParameterEntity.MACD_CROSS_ZERO };
		for(Integer val : type) {
			parameter = new SignalParameterEntity();
			parameter.setMacdType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getMacdTypeAboveDailyParameter() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = new SignalParameterEntity();
		parameter.setMacdType(SignalParameterEntity.MACD_ABOVE_DAILY);
		parameterList.add(parameter);
		return parameterList;
	}
	
	
	public static List<SignalParameterEntity> getMacdRelativeParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		Integer[] type = { SignalParameterEntity.MACD_HIGHER, SignalParameterEntity.MACD_LOWER };
		for(Integer val : type) {
			parameter = new SignalParameterEntity();
			parameter.setMacdType(val);
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getPriceNegativeDiffParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(int i=-3; i>=-20; i--) {
			parameter = new SignalParameterEntity();
			parameter.setLowerPriceDiff(BigDecimal.valueOf(i));
			parameterList.add(parameter);
		}
		return parameterList;
	}

	public static List<SignalParameterEntity> getPricePositiveDiffParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(int i=3; i<=20; i++) {
			parameter = new SignalParameterEntity();
			parameter.setUpperPriceDiff(BigDecimal.valueOf(i));
			parameterList.add(parameter);
		}
		return parameterList;
	}
	
	public static List<SignalParameterEntity> getMacdPeriodParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(int i=5; i<50; i+=5) {
			parameter = new SignalParameterEntity();
			parameter.setLowerPeriod(i);
			parameterList.add(parameter);
		}
		return parameterList;
	}

	public static List<SignalParameterEntity> getSmaPeriodParameterList() throws Exception {
		List<SignalParameterEntity> parameterList = new ArrayList<SignalParameterEntity>();
		SignalParameterEntity parameter = null;
		for(int i=5; i<50; i+=5) {
			parameter = new SignalParameterEntity();
			parameter.setLowerPeriod(i);
			parameterList.add(parameter);
		}
		return parameterList;
	}
}
