package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

public class StockResultVo extends BaseEntity {
	
	private static final Logger logger = LogManager.getLogger();
	private StockEntity stock;
	private List<StockPriceEntity> stockPriceList;
	private List<StockPriceVo> stockPriceVoList;
	private List<Integer> tradeIndexList;
	private String desc;
	private DescriptiveStatistics maxStats;
	private DescriptiveStatistics minStats;
	private boolean isHit;
		
	public StockResultVo(List<StockPriceEntity> stockPriceList) {
		return;
	}
	
	public StockResultVo(StockEntity stock, List<StockPriceVo> stockPriceVoList) {
		this.stock = stock;
		this.stockPriceVoList = stockPriceVoList;
		this.maxStats = new DescriptiveStatistics();
		this.minStats = new DescriptiveStatistics();
		return;
	}

	public List<Integer> getTradeIndexList() {
		return tradeIndexList;
	}

	public void setTradeIndexList(List<Integer> tradeIndexList) {
		this.tradeIndexList = tradeIndexList;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public void generateTradeStat(int predicateDay, int targetReturn) {
		if( this.tradeIndexList.isEmpty() ) {
			return;
		}
		
		for(int tradeIndex: this.tradeIndexList) {
//			logger.info(this.stockPriceVoList.get(tradeIndex).getTradeDate());
			BigDecimal min = BigDecimal.valueOf(999);
			BigDecimal max = BigDecimal.valueOf(-999);
			if(tradeIndex == this.stockPriceVoList.size()-1) {
				this.isHit = true;
			}
			if( tradeIndex + predicateDay < this.stockPriceVoList.size() ) {
				BigDecimal currentPrice = this.stockPriceVoList.get(tradeIndex).getClosePrice();
				for(int i=1; i<=predicateDay; i++) {
					BigDecimal high = this.stockPriceVoList.get(tradeIndex+i).getDayHigh();
					BigDecimal low = this.stockPriceVoList.get(tradeIndex+i).getDayLow();
					BigDecimal highDiff = MathUtils.getPriceDiff(currentPrice, high, 2);
					BigDecimal lowDiff = MathUtils.getPriceDiff(currentPrice, low, 2);
					
					if( min.compareTo(lowDiff) > 0 ) {
						min = lowDiff;
					}
					if( max.compareTo(highDiff) < 0 ) {
						max = highDiff;
					}
				}
			}
			this.maxStats.addValue(max.doubleValue());
			this.minStats.addValue(min.doubleValue());
		}
		if( this.maxStats.getN() > 3 && this.maxStats.getPercentile(30) > 5 && Math.abs(this.maxStats.getPercentile(30)) > Math.abs(this.minStats.getPercentile(20)) ) {
//			logger.info(this.desc);
			logger.info(String.format("%s : %s-%s [%s to %s]: n: %s, 70%% [%.2f to %.2f] [%s]", 
				this.desc, stock.getStockCode(), stock.getStockCname(), 
				this.stockPriceVoList.get(this.tradeIndexList.get(0)).getTradeDate(), this.stockPriceVoList.get(this.tradeIndexList.get(tradeIndexList.size()-1)).getTradeDate(),
				this.minStats.getN(), 
				this.minStats.getPercentile(30), this.maxStats.getPercentile(30), this.isHit));
		}
		return;
	}
	
	
	
	
	/*
	public static void priceDiffStat(List<BigDecimal[]> priceDiffList) {
		if( priceDiffList.isEmpty() ) {
			return;
		}
		
		
		for(BigDecimal[] diffList : priceDiffList ) {
			for(BigDecimal diff: diffList) {
				if( diff.compareTo(minPrice) < 0 ) {
					minPrice = diff;
				} else if( diff.compareTo(maxPrice) > 0 ) {
					maxPrice = diff;
				}
			}
		}
		
		for(BigDecimal[] diffList : priceDiffList ) {
			for(int i=0; i<=10; i++) {
				for(BigDecimal diff: diffList) {
					if( diff.compareTo(BigDecimal.valueOf(i)) > 0 ) {
						positiveCount[i]++;
						break;
					}
				}
			}
			for(int i=0; i<=10; i++) {
				for(BigDecimal diff: diffList) {
					 if( diff.compareTo(BigDecimal.valueOf(i*-1)) < 0 ) {
						negativeCount[i]++;
						break;
					}
				}
			}
		}
		int tradeDay = priceDiffList.get(0).length;
		double size = priceDiffList.size()/100.0;
		System.out.println(String.format("Close Price in next %s day: Min:%s, Max:%s, Sample Size:%s", tradeDay, minPrice, maxPrice, priceDiffList.size() ));
		System.out.println(String.format(">0 : %.2f, >1 : %.2f, >2 : %.2f, >3 : %.2f, >4 : %.2f, >5 : %.2f, >6 : %.2f, >7 : %.2f, >8 : %.2f, >9 : %.2f, >10 : %.2f", 
				positiveCount[0]/size, positiveCount[1]/size, positiveCount[2]/size, positiveCount[3]/size, positiveCount[4]/size, positiveCount[5]/size
				, positiveCount[6]/size, positiveCount[7]/size, positiveCount[8]/size, positiveCount[9]/size, positiveCount[10]/size));
		System.out.println(String.format("<0 : %.2f, <-1 : %.2f, <-2 : %.2f, <-3 : %.2f, <-4 : %.2f, <-5 : %.2f, <-6 : %.2f, <-7 : %.2f, <-8 : %.2f, <-9 : %.2f, <-10 : %.2f", 
				negativeCount[0]/size, negativeCount[1]/size, negativeCount[2]/size, negativeCount[3]/size, negativeCount[4]/size, negativeCount[5]/size
				, negativeCount[6]/size, negativeCount[7]/size, negativeCount[8]/size, negativeCount[9]/size, negativeCount[10]/size));
		return;
	}
	*/
}
