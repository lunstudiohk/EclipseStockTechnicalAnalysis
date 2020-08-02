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
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public abstract class GeneralSignal {

	protected static final Integer MIN_TRADE_COUNT = 5;
	
	private static final Logger logger = LogManager.getLogger();

	protected StockEntity stock;
	protected List<StockPriceVo> stockPriceVoList;
	protected Integer signalType;
	protected String type;
	protected String priceType;
	protected Integer period;
	protected List<CandlestickEntity> candlestickList;
	protected List<Integer> tradeIndexList;
	
	public abstract List<StockSignalEntity> getSignalParameterList() throws Exception;
	
	public abstract StockSignalEntity findInvalidSignal(StockSignalEntity signal1, StockSignalEntity signal2) throws Exception;
	
	public abstract boolean isValid(StockSignalEntity parameter, Integer tradeIndex) throws Exception;

	public List<Integer> getTradeIndexList() {
		return this.tradeIndexList;
	}
	
	public GeneralSignal(StockEntity stock, String priceType, List<StockPriceVo> stockPriceVoList, Integer signalType, String type) throws Exception{
		this.stock = stock;
		this.stockPriceVoList = stockPriceVoList;
		this.signalType = signalType;
		this.type = type;
		this.priceType = priceType;
		return;
	}
/*
	protected List<Integer> getTradeIndexList(StockSignalEntity signal) throws Exception {
		List<Integer> tradeIndexList = new ArrayList<Integer>();
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if( this.isValid(signal, i) ) {
				tradeIndexList.add(i);
			}
		}
		return tradeIndexList;
	}
*/	
	protected List<StockSignalEntity> filterInvalidSignal(List<StockSignalEntity> signalList) throws Exception {
		List<StockSignalEntity> finalList = new ArrayList<StockSignalEntity>();
		for(int i=0; i<signalList.size()-1; i++) {
			StockSignalEntity signal1 = signalList.get(i);
			if( signal1.getStockCode() != null ) {
				for(int j=i+1; j<signalList.size(); j++) {
					StockSignalEntity signal2 = signalList.get(j);
					if( signal2.getStockCode() != null ) {
						if( signal1.isSame(signal2) ) {
							StockSignalEntity signal = this.findInvalidSignal(signal1, signal2);
							if( signal != null ) {
								signal.setStockCode(null);
							}
						}
					}
				}
			}
		}
		for(StockSignalEntity signal : signalList) {
			if( signal.getStockCode() != null ) {
				finalList.add(signal);
			}
		}
		
		return finalList;
	}
	
	protected List<StockSignalEntity> getTheMaxConfidentSignal(List<StockSignalEntity> signalList) throws Exception {
		List<StockSignalEntity> finalList = new ArrayList<StockSignalEntity>();
		//Get the max confident signal
		for(int i=0; i<signalList.size(); i++) {
			if( finalList.isEmpty() ) {
				finalList.add(signalList.get(i));
			} else {
				if( finalList.get(0).getConfident().compareTo(signalList.get(i).getConfident()) < 0 ) {
					finalList.set(0, signalList.get(i));
				}
			}
		}
		return finalList;
	}
	
	
	protected List<StockSignalEntity> getValidSignalList(List<StockSignalEntity> signalList) throws Exception {
		List<StockSignalEntity> list = new ArrayList<StockSignalEntity>();
		for(StockSignalEntity signal : signalList ) {
			/*
			if( this.isValidSignal(signal) ) {
				list.add(signal);
			}
			*/
			
		}
		return list;
		//return this.filterInvalidSignal(list);
		//return this.getTheMaxConfidentSignal(list);
	}
	
	
	
	private boolean isValidSignal(StockSignalEntity signal) throws Exception {
		List<Integer> signalTradeIndexList = new ArrayList<Integer>();

		//======================================================= Review
		for(int i : this.tradeIndexList ) {
			if( this.isValid(signal, i) ) {
				signalTradeIndexList.add(i);
			}
		}

		if( signalTradeIndexList.isEmpty() ) {
			return false;
		}

		//Only generate today stock signal
		/*
		if( !tradeIndexList.contains(stockPriceVoList.size()-1) ) {
			return false;
		}
		*/
		//======================================================= Review
		
		
		for(int k=GeneralSignal.MIN_TRADE_COUNT; k<signalTradeIndexList.size(); k++) {
			
		}
		
		List<StockSignalDateEntity> signalDateList = new ArrayList<StockSignalDateEntity>();
		DescriptiveStatistics maxPeriodStats = new DescriptiveStatistics();
		DescriptiveStatistics maxStats = new DescriptiveStatistics();
		DescriptiveStatistics minPeriodStats = new DescriptiveStatistics();
		DescriptiveStatistics minStats = new DescriptiveStatistics();
		Date tradeDate = this.stockPriceVoList.get(signalTradeIndexList.get(signalTradeIndexList.size()-1)).getTradeDate();
		for(int tradeIndex: signalTradeIndexList) {
			//tradeDate = this.stockPriceVoList.get(tradeIndex).getTradeDate();
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
				
				StockSignalDateEntity signalDate = new StockSignalDateEntity();
				signalDate.setStockCode(this.stock.getStockCode());
				signalDate.setTradeDate(tradeDate);
				signalDate.setSignalDate(this.stockPriceVoList.get(tradeIndex).getTradeDate());
				signalDate.setSignalType(this.signalType);
				signalDate.setSignalPrice(this.stockPriceVoList.get(tradeIndex).getClosePrice());
				signalDate.setHighDay(maxPeriod);
				signalDate.setHighReturn(max);
				signalDate.setLowDay(minPeriod);
				signalDate.setLowReturn(min);
				signalDateList.add(signalDate);
			}
		}
		
		if( maxStats.getN() < 10 ) {
			return false;
		} 
		
		double confident = 0, target = 0;
		if( StockSignalEntity.SIGNAL_TYPE_BUY.equals(this.signalType) ) {
			double[] maxReturn = maxStats.getSortedValues();
			int index = this.getMinIndex(maxReturn, 3);
			if( index == -1 ) {
				return false;
			}
			confident = 100* (maxReturn.length - index) / (double) maxReturn.length;
			target = maxReturn[index];
			if( confident < 90) {
				return false;
			}
		} else if( StockSignalEntity.SIGNAL_TYPE_SELL.equals(this.signalType) ) {
			double[] minReturn = minStats.getSortedValues();
			int index = this.getMaxIndex(minReturn, -3);
			confident = 100 * (index+1) / (double) minReturn.length;
			if( index == -1 ) {
				return false;
			}
			target = minReturn[index];
			if( confident < 90) {
				return false;
			}
		}

		
		signal.setStockCode(this.stock.getStockCode());
		signal.setSignalType(this.signalType);
		signal.setType(this.type);
		signal.setPeriod(this.period);
		signal.setPriceType(this.priceType);
		signal.setCount((int)maxStats.getN());
		signal.setUpperMax(BigDecimal.valueOf(maxStats.getMax()).setScale(2, RoundingMode.HALF_UP));
		signal.setUpperMin(BigDecimal.valueOf(maxStats.getMin()).setScale(2, RoundingMode.HALF_UP));
		signal.setUpperMedian(BigDecimal.valueOf(maxStats.getPercentile(50)).setScale(2, RoundingMode.HALF_UP));
		signal.setUpperDayMedian(BigDecimal.valueOf(maxPeriodStats.getPercentile(50)).setScale(0, RoundingMode.HALF_UP).intValue());
		signal.setLowerMax(BigDecimal.valueOf(minStats.getMax()).setScale(2, RoundingMode.HALF_UP));
		signal.setLowerMin(BigDecimal.valueOf(minStats.getMin()).setScale(2, RoundingMode.HALF_UP));
		signal.setLowerMedian(BigDecimal.valueOf(minStats.getPercentile(50)).setScale(2, RoundingMode.HALF_UP));
		signal.setLowerDayMedian(BigDecimal.valueOf(minPeriodStats.getPercentile(50)).setScale(0, RoundingMode.HALF_UP).intValue());
		signal.setTradeDate(tradeDate);
		
		signal.setConfident(BigDecimal.valueOf(confident).setScale(2, RoundingMode.HALF_UP));
		signal.setTargetReturn(BigDecimal.valueOf(target));
	
		StockSignalDateEntity signalDate = new StockSignalDateEntity();
		signalDate.setStockCode(this.stock.getStockCode());
		signalDate.setTradeDate(tradeDate);
		signalDate.setSignalType(this.signalType);
		signalDate.setSignalDate(this.stockPriceVoList.get(stockPriceVoList.size()-1).getTradeDate());
		signalDate.setSignalPrice(this.stockPriceVoList.get(stockPriceVoList.size()-1).getClosePrice());
		signalDateList.add(signalDate);
		signal.setStockSignalDateList(signalDateList);
		
		return true;
	}
	
	
	
	
	private int getMinIndex(double[] maxReturn, double target) {
		for(int i=0; i<maxReturn.length; i++) {
			if( maxReturn[i] > target ) {
				return i;
			}
		}
		return -1;
	}
	
	private int getMaxIndex(double[] minReturn, double target) {
		for(int i=minReturn.length-1; i>=0; i--) {
			if( minReturn[i] < target ) {
				return i;
			}
		}
		return -1;
	}
	/*
	private String getRelativeStrength(int period) {
		BigDecimal stockPriceDiff = this.getStockPriceDiff(period);
		BigDecimal refPriceDiff = this.getRefPriceDiff(period);
		//logger.info(String.format("%s - stock: %s , hsi: %s", period, stockPriceDiff, refPriceDiff));
		if( stockPriceDiff != null && refPriceDiff != null ) {
			if( stockPriceDiff.compareTo(refPriceDiff) > 0 ) {
				return StockSignalEntity.STRENGTH_STRONG;
			} else if( stockPriceDiff.compareTo(refPriceDiff) == 0 ) {
				return StockSignalEntity.STRENGTH_EQUAL;
			} else if( stockPriceDiff.compareTo(refPriceDiff) < 0 ) {
				return StockSignalEntity.STRENGTH_WEAK;
			}
		}
		return null;
	}
 	*/
	private BigDecimal getStockPriceDiff(int period) {
		int beginIndex = this.stockPriceVoList.size() - period;
		int endIndex = this.stockPriceVoList.size()-1;
		return MathUtils.getPriceDiffOnly(this.stockPriceVoList.get(beginIndex).getClosePrice(), 
				this.stockPriceVoList.get(endIndex).getClosePrice(), 5);
	}
	/*
	private BigDecimal getRefPriceDiff(int period) {
		int beginIndex = this.stockPriceVoList.size() - period;
		int endIndex = this.stockPriceVoList.size()-1;
		Date beginDate = this.stockPriceVoList.get(beginIndex).getTradeDate();
		Date endDate = this.stockPriceVoList.get(endIndex).getTradeDate();
		return MathUtils.getPriceDiffOnly(this.refPriceDateMap.get(beginDate), 
				this.refPriceDateMap.get(endDate), 2);
	}
	*/
	/*
	protected boolean isMeetCriteria(DescriptiveStatistics maxStats, DescriptiveStatistics minStats) throws Exception {
		return false;
	}
	*/
	
	public static String getSignalDesc(StockSignalEntity signal) {
		StringBuffer buf = new StringBuffer();
		
		if( signal.getRsiType() != null ) {
			if( StockSignalEntity.RSI_ABOVE.compareTo(signal.getRsiType()) == 0 ) {
				buf.append("[RSI-5 > RSI-14] ");
			} else if( StockSignalEntity.RSI_BELOW.compareTo(signal.getRsiType()) == 0 ) {
				buf.append("[RSI-5 < RSI-14] ");
			}
		}
		if( signal.getLowerDailyRsi() != null && signal.getUpperDailyRsi() != null ) {
			buf.append(String.format("[RSI: %s - %s] ", signal.getLowerDailyRsi(), signal.getUpperDailyRsi()));
		}
		if( signal.getMacdType() != null ) {
			if( StockSignalEntity.MACD_ABOVE_ZERO == signal.getMacdType() ) {
				buf.append("[MACD > 0] ");
			} else if( StockSignalEntity.MACD_BELOW_ZERO == signal.getMacdType() ) {
				buf.append("[MACD < 0] ");
			} else if( StockSignalEntity.MACD_CROSS_ZERO == signal.getMacdType() ) {
				buf.append("[MACD 升穿 0] ");
			} else if( StockSignalEntity.MACD_HIGHER == signal.getMacdType() ) {
				buf.append("[MACD > 上次MACD] ");
			} else if( StockSignalEntity.MACD_LOWER == signal.getMacdType() ) {
				buf.append("[MACD < 上次MACD] ");
			}
		}
		if( signal.getCandlestickType() != null ) {
			if( StockSignalEntity.CANDLESTICK_HALLOW.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[陽蠋] ");
			} else if( StockSignalEntity.CANDLESTICK_FILLED.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[陰蠋] ");
			} else if( StockSignalEntity.CANDLESTICK_FILLED_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口高開陰蠋] ");
			} else if( StockSignalEntity.CANDLESTICK_FILLED_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口低開陰蠋] ");
			} else if( StockSignalEntity.CANDLESTICK_HALLOW_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口高開陽蠋] ");
			} else if( StockSignalEntity.CANDLESTICK_HALLOW_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				buf.append("[裂口高開陽蠋] ");
			}
		}
		
		if( signal.getSmaType() != null ) {
			if( StockSignalEntity.SMA_SHORT_MEDIUM_LONG.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[10MA > 20MA > 50MA]");
			} else if( StockSignalEntity.SMA_SHORT_LONG_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[10MA > 50MA > 20MA]");
			} else if( StockSignalEntity.SMA_MEDIUM_SHORT_LONG.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[20MA > 10MA > 50MA]");
			} else if( StockSignalEntity.SMA_MEDIUM_LONG_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[20MA > 50MA > 10MA]");
			} else if( StockSignalEntity.SMA_LONG_MEDIUM_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[50MA > 20MA > 10MA]");
			} else if( StockSignalEntity.SMA_LONG_SHORT_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				buf.append("[50MA > 10MA > 20MA]");
			}
		}

		if( signal.getUpperDailySma() != null && signal.getUpperPriceDiff() != null ) {
			buf.append(String.format("[高於%sMA %s%%]", signal.getUpperDailySma(), signal.getUpperPriceDiff()));
		} 
		if( signal.getLowerDailySma() != null && signal.getLowerPriceDiff() != null ) {
			buf.append(String.format("[低於%sMA %s%%]", signal.getLowerDailySma(), signal.getLowerPriceDiff()));
		}
		
		buf.append(String.format("[回報: %s, 信心: %s]", signal.getTargetReturn(), signal.getConfident()));
		buf.append(String.format("[次數: %s次] ", signal.getCount()));
		buf.append(String.format("[未來: %s日] ", signal.getPeriod()));
		if( signal.getLongStrength() != null || signal.getMediumStrength() != null || signal.getShortStrength() != null ) {
			buf.append(String.format("[相對強弱: 短線(%s) 中線(%s) 長線(%s)]", signal.getShortStrength(), signal.getMediumStrength(), signal.getLongStrength()));
		}
		buf.append(String.format("[升幅(%s): %s, %s, %s] ", signal.getUpperDayMedian(), signal.getUpperMin(), signal.getUpperMedian(), signal.getUpperMax()));
		buf.append(String.format("[跌幅(%s): %s, %s, %s] ", signal.getLowerDayMedian(), signal.getLowerMax(), signal.getLowerMedian(), signal.getLowerMin()));
		
		//Display Date
		//Comment for Debug
		/*
		buf.append("\n");
		buf.append(signal.getStockSignalDateList());
		*/
		return buf.toString();
	}
	
	public static List<String> getSecondarySignalDesc(StockSignalEntity signal) {
		List<String> lists = new ArrayList<String>();
		
		if( signal.getRsiType() != null ) {
			if( StockSignalEntity.RSI_ABOVE.compareTo(signal.getRsiType()) == 0 ) {
				lists.add("RSI-5 > RSI-14");
			} else if( StockSignalEntity.RSI_BELOW.compareTo(signal.getRsiType()) == 0 ) {
				lists.add("RSI-5 < RSI-14");
			}
		}
		if( signal.getLowerDailyRsi() != null && signal.getUpperDailyRsi() != null ) {
			lists.add(String.format("RSI: %s - %s", signal.getLowerDailyRsi().setScale(0), signal.getUpperDailyRsi().setScale(0)));
		}
		if( signal.getMacdType() != null ) {
			if( StockSignalEntity.MACD_ABOVE_ZERO == signal.getMacdType() ) {
				lists.add("MACD > 0");
			} else if( StockSignalEntity.MACD_BELOW_ZERO == signal.getMacdType() ) {
				lists.add("MACD < 0");
			} else if( StockSignalEntity.MACD_CROSS_ZERO == signal.getMacdType() ) {
				lists.add("MACD 升穿 0");
			} else if( StockSignalEntity.MACD_HIGHER == signal.getMacdType() ) {
				lists.add("MACD > 上次MACD");
			} else if( StockSignalEntity.MACD_LOWER == signal.getMacdType() ) {
				lists.add("MACD < 上次MACD");
			}
		}
		if( signal.getCandlestickType() != null ) {
			if( StockSignalEntity.CANDLESTICK_HALLOW.compareTo(signal.getCandlestickType()) == 0 ) {
				lists.add("陽蠋");
			} else if( StockSignalEntity.CANDLESTICK_FILLED.compareTo(signal.getCandlestickType()) == 0 ) {
				lists.add("陰蠋");
			} else if( StockSignalEntity.CANDLESTICK_FILLED_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				lists.add("裂口高開陰蠋");
			} else if( StockSignalEntity.CANDLESTICK_FILLED_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				lists.add("裂口低開陰蠋");
			} else if( StockSignalEntity.CANDLESTICK_HALLOW_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				lists.add("裂口高開陽蠋");
			} else if( StockSignalEntity.CANDLESTICK_HALLOW_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				lists.add("裂口高開陽蠋");
			}
		}
		
		if( signal.getSmaType() != null ) {
			if( StockSignalEntity.SMA_SHORT_MEDIUM_LONG.compareTo(signal.getSmaType()) == 0 ) {
				lists.add("10MA > 20MA > 50MA");
			} else if( StockSignalEntity.SMA_SHORT_LONG_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				lists.add("10MA > 50MA > 20MA");
			} else if( StockSignalEntity.SMA_MEDIUM_SHORT_LONG.compareTo(signal.getSmaType()) == 0 ) {
				lists.add("20MA > 10MA > 50MA");
			} else if( StockSignalEntity.SMA_MEDIUM_LONG_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				lists.add("20MA > 50MA > 10MA");
			} else if( StockSignalEntity.SMA_LONG_MEDIUM_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				lists.add("50MA > 20MA > 10MA");
			} else if( StockSignalEntity.SMA_LONG_SHORT_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				lists.add("50MA > 10MA > 20MA");
			}
		}
		
		if( signal.getUpperDailySma() != null && signal.getUpperPriceDiff() != null ) {
			lists.add(String.format("高於%sMA %s%%", signal.getUpperDailySma().setScale(0), signal.getUpperPriceDiff().setScale(0)));
		} 
		if( signal.getLowerDailySma() != null && signal.getLowerPriceDiff() != null ) {
			lists.add(String.format("低於%sMA %s%%", signal.getLowerDailySma().setScale(0), signal.getLowerPriceDiff().setScale(0)));
		}
		
		return lists;
	}
}
