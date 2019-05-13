package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CbbcPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.WarrantPriceVo;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SmoothedRSIIndicator;
import eu.verdelhan.ta4j.indicators.volume.NVIIndicator;
import eu.verdelhan.ta4j.indicators.volume.PVIIndicator;

@Service
public class StockPriceSrv {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockPriceDao stockPriceDao;

	@Autowired
	private StockOptionsStatSrv stockOptionsStatSrv;
	
	@Autowired
	private WarrantSrv warrantSrv;
	
	@Autowired
	private CbbcSrv cbbcSrv;
	
	@Autowired
	private CandleStickSrv candlestickSrv;
	
	@Autowired
	private StockVolatilitySrv stockVolatilitySrv;
	
	@Autowired
	private FuturesSrv futureSrv;
	
	public void saveStockPrice(StockPriceEntity stockPriceEntity) {
		this.stockPriceDao.save(stockPriceEntity);
		return;
	}
	
	public void saveStockPrice(List<StockPriceEntity> stockPriceEntityList) {
		this.stockPriceDao.save(stockPriceEntityList, stockPriceEntityList.size());
		return;
	}

	public StockPriceEntity getDailyStockPrice(String stockCode, Date tradeDate) {
		return this.stockPriceDao.getStockPrice(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}
	
	public List<StockPriceEntity> getDailyStockPriceList(Date tradeDate) {
		return this.stockPriceDao.getStockPriceList(tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	public StockPriceEntity getLatestDailyStockPriceEntity(String stockCode) {
		try {
			return this.getLastDailyStockPriceEntityList(stockCode, 1).get(0);
		} catch(Exception e) {
			return null;
		}
	}
	
	public StockPriceEntity getPreviousDailyStockPriceEntity(String stockCode, Date tradeDate) {
		return this.stockPriceDao.getPreviousStockPrice(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}
	
	public List<StockPriceEntity> getLastDailyStockPriceEntityList(String stockCode, Integer size) {
		return this.stockPriceDao.getLastStockPriceList(stockCode, size, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	public Map<Date, StockPriceEntity> getStockPriceDateMap(List<StockPriceEntity> stockPriceList) {
		Map<Date, StockPriceEntity> stockPriceDateMap = new HashMap<Date, StockPriceEntity>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockPriceDateMap.put(stockPrice.getTradeDate(), stockPrice);
		}
		return stockPriceDateMap;
	}
	
	public List<StockPriceVo> getFirbaseStockPriceVoList(String stockCode, Integer size) throws Exception {
		List<StockPriceVo> stockPriceVoList = new ArrayList<StockPriceVo>();
		List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stockCode, size);
		Map<Date, StockPriceEntity> stockPriceDateMap = this.getStockPriceDateMap(stockPriceList); 
		Map<Date, WarrantPriceVo> warrantPriceDateMap = this.warrantSrv.getWarrantAmountDateMap(stockCode, stockPriceList.get(0).getTradeDate(), stockPriceDateMap);
		Map<Date, CbbcPriceVo> cbbcPriceDateMap = this.cbbcSrv.getCbbcAmountDateMap(stockCode, stockPriceList.get(0).getTradeDate(), stockPriceDateMap);
		Map<Date, StockVolatilityEntity> stockVolatilityDateMap = this.stockVolatilitySrv.getStockVolatilityDateMap(stockCode, stockPriceList.get(0).getTradeDate());
		Map<Date, List<CandlestickEntity>> candlestickDateMap = this.candlestickSrv.getCandlestickDateMapFromDate(stockCode, stockPriceList.get(0).getTradeDate());
		Map<Date, IndexFuturesEntity[]> futureDateMap = null;
		if( stockCode.startsWith("INDEX") ) {
			futureDateMap = this.futureSrv.getIndexFutureDateMap(stockPriceList.get(0).getTradeDate());
		}
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
        MACDIndicator dailyMacd = new MACDIndicator(dailyClosePrice, 12, 26);
        EMAIndicator dailyMacdSignal = new EMAIndicator(dailyMacd, 9);
        SmoothedRSIIndicator shortRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, 5);
		SmoothedRSIIndicator longRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, 14);
		SMAIndicator dailyShortSma = new SMAIndicator(dailyClosePrice, 20);
		SMAIndicator dailyMediumSma = new SMAIndicator(dailyClosePrice, 50);
		SMAIndicator dailyLongSma = new SMAIndicator(dailyClosePrice, 250);
		PVIIndicator pviIndicator = new PVIIndicator(dailySeries);
		NVIIndicator nviIndicator = new NVIIndicator(dailySeries);
		
		double monthlyVolUpperTarget1sd = 0;
		double monthlyVolLowerTarget1sd = 0;
		double monthlyVolUpperTarget2sd = 0;
		double monthlyVolLowerTarget2sd = 0;
		
		double weeklyVolUpperTarget1sd = 0;
		double weeklyVolLowerTarget1sd = 0;
		
        for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity current = stockPriceList.get(i);
			StockPriceEntity previous = stockPriceList.get(i-1);

        	StockPriceEntity stockPriceEntity = stockPriceList.get(i);
        	StockPriceVo stockPriceVo = new StockPriceVo(stockPriceEntity);
        	stockPriceVo.setDailyMacd(new BigDecimal(dailyMacd.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdHistogram(stockPriceVo.getDailyMacd().subtract(stockPriceVo.getDailyMacdSignal()));
        	stockPriceVo.setDailyShortRsi(new BigDecimal(shortRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
        	stockPriceVo.setDailyLongRsi(new BigDecimal(longRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
        	stockPriceVo.setDailyShortSma(new BigDecimal(dailyShortSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMediumSma(new BigDecimal(dailyMediumSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyLongSma(new BigDecimal(dailyLongSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			stockPriceVo.setPviValue(new BigDecimal(pviIndicator.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			stockPriceVo.setNviValue(new BigDecimal(nviIndicator.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			
			BigDecimal dayDiff = null;
			try {
				dayDiff = MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 5);
			}catch(Exception e) {
				return null;
			}
			stockPriceVo.setDayDiff(dayDiff);
			
			WarrantPriceVo warrantPriceVo = warrantPriceDateMap.get(current.getTradeDate());
			if( warrantPriceVo != null ) {
				stockPriceVo.setWarrantCallAmount(warrantPriceVo.getWarrantCallAmount());
				stockPriceVo.setWarrantPutAmount(warrantPriceVo.getWarrantPutAmount());
				stockPriceVo.setWarrantCallCost(warrantPriceVo.getWarrantCallCost());
				stockPriceVo.setWarrantPutCost(warrantPriceVo.getWarrantPutCost());
				
				stockPriceVo.setWarrantAllCallAmount(warrantPriceVo.getWarrantAllCallAmount());
				stockPriceVo.setWarrantAllPutAmount(warrantPriceVo.getWarrantAllPutAmount());
				stockPriceVo.setWarrantAllCallTurnover(warrantPriceVo.getWarrantAllCallTurnover());
				stockPriceVo.setWarrantAllPutTurnover(warrantPriceVo.getWarrantAllPutTurnover());
				
				stockPriceVo.setWarrantOtmCallAmount(warrantPriceVo.getWarrantOtmCallAmount());
				stockPriceVo.setWarrantOtmPutAmount(warrantPriceVo.getWarrantOtmPutAmount());
				stockPriceVo.setWarrantOtmCallTurnover(warrantPriceVo.getWarrantOtmCallTurnover());
				stockPriceVo.setWarrantOtmPutTurnover(warrantPriceVo.getWarrantOtmPutTurnover());
			}
			
			CbbcPriceVo cbbcPriceVo = cbbcPriceDateMap.get(current.getTradeDate());
			if( cbbcPriceVo != null ) {
				stockPriceVo.setCbbcBullAmount(cbbcPriceVo.getCbbcBullAmount());
				stockPriceVo.setCbbcBearAmount(cbbcPriceVo.getCbbcBearAmount());
				stockPriceVo.setCbbcBullCost(cbbcPriceVo.getCbbcBullCost());
				stockPriceVo.setCbbcBearCost(cbbcPriceVo.getCbbcBearCost());
				
				stockPriceVo.setCbbcAllBullAmount(cbbcPriceVo.getCbbcAllBullAmount());
				stockPriceVo.setCbbcAllBearAmount(cbbcPriceVo.getCbbcAllBearAmount());
				stockPriceVo.setCbbcAllBullTurnover(cbbcPriceVo.getCbbcAllBullTurnover());
				stockPriceVo.setCbbcAllBearTurnover(cbbcPriceVo.getCbbcAllBearTurnover());
				
				stockPriceVo.setCbbcNearBullAmount(cbbcPriceVo.getCbbcNearBullAmount());
				stockPriceVo.setCbbcNearBearAmount(cbbcPriceVo.getCbbcNearBearAmount());
				stockPriceVo.setCbbcNearBullTurnover(cbbcPriceVo.getCbbcNearBullTurnover());
				stockPriceVo.setCbbcNearBearTurnover(cbbcPriceVo.getCbbcNearBearTurnover());
			}
			
			StockVolatilityEntity stockVolatility = stockVolatilityDateMap.get(current.getTradeDate());
			if( stockVolatility != null ) {
				stockPriceVo.setImplVol(stockVolatility.getImplVol());
			}
			
			if( DateUtils.isMonthBeginTradeDate(current.getTradeDate(), previous.getTradeDate() ) ) {
				StockVolatilityEntity monthEndStockVolatility = stockVolatilityDateMap.get(previous.getTradeDate());
				if( monthEndStockVolatility != null ) {
					monthlyVolUpperTarget1sd = previous.getClosePrice().doubleValue() + (previous.getClosePrice().doubleValue() * (monthEndStockVolatility.getImplVol().doubleValue()/100) * 0.2887);
					monthlyVolLowerTarget1sd = previous.getClosePrice().doubleValue() - (previous.getClosePrice().doubleValue() * (monthEndStockVolatility.getImplVol().doubleValue()/100) * 0.2887);
					
					monthlyVolUpperTarget2sd = previous.getClosePrice().doubleValue() + (previous.getClosePrice().doubleValue() * (monthEndStockVolatility.getImplVol().doubleValue()/100) * 0.3695);
					monthlyVolLowerTarget2sd = previous.getClosePrice().doubleValue() - (previous.getClosePrice().doubleValue() * (monthEndStockVolatility.getImplVol().doubleValue()/100) * 0.3695);
				} else {
					monthlyVolUpperTarget1sd = 0;
					monthlyVolLowerTarget1sd = 0;
					monthlyVolUpperTarget2sd = 0;
					monthlyVolLowerTarget2sd = 0;
				}
			}
			if( monthlyVolUpperTarget1sd > 0 && monthlyVolLowerTarget1sd > 0 && monthlyVolUpperTarget2sd > 0 && monthlyVolLowerTarget2sd > 0 ) {
				stockPriceVo.setMonthlyVolUpperTarget1sd(BigDecimal.valueOf(monthlyVolUpperTarget1sd).setScale(2, RoundingMode.HALF_UP));
				stockPriceVo.setMonthlyVolLowerTarget1sd(BigDecimal.valueOf(monthlyVolLowerTarget1sd).setScale(2, RoundingMode.HALF_UP));
				stockPriceVo.setMonthlyVolUpperTarget2sd(BigDecimal.valueOf(monthlyVolUpperTarget2sd).setScale(2, RoundingMode.HALF_UP));
				stockPriceVo.setMonthlyVolLowerTarget2sd(BigDecimal.valueOf(monthlyVolLowerTarget2sd).setScale(2, RoundingMode.HALF_UP));
			}
			
			if( DateUtils.isWeekBeginTradeDate(current.getTradeDate(), previous.getTradeDate() ) ) {
				StockVolatilityEntity weekEndStockVolatility = stockVolatilityDateMap.get(previous.getTradeDate());
				if( weekEndStockVolatility != null ) {
					weeklyVolUpperTarget1sd = previous.getClosePrice().doubleValue() + (previous.getClosePrice().doubleValue() * (weekEndStockVolatility.getImplVol().doubleValue()/100) * 0.1387);
					weeklyVolLowerTarget1sd = previous.getClosePrice().doubleValue() - (previous.getClosePrice().doubleValue() * (weekEndStockVolatility.getImplVol().doubleValue()/100) * 0.1387);
					
				} else {
					weeklyVolUpperTarget1sd = 0;
					weeklyVolLowerTarget1sd = 0;
				}
			}
			if( weeklyVolUpperTarget1sd > 0 && weeklyVolLowerTarget1sd > 0 ) {
				stockPriceVo.setWeeklyVolUpperTarget1sd(BigDecimal.valueOf(weeklyVolUpperTarget1sd).setScale(2, RoundingMode.HALF_UP));
				stockPriceVo.setWeeklyVolLowerTarget1sd(BigDecimal.valueOf(weeklyVolLowerTarget1sd).setScale(2, RoundingMode.HALF_UP));
			}
			
			List<CandlestickEntity> candlestickList = candlestickDateMap.get(current.getTradeDate());
			if( candlestickList != null ) {
				for(CandlestickEntity candlestick : candlestickList) {
					if( CandlestickEntity.Buy.equals(candlestick.getType()) ) {
						stockPriceVo.setBuyPrice(candlestick.getConfirmPrice());
					} else {
						stockPriceVo.setSellPrice(candlestick.getConfirmPrice());
					}
				}
			}
			
			
			if( futureDateMap != null ) {
				IndexFuturesEntity[] futures = futureDateMap.get(current.getTradeDate());
				if( futures != null ) {
					if( futures[0] != null ) {
						stockPriceVo.setFutureThisMonthPrice(futures[0].getDayClose());
						stockPriceVo.setFutureThisMonthOI(futures[0].getOpenInterest());
						stockPriceVo.setFutureThisMonthVol(futures[0].getVolume());
					} else {
						stockPriceVo.setFutureThisMonthPrice(futures[1].getDayClose());
						stockPriceVo.setFutureThisMonthOI(0);
						stockPriceVo.setFutureThisMonthVol(0);
					}
					stockPriceVo.setFutureNextMonthPrice(futures[1].getDayClose());
					stockPriceVo.setFutureNextMonthOI(futures[1].getOpenInterest());
					stockPriceVo.setFutureNextMonthVol(futures[1].getVolume());
				}
			}
			
        	stockPriceVoList.add(stockPriceVo);
        }
		return stockPriceVoList;
	}
	
	
	
	
	public List<StockPriceData> getFirbaseStockPriceDataList(String stockCode, Integer size) throws Exception {
		List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stockCode, size);
		List<StockPriceData> stockPriceDataList = new ArrayList<StockPriceData>();
		this.generateDailyMacdTechnicalIndicator(stockCode, stockPriceList, 12, 26, 9);
		this.generateDailyRsiTechnicalIndicator(stockCode, stockPriceList, 5, 14);
		this.generateDailyMovingAverageTechnicalIndicator(stockCode, stockPriceList, 20, 50, 250);
		//this.getStockOptionStat(stockCode, stockPriceList);
		//this.generateWeeklyMacdTechnicalIndicator(stockCode, stockPriceList, 12, 26, 9);
		Map<Date, StockOptionsStatEntity> stockOptionDateMap = this.stockOptionsStatSrv.getStockOptionSttDateMap(stockCode, stockPriceList.get(0).getTradeDate());
		
		for(int i=2; i<stockPriceList.size(); i++) {
			StockPriceEntity current = stockPriceList.get(i);
			StockPriceEntity previous = stockPriceList.get(i-1);
			StockPriceData stockPriceData = new StockPriceData(current);
			stockPriceData.initDetail(current);
			StockOptionsStatEntity stockOption = stockOptionDateMap.get(current.getTradeDate());
			if( stockOption != null ) {
				stockPriceData.setIv(stockOption.getImplVol().doubleValue());
				stockPriceData.setOpc(stockOption.getCalls().longValue());
				stockPriceData.setOpp(stockOption.getPuts().longValue());
				stockPriceData.setOpoic(stockOption.getOpenInterestCalls().longValue());
				stockPriceData.setOpoip(stockOption.getOpenInterestPuts().longValue());
			}
			Double dayDiff = null;
			try {
				dayDiff = MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 5).doubleValue();
			}catch(Exception e) {
				return null;
			}
			stockPriceData.setD(dayDiff);
			
			Double openDiff = MathUtils.getPriceDiff(previous.getClosePrice(), current.getOpenPrice(), 5).doubleValue();
			stockPriceData.setOc(openDiff);
			
			if( current.getDailyShortRsi().compareTo(current.getDailyLongRsi()) > 0 && 
					previous.getDailyShortRsi().compareTo(previous.getDailyLongRsi()) < 0 ) {
				stockPriceData.setRc(StockPriceData.UP);
			} else if( current.getDailyShortRsi().compareTo(current.getDailyLongRsi()) < 0 && 
					previous.getDailyShortRsi().compareTo(previous.getDailyLongRsi()) > 0 ) {
				stockPriceData.setRc(StockPriceData.DOWN);
			}
			
			if( current.getDailyMacdHistogram() != null && previous.getDailyMacdHistogram() != null ) {
				BigDecimal histogramDiff = current.getDailyMacdHistogram().subtract(previous.getDailyMacdHistogram());
				stockPriceData.setDhd(histogramDiff.doubleValue());
				
				StockPriceEntity first = stockPriceList.get(i-2);
				if( first.getDailyMacdHistogram() != null ) {
					if( previous.getDailyMacdHistogram().compareTo(first.getDailyMacdHistogram()) > 0 
							&& previous.getDailyMacdHistogram().compareTo(current.getDailyMacdHistogram()) > 0 ) {
						stockPriceData.setDhc(StockPriceData.DOWN);
					} else if( previous.getDailyMacdHistogram().compareTo(first.getDailyMacdHistogram()) < 0 
							&& previous.getDailyMacdHistogram().compareTo(current.getDailyMacdHistogram()) < 0 ) {
						stockPriceData.setDhc(StockPriceData.UP);
					}
				}
			}

			if( current.getClosePrice().compareTo(current.getDailyShortSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getDailyShortSma()) < 0 ) {
				stockPriceData.setSsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getDailyShortSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getDailyShortSma()) > 0 ) {
				stockPriceData.setSsc(StockPriceData.DOWN);
			}
			
			if( current.getClosePrice().compareTo(current.getDailyMediumSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getDailyMediumSma()) < 0 ) {
				stockPriceData.setMsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getDailyMediumSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getDailyMediumSma()) > 0 ) {
				stockPriceData.setMsc(StockPriceData.DOWN);
			}
			
			if( current.getClosePrice().compareTo(current.getDailyLongSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getDailyLongSma()) < 0 ) {
				stockPriceData.setLsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getDailyLongSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getDailyLongSma()) > 0 ) {
				stockPriceData.setLsc(StockPriceData.DOWN);
			}
			
			if( previous.getDailyMacd().compareTo(previous.getDailyMacdSignal()) < 0 
					&& current.getDailyMacd().compareTo(current.getDailyMacdSignal()) > 0 ) {
				stockPriceData.setDmc(StockPriceData.UP);
			} else if( previous.getDailyMacd().compareTo(previous.getDailyMacdSignal()) > 0 
					&& current.getDailyMacd().compareTo(current.getDailyMacdSignal()) < 0 ) {
				stockPriceData.setDmc(StockPriceData.DOWN);
			} 
			
			if( this.isNthDayHigh(10, i, stockPriceList) || this.isNthDayLow(10, i, stockPriceList) ) {
				Double tenDiff = MathUtils.getPriceDiff(stockPriceList.get(i-10).getClosePrice(), current.getClosePrice(), 5).doubleValue();
				stockPriceData.setD10(tenDiff);
			} else if( this.isNthDayHigh(20, i, stockPriceList) || this.isNthDayLow(20, i, stockPriceList) ) {
				Double twentyDiff = MathUtils.getPriceDiff(stockPriceList.get(i-20).getClosePrice(), current.getClosePrice(), 5).doubleValue();
				stockPriceData.setD20(twentyDiff);
			}
			stockPriceDataList.add(stockPriceData);
		}
		return stockPriceDataList;
	}
	
	
	
	
	public void generateDailyMovingAverageTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortSma, int mediumSma, int longSma) throws Exception {
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		SMAIndicator dailyShortSma = new SMAIndicator(dailyClosePrice, shortSma);
		SMAIndicator dailyMediumSma = new SMAIndicator(dailyClosePrice, mediumSma);
		SMAIndicator dailyLongSma = new SMAIndicator(dailyClosePrice, longSma);
		for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
			stockPriceEntity.setDailyShortSma(new BigDecimal(dailyShortSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setDailyMediumSma(new BigDecimal(dailyMediumSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setDailyLongSma(new BigDecimal(dailyLongSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
		}
		return;
	}
	
	public void generateDailyMacdTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortSma, int longSma, int ema) throws Exception {
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
        MACDIndicator dailyMacd = new MACDIndicator(dailyClosePrice, shortSma, longSma);
        EMAIndicator dailyMacdSignal = new EMAIndicator(dailyMacd, ema);
        for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
    		stockPriceEntity.setDailyMacd(new BigDecimal(dailyMacd.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
    		stockPriceEntity.setDailyMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
    		stockPriceEntity.setDailyMacdHistogram(stockPriceEntity.getDailyMacd().subtract(stockPriceEntity.getDailyMacdSignal()));
        }
		return;
	}
	
	public void generateDailyRsiTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortRsi, int longRsi) throws Exception {
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		SmoothedRSIIndicator shortRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, shortRsi);
		SmoothedRSIIndicator longRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, longRsi);
        for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
        	stockPriceEntity.setDailyShortRsi(new BigDecimal(shortRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
    		stockPriceEntity.setDailyLongRsi(new BigDecimal(longRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
        }
		return;
	}
	
	private StockPriceEntity getLastDateWeeklyStockPrice(List<StockPriceEntity> dailyStockPriceList, Date endDate) {
		Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(endDate.getTime());
    	int year = cal.get(Calendar.YEAR);
    	int week = cal.get(Calendar.WEEK_OF_YEAR);
    	int startIndex = -1;
    	for(int i=dailyStockPriceList.size()-1; i>=0; i--) {
    		cal.setTimeInMillis(dailyStockPriceList.get(i).getTradeDate().getTime());
    		if( year == cal.get(Calendar.YEAR) && week == cal.get(Calendar.WEEK_OF_YEAR) ) {
    			startIndex = i;
    			continue;
    		}
    		break;
    	}
    	StockPriceEntity weeklyStockPriceEntity = null;
    	if( startIndex != - 1) {
    		weeklyStockPriceEntity = dailyStockPriceList.get(startIndex);
    		for(int i=startIndex+1; i<dailyStockPriceList.size(); i++) {
    			StockPriceEntity dateStockPriceEntity = dailyStockPriceList.get(i);
    			weeklyStockPriceEntity.setTradeDate(dateStockPriceEntity.getTradeDate());
    			weeklyStockPriceEntity.setClosePrice(dateStockPriceEntity.getClosePrice());
    			if( dateStockPriceEntity.getDayHigh().compareTo(weeklyStockPriceEntity.getDayHigh()) > 0 ) {
    				weeklyStockPriceEntity.setDayHigh(dateStockPriceEntity.getDayHigh());
    			}
    			if( dateStockPriceEntity.getDayLow().compareTo(weeklyStockPriceEntity.getDayLow()) < 0 ) {
    				weeklyStockPriceEntity.setDayLow(dateStockPriceEntity.getDayLow());
    			}
    			if( dateStockPriceEntity.getDayVolume() != null ) {
    				weeklyStockPriceEntity.setDayVolume(weeklyStockPriceEntity.getDayVolume().add(dateStockPriceEntity.getDayVolume()));
    			}
    		}
    	}
    	return weeklyStockPriceEntity;
	}
	
	public void generateWeeklyMacdTechnicalIndicator(String stockCode, List<StockPriceEntity> dailyStockPriceList, int shortSma, int longSma, int ema) throws Exception {
		
		java.sql.Date startDate = dailyStockPriceList.get(0).getTradeDate();
		java.sql.Date endDate = dailyStockPriceList.get(dailyStockPriceList.size()-1).getTradeDate();
		List<StockPriceEntity> weeklyStockPriceList = this.stockPriceDao.getStockPriceEntityListInDate(stockCode, startDate, endDate, StockPriceEntity.PRICE_TYPE_WEEKLY);
		
		if( weeklyStockPriceList.get(weeklyStockPriceList.size()-1).getTradeDate().compareTo(endDate) != 0 ) {
			StockPriceEntity weeklyStockPriceEntity = this.getLastDateWeeklyStockPrice(dailyStockPriceList, endDate);
			if( weeklyStockPriceEntity != null ) {
				weeklyStockPriceList.add(weeklyStockPriceEntity);
			}
		}
		
		TimeSeries weeklySeries = this.getStockTimeSeries(stockCode, weeklyStockPriceList);
		ClosePriceIndicator weeklyClosePrice = new ClosePriceIndicator(weeklySeries);
        MACDIndicator weeklyMacd = new MACDIndicator(weeklyClosePrice, shortSma, longSma);
        EMAIndicator weeklyMacdSignal = new EMAIndicator(weeklyMacd, ema);
        Map<Date, Integer> weeklytStockPriceEntityIndexMap = new HashMap<Date, Integer>();
        for(int i=0; i<weeklyStockPriceList.size(); i++) {
        	weeklytStockPriceEntityIndexMap.put(weeklyStockPriceList.get(i).getTradeDate(), i);
        }
        
        for(int i=0; i<dailyStockPriceList.size(); i++) {
        	StockPriceEntity stockPriceEntity = dailyStockPriceList.get(i);
        	if( weeklytStockPriceEntityIndexMap.containsKey(stockPriceEntity.getTradeDate()) ) {
        		int index = weeklytStockPriceEntityIndexMap.get(stockPriceEntity.getTradeDate());
        		stockPriceEntity.setWeeklyMacd(new BigDecimal(weeklyMacd.getValue(index).toDouble()));
        		stockPriceEntity.setWeeklyMacdSignal(new BigDecimal(weeklyMacdSignal.getValue(index).toDouble()));
        		stockPriceEntity.setWeeklyMacdHistogram(stockPriceEntity.getWeeklyMacd().subtract(stockPriceEntity.getWeeklyMacdSignal()));
        	}
        }
        
		return;
	}

	public TimeSeries getStockTimeSeries(String stockCode, List<StockPriceEntity> stockPriceEntityList) throws Exception {
		List<Tick> ticks = new ArrayList<Tick>();
		for(int i=0; i<stockPriceEntityList.size(); i++) {
			try {
				ticks.add(stockPriceEntityList.get(i).toTick());
			}catch(Exception e) {
				
			}
		}
		return new TimeSeries(stockCode, ticks);
	}
	
	private boolean isNthDayHigh(int nth, int index, List<StockPriceEntity> stockPriceList) {
		if( index - nth >= 0 ) {
			StockPriceEntity current = stockPriceList.get(index);
			StockPriceEntity init = stockPriceList.get(index-nth);
			if( current.getClosePrice().compareTo(init.getClosePrice()) > 0 ) {
				for(int i=index-nth; i<index; i++) {
					if( stockPriceList.get(i).getClosePrice().compareTo(init.getClosePrice()) > 0 ) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean isNthDayLow(int nth, int index, List<StockPriceEntity> stockPriceList) {
		if( index - nth >= 0 ) {
			StockPriceEntity current = stockPriceList.get(index);
			StockPriceEntity init = stockPriceList.get(index-nth);
			if( current.getClosePrice().compareTo(init.getClosePrice()) < 0 ) {
				for(int i=index-nth; i<index; i++) {
					if( stockPriceList.get(i).getClosePrice().compareTo(init.getClosePrice()) < 0 ) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
 