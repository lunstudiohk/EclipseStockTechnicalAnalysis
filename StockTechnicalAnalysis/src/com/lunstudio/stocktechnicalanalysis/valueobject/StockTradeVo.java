package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

public class StockTradeVo extends BaseEntity {

	public final static String TRADE_TYPE_LONG = "L";
	public final static String TRADE_TYPE_SHORT = "S";
	
	private String stockCode;
	private String tradeType;
	private Date buyDate;
	private Date buySignalDate;
	private BigDecimal buyPrice;
	private BigDecimal stoplossPrice;
	private Date sellDate;
	private Date sellSignalDate;
	private BigDecimal sellPrice;
	private String reason;
	private StockPriceVo buySignal;
	private StockPriceVo buyRefSignal;
	private StockPriceVo sellSignal;
	private StockPriceVo sellRefSignal;
	private BigDecimal totalHistogram;
	private boolean isReadyToSell;
	
	public StockTradeVo(String stockCode) {
		this.stockCode = stockCode;
		return;
	}
	
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public Date getSellDate() {
		return sellDate;
	}
	public void setSellDate(Date sellDate) {
		this.sellDate = sellDate;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public Date getBuySignalDate() {
		return buySignalDate;
	}
	public void setBuySignalDate(Date buySignalDate) {
		this.buySignalDate = buySignalDate;
	}
	public Date getSellSignalDate() {
		return sellSignalDate;
	}
	public void setSellSignalDate(Date sellSignalDate) {
		this.sellSignalDate = sellSignalDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	

	public StockPriceVo getBuySignal() {
		return buySignal;
	}

	public void setBuySignal(StockPriceVo buySignal) {
		this.buySignal = buySignal;
	}

	public StockPriceVo getSellSignal() {
		return sellSignal;
	}

	public void setSellSignal(StockPriceVo sellSignal) {
		this.sellSignal = sellSignal;
	}

	public StockPriceVo getBuyRefSignal() {
		return buyRefSignal;
	}

	public void setBuyRefSignal(StockPriceVo buyRefSignal) {
		this.buyRefSignal = buyRefSignal;
	}

	public StockPriceVo getSellRefSignal() {
		return sellRefSignal;
	}

	public void setSellRefSignal(StockPriceVo sellRefSignal) {
		this.sellRefSignal = sellRefSignal;
	}

	public BigDecimal getTotalHistogram() {
		return totalHistogram;
	}

	public void setTotalHistogram(BigDecimal totalHistogram) {
		this.totalHistogram = totalHistogram;
	}

	public BigDecimal getStoplossPrice() {
		return stoplossPrice;
	}

	public void setStoplossPrice(BigDecimal stoplossPrice) {
		this.stoplossPrice = stoplossPrice;
	}

	public boolean isReadyToSell() {
		return isReadyToSell;
	}

	public void setReadyToSell(boolean isReadyToSell) {
		this.isReadyToSell = isReadyToSell;
	}

	public static String toStringHeader() {
		return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
				"Stock", "Type", "Buy Date", "Buy Price", "Sell Date", "Sell Price", "Profit", "Histogram", 
				"Daily Histogram Change", "Daily Macd Trend", "Daily Macd Signal Trend",
				"Weekly Histogram Change", "Weekly Macd Trend", "Weekly Macd Signal Trend"
			);
	}
	public String toString() {
		return String.format("%s, %s, %s, %s, %s, %s, [%s], [%s, %s, %s], [%s, %s, %s]",
				this.stockCode, this.tradeType, this.buyDate, this.buyPrice, this.sellDate, this.sellPrice, this.getProfit(), 
				this.buySignal.getDailyMacd(), this.buySignal.getDailyMacdSignal(), this.buySignal.getDailyMacdHistogram(),
				this.buySignal.getWeeklyMacd(), this.buySignal.getWeeklyMacdSignal(), this.buySignal.getWeeklyMacdHistogram()
			);
	}
	
	public BigDecimal getProfit() {
		if( TRADE_TYPE_LONG.equals(this.tradeType) ) {
			return MathUtils.getPriceDiff(this.buyPrice, this.sellPrice, 2);
		} else if( TRADE_TYPE_SHORT.equals(this.tradeType) ) {
			return MathUtils.getPriceDiff(this.sellPrice, this.buyPrice, 2);
		}
		return BigDecimal.ZERO;
	}
	
	public static BigDecimal getTotalProfit(List<StockTradeVo> stockTradeVoList, String tradeType, Date startDate) {
		BigDecimal totalProfit = BigDecimal.ZERO;
		for(StockTradeVo stockTradeVo : stockTradeVoList) {
			if( tradeType == null ) {
				if( startDate == null ) {
					totalProfit = totalProfit.add(stockTradeVo.getProfit());
				} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
					totalProfit = totalProfit.add(stockTradeVo.getProfit());
				}
			} else if( stockTradeVo.getTradeType().equals(tradeType) ) {
				if( startDate == null ) {
					totalProfit = totalProfit.add(stockTradeVo.getProfit());
				} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
					totalProfit = totalProfit.add(stockTradeVo.getProfit());
				}
			}
		}
		return totalProfit;
	}
	
	public static BigDecimal getTotalWinProfit(List<StockTradeVo> stockTradeVoList, String tradeType, Date startDate) {
		BigDecimal totalProfit = BigDecimal.ZERO;
		for(StockTradeVo stockTradeVo : stockTradeVoList) {
			if( stockTradeVo.getProfit().compareTo(BigDecimal.ZERO) > 0 ) {
				if( tradeType == null ) {
					if( startDate == null ) {
						totalProfit = totalProfit.add(stockTradeVo.getProfit());
					} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
						totalProfit = totalProfit.add(stockTradeVo.getProfit());
					}
				} else if( stockTradeVo.getTradeType().equals(tradeType) ) {
					if( startDate == null ) {
						totalProfit = totalProfit.add(stockTradeVo.getProfit());
					} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
						totalProfit = totalProfit.add(stockTradeVo.getProfit());
					}
				}
			}
		}
		return totalProfit;
	}
	
	public static int getWinCount(List<StockTradeVo> stockTradeVoList, String tradeType, Date startDate) {
		int count = 0;
		for(StockTradeVo stockTradeVo : stockTradeVoList) {
			if( tradeType == null ) {
				if( startDate == null ) {
					count++;
				} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
					count++;
				}
			} else if( stockTradeVo.getTradeType().equals(tradeType) && stockTradeVo.getProfit().compareTo(BigDecimal.ZERO) > 0 ) {
				if( startDate == null ) {
					count++;
				} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
					count++;
				}
			}
		}
		return count;
	}
	
	public static int getLossCount(List<StockTradeVo> stockTradeVoList, String tradeType, Date startDate) {
		int count = 0;
		for(StockTradeVo stockTradeVo : stockTradeVoList) {
			if( tradeType == null ) {
				if( startDate == null ) {
					count++;
				} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
					count++;
				}
			} else if( stockTradeVo.getTradeType().equals(tradeType) && stockTradeVo.getProfit().compareTo(BigDecimal.ZERO) < 0 ) {
				if( startDate == null ) {
					count++;
				} else if( stockTradeVo.getBuyDate().compareTo(startDate) >= 0 ) {
					count++;
				}
			}
		}
		return count;
	}
	
	public static String getTotalProfitDetail(List<StockTradeVo> stockTradeVoList, Date startDate) {
		String stockCode = stockTradeVoList.get(0).getStockCode();
		if( startDate == null ) {
			startDate = stockTradeVoList.get(0).getBuyDate();
		}
		Date endDate = stockTradeVoList.get(stockTradeVoList.size()-1).getSellDate();
		int longCount = 0;
		int shortCount = 0;
		for(StockTradeVo stockTradeVo : stockTradeVoList) {
			if( TRADE_TYPE_LONG.equals(stockTradeVo.tradeType) ) {
				longCount++;
			} else if( TRADE_TYPE_SHORT.equals(stockTradeVo.tradeType) ) {
				shortCount++;
			}
		}
		BigDecimal totalLongProfit = StockTradeVo.getTotalProfit(stockTradeVoList, TRADE_TYPE_LONG, startDate);
		int longWinCount = StockTradeVo.getWinCount(stockTradeVoList, TRADE_TYPE_LONG, startDate);
		int longLossCount = StockTradeVo.getLossCount(stockTradeVoList, TRADE_TYPE_LONG, startDate);
		
		BigDecimal totalShortProfit = StockTradeVo.getTotalProfit(stockTradeVoList, TRADE_TYPE_SHORT, startDate);
		int shortWinCount = StockTradeVo.getWinCount(stockTradeVoList, TRADE_TYPE_SHORT, startDate);
		int shortLossCount = StockTradeVo.getLossCount(stockTradeVoList, TRADE_TYPE_SHORT, startDate);
		
		BigDecimal totalProfit = StockTradeVo.getTotalProfit(stockTradeVoList, null, startDate);
		BigDecimal totalWinProfit = StockTradeVo.getTotalWinProfit(stockTradeVoList, null, startDate);
		return String.format("%s - From: %s to %s, "
				+ "Long Profit[%s(%s/%s)]: %s, "
				+ "Short Profit[%s(%s/%s)]: %s, "
				+ "Trade Profit: %s, Only Win Profit: %s", 
				stockCode, startDate, endDate, 
				longCount, longWinCount, longLossCount, totalLongProfit, 
				shortCount, shortWinCount, shortLossCount, totalShortProfit, 
				totalProfit, totalWinProfit);
	}
	
}
