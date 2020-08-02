package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.signal.BaseSignal.SignalIndicatorPattern;

public class StockTradeSignal {

	public final static Integer LONG = 1;
	public final static Integer MEDIUM = 2;
	public final static Integer SHORT = 3;
	
	//private final static Integer MIN_OCCUR = 10;
	
	//private final static BigDecimal BULLISH_SIGNAL_CRITERIA = BigDecimal.valueOf(0.9);	//80%
	//private final static BigDecimal BEARISH_SIGNAL_CRITERIA = BigDecimal.valueOf(0.9);	//80%
	
	//private final static BigDecimal BULLISH_SHORT_RETURN = BigDecimal.valueOf(5);
	//private final static BigDecimal BEARISH_SHORT_RETURN = BigDecimal.valueOf(-5);

	private final static BigDecimal BULLISH_MEDIUM_RETURN = BigDecimal.valueOf(10);
	private final static BigDecimal BEARISH_MEDIUM_RETURN = BigDecimal.valueOf(-10);
	
	private final static BigDecimal BULLISH_LONG_RETURN = BigDecimal.valueOf(20);
	private final static BigDecimal BEARISH_LONG_RETURN = BigDecimal.valueOf(-20);
	
	private final static Integer TYPE_INVALID = -1;
	private final static Integer TYPE_SHORT_BOTH = 0;
	private final static Integer TYPE_SHORT_BUY = 1;
	private final static Integer TYPE_SHORT_SELL = 2;
	private final static Integer TYPE_MEDIUM_BOTH = 3;
	private final static Integer TYPE_MEDIUM_BUY = 4;
	private final static Integer TYPE_MEDIUM_SELL = 5;
	private final static Integer TYPE_LONG_BOTH = 6;
	private final static Integer TYPE_LONG_BUY = 7;
	private final static Integer TYPE_LONG_SELL = 8;
	
	
	private Integer signalType = TYPE_INVALID;
	private StockEntity stock = null;
	private List<StockPriceEntity> stockPriceList = null;
	private List<Integer> tradeIndexList = null;
	private List<Integer> subTradeIndexList = null;
	private List<BaseSignal> signalList = null;
	
	/*
	private DescriptiveStatistics shortMinReturn = null;
	private DescriptiveStatistics shortMaxReturn = null;
	private DescriptiveStatistics mediumMinReturn = null;
	private DescriptiveStatistics mediumMaxReturn = null;
	private DescriptiveStatistics longMinReturn = null;
	private DescriptiveStatistics longMaxReturn = null;
	*/
	public static BigDecimal shortBullishCriteria;
	public static BigDecimal shortBearishCriteria;
	public static BigDecimal shortBullishTarget;
	public static BigDecimal shortBearishTarget;
	
	public static void init(BigDecimal shortBullishCriteria, BigDecimal shortBearishCriteria, BigDecimal shortBullishTarget, BigDecimal shortBearishTarget) {
		StockTradeSignal.shortBullishCriteria = shortBullishCriteria;
		StockTradeSignal.shortBearishCriteria = shortBearishCriteria;
		StockTradeSignal.shortBullishTarget = shortBullishTarget;
		StockTradeSignal.shortBearishTarget = shortBearishTarget;
		return;
	}
	
	public StockTradeSignal(StockTradeSignal s1, StockTradeSignal s2) {
		this.stock = s1.stock;
		this.stockPriceList = s1.stockPriceList;
		this.signalList = new ArrayList<BaseSignal>();
		this.signalList.addAll(s1.getSignalList());
		this.signalList.addAll(s2.getSignalList());
		this.tradeIndexList = s1.tradeIndexList.stream().filter(s2.tradeIndexList::contains).collect(Collectors.toList());
		return;
	}
	
	public StockTradeSignal(StockEntity stock, List<StockPriceEntity> stockPriceList) {
		this.signalList = new ArrayList<BaseSignal>();
		this.stock = stock;
		this.stockPriceList = stockPriceList;
		return;
	}
	
	public StockTradeSignal(StockEntity stock, List<StockPriceEntity> stockPriceList, List<BaseSignal> signals) {
		this.signalList = new ArrayList<BaseSignal>();
		this.stock = stock;
		this.stockPriceList = stockPriceList;
		this.signalList.addAll(signals);
		return;
	}
	
	public StockTradeSignal(StockEntity stock, List<StockPriceEntity> stockPriceList, BaseSignal signal) {
		this.signalList = new ArrayList<BaseSignal>();
		this.stock = stock;
		this.stockPriceList = stockPriceList;
		this.signalList.add(signal);
		this.tradeIndexList = new ArrayList<Integer>();
		this.tradeIndexList.addAll(signal.getTradeIndex());
		return;
	}
	
	public List<BaseSignal> getSignalList() {
		return this.signalList;
	}
	/*
	public boolean addSignal(List<BaseSignal> signals) {	
		//Add Signal => Reset trade index
		this.tradeIndexList = null;
		this.signalList.addAll(signals);
		if( this.getTradeIndexList().size() >= MIN_OCCUR ) {
			return true;
		} else {
			return false;
		}
	}
	*/
	public void addSignalOnly(BaseSignal signal) {
		this.signalList.add(signal);
		return;
	}
	/*
	public boolean addSignal(BaseSignal signal) {
		//Add Signal => Reset trade index
		this.tradeIndexList = null;
		this.signalList.add(signal);
		if( this.getTradeIndexList().size() >= MIN_OCCUR ) {
			return true;
		} else {
			return false;
		}
	}
	*/
	public boolean isContainTradeIndex(Integer index) {
		return this.subTradeIndexList.contains(index);
		/*
		if( this.tradeIndexList.isEmpty() ) {
			return false;
		} else {
			return this.tradeIndexList.get(this.tradeIndexList.size()-1).compareTo(index) == 0;
		}
		*/
	}
	
	public List<Integer> getTradeIndexList(int index) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i: this.tradeIndexList) {
			if( i < index ) {
				list.add(i);
			}
		}
		return list;
	}
	
	public List<Integer> getTradeIndexList() {
		if( this.tradeIndexList != null ) {
			return this.tradeIndexList;
		} else {
			//this.tradeIndexList = new ArrayList<Integer>();
			//this.tradeIndexList.addAll(this.signalList.get(0).getTradeIndex());
			this.tradeIndexList = this.signalList.get(0).getTradeIndex();
			
			
			
			for(int i=1; i<signalList.size(); i++) {
				if( !this.signalList.get(i).getTradeIndex().isEmpty() ) {
					this.tradeIndexList = this.tradeIndexList.stream().filter(this.signalList.get(i).getTradeIndex()::contains).collect(Collectors.toList());
					//this.tradeIndexList.retainAll(this.signalList.get(i).getTradeIndex());
					//System.out.println(this.signalList.get(i).getSignalDesc() + " : " + this.signalList.get(i).getTradeIndex());
				}
			}
			return this.tradeIndexList;
		}
	}
	
	
	
	public List<Integer> getSubTradeIndexList() {
		return this.subTradeIndexList;
	}
	
	public String getSignalDesc() {
		String desc = "";
		for(BaseSignal signal : this.signalList) {
			desc = desc + signal.getSignalDesc() + "; ";
		}
		return desc;
	}
	
	public String getSignalDateDesc(int tradeIndex) {
		StringBuffer buf = new StringBuffer();
		for(int i : this.tradeIndexList) {
			if( i< tradeIndex ) {
				buf.append(this.stockPriceList.get(i).getTradeDate()).append("[").append(this.stockPriceList.get(i).getShortMaxReturn()).append("], ");
			} else {
				break;
			}
		}
		return buf.toString();
	}
	
	public void calculate(Integer maxIndex) throws Exception {
		/*
		this.shortMinReturn = null;
		this.shortMaxReturn = null;
		this.subTradeIndexList = new ArrayList<Integer>();
		if( !this.tradeIndexList.contains(maxIndex) ) {
			return;
		}
		for(int i : this.tradeIndexList ) {
			if( i < maxIndex) {
				this.subTradeIndexList.add(i);
			}
		}
		
		this.shortMinReturn = new DescriptiveStatistics();
		this.shortMaxReturn = new DescriptiveStatistics();
		for(int i : this.subTradeIndexList) {
			this.shortMinReturn.addValue(this.stockPriceList.get(i).getShortMinReturn().doubleValue());
			this.shortMaxReturn.addValue(this.stockPriceList.get(i).getShortMaxReturn().doubleValue());
		}
		*/
		return;
	}
	
	
	public boolean isTriggerSignal() {
		for(BaseSignal signal : this.signalList) {
			if( signal.isTriggerSignal ) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * At least 20 occurrence and 5% on short period
	 * @return
	 */
	/*
	public boolean isMeetShortCriteria() {
		int bullCount = 0;
		int bearCount = 0;
		for(int index : this.getTradeIndexList() ) {
			if(this.stockPriceList.get(index).getShortMinReturn().doubleValue() < -5) {
				if(bearCount++ > 10) {
					return true;
				}
			}
			if(this.stockPriceList.get(index).getShortMaxReturn().doubleValue() > 5 ) {
				if(bullCount++ > 10 ) {
					return true;
				}
			}
		}
		return false;
	}
	*/
	public boolean isMeetMediumCriteria(Integer type) {
		return true;
		/*
		int bullCount = 0;
		int bearCount = 0;
		for(int index : this.getTradeIndexList() ) {
			
			if( type == LONG && this.stockPriceList.get(index).getLongMinReturn().doubleValue() < -5) {
				bearCount++;
			}
			if(type == LONG && this.stockPriceList.get(index).getLongMaxReturn().doubleValue() > 5 ) {
				bullCount++;
			}

			if( type == MEDIUM && this.stockPriceList.get(index).getMediumMinReturn().doubleValue() < -5) {
				bearCount++;
			}
			if(type == MEDIUM && this.stockPriceList.get(index).getMediumMaxReturn().doubleValue() > 5 ) {
				bullCount++;
			}

			if( type == SHORT && this.stockPriceList.get(index).getShortMinReturn().doubleValue() < -5) {
				bearCount++;
			}
			if(type == SHORT && this.stockPriceList.get(index).getShortMaxReturn().doubleValue() > 5 ) {
				bullCount++;
			}

			if( bearCount > 10 || bullCount > 10 ) {
				return true;
			}
		}
		return false;
		*/
	}
	
	
	public boolean isMeetLongCriteria() {
		return false;
	}
	
	public boolean isConfirmSignal() {
		for(BaseSignal signal : this.signalList) {
			if( signal.pattern == SignalIndicatorPattern.ConfirmCloseAbove
					|| signal.pattern == SignalIndicatorPattern.ConfirmCloseBelow 
					|| signal.pattern == SignalIndicatorPattern.ConfirmCloseAboveHigh 
					|| signal.pattern == SignalIndicatorPattern.ConfirmCloseBelowLow 
					|| signal.pattern == SignalIndicatorPattern.ConfirmVolumeAbove 
					|| signal.pattern == SignalIndicatorPattern.ConfirmVolumeBelow ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isValid(int tradeIndex, int size, BigDecimal target, Integer period) {
		List<Integer> subTradeIndexList = this.getTradeIndexList(tradeIndex);
		int startIndex = subTradeIndexList.size()-size-1;
		if( startIndex < 0 ) {
			return false;
		}
		for(int i=startIndex; i<subTradeIndexList.size(); i++) {
			if( period == StockTradeSignal.SHORT && stockPriceList.get(subTradeIndexList.get(i)).getShortMaxReturn().compareTo(target) < 0 ) {
				return false;
			}
			if( period == StockTradeSignal.MEDIUM && stockPriceList.get(subTradeIndexList.get(i)).getMediumMaxReturn().compareTo(target) < 0 ) {
				return false;
			}
			if( period == StockTradeSignal.LONG && stockPriceList.get(subTradeIndexList.get(i)).getLongMaxReturn().compareTo(target) < 0 ) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid() throws Exception {
/*
		if( this.shortMinReturn == null || this.shortMaxReturn == null ) {
			return false;
		}
		
		if( !this.isTriggerSignal() ) {
			return false;
		}
		this.signalType = TYPE_INVALID;
		
		boolean buy = false;
		boolean sell = false;
		
		
		//90% meet criteria or last 5 records meet criteria
		//Max: 1,2,3,4,5,6,7,8,9,10,11
		//Min: -11, -10, -9. -8, -7, -6, -5, -4, -3, -2, -1
		BigDecimal decimal = BigDecimal.valueOf(this.subTradeIndexList.size());
		
		int index = decimal.multiply(BigDecimal.ONE.subtract(StockTradeSignal.shortBullishCriteria)).setScale(0, RoundingMode.HALF_UP).intValue();
		if( this.shortMaxReturn.getSortedValues()[index] > StockTradeSignal.shortBullishTarget.doubleValue() ) {
			//if( this.shortMinReturn.getMin() > -2 ) {
				buy = true;
			//}
		}

		index = decimal.multiply(StockTradeSignal.shortBearishCriteria).setScale(0, RoundingMode.HALF_UP).intValue()-1;
		if( this.shortMinReturn.getSortedValues()[index] < StockTradeSignal.shortBearishTarget.doubleValue() ) {
			sell = false;	//Try Bullish Only
		}
		if( buy && sell ) {
			this.signalType = TYPE_SHORT_BOTH;
			return true;
		} else if( buy ) {
			this.signalType = TYPE_SHORT_BUY;
			return true;
		} else if( sell ) {
			this.signalType = TYPE_SHORT_SELL;
			return true;
		}
*/
		return false;
	}	
	
	public void print(int index) throws Exception {
		/*
		StockPriceEntity stockPrice = this.stockPriceList.get(index);
		if( this.signalType == TYPE_SHORT_BUY ) {
			System.out.println(String.format("[BUY] %s - %s [%s]", stockPrice.getTradeDate(), stockPrice.getShortMaxReturn(), this.shortMaxReturn.getN()));
		}
		if( this.signalType == TYPE_SHORT_SELL ) {
			System.out.println(String.format("[SELL] %s - %s [%s]", stockPrice.getTradeDate(), stockPrice.getShortMinReturn(), this.shortMinReturn.getN()));
		}
		*/
		return;
	}
	
	public void simplePrint() throws Exception {
		System.out.println(this.getSignalDesc());
		//System.out.println(this.getSignalDateDesc());
		return;
	}
	
	public String toString(int tradeIndex) {
		return this.getSignalDesc() + "\n" + this.getSignalDateDesc(tradeIndex);
	}
	
	public void print() throws Exception {
		/*
		if( this.signalType == TYPE_SHORT_BUY ) {
			System.out.println(String.format("[BUY] %s N=%s; Min: %s; Median: %s (%s); Max: %s (%s)", 
					this.getSignalDesc(), this.subTradeIndexList.size(), this.shortMaxReturn.getMin(), this.shortMaxReturn.getPercentile(50), this.shortMinReturn.getPercentile(50),
					this.shortMaxReturn.getMax(), this.shortMinReturn.getMin()));
			//System.out.println(Arrays.toString(this.shortMaxReturn.getValues()));
			System.out.println(this.getSignalDateDesc());
			if( this.subTradeIndexList.size() == 0 ) {
				return;
			}
		} else if( this.signalType == TYPE_SHORT_SELL ) {
			System.out.println(String.format("[SELL] %s N=%s; Min: %s; Median: %s (%s); Max: %s (%s)", 
					this.getSignalDesc(), this.subTradeIndexList.size(), this.shortMinReturn.getMax(), this.shortMinReturn.getPercentile(50), this.shortMaxReturn.getPercentile(50), 
					this.shortMinReturn.getMin(), this.shortMaxReturn.getMax()));
			//System.out.println(Arrays.toString(this.shortMinReturn.getValues()));
			System.out.println(this.getSignalDateDesc());
		} else if( this.signalType == TYPE_SHORT_BOTH ) {
			System.out.println("[BOTH]" + this.getSignalDesc());
			System.out.println(String.format("N=%s; Min: %s; Median: %s; Max: %s", 
					this.subTradeIndexList.size(), this.shortMaxReturn.getMin(), this.shortMaxReturn.getPercentile(50), this.shortMaxReturn.getMax()));
			System.out.println(String.format("N=%s; Min: %s; Median: %s; Max: %s", 
					this.subTradeIndexList.size(), this.shortMinReturn.getMin(), this.shortMinReturn.getPercentile(50), this.shortMinReturn.getMax()));
			//System.out.println(Arrays.toString(this.shortMaxReturn.getValues()));
			//System.out.println(Arrays.toString(this.shortMinReturn.getValues()));
			//System.out.println(this.getSignalDateDesc());
		}
		*/
		return;
	}
	
	
	public static List<List<StockTradeSignal>> getStockTradeSignalList(StockEntity stock, List<StockPriceEntity> stockPriceList, List<BaseSignal[]> signalList, Integer startTradeIndex, Integer type) {
		List<List<StockTradeSignal>> stockTradeSignalList = new ArrayList<List<StockTradeSignal>>();
		for(BaseSignal[] signals : signalList) {
			List<StockTradeSignal> stockTradeSignals = new ArrayList<StockTradeSignal>(signals.length);
			for(int index=0; index<signals.length; index++) {
				BaseSignal signal = signals[index];
				if (signal.getTradeIndex().stream().anyMatch(i -> i >= startTradeIndex)) {
					StockTradeSignal stockTradeSignal = new StockTradeSignal(stock, stockPriceList, signals[index]);
					if( stockTradeSignal.isMeetMediumCriteria(type) ) {
						//System.out.println(stockTradeSignal.toString());
						stockTradeSignals.add(stockTradeSignal);
					}
				}
			}
			stockTradeSignalList.add(stockTradeSignals);
		}
		return stockTradeSignalList;
	}
	/*
	public static List<StockTradeSignal[]> getStockTradeSignals(StockEntity stock, List<StockPriceEntity> stockPriceList, List<BaseSignal[]> signalList) {
		List<StockTradeSignal[]> stockTradeSignalList = new ArrayList<StockTradeSignal[]>();
		for(BaseSignal[] signals : signalList) {
			StockTradeSignal[] stockTradeSignals = new StockTradeSignal[signals.length];
			for(int i=0; i<signals.length; i++) {
				stockTradeSignals[i] = new StockTradeSignal(stock, stockPriceList, signals[i]);
			}
			stockTradeSignalList.add(stockTradeSignals);
		}
		
		return stockTradeSignalList;
	}
	*/

	public Integer getSignalType() {
		return signalType;
	}
	
	public DescriptiveStatistics getMaxReturn(Integer type, int tradeIndex) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(int i : this.tradeIndexList ) {
			if( i < tradeIndex ) {
				if( type == LONG ) {
					stats.addValue(this.stockPriceList.get(i).getLongMaxReturn().doubleValue());
				} else if( type == MEDIUM ) {
					stats.addValue(this.stockPriceList.get(i).getMediumMaxReturn().doubleValue());
				} else if( type == SHORT ) {
					stats.addValue(this.stockPriceList.get(i).getShortMaxReturn().doubleValue());
				}
			} else {
				break;
			}
		}
		return stats;
	}
	
	public DescriptiveStatistics getMinReturn(Integer type, int tradeIndex) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(int i : this.tradeIndexList ) {
			if( i < tradeIndex ) {
				if( type == LONG ) {
					stats.addValue(this.stockPriceList.get(i).getLongMinReturn().doubleValue());
				} else if( type == MEDIUM ) {
					stats.addValue(this.stockPriceList.get(i).getMediumMinReturn().doubleValue());
				} else if( type == SHORT ) {
					stats.addValue(this.stockPriceList.get(i).getShortMinReturn().doubleValue());
				}
			} else {
				break;
			}
		}
		return stats;
	}
	
	public DescriptiveStatistics getReturnStatistics(Integer type, int tradeIndex, int size) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		stats.setWindowSize(size*2);
		for(int i : this.tradeIndexList ) {
			if( i < tradeIndex ) {
				if( type == LONG ) {
					stats.addValue(this.stockPriceList.get(i).getLongMinReturn().doubleValue());
					stats.addValue(this.stockPriceList.get(i).getLongMaxReturn().doubleValue());
				} else if( type == MEDIUM ) {
					stats.addValue(this.stockPriceList.get(i).getMediumMinReturn().doubleValue());
					stats.addValue(this.stockPriceList.get(i).getMediumMaxReturn().doubleValue());
				} else if( type == SHORT ) {
					stats.addValue(this.stockPriceList.get(i).getShortMinReturn().doubleValue());
					stats.addValue(this.stockPriceList.get(i).getShortMaxReturn().doubleValue());
				}
			} else {
				break;
			}
		}
		return stats;
	}
	
}
