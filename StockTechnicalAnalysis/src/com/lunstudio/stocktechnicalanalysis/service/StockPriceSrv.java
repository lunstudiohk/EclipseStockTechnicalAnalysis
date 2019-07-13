package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CbbcAmountVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceSummaryVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.WarrantAmountVo;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.FixedIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDPercentageIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SlopeIndicator;
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
	
	@Autowired
	private OptionsSrv optionSrv;
	
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

	public List<StockPriceEntity> getDailyStockPriceList(String stockCode, Date startDate) {
		return this.stockPriceDao.getStockPriceListAfter(stockCode, startDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}
	
	public List<StockPriceEntity> getWeeklyStockPriceList(String stockCode, Date startDate) {
		return this.stockPriceDao.getStockPriceListAfter(stockCode, startDate, StockPriceEntity.PRICE_TYPE_WEEKLY);
	}
	
	public StockPriceEntity getLatestDailyStockPriceEntity(String stockCode) {
		try {
			return this.getLastDailyStockPriceEntityList(stockCode, 1).get(0);
		} catch(Exception e) {
			return null;
		}
	}
	
	public StockPriceEntity getLatestWeeklyStockPriceEntity(String stockCode) {
		try {
			return this.stockPriceDao.getLastStockPriceList(stockCode, 1, StockPriceEntity.PRICE_TYPE_WEEKLY).get(0);
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

	public List<StockPriceEntity> getLastWeeklyStockPriceEntityList(String stockCode, Integer size) {
		return this.stockPriceDao.getLastStockPriceList(stockCode, size, StockPriceEntity.PRICE_TYPE_WEEKLY);
	}
	
	public List<StockPriceEntity> getLastStockPriceEntityList(String stockCode, Integer size, String priceType) {
		return this.stockPriceDao.getLastStockPriceList(stockCode, size, priceType);
	}
	
	public Map<Date, StockPriceEntity> getStockPriceDateMap(List<StockPriceEntity> stockPriceList) {
		Map<Date, StockPriceEntity> stockPriceDateMap = new HashMap<Date, StockPriceEntity>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockPriceDateMap.put(stockPrice.getTradeDate(), stockPrice);
		}
		return stockPriceDateMap;
	}
	
	public List<StockPriceVo> getFirbaseStockPriceWeeklyVoList(String stockCode, Integer size) throws Exception {
		List<StockPriceVo> stockPriceVoList = new ArrayList<StockPriceVo>();
		List<StockPriceEntity> stockPriceList = this.getLastWeeklyStockPriceEntityList(stockCode, size);
		TimeSeries weeklySeries = this.getStockTimeSeries(stockCode, stockPriceList);
		ClosePriceIndicator weeklyClosePrice = new ClosePriceIndicator(weeklySeries);
		MACDPercentageIndicator weeklyMacd = new MACDPercentageIndicator(weeklyClosePrice, 12, 26);
        EMAIndicator weeklyMacdSignal = new EMAIndicator(weeklyMacd, 9);
        SmoothedRSIIndicator shortRsiIndicator = new SmoothedRSIIndicator(weeklyClosePrice, 5);
		SmoothedRSIIndicator longRsiIndicator = new SmoothedRSIIndicator(weeklyClosePrice, 14);
		Map<Date, List<CandlestickEntity>> candlestickDateMap = this.candlestickSrv.getCandlestickDateMapFromDate(stockCode, stockPriceList.get(0).getTradeDate(), CandlestickEntity.WEEKLY);
		
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity current = stockPriceList.get(i);
			StockPriceEntity previous = stockPriceList.get(i-1);
			
        	StockPriceEntity stockPriceEntity = stockPriceList.get(i);
        	StockPriceVo stockPriceVo = new StockPriceVo(stockPriceEntity);
			stockPriceVo.setDayDiff(MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 5));
        	stockPriceVo.setDailyMacd(new BigDecimal(weeklyMacd.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdSignal(new BigDecimal(weeklyMacdSignal.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdHistogram(stockPriceVo.getDailyMacd().subtract(stockPriceVo.getDailyMacdSignal()));
        	stockPriceVo.setDailyShortRsi(new BigDecimal(shortRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
        	stockPriceVo.setDailyLongRsi(new BigDecimal(longRsiIndicator.getValue(i).toDouble()).setScale(3,RoundingMode.HALF_UP));
        	
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
        	stockPriceVoList.add(stockPriceVo);
        	
        	
		}
		return stockPriceVoList;
	}
	
	public List<StockPriceVo> getFirbaseStockPriceVoList(String stockCode, Integer size) throws Exception {
		List<StockPriceVo> stockPriceVoList = new ArrayList<StockPriceVo>();
		List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stockCode, size);
		Map<Date, StockPriceEntity> stockPriceDateMap = this.getStockPriceDateMap(stockPriceList); 
		Map<Date, WarrantAmountVo> warrantAmountDateMap = this.warrantSrv.getWarrantAmountDateMap(stockCode, stockPriceList.get(0).getTradeDate());
		Map<Date, CbbcAmountVo> cbbcAmountDateMap = this.cbbcSrv.getCbbcAmountDateMap(stockCode, stockPriceList.get(0).getTradeDate());
		Map<Date, OptionAmountVo> optionAmountDateMap = this.optionSrv.getOptionAmountDateMap(stockCode, stockPriceList.get(0).getTradeDate(), stockPriceDateMap);
		Map<Date, StockVolatilityEntity> stockVolatilityDateMap = this.stockVolatilitySrv.getStockVolatilityDateMap(stockCode, stockPriceList.get(0).getTradeDate());
		Map<Date, List<CandlestickEntity>> candlestickDateMap = this.candlestickSrv.getCandlestickDateMapFromDate(stockCode, stockPriceList.get(0).getTradeDate(), CandlestickEntity.DAILY);
		
		Map<Date, IndexFuturesEntity[]> futureDateMap = null;
		if( stockCode.startsWith("INDEX") ) {
			futureDateMap = this.futureSrv.getIndexFutureDateMap(stockPriceList.get(0).getTradeDate());
		}
		TimeSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		MACDPercentageIndicator dailyMacd = new MACDPercentageIndicator(dailyClosePrice, 12, 26);
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
        	//stockPriceVo.setDailyMacd(new BigDecimal(dailyMacd.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
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
			
			//BigDecimal dayDiff = MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 5);
			stockPriceVo.setDayDiff(MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 5));
			
			WarrantAmountVo warrantAmountVo = warrantAmountDateMap.get(current.getTradeDate());
			if( warrantAmountVo != null ) {
				stockPriceVo.setWarrantCallAmount(warrantAmountVo.getWarrantCallAmount());
				stockPriceVo.setWarrantPutAmount(warrantAmountVo.getWarrantPutAmount());
				stockPriceVo.setWarrantCallTurnover(warrantAmountVo.getWarrantCallTurnover());
				stockPriceVo.setWarrantPutTurnover(warrantAmountVo.getWarrantPutTurnover());
			}
			
			CbbcAmountVo cbbcAmountVo = cbbcAmountDateMap.get(current.getTradeDate());
			if( cbbcAmountVo != null ) {
				stockPriceVo.setCbbcBullAmount(cbbcAmountVo.getCbbcBullAmount());
				stockPriceVo.setCbbcBearAmount(cbbcAmountVo.getCbbcBearAmount());
				stockPriceVo.setCbbcBullTurnover(cbbcAmountVo.getCbbcBullTurnover());
				stockPriceVo.setCbbcBearTurnover(cbbcAmountVo.getCbbcBearTurnover());
			}
			
			OptionAmountVo optionAmountVo = optionAmountDateMap.get(current.getTradeDate());
			if( optionAmountVo != null ) {
				stockPriceVo.setOptionCallOpenInterest(optionAmountVo.getOptionCallOpenInterest());
				stockPriceVo.setOptionPutOpenInterest(optionAmountVo.getOptionPutOpenInterest());
				stockPriceVo.setOptionCallVolume(optionAmountVo.getOptionCallVolume());
				stockPriceVo.setOptionPutVolume(optionAmountVo.getOptionPutVolume());
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
	
	public List<StockPriceEntity> getWeeklyStockPriceEntityList(List<StockPriceEntity> dailyStockPriceList) {
		List<StockPriceEntity> weeklyStockPriceList = new ArrayList<StockPriceEntity>();
		Calendar cal = Calendar.getInstance();
		
		StockPriceEntity weeklyStockPrice = new StockPriceEntity(dailyStockPriceList.get(0));
		weeklyStockPrice.setPriceType(StockPriceEntity.PRICE_TYPE_WEEKLY);
		weeklyStockPriceList.add(weeklyStockPrice);
		cal.setTime(weeklyStockPrice.getTradeDate());
		int weeklyWeek = cal.get(Calendar.WEEK_OF_YEAR);
		for(int i=1; i<dailyStockPriceList.size(); i++) {
			StockPriceEntity dailyStockPrice = dailyStockPriceList.get(i);
			cal.setTime(dailyStockPrice.getTradeDate());
			int dailyWeek = cal.get(Calendar.WEEK_OF_YEAR);
			if( weeklyWeek == dailyWeek ) {
				weeklyStockPrice.setTradeDate(dailyStockPrice.getTradeDate());
				if( dailyStockPrice.getDayHigh().compareTo(weeklyStockPrice.getDayHigh()) > 0 ) {
					weeklyStockPrice.setDayHigh(dailyStockPrice.getDayHigh());
				}
				if( dailyStockPrice.getDayLow().compareTo(weeklyStockPrice.getDayLow()) < 0 ) {
					weeklyStockPrice.setDayLow(dailyStockPrice.getDayLow());
				}
				weeklyStockPrice.setClosePrice(dailyStockPrice.getClosePrice());
				weeklyStockPrice.setDayVolume(weeklyStockPrice.getDayVolume().add(dailyStockPrice.getDayVolume()));
			} else {
				weeklyStockPrice = new StockPriceEntity(dailyStockPrice);
				weeklyStockPrice.setPriceType(StockPriceEntity.PRICE_TYPE_WEEKLY);
				weeklyStockPriceList.add(weeklyStockPrice);
				cal.setTime(weeklyStockPrice.getTradeDate());
				weeklyWeek = cal.get(Calendar.WEEK_OF_YEAR);
			}
		}
		return weeklyStockPriceList;
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
	
	public List<StockPriceVo> getStockPriceVoList(StockEntity stock) throws Exception {
		List<StockPriceEntity> dailyStockPriceList = this.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
		List<StockPriceEntity> weeklyStockPriceList = this.getLastWeeklyStockPriceEntityList(stock.getStockCode(), null);
		return this.getStockPriceVoList(stock, dailyStockPriceList, weeklyStockPriceList);
	}
	
	public List<StockPriceVo> getStockPriceVoList(StockEntity stock, List<StockPriceEntity> dailyStockPriceList, List<StockPriceEntity> weeklyStockPriceList) throws Exception {
        List<StockPriceVo> stockDataList = new ArrayList<StockPriceVo>();
//        List<StockPriceEntity> dailyStockPriceList = this.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
		TimeSeries dailySeries = this.getStockTimeSeries(stock.getStockCode(), dailyStockPriceList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		SmoothedRSIIndicator rsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, 14);
		MACDPercentageIndicator dailyMacd = new MACDPercentageIndicator(dailyClosePrice, 12, 26);
        EMAIndicator dailyMacdSignal = new EMAIndicator(dailyMacd, 9);
        FixedIndicator<Decimal> dailyMacdHistogram = new FixedIndicator<Decimal>();
        for(int i=0; i<dailyMacd.getTimeSeries().getTickCount(); i++) {
        	dailyMacdHistogram.addValue(dailyMacd.getValue(i).minus(dailyMacdSignal.getValue(i)));
        }
        SMAIndicator dailyMacdSma = new SMAIndicator(dailyMacdHistogram, 13);
        
//		List<StockPriceEntity> weeklyStockPriceList = this.getLastWeeklyStockPriceEntityList(stock.getStockCode(), null);
		TimeSeries weeklySeries = this.getStockTimeSeries(stock.getStockCode(), weeklyStockPriceList);
		ClosePriceIndicator weeklyClosePrice = new ClosePriceIndicator(weeklySeries);
		MACDPercentageIndicator weeklyMacd = new MACDPercentageIndicator(weeklyClosePrice, 12, 26);
        EMAIndicator weeklyMacdSignal = new EMAIndicator(weeklyMacd, 9);
        FixedIndicator<Decimal> weeklyMacdHistogram = new FixedIndicator<Decimal>();
        for(int i=0; i<weeklyMacd.getTimeSeries().getTickCount(); i++) {
        	weeklyMacdHistogram.addValue(weeklyMacd.getValue(i).minus(weeklyMacdSignal.getValue(i)));
        }
        SMAIndicator weeklyMacdSma = new SMAIndicator(weeklyMacdHistogram, 5);
		StockPriceVo stockPriceVo = null, prevDailyStockPriceVo = null, prevWeeklyStockPriceVo = null;
		int j=3;
		for(int i=5; i<dailyStockPriceList.size(); i++) {
			
			stockPriceVo = new StockPriceVo(dailyStockPriceList.get(i));
			stockPriceVo.setDailyMacd(new BigDecimal(dailyMacd.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
			//stockPriceVo.setDailyMacdSlope(new BigDecimal(dailyMacdSlope.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMacdSignalSlope(new BigDecimal(dailyMacdSignalSlope.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdHistogram(new BigDecimal(dailyMacdHistogram.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyMacdHistogramSma(new BigDecimal(dailyMacdSma.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	stockPriceVo.setDailyLongRsi(new BigDecimal(rsiIndicator.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMacdHistogram(new BigDecimal(dailyMacdHistogram.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMacdHistogramSlope(new BigDecimal(dailyMacdHistogramSlope.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	
        	if( stockDataList.size() > 2 ) {
        		/*
	        	if( stockPriceVo.getDailyMacdHistogram().compareTo(stockDataList.get(stockDataList.size()-1).getDailyMacdHistogram()) > 0
	        			&& stockDataList.get(stockDataList.size()-2).getDailyMacdHistogram().compareTo(stockDataList.get(stockDataList.size()-1).getDailyMacdHistogram()) > 0 ) {
	        		stockPriceVo.setDailyMacdHistogramChange(stockPriceVo.getDailyMacdHistogram().subtract(stockDataList.get(stockDataList.size()-1).getDailyMacdHistogram()));
	        	} else if( stockPriceVo.getDailyMacdHistogram().compareTo(stockDataList.get(stockDataList.size()-1).getDailyMacdHistogram()) < 0
	        			&& stockDataList.get(stockDataList.size()-2).getDailyMacdHistogram().compareTo(stockDataList.get(stockDataList.size()-1).getDailyMacdHistogram()) < 0 ) {
	        		stockPriceVo.setDailyMacdHistogramChange(stockPriceVo.getDailyMacdHistogram().subtract(stockDataList.get(stockDataList.size()-1).getDailyMacdHistogram()));
	        	}
	        	*/
        		if( stockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) >= 0 && prevDailyStockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 ) {
        			stockPriceVo.setDailyMacdHistogramChange(stockPriceVo.getDailyMacdHistogram().subtract(prevDailyStockPriceVo.getDailyMacdHistogram()));
        		} else if( stockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) <= 0 && prevDailyStockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) > 0 ) {
        			stockPriceVo.setDailyMacdHistogramChange(stockPriceVo.getDailyMacdHistogram().subtract(prevDailyStockPriceVo.getDailyMacdHistogram()));
        		}
        	}
        	//Weekly Technical Indicator
        	if( j<weeklyStockPriceList.size() ) {
	        	if( dailyStockPriceList.get(i).getTradeDate().compareTo(weeklyStockPriceList.get(j).getTradeDate()) == 0 ) {
	        		stockPriceVo.setWeeklyMacd(new BigDecimal(weeklyMacd.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	    			//stockPriceVo.setWeeklyMacdSlope(new BigDecimal(weeklyMacdSlope.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	stockPriceVo.setWeeklyMacdSignal(new BigDecimal(weeklyMacdSignal.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	//stockPriceVo.setWeeklyMacdSignalSlope(new BigDecimal(weeklyMacdSignalSlope.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	stockPriceVo.setWeeklyMacdHistogram(new BigDecimal(weeklyMacdHistogram.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	stockPriceVo.setWeeklyMacdHistogramSma(new BigDecimal(weeklyMacdSma.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	if( stockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) > 0 && prevWeeklyStockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 ) {
	            		stockPriceVo.setWeeklyMacdHistogramChange(stockPriceVo.getWeeklyMacdHistogram().subtract(prevWeeklyStockPriceVo.getWeeklyMacdHistogram()));
	            	} else if( stockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 && prevWeeklyStockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) > 0 ) {
	            		stockPriceVo.setWeeklyMacdHistogramChange(stockPriceVo.getWeeklyMacdHistogram().subtract(prevWeeklyStockPriceVo.getWeeklyMacdHistogram()));
	            	}
	            	prevWeeklyStockPriceVo = stockPriceVo;
	            	j++;
	        	} else {
	        		
	        		if( prevDailyStockPriceVo != null ) {
		        		//stockPriceVo.setWeeklyMacd(prevDailyStockPriceVo.getWeeklyMacd());
		            	//stockPriceVo.setWeeklyMacdSignal(prevDailyStockPriceVo.getWeeklyMacdSignal());
		            	//stockPriceVo.setWeeklyMacdHistogram(prevDailyStockPriceVo.getWeeklyMacdHistogram());
		            	//stockPriceVo.setWeeklyMacdHistogramChange(prevDailyStockPriceVo.getWeeklyMacdHistogramChange());
	        		}
	        		
	        	}
        	}
        	prevDailyStockPriceVo = stockPriceVo;
			stockDataList.add(stockPriceVo);
		}
        
        return stockDataList;
	}
	
	
}
 