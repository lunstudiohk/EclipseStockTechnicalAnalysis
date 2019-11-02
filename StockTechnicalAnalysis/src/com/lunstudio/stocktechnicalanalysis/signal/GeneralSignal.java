package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.SignalParameterEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public abstract class GeneralSignal {
	
	private static final Logger logger = LogManager.getLogger();

	protected StockEntity stock;
	protected List<StockPriceVo> stockPriceVoList;
	protected String signalType;
	protected Integer type;
	protected String priceType;
	protected Integer period;
	protected Map<Date,BigDecimal> refPriceDateMap;
	protected List<CandlestickEntity> candlestickList;
	
	public abstract List<SignalParameterEntity> getSignalParameterList() throws Exception;
	
	public abstract SignalParameterEntity findInvalidSignal(SignalParameterEntity signal1, SignalParameterEntity signal2) throws Exception;
	
	public abstract boolean isValid(SignalParameterEntity parameter, Integer tradeIndex) throws Exception;

	public GeneralSignal(StockEntity stock, String priceType, Map<Date,BigDecimal> refPriceDateMap, List<StockPriceVo> stockPriceVoList, String signalType, Integer type) throws Exception{
		this.stock = stock;
		this.stockPriceVoList = stockPriceVoList;
		this.signalType = signalType;
		this.type = type;
		this.refPriceDateMap = refPriceDateMap;
		this.priceType = priceType;
		return;
	}
		
	protected List<Integer> getTradeIndexList(SignalParameterEntity signal) throws Exception {
		List<Integer> tradeIndexList = new ArrayList<Integer>();
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if( this.isValid(signal, i) ) {
				tradeIndexList.add(i);
			}
		}
		return tradeIndexList;
	}
	
	protected List<SignalParameterEntity> filterInvalidSignal(List<SignalParameterEntity> signalList) throws Exception {
		List<SignalParameterEntity> finalList = new ArrayList<SignalParameterEntity>();
		for(int i=0; i<signalList.size()-1; i++) {
			SignalParameterEntity signal1 = signalList.get(i);
			if( signal1.getStockCode() != null ) {
				for(int j=i+1; j<signalList.size(); j++) {
					SignalParameterEntity signal2 = signalList.get(j);
					if( signal2.getStockCode() != null ) {
						if( signal1.isSame(signal2) ) {
							SignalParameterEntity signal = this.findInvalidSignal(signal1, signal2);
							if( signal != null ) {
								signal.setStockCode(null);
							}
						}
					}
				}
			}
		}
		for(SignalParameterEntity signal : signalList) {
			if( signal.getStockCode() != null ) {
				finalList.add(signal);
			}
		}
		return finalList;
	}
	
	protected List<SignalParameterEntity> getValidSignalList(List<SignalParameterEntity> signalList) throws Exception {
		List<SignalParameterEntity> list = new ArrayList<SignalParameterEntity>();
		for(SignalParameterEntity signal : signalList ) {
			if( this.isValidSignal(signal) ) {
				list.add(signal);
			}
		}
		return this.filterInvalidSignal(list);
	}
	
	private boolean isValidSignal(SignalParameterEntity signal) throws Exception {
		List<Integer> tradeIndexList = new ArrayList<Integer>();
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if( this.isValid(signal, i) ) {
				tradeIndexList.add(i);
			}
		}
		if( tradeIndexList.isEmpty() ) {
			return false;
		}
		DescriptiveStatistics maxPeriodStats = new DescriptiveStatistics();
		DescriptiveStatistics maxStats = new DescriptiveStatistics();
		DescriptiveStatistics minPeriodStats = new DescriptiveStatistics();
		DescriptiveStatistics minStats = new DescriptiveStatistics();
		Date tradeDate = null;
		for(int tradeIndex: tradeIndexList) {
			BigDecimal min = BigDecimal.valueOf(999);
			BigDecimal max = BigDecimal.valueOf(-999);
			int maxPeriod = -1;
			int minPeriod = -1;
			if( tradeIndex + signal.getPeriod() < this.stockPriceVoList.size() ) {
				BigDecimal currentPrice = this.stockPriceVoList.get(tradeIndex).getClosePrice();
				for(int i=1; i<=signal.getPeriod(); i++) {
					BigDecimal high = this.stockPriceVoList.get(tradeIndex+i).getDayHigh();
					BigDecimal low = this.stockPriceVoList.get(tradeIndex+i).getDayLow();
					BigDecimal highDiff = MathUtils.getPriceDiff(currentPrice, high, 2);
					BigDecimal lowDiff = MathUtils.getPriceDiff(currentPrice, low, 2);
					
					if( min.compareTo(lowDiff) > 0 ) {
						min = lowDiff;
						minPeriod = i;
					}
					if( max.compareTo(highDiff) < 0 ) {
						max = highDiff;
						maxPeriod = i;
					}
				}
				
				maxStats.addValue(max.doubleValue());
				maxPeriodStats.addValue(maxPeriod);
				minStats.addValue(min.doubleValue());
				minPeriodStats.addValue(minPeriod);
			}
			
			tradeDate = this.stockPriceVoList.get(tradeIndex).getTradeDate();
		}
		
		if( this.isMeetCriteria(maxStats, minStats) ) {
			signal.setStockCode(this.stock.getStockCode());
			signal.setSignalType(this.signalType);
			signal.setType(this.type);
			signal.setPeriod(this.period);
			signal.setPriceType(this.priceType);
			signal.setCount((int)maxStats.getN());
			signal.setUpperMax(BigDecimal.valueOf(maxStats.getMax()).setScale(2, RoundingMode.HALF_UP));
			signal.setUpperMin(BigDecimal.valueOf(maxStats.getMin()).setScale(2, RoundingMode.HALF_UP));
			signal.setUpperMedian(BigDecimal.valueOf(maxStats.getPercentile(50)).setScale(2, RoundingMode.HALF_UP));
			signal.setUpperDayMedian(BigDecimal.valueOf(maxPeriodStats.getPercentile(50)).setScale(0, RoundingMode.HALF_UP));
			signal.setLowerMax(BigDecimal.valueOf(minStats.getMax()).setScale(2, RoundingMode.HALF_UP));
			signal.setLowerMin(BigDecimal.valueOf(minStats.getMin()).setScale(2, RoundingMode.HALF_UP));
			signal.setLowerMedian(BigDecimal.valueOf(minStats.getPercentile(50)).setScale(2, RoundingMode.HALF_UP));
			signal.setLowerDayMedian(BigDecimal.valueOf(minPeriodStats.getPercentile(50)).setScale(0, RoundingMode.HALF_UP));
			signal.setTradeDate(tradeDate);
			if( tradeDate.compareTo(this.stockPriceVoList.get(this.stockPriceVoList.size()-1).getTradeDate()) == 0 ) {
				if( this.stockPriceVoList.size() > 50 ) {
					signal.setLongStrength(this.getRelativeStrength(50));
				}
				if( this.stockPriceVoList.size() > 20 ) {
					signal.setMediumStrength(this.getRelativeStrength(20));
				}
				if( this.stockPriceVoList.size() > 10 ) {
					signal.setShortStrength(this.getRelativeStrength(10));
				}
			}
			return true;
		}
		return false;
	}
	
	private String getRelativeStrength(int period) {
		BigDecimal stockPriceDiff = this.getStockPriceDiff(period);
		BigDecimal refPriceDiff = this.getRefPriceDiff(period);
		//logger.info(String.format("%s - stock: %s , hsi: %s", period, stockPriceDiff, refPriceDiff));
		if( stockPriceDiff != null && refPriceDiff != null ) {
			if( stockPriceDiff.compareTo(refPriceDiff) > 0 ) {
				return SignalParameterEntity.STRENGTH_STRONG;
			} else if( stockPriceDiff.compareTo(refPriceDiff) == 0 ) {
				return SignalParameterEntity.STRENGTH_EQUAL;
			} else if( stockPriceDiff.compareTo(refPriceDiff) < 0 ) {
				return SignalParameterEntity.STRENGTH_WEAK;
			}
		}
		return null;
	}
 	
	private BigDecimal getStockPriceDiff(int period) {
		int beginIndex = this.stockPriceVoList.size() - period;
		int endIndex = this.stockPriceVoList.size()-1;
		return MathUtils.getPriceDiffOnly(this.stockPriceVoList.get(beginIndex).getClosePrice(), 
				this.stockPriceVoList.get(endIndex).getClosePrice(), 5);
	}
	
	private BigDecimal getRefPriceDiff(int period) {
		int beginIndex = this.stockPriceVoList.size() - period;
		int endIndex = this.stockPriceVoList.size()-1;
		Date beginDate = this.stockPriceVoList.get(beginIndex).getTradeDate();
		Date endDate = this.stockPriceVoList.get(endIndex).getTradeDate();
		return MathUtils.getPriceDiffOnly(this.refPriceDateMap.get(beginDate), 
				this.refPriceDateMap.get(endDate), 2);
	}
	
	protected boolean isMeetCriteria(DescriptiveStatistics maxStats, DescriptiveStatistics minStats) throws Exception {
		return false;
	}
	
	public static String getSignalDesc(SignalParameterEntity signal) {
		StringBuffer buf = new StringBuffer();
		
		if( signal.getRsiType() != null ) {
			if( SignalParameterEntity.RSI_ABOVE.compareTo(signal.getRsiType()) == 0 ) {
				buf.append("[RSI-5 > RSI-14] ");
			} else if( SignalParameterEntity.RSI_BELOW.compareTo(signal.getRsiType()) == 0 ) {
				buf.append("[RSI-5 < RSI-14] ");
			}
		}
		if( signal.getLowerDailyRsi() != null && signal.getUpperDailyRsi() != null ) {
			buf.append(String.format("[RSI: %s - %s] ", signal.getLowerDailyRsi(), signal.getUpperDailyRsi()));
		}
		if( signal.getMacdType() != null ) {
			if( SignalParameterEntity.MACD_ABOVE_ZERO == signal.getMacdType() ) {
				buf.append("[MACD > 0] ");
			} else if( SignalParameterEntity.MACD_BELOW_ZERO == signal.getMacdType() ) {
				buf.append("[MACD < 0] ");
			} else if( SignalParameterEntity.MACD_CROSS_ZERO == signal.getMacdType() ) {
				buf.append("[MACD 升穿 0] ");
			} else if( SignalParameterEntity.MACD_HIGHER == signal.getMacdType() ) {
				buf.append("[MACD > 上次MACD] ");
			} else if( SignalParameterEntity.MACD_LOWER == signal.getMacdType() ) {
				buf.append("[MACD < 上次MACD] ");
			}
		}
		if( signal.getCandlestickType() != null ) {
			if( SignalParameterEntity.CANDLESTICK_HALLOW.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[陽蠋] ");
			} else if( SignalParameterEntity.CANDLESTICK_FILLED.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[陰蠋] ");
			} else if( SignalParameterEntity.CANDLESTICK_FILLED_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口高開陰蠋] ");
			} else if( SignalParameterEntity.CANDLESTICK_FILLED_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口低開陰蠋] ");
			} else if( SignalParameterEntity.CANDLESTICK_HALLOW_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口高開陽蠋] ");
			} else if( SignalParameterEntity.CANDLESTICK_HALLOW_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口高開陽蠋] ");
			}
		}
		
		if( signal.getSmaType() != null ) {
			if( SignalParameterEntity.SMA_SHORT_MEDIUM_LONG.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[10MA > 20MA > 50MA]");
			} else if( SignalParameterEntity.SMA_SHORT_LONG_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[10MA > 50MA > 20MA]");
			} else if( SignalParameterEntity.SMA_MEDIUM_SHORT_LONG.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[20MA > 10MA > 50MA]");
			} else if( SignalParameterEntity.SMA_MEDIUM_LONG_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[20MA > 50MA > 10MA]");
			} else if( SignalParameterEntity.SMA_LONG_MEDIUM_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[50MA > 20MA > 10MA]");
			} else if( SignalParameterEntity.SMA_LONG_SHORT_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[50MA > 10MA > 20MA]");
			}
		}

		buf.append(String.format("[次數: %s次] ", signal.getCount()));
		buf.append(String.format("[未來: %s日] ", signal.getPeriod()));
		if( signal.getLongStrength() != null || signal.getMediumStrength() != null || signal.getShortStrength() != null ) {
			buf.append(String.format("[相對強弱: 短線(%s) 中線(%s) 長線(%s)]", signal.getShortStrength(), signal.getMediumStrength(), signal.getLongStrength()));
		}
		buf.append(String.format("[升幅(%s): %s, %s, %s] ", signal.getUpperDayMedian(), signal.getUpperMin(), signal.getUpperMedian(), signal.getUpperMax()));
		buf.append(String.format("[跌幅(%s): %s, %s, %s] ", signal.getLowerDayMedian(), signal.getLowerMax(), signal.getLowerMedian(), signal.getLowerMin()));
		return buf.toString();
	}
	

}
