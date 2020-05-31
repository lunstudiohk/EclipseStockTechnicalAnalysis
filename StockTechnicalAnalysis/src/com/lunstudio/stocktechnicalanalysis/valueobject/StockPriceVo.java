package com.lunstudio.stocktechnicalanalysis.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.lunstudio.stocktechnicalanalysis.entity.BaseEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class StockPriceVo extends BaseEntity {

	private Date tradeDate;

	private String stockCode;

	private BigDecimal openPrice;
	private BigDecimal closePrice;
	private BigDecimal dayHigh;
	private BigDecimal dayLow;
	private BigDecimal dayVolume;
	private BigDecimal dayDiff;
	private BigDecimal dailyMacd;
	private BigDecimal dailyMacdSlope;
	private BigDecimal dailyMacdSignal;
	private BigDecimal dailyMacdSignalSlope;
	private BigDecimal dailyMacdHistogram;
	private BigDecimal dailyMacdHistogramSma;
	private BigDecimal dailyMacdHistogramChange;
	private BigDecimal dailyMacdHistogramSlope;
	private BigDecimal weeklyRsi;
	private BigDecimal weeklyMacd;
	private BigDecimal weeklyMacdSignal;
	private BigDecimal weeklyMacdSlope;
	private BigDecimal weeklyMacdSignalSlope;
	private BigDecimal weeklyMacdHistogram;
	private BigDecimal weeklyMacdHistogramSma;
	private BigDecimal weeklyMacdHistogramChange;
	private BigDecimal dailyShortRsi;
	private BigDecimal dailyLongRsi;
	private BigDecimal dailyShortSma;	
	private BigDecimal dailyMediumSma;
	private BigDecimal dailyLongSma;
	
	private BigDecimal warrantCallAmount = BigDecimal.ZERO;
	private BigDecimal warrantPutAmount = BigDecimal.ZERO;
	private BigDecimal warrantCallTurnover = BigDecimal.ZERO;
	private BigDecimal warrantPutTurnover = BigDecimal.ZERO;
	
	private BigDecimal implVol;
	private BigDecimal monthlyVolUpperTarget1sd;
	private BigDecimal monthlyVolLowerTarget1sd;
	private BigDecimal monthlyVolUpperTarget2sd;
	private BigDecimal monthlyVolLowerTarget2sd;
	
	private BigDecimal weeklyVolUpperTarget1sd;
	private BigDecimal weeklyVolLowerTarget1sd;
	
	private BigDecimal buyPrice;
	private BigDecimal sellPrice;
	
	private Integer futureThisMonthPrice;
	private Integer futureNextMonthPrice;
	private Integer futureThisMonthOI;
	private Integer futureNextMonthOI;
	private Integer futureThisMonthVol;
	private Integer futureNextMonthVol;
		
	private BigDecimal cbbcBullAmount = BigDecimal.ZERO;
	private BigDecimal cbbcBearAmount = BigDecimal.ZERO;
	private BigDecimal cbbcBullTurnover = BigDecimal.ZERO;
	private BigDecimal cbbcBearTurnover = BigDecimal.ZERO;
	
	private BigDecimal pviValue;
	private BigDecimal nviValue;
	
	private Long optionCallOpenInterest = Long.valueOf(0);
	private Long optionPutOpenInterest = Long.valueOf(0);
	private Long optionCallVolume = Long.valueOf(0);
	private Long optionPutVolume = Long.valueOf(0);
	
	public StockPriceVo() {
		super();
		return;
	}
	
	public StockPriceVo(StockPriceEntity entity) {
		super();
		this.stockCode = entity.getStockCode();
		this.tradeDate = entity.getTradeDate();
		this.openPrice = entity.getOpenPrice();
		this.closePrice = entity.getClosePrice();
		this.dayHigh = entity.getHighPrice();
		this.dayLow = entity.getDayLow();
		this.dayVolume = entity.getDayVolume();
		return;
	}
	
	public String getStockPriceDataString() {
		return String.format("%s,%s,%s,%s,%s,"
				+ "%s,%s,%s,%s,%s,"
				+ "%s,%s,%s,%s,%s,"
				+ "%s,%s,%s",
				"",		//1 
				this.openPrice, 
				this.dayHigh,
				this.dayLow, 
				this.closePrice, 	//5
				this.dayDiff, 
				this.dayVolume,
				this.dailyShortSma, 
				this.dailyMediumSma, 
				this.dailyLongSma,	//10
				this.dailyMacd, 
				this.dailyMacdSignal, 
				this.dailyMacdHistogram,
				this.dailyShortRsi, 
				this.dailyLongRsi,	//15
				this.implVol!=null?this.implVol:"",
				this.warrantCallAmount!=null?this.warrantCallAmount:"", 
				this.warrantPutAmount!=null?this.warrantPutAmount:""
					
		);
	}
	
	public List<Object> getDataList() {
		List<Object> dataList = new ArrayList<Object>();
		dataList.add(this.openPrice.doubleValue()); 
		dataList.add(this.dayHigh.doubleValue());
		dataList.add(this.dayLow.doubleValue()); 
		dataList.add(this.closePrice.doubleValue());
		dataList.add(this.dayDiff!=null?this.dayDiff.doubleValue():0);		//5 
		
		dataList.add(this.dayVolume.doubleValue());
		dataList.add(this.dailyShortSma!=null?this.dailyShortSma.doubleValue():0); 
		dataList.add(this.dailyMediumSma!=null?this.dailyMediumSma.doubleValue():0); 
		dataList.add(this.dailyLongSma!=null?this.dailyLongSma.doubleValue():0);
		dataList.add(this.dailyMacd.doubleValue()); 	//10
		
		dataList.add(this.dailyMacdSignal.doubleValue()); 
		dataList.add(this.dailyMacdHistogram.doubleValue());
		dataList.add(this.dailyShortRsi.doubleValue()); 
		dataList.add(this.dailyLongRsi.doubleValue());
		dataList.add(this.implVol!=null?this.implVol.doubleValue():0);	//15

		dataList.add(this.monthlyVolUpperTarget1sd!=null?this.monthlyVolUpperTarget1sd.doubleValue():0);
		dataList.add(this.monthlyVolLowerTarget1sd!=null?this.monthlyVolLowerTarget1sd.doubleValue():0);
		dataList.add(this.monthlyVolUpperTarget2sd!=null?this.monthlyVolUpperTarget2sd.doubleValue():0);
		dataList.add(this.monthlyVolLowerTarget2sd!=null?this.monthlyVolLowerTarget2sd.doubleValue():0);
		dataList.add(this.weeklyVolUpperTarget1sd!=null?this.weeklyVolUpperTarget1sd.doubleValue():0);		//20
		
		dataList.add(this.weeklyVolLowerTarget1sd!=null?this.weeklyVolLowerTarget1sd.doubleValue():0);
		dataList.add(this.buyPrice!=null?this.buyPrice.doubleValue():"");
		dataList.add(this.sellPrice!=null?this.sellPrice.doubleValue():"");
		dataList.add(this.futureThisMonthPrice!=null?this.futureThisMonthPrice:0);
		dataList.add(this.futureThisMonthOI!=null?this.futureThisMonthOI:0);		//25
		
		dataList.add(this.futureThisMonthVol!=null?this.futureThisMonthVol:0);
		dataList.add(this.futureNextMonthPrice!=null?this.futureNextMonthPrice:0);
		dataList.add(this.futureNextMonthOI!=null?this.futureNextMonthOI:0);
		dataList.add(this.futureNextMonthVol!=null?this.futureNextMonthVol:0);
		dataList.add(this.pviValue!=null?this.pviValue.doubleValue():0);		//30
		
		dataList.add(this.nviValue!=null?this.nviValue.doubleValue():0);
		dataList.add(this.warrantCallAmount.doubleValue());
		dataList.add(this.warrantPutAmount.doubleValue());
		dataList.add(this.warrantCallTurnover.doubleValue());
		dataList.add(this.warrantPutTurnover.doubleValue());			//35

		dataList.add(this.cbbcBullAmount.doubleValue());
		dataList.add(this.cbbcBearAmount.doubleValue());
		dataList.add(this.cbbcBullTurnover.doubleValue());
		dataList.add(this.cbbcBearTurnover.doubleValue());
		
		dataList.add(this.optionCallOpenInterest);			//40
		dataList.add(this.optionPutOpenInterest);
		dataList.add(this.optionCallVolume);
		dataList.add(this.optionPutVolume);
		return dataList;
	}
	
	public List<Object> getPriceData() {
		List<Object> dataList = new ArrayList<Object>();
		dataList.add(this.openPrice.doubleValue()); 
		dataList.add(this.dayHigh.doubleValue());
		dataList.add(this.dayLow.doubleValue()); 
		dataList.add(this.closePrice.doubleValue());
		dataList.add(this.dayDiff.doubleValue()); 
		dataList.add(this.dayVolume.doubleValue());
		dataList.add(this.dailyShortSma.doubleValue()); 
		dataList.add(this.dailyMediumSma.doubleValue()); 
		dataList.add(this.dailyLongSma.doubleValue());
		return dataList;
	}
	
	public BigDecimal getDailyMacdHistogramSlope() {
		return dailyMacdHistogramSlope;
	}

	public void setDailyMacdHistogramSlope(BigDecimal dailyMacdHistogramSlope) {
		this.dailyMacdHistogramSlope = dailyMacdHistogramSlope;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public BigDecimal getDayHigh() {
		return dayHigh;
	}

	public void setDayHigh(BigDecimal dayHigh) {
		this.dayHigh = dayHigh;
	}

	public BigDecimal getDayLow() {
		return dayLow;
	}

	public void setDayLow(BigDecimal dayLow) {
		this.dayLow = dayLow;
	}

	public BigDecimal getDayVolume() {
		return dayVolume;
	}

	public void setDayVolume(BigDecimal dayVolume) {
		this.dayVolume = dayVolume;
	}

	public BigDecimal getDailyMacd() {
		return dailyMacd;
	}

	public void setDailyMacd(BigDecimal dailyMacd) {
		this.dailyMacd = dailyMacd;
	}

	public BigDecimal getDailyMacdSignal() {
		return dailyMacdSignal;
	}

	public void setDailyMacdSignal(BigDecimal dailyMacdSignal) {
		this.dailyMacdSignal = dailyMacdSignal;
	}

	public BigDecimal getDailyMacdHistogram() {
		return dailyMacdHistogram;
	}

	public void setDailyMacdHistogram(BigDecimal dailyMacdHistogram) {
		this.dailyMacdHistogram = dailyMacdHistogram;
	}

	public BigDecimal getDailyShortRsi() {
		return dailyShortRsi;
	}

	public void setDailyShortRsi(BigDecimal dailyShortRsi) {
		this.dailyShortRsi = dailyShortRsi;
	}

	public BigDecimal getDailyLongRsi() {
		return dailyLongRsi;
	}

	public void setDailyLongRsi(BigDecimal dailyLongRsi) {
		this.dailyLongRsi = dailyLongRsi;
	}

	public BigDecimal getDailyShortSma() {
		return dailyShortSma;
	}

	public void setDailyShortSma(BigDecimal dailyShortSma) {
		this.dailyShortSma = dailyShortSma;
	}

	public BigDecimal getDailyMediumSma() {
		return dailyMediumSma;
	}

	public void setDailyMediumSma(BigDecimal dailyMediumSma) {
		this.dailyMediumSma = dailyMediumSma;
	}

	public BigDecimal getDailyLongSma() {
		return dailyLongSma;
	}

	public void setDailyLongSma(BigDecimal dailyLongSma) {
		this.dailyLongSma = dailyLongSma;
	}

	public BigDecimal getImplVol() {
		return implVol;
	}

	public void setImplVol(BigDecimal implVol) {
		this.implVol = implVol;
	}

	public BigDecimal getDayDiff() {
		return dayDiff;
	}

	public void setDayDiff(BigDecimal dayDiff) {
		this.dayDiff = dayDiff;
	}

	public BigDecimal getWarrantCallAmount() {
		return warrantCallAmount;
	}

	public void setWarrantCallAmount(BigDecimal warrantCallAmount) {
		this.warrantCallAmount = warrantCallAmount;
	}

	public BigDecimal getWarrantPutAmount() {
		return warrantPutAmount;
	}

	public void setWarrantPutAmount(BigDecimal warrantPutAmount) {
		this.warrantPutAmount = warrantPutAmount;
	}

	public BigDecimal getCbbcBullAmount() {
		return cbbcBullAmount;
	}

	public void setCbbcBullAmount(BigDecimal cbbcBullAmount) {
		this.cbbcBullAmount = cbbcBullAmount;
	}

	public BigDecimal getCbbcBearAmount() {
		return cbbcBearAmount;
	}

	public void setCbbcBearAmount(BigDecimal cbbcBearAmount) {
		this.cbbcBearAmount = cbbcBearAmount;
	}

	public BigDecimal getMonthlyVolUpperTarget1sd() {
		return monthlyVolUpperTarget1sd;
	}

	public void setMonthlyVolUpperTarget1sd(BigDecimal monthlyVolUpperTarget1sd) {
		this.monthlyVolUpperTarget1sd = monthlyVolUpperTarget1sd;
	}

	public BigDecimal getMonthlyVolLowerTarget1sd() {
		return monthlyVolLowerTarget1sd;
	}

	public void setMonthlyVolLowerTarget1sd(BigDecimal monthlyVolLowerTarget1sd) {
		this.monthlyVolLowerTarget1sd = monthlyVolLowerTarget1sd;
	}

	public BigDecimal getMonthlyVolUpperTarget2sd() {
		return monthlyVolUpperTarget2sd;
	}

	public void setMonthlyVolUpperTarget2sd(BigDecimal monthlyVolUpperTarget2sd) {
		this.monthlyVolUpperTarget2sd = monthlyVolUpperTarget2sd;
	}

	public BigDecimal getMonthlyVolLowerTarget2sd() {
		return monthlyVolLowerTarget2sd;
	}

	public void setMonthlyVolLowerTarget2sd(BigDecimal monthlyVolLowerTarget2sd) {
		this.monthlyVolLowerTarget2sd = monthlyVolLowerTarget2sd;
	}

	public BigDecimal getWeeklyVolUpperTarget1sd() {
		return weeklyVolUpperTarget1sd;
	}

	public void setWeeklyVolUpperTarget1sd(BigDecimal weeklyVolUpperTarget1sd) {
		this.weeklyVolUpperTarget1sd = weeklyVolUpperTarget1sd;
	}

	public BigDecimal getWeeklyVolLowerTarget1sd() {
		return weeklyVolLowerTarget1sd;
	}

	public void setWeeklyVolLowerTarget1sd(BigDecimal weeklyVolLowerTarget1sd) {
		this.weeklyVolLowerTarget1sd = weeklyVolLowerTarget1sd;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Integer getFutureThisMonthPrice() {
		return futureThisMonthPrice;
	}

	public void setFutureThisMonthPrice(Integer futureThisMonthPrice) {
		this.futureThisMonthPrice = futureThisMonthPrice;
	}

	public Integer getFutureNextMonthPrice() {
		return futureNextMonthPrice;
	}

	public void setFutureNextMonthPrice(Integer futureNextMonthPrice) {
		this.futureNextMonthPrice = futureNextMonthPrice;
	}

	public Integer getFutureThisMonthOI() {
		return futureThisMonthOI;
	}

	public void setFutureThisMonthOI(Integer futureThisMonthOI) {
		this.futureThisMonthOI = futureThisMonthOI;
	}

	public Integer getFutureNextMonthOI() {
		return futureNextMonthOI;
	}

	public void setFutureNextMonthOI(Integer futureNextMonthOI) {
		this.futureNextMonthOI = futureNextMonthOI;
	}

	public Integer getFutureThisMonthVol() {
		return futureThisMonthVol;
	}

	public void setFutureThisMonthVol(Integer futureThisMonthVol) {
		this.futureThisMonthVol = futureThisMonthVol;
	}

	public Integer getFutureNextMonthVol() {
		return futureNextMonthVol;
	}

	public void setFutureNextMonthVol(Integer futureNextMonthVol) {
		this.futureNextMonthVol = futureNextMonthVol;
	}

	public BigDecimal getPviValue() {
		return pviValue;
	}

	public void setPviValue(BigDecimal pviValue) {
		this.pviValue = pviValue;
	}

	public BigDecimal getNviValue() {
		return nviValue;
	}

	public void setNviValue(BigDecimal nviValue) {
		this.nviValue = nviValue;
	}

	public BigDecimal getWarrantCallTurnover() {
		return warrantCallTurnover;
	}

	public void setWarrantCallTurnover(BigDecimal warrantCallTurnover) {
		this.warrantCallTurnover = warrantCallTurnover;
	}

	public BigDecimal getWarrantPutTurnover() {
		return warrantPutTurnover;
	}

	public void setWarrantPutTurnover(BigDecimal warrantPutTurnover) {
		this.warrantPutTurnover = warrantPutTurnover;
	}

	public BigDecimal getCbbcBullTurnover() {
		return cbbcBullTurnover;
	}

	public void setCbbcBullTurnover(BigDecimal cbbcBullTurnover) {
		this.cbbcBullTurnover = cbbcBullTurnover;
	}

	public BigDecimal getCbbcBearTurnover() {
		return cbbcBearTurnover;
	}

	public void setCbbcBearTurnover(BigDecimal cbbcBearTurnover) {
		this.cbbcBearTurnover = cbbcBearTurnover;
	}

	public Long getOptionCallOpenInterest() {
		return optionCallOpenInterest;
	}

	public void setOptionCallOpenInterest(Long optionCallOpenInterest) {
		this.optionCallOpenInterest = optionCallOpenInterest;
	}

	public Long getOptionPutOpenInterest() {
		return optionPutOpenInterest;
	}

	public void setOptionPutOpenInterest(Long optionPutOpenInterest) {
		this.optionPutOpenInterest = optionPutOpenInterest;
	}

	public Long getOptionCallVolume() {
		return optionCallVolume;
	}

	public void setOptionCallVolume(Long optionCallVolume) {
		this.optionCallVolume = optionCallVolume;
	}

	public Long getOptionPutVolume() {
		return optionPutVolume;
	}

	public void setOptionPutVolume(Long optionPutVolume) {
		this.optionPutVolume = optionPutVolume;
	}

	public BigDecimal getWeeklyMacd() {
		return weeklyMacd;
	}

	public void setWeeklyMacd(BigDecimal weeklyMacd) {
		this.weeklyMacd = weeklyMacd;
	}

	public BigDecimal getWeeklyMacdSignal() {
		return weeklyMacdSignal;
	}

	public void setWeeklyMacdSignal(BigDecimal weeklyMacdSignal) {
		this.weeklyMacdSignal = weeklyMacdSignal;
	}

	public BigDecimal getWeeklyMacdHistogram() {
		return weeklyMacdHistogram;
	}

	public void setWeeklyMacdHistogram(BigDecimal weeklyMacdHistogram) {
		this.weeklyMacdHistogram = weeklyMacdHistogram;
	}

	public BigDecimal getDailyMacdSlope() {
		return dailyMacdSlope;
	}

	public void setDailyMacdSlope(BigDecimal dailyMacdSlope) {
		this.dailyMacdSlope = dailyMacdSlope;
	}

	public BigDecimal getDailyMacdSignalSlope() {
		return dailyMacdSignalSlope;
	}

	public void setDailyMacdSignalSlope(BigDecimal dailyMacdSignalSlope) {
		this.dailyMacdSignalSlope = dailyMacdSignalSlope;
	}

	public BigDecimal getWeeklyMacdSlope() {
		return weeklyMacdSlope;
	}

	public void setWeeklyMacdSlope(BigDecimal weeklyMacdSlope) {
		this.weeklyMacdSlope = weeklyMacdSlope;
	}

	public BigDecimal getWeeklyMacdSignalSlope() {
		return weeklyMacdSignalSlope;
	}

	public void setWeeklyMacdSignalSlope(BigDecimal weeklyMacdSignalSlope) {
		this.weeklyMacdSignalSlope = weeklyMacdSignalSlope;
	}
	
	public BigDecimal getDailyMacdHistogramChange() {
		return dailyMacdHistogramChange;
	}

	public void setDailyMacdHistogramChange(BigDecimal dailyMacdHistogramChange) {
		this.dailyMacdHistogramChange = dailyMacdHistogramChange;
	}

	public BigDecimal getWeeklyMacdHistogramChange() {
		return weeklyMacdHistogramChange;
	}

	public void setWeeklyMacdHistogramChange(BigDecimal weeklyMacdHistogramChange) {
		this.weeklyMacdHistogramChange = weeklyMacdHistogramChange;
	}

	public BigDecimal getWeeklyMacdHistogramSma() {
		return weeklyMacdHistogramSma;
	}

	public void setWeeklyMacdHistogramSma(BigDecimal weeklyMacdHistogramSma) {
		this.weeklyMacdHistogramSma = weeklyMacdHistogramSma;
	}

	public BigDecimal getDailyMacdHistogramSma() {
		return dailyMacdHistogramSma;
	}

	public void setDailyMacdHistogramSma(BigDecimal dailyMacdHistogramSma) {
		this.dailyMacdHistogramSma = dailyMacdHistogramSma;
	}

	public BigDecimal getWeeklyRsi() {
		return weeklyRsi;
	}

	public void setWeeklyRsi(BigDecimal weeklyRsi) {
		this.weeklyRsi = weeklyRsi;
	}

	public String toStringCsv() {
		return String.format("%s,%s,%s,   %s,%s,%s,%s,%s,%s,%s,   %s,%s,%s,%s,%s,%s", 
				this.tradeDate, this.stockCode, this.closePrice,
				this.dailyMacd, this.dailyMacdSlope, this.dailyMacdSignal, this.dailyMacdSignalSlope, this.dailyMacdHistogram, this.dailyMacdHistogramChange, this.dailyMacdHistogramSlope,
				this.weeklyMacd, this.weeklyMacdSlope, this.weeklyMacdSignal, this.weeklyMacdSignalSlope, this.weeklyMacdHistogram, this.weeklyMacdHistogramChange
				);
	}
	

	
}
