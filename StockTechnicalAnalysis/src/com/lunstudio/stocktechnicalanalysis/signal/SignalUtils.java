package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class SignalUtils {


	
	public static List<Integer> getDailyMacdCrossAboveTradeIndexList(List<StockPriceVo> stockPriceVoList) throws Exception {
		List<Integer> tradeIndexList = new ArrayList<Integer>();
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if(stockPriceVoList.get(i).getDailyMacdHistogramChange() != null && stockPriceVoList.get(i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
				tradeIndexList.add(i);
			}
		}
		return tradeIndexList;
	}
	
	
	
	
	public static List<Integer> getDailyMacdCrossTradeIndexList(List<StockPriceVo> stockPriceVoList) throws Exception {
		List<Integer> macdCrossTradeIndexList = new ArrayList<Integer>();
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if(stockPriceVoList.get(i).getDailyMacdHistogramChange() != null && stockPriceVoList.get(i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) < 0 ) {
				macdCrossTradeIndexList.add(i*-1);
			} else if(stockPriceVoList.get(i).getDailyMacdHistogramChange() != null && stockPriceVoList.get(i).getDailyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
				macdCrossTradeIndexList.add(i);
			}
		}
		return macdCrossTradeIndexList;
	}

	public static List<Integer> getWeeklyMacdCrossTradeIndexList(List<StockPriceVo> stockPriceVoList) throws Exception {
		List<Integer> macdCrossTradeIndexList = new ArrayList<Integer>();
		for(int i=0; i<stockPriceVoList.size(); i++) {
			if(stockPriceVoList.get(i).getWeeklyMacdHistogramChange() != null && stockPriceVoList.get(i).getWeeklyMacdHistogramChange().compareTo(BigDecimal.ZERO) < 0 ) {
				macdCrossTradeIndexList.add(i*-1);
			} else if(stockPriceVoList.get(i).getWeeklyMacdHistogramChange() != null && stockPriceVoList.get(i).getWeeklyMacdHistogramChange().compareTo(BigDecimal.ZERO) > 0 ) {
				macdCrossTradeIndexList.add(i);
			}
		}
		return macdCrossTradeIndexList;
	}
	
	public static BigDecimal getMinDailyMacd(List<StockPriceVo> stockPriceVoList, int startIndex, int endIndex) {
		BigDecimal min = BigDecimal.valueOf(99999);
		for(int i=startIndex; i<=endIndex; i++) {
			if( min.compareTo(stockPriceVoList.get(i).getDailyMacd()) > 0 ) {
				min = stockPriceVoList.get(i).getDailyMacd().add(BigDecimal.ZERO);
			}
		}
		return min;
	}
	
	public static BigDecimal getMaxDailyMacd(List<StockPriceVo> stockPriceVoList, int startIndex, int endIndex) {
		BigDecimal max = BigDecimal.valueOf(-99999);
		for(int i=startIndex; i<=endIndex; i++) {
			if( max.compareTo(stockPriceVoList.get(i).getDailyMacd()) < 0 ) {
				max = stockPriceVoList.get(i).getDailyMacd().add(BigDecimal.ZERO);
			}
		}
		return max;
	}
	
	public static BigDecimal getMinWeeklyMacd(List<StockPriceVo> stockPriceVoList, int startIndex, int endIndex) throws Exception {
		BigDecimal min = BigDecimal.valueOf(99999);
		for(int i=startIndex; i<=endIndex; i++) {
			if( stockPriceVoList.get(i).getWeeklyMacd() != null && min.compareTo(stockPriceVoList.get(i).getWeeklyMacd()) > 0 ) {
				min = stockPriceVoList.get(i).getWeeklyMacd().add(BigDecimal.ZERO);
			}
		}
		return min;
	}
	
	public static BigDecimal getMinClosePrice(List<StockPriceVo> stockPriceVoList, int startIndex, int endIndex) throws Exception {
		BigDecimal min = BigDecimal.valueOf(99999);
		for(int i=startIndex; i<=endIndex; i++) {
			if( min.compareTo(stockPriceVoList.get(i).getClosePrice()) > 0 ) {
				min = stockPriceVoList.get(i).getClosePrice().add(BigDecimal.ZERO);
			}
		}
		return min;
	}
}
