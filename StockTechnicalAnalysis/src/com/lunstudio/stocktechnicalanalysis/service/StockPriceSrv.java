package com.lunstudio.stocktechnicalanalysis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import com.lunstudio.stocktechnicalanalysis.dao.StockPriceDao;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPrice;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.CbbcAmountVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceSummaryVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;
import com.lunstudio.stocktechnicalanalysis.valueobject.WarrantAmountVo;

@Service
public class StockPriceSrv {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private StockPriceDao stockPriceDao;

	@Autowired
	private StockOptionsStatSrv stockOptionsStatSrv;
	
	@Autowired
	private StockSignalSrv stockSignalSrv;
	
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
	
	public static final Integer ORDER_BY_ASC = 0;
	public static final Integer ORDER_BY_DESC = 1;
	
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
	/*
	 * Get stock price after the startDate (not include the startDate)
	 */
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
	
	public List<StockPriceEntity> getNoVolumeStockPriceList(String stockCode) {
		try {
			return this.stockPriceDao.getNoVolumeStockPriceList(stockCode);
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
	/**
	 * Return in asc order
	 * @param stockCode
	 * @param size
	 * @return
	 */
	public List<StockPriceEntity> getLastDailyStockPriceEntityList(String stockCode, Integer size) {
		return this.stockPriceDao.getLastStockPriceList(stockCode, size, StockPriceEntity.PRICE_TYPE_DAILY);
	}
	
	public Date getLastDailyStockPriceTradeDate(String stockCode) {
		List<StockPriceEntity> entityList = this.stockPriceDao.getLastStockPriceList(stockCode, 1, StockPriceEntity.PRICE_TYPE_DAILY);
		return entityList.get(0).getTradeDate();
	}
	
	public List<Date> getLastDailyStockPriceTradeDateList(String stockCode, Integer size, Integer orderBy) {
		List<StockPriceEntity> entityList = this.stockPriceDao.getLastStockPriceList(stockCode, size, StockPriceEntity.PRICE_TYPE_DAILY);
		List<Date> dateList = new ArrayList<Date>();
		if( orderBy == ORDER_BY_ASC ) {
			for(StockPriceEntity entity : entityList) {
				dateList.add(entity.getTradeDate());
			}
		} else {
			for(StockPriceEntity entity : entityList) {
				dateList.add(0, entity.getTradeDate());
			}
		}
		return dateList;
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
	
	public Map<Date, BigDecimal> getStockClosePriceDateMap(List<StockPriceEntity> stockPriceList) {
		Map<Date, BigDecimal> stockPriceDateMap = new HashMap<Date, BigDecimal>();
		for(StockPriceEntity stockPrice : stockPriceList) {
			stockPriceDateMap.put(stockPrice.getTradeDate(), stockPrice.getClosePrice());
		}
		return stockPriceDateMap;
	}
	
	
	public List<StockPriceVo> getFirbaseStockPriceWeeklyVoList(String stockCode, Integer size) throws Exception {
		List<StockPriceVo> stockPriceVoList = new ArrayList<StockPriceVo>();
		/*
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
		*/
		return stockPriceVoList;
	}
	
	public List<StockPriceVo> getFirbaseStockPriceVoList(String stockCode, Integer size) throws Exception {
		List<StockPriceVo> stockPriceVoList = new ArrayList<StockPriceVo>();
		/*
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
        */
		return stockPriceVoList;
	}
	
	
	public List<StockPrice> getFirbaseStockPriceList(String stockCode, Integer size) throws Exception {
		List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stockCode, size);
		this.generateDailyMacdTechnicalIndicator(stockCode, stockPriceList, 12, 26, 9);
		this.generateDailyRsiTechnicalIndicator(stockCode, stockPriceList, 5, 14);
		this.generateDailyMovingAverageAndBBTechnicalIndicator(stockCode, stockPriceList, 10, 20, 50);
		
		List<StockPrice> dataList = new ArrayList<StockPrice>();
		for(StockPriceEntity entity : stockPriceList) {
			StockPrice stockPrice = new StockPrice(entity.getStockCode(), entity.getTradeDate());
			stockPrice.setPrice(entity.getOpenPrice().doubleValue(), entity.getClosePrice().doubleValue(), entity.getHighPrice().doubleValue(), entity.getLowPrice().doubleValue(), entity.getDayVolume().doubleValue());
			stockPrice.setRsi("5", entity.getShortRsi().doubleValue());
			stockPrice.setRsi("14", entity.getLongRsi().doubleValue());
			
			stockPrice.setSma("10", entity.getShortSma().doubleValue());
			stockPrice.setSma("20", entity.getMediumSma().doubleValue());
			stockPrice.setSma("50", entity.getLongSma().doubleValue());
			
			stockPrice.setMacd(entity.getMacd().doubleValue(), entity.getMacdSignal().doubleValue());
			dataList.add(stockPrice);
		}
		
		
		return dataList;
	}
	
	public List<StockPriceData> getFirbaseStockPriceDataList(String stockCode, Integer size) throws Exception {
		List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stockCode, size);
		List<StockPriceData> stockPriceDataList = new ArrayList<StockPriceData>();
		this.generateDailyMacdTechnicalIndicator(stockCode, stockPriceList, 12, 26, 9);
		this.generateDailyRsiTechnicalIndicator(stockCode, stockPriceList, 5, 14);
		this.generateDailyMovingAverageAndBBTechnicalIndicator(stockCode, stockPriceList, 20, 50, 250);
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
			
			if( current.getShortRsi().compareTo(current.getLongRsi()) > 0 && 
					previous.getShortRsi().compareTo(previous.getLongRsi()) < 0 ) {
				stockPriceData.setRc(StockPriceData.UP);
			} else if( current.getShortRsi().compareTo(current.getLongRsi()) < 0 && 
					previous.getShortRsi().compareTo(previous.getLongRsi()) > 0 ) {
				stockPriceData.setRc(StockPriceData.DOWN);
			}
			
			if( current.getMacdHistogram() != null && previous.getMacdHistogram() != null ) {
				BigDecimal histogramDiff = current.getMacdHistogram().subtract(previous.getMacdHistogram());
				stockPriceData.setDhd(histogramDiff.doubleValue());
				
				StockPriceEntity first = stockPriceList.get(i-2);
				if( first.getMacdHistogram() != null ) {
					if( previous.getMacdHistogram().compareTo(first.getMacdHistogram()) > 0 
							&& previous.getMacdHistogram().compareTo(current.getMacdHistogram()) > 0 ) {
						stockPriceData.setDhc(StockPriceData.DOWN);
					} else if( previous.getMacdHistogram().compareTo(first.getMacdHistogram()) < 0 
							&& previous.getMacdHistogram().compareTo(current.getMacdHistogram()) < 0 ) {
						stockPriceData.setDhc(StockPriceData.UP);
					}
				}
			}

			if( current.getClosePrice().compareTo(current.getShortSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getShortSma()) < 0 ) {
				stockPriceData.setSsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getShortSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getShortSma()) > 0 ) {
				stockPriceData.setSsc(StockPriceData.DOWN);
			}
			
			if( current.getClosePrice().compareTo(current.getMediumSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getMediumSma()) < 0 ) {
				stockPriceData.setMsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getMediumSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getMediumSma()) > 0 ) {
				stockPriceData.setMsc(StockPriceData.DOWN);
			}
			
			if( current.getClosePrice().compareTo(current.getLongSma()) > 0 && 
					previous.getClosePrice().compareTo(current.getLongSma()) < 0 ) {
				stockPriceData.setLsc(StockPriceData.UP);
			} else if( current.getClosePrice().compareTo(current.getLongSma()) < 0 && 
					previous.getClosePrice().compareTo(current.getLongSma()) > 0 ) {
				stockPriceData.setLsc(StockPriceData.DOWN);
			}
			
			if( previous.getMacd().compareTo(previous.getMacdSignal()) < 0 
					&& current.getMacd().compareTo(current.getMacdSignal()) > 0 ) {
				stockPriceData.setDmc(StockPriceData.UP);
			} else if( previous.getMacd().compareTo(previous.getMacdSignal()) > 0 
					&& current.getMacd().compareTo(current.getMacdSignal()) < 0 ) {
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
	
	private BarSeries getStockTimeSeries(String stockCode, List<StockPriceEntity> stockPriceEntityList) throws Exception {
        BarSeries series = new BaseBarSeries();
		for(StockPriceEntity stockPrice : stockPriceEntityList) {
			try {
				ZonedDateTime tradeDate = DateUtils.getLocalDate(stockPrice.getTradeDate()).atStartOfDay(ZoneId.systemDefault());
				series.addBar(tradeDate, stockPrice.getOpenPrice(), stockPrice.getHighPrice(), stockPrice.getLowPrice(), stockPrice.getClosePrice(), stockPrice.getVolume()); 
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return series;
	}
	
	public void generateDailyCandlestickTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int sma) throws Exception {
		DescriptiveStatistics bodyStats = new DescriptiveStatistics();
		DescriptiveStatistics candlestickStats = new DescriptiveStatistics();
		bodyStats.setWindowSize(sma);
		candlestickStats.setWindowSize(sma);
		for(int i=0; i<stockPriceEntityList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceEntityList.get(i);
			BigDecimal body = stockPrice.getOpenPrice().subtract(stockPrice.getClosePrice()).abs(); //MathUtils.getPriceDiff(stockPrice.getOpenPrice(), stockPrice.getClosePrice(), 3).abs();
			BigDecimal candlestick = stockPrice.getHighPrice().subtract(stockPrice.getLowPrice());
			bodyStats.addValue(body.doubleValue());
			candlestickStats.addValue(candlestick.doubleValue());
			stockPrice.setBodyMedian(new BigDecimal(bodyStats.getPercentile(50)).setScale(3, RoundingMode.HALF_UP));
			stockPrice.setShortBody(new BigDecimal(bodyStats.getPercentile(25)).setScale(3, RoundingMode.HALF_UP));
			stockPrice.setLongBody(new BigDecimal(bodyStats.getPercentile(75)).setScale(3, RoundingMode.HALF_UP));
			
			stockPrice.setHighlowMedian(new BigDecimal(candlestickStats.getPercentile(50)).setScale(3, RoundingMode.HALF_UP));
			stockPrice.setShortCandle(new BigDecimal(candlestickStats.getPercentile(25)).setScale(3, RoundingMode.HALF_UP));
			stockPrice.setLongCandle(new BigDecimal(candlestickStats.getPercentile(75)).setScale(3, RoundingMode.HALF_UP));
			
		}
		return;
		
	}
	
	public void generateDailyMinMaxReturn(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortDay, int mediumDay, int longDay) throws Exception {
		DescriptiveStatistics shortMinReturn = new DescriptiveStatistics();
		shortMinReturn.setWindowSize(shortDay);
		DescriptiveStatistics shortMaxReturn = new DescriptiveStatistics();
		shortMaxReturn.setWindowSize(shortDay);
		
		DescriptiveStatistics mediumMinReturn = new DescriptiveStatistics();
		mediumMinReturn.setWindowSize(mediumDay);
		DescriptiveStatistics mediumMaxReturn = new DescriptiveStatistics();
		mediumMaxReturn.setWindowSize(mediumDay);
		
		DescriptiveStatistics longMinReturn = new DescriptiveStatistics();
		longMinReturn.setWindowSize(longDay);
		DescriptiveStatistics longMaxReturn = new DescriptiveStatistics();
		longMaxReturn.setWindowSize(longDay);
		
		for(int i=1; i<shortDay; i++) {
			shortMinReturn.addValue(stockPriceEntityList.get(i).getLowPrice().doubleValue());
			shortMaxReturn.addValue(stockPriceEntityList.get(i).getHighPrice().doubleValue());
		}
		for(int i=1; i<mediumDay; i++) {
			mediumMinReturn.addValue(stockPriceEntityList.get(i).getLowPrice().doubleValue());
			mediumMaxReturn.addValue(stockPriceEntityList.get(i).getHighPrice().doubleValue());
		}
		for(int i=1; i<longDay; i++) {
			longMinReturn.addValue(stockPriceEntityList.get(i).getLowPrice().doubleValue());
			longMaxReturn.addValue(stockPriceEntityList.get(i).getHighPrice().doubleValue());
		}
		for(int i=0; i<stockPriceEntityList.size()-1; i++) {
			if( i + shortDay < stockPriceEntityList.size() ) {
				shortMinReturn.addValue(stockPriceEntityList.get(i + shortDay).getLowPrice().doubleValue());
				shortMaxReturn.addValue(stockPriceEntityList.get(i + shortDay).getHighPrice().doubleValue());
			} else {
				shortMinReturn.addValue(stockPriceEntityList.get(stockPriceEntityList.size()-1).getLowPrice().doubleValue());
				shortMaxReturn.addValue(stockPriceEntityList.get(stockPriceEntityList.size()-1).getHighPrice().doubleValue());
			}
			
			if( i + mediumDay < stockPriceEntityList.size() ) {
				mediumMinReturn.addValue(stockPriceEntityList.get(i + mediumDay).getLowPrice().doubleValue());
				mediumMaxReturn.addValue(stockPriceEntityList.get(i + mediumDay).getHighPrice().doubleValue());
			} else {
				mediumMinReturn.addValue(stockPriceEntityList.get(stockPriceEntityList.size()-1).getLowPrice().doubleValue());
				mediumMaxReturn.addValue(stockPriceEntityList.get(stockPriceEntityList.size()-1).getHighPrice().doubleValue());
			}
			if( i + longDay < stockPriceEntityList.size() ) {
				longMinReturn.addValue(stockPriceEntityList.get(i + longDay).getLowPrice().doubleValue());
				longMaxReturn.addValue(stockPriceEntityList.get(i + longDay).getHighPrice().doubleValue());
			} else {
				longMinReturn.addValue(stockPriceEntityList.get(stockPriceEntityList.size()-1).getLowPrice().doubleValue());
				longMaxReturn.addValue(stockPriceEntityList.get(stockPriceEntityList.size()-1).getHighPrice().doubleValue());
			}

			stockPriceEntityList.get(i).setShortMaxReturn(MathUtils.getPriceDiff(stockPriceEntityList.get(i).getClosePrice(), new BigDecimal(shortMaxReturn.getMax()), 3));
			stockPriceEntityList.get(i).setShortMinReturn(MathUtils.getPriceDiff(stockPriceEntityList.get(i).getClosePrice(), new BigDecimal(shortMinReturn.getMin()), 3));
			
			stockPriceEntityList.get(i).setMediumMaxReturn(MathUtils.getPriceDiff(stockPriceEntityList.get(i).getClosePrice(), new BigDecimal(mediumMaxReturn.getMax()), 3));
			stockPriceEntityList.get(i).setMediumMinReturn(MathUtils.getPriceDiff(stockPriceEntityList.get(i).getClosePrice(), new BigDecimal(mediumMinReturn.getMin()), 3));
			
			stockPriceEntityList.get(i).setLongMaxReturn(MathUtils.getPriceDiff(stockPriceEntityList.get(i).getClosePrice(), new BigDecimal(longMaxReturn.getMax()), 3));
			stockPriceEntityList.get(i).setLongMinReturn(MathUtils.getPriceDiff(stockPriceEntityList.get(i).getClosePrice(), new BigDecimal(longMinReturn.getMin()), 3));
		}
		stockPriceEntityList.get(stockPriceEntityList.size()-1).setShortMaxReturn(BigDecimal.ZERO);
		stockPriceEntityList.get(stockPriceEntityList.size()-1).setShortMinReturn(BigDecimal.ZERO);
		
		stockPriceEntityList.get(stockPriceEntityList.size()-1).setMediumMaxReturn(BigDecimal.ZERO);
		stockPriceEntityList.get(stockPriceEntityList.size()-1).setMediumMinReturn(BigDecimal.ZERO);
		
		stockPriceEntityList.get(stockPriceEntityList.size()-1).setLongMaxReturn(BigDecimal.ZERO);
		stockPriceEntityList.get(stockPriceEntityList.size()-1).setLongMinReturn(BigDecimal.ZERO);
		return;
	}
	
	
	public void generateDailyVolumeMovingAverageTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortSma, int mediumSma, int longSma) throws Exception {
		BarSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		VolumeIndicator dailyVolume = new VolumeIndicator(dailySeries);
				
		SMAIndicator dailyVolShortSma = new SMAIndicator(dailyVolume, shortSma);
		SMAIndicator dailyVolMediumSma = new SMAIndicator(dailyVolume, mediumSma);
		SMAIndicator dailyVolLongSma = new SMAIndicator(dailyVolume, longSma);
		
		for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
			
			stockPriceEntity.setVolShortSma(new BigDecimal(dailyVolShortSma.getValue(i).doubleValue()).setScale(0, RoundingMode.HALF_UP));
			stockPriceEntity.setVolMediumSma(new BigDecimal(dailyVolMediumSma.getValue(i).doubleValue()).setScale(0, RoundingMode.HALF_UP));
			stockPriceEntity.setVolLongSma(new BigDecimal(dailyVolLongSma.getValue(i).doubleValue()).setScale(0, RoundingMode.HALF_UP));
			
		}
		return;
	}
	
	public void generateDailyMovingAverageAndBBTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortSma, int mediumSma, int longSma) throws Exception {
		BarSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		VolumeIndicator dailyVolume = new VolumeIndicator(dailySeries);
		
		SMAIndicator dailyShortSma = new SMAIndicator(dailyClosePrice, shortSma);
		SMAIndicator dailyMediumSma = new SMAIndicator(dailyClosePrice, mediumSma);
		SMAIndicator dailyLongSma = new SMAIndicator(dailyClosePrice, longSma);
		
		SMAIndicator dailyVolShortSma = new SMAIndicator(dailyVolume, shortSma);
		SMAIndicator dailyVolMediumSma = new SMAIndicator(dailyVolume, mediumSma);
		SMAIndicator dailyVolLongSma = new SMAIndicator(dailyVolume, longSma);
		
	    StandardDeviationIndicator standardDeviation = new StandardDeviationIndicator(dailyClosePrice, 20);
	    BollingerBandsMiddleIndicator bbmSMA = new BollingerBandsMiddleIndicator(dailyMediumSma);
	    BollingerBandsUpperIndicator bbuSMA = new BollingerBandsUpperIndicator(bbmSMA, standardDeviation);
	    BollingerBandsLowerIndicator bblSMA = new BollingerBandsLowerIndicator(bbmSMA, standardDeviation);

		for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
			stockPriceEntity.setShortSma(new BigDecimal(dailyShortSma.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setMediumSma(new BigDecimal(dailyMediumSma.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setLongSma(new BigDecimal(dailyLongSma.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
			
			stockPriceEntity.setVolShortSma(new BigDecimal(dailyVolShortSma.getValue(i).doubleValue()).setScale(0, RoundingMode.HALF_UP));
			stockPriceEntity.setVolMediumSma(new BigDecimal(dailyVolMediumSma.getValue(i).doubleValue()).setScale(0, RoundingMode.HALF_UP));
			stockPriceEntity.setVolLongSma(new BigDecimal(dailyVolLongSma.getValue(i).doubleValue()).setScale(0, RoundingMode.HALF_UP));
			
			stockPriceEntity.setBbUpper(new BigDecimal(bbuSMA.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
			stockPriceEntity.setBbLower(new BigDecimal(bblSMA.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
		}
		return;
	}
	
	public void generateDailyMacdTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortEma, int longEma, int ema) throws Exception {
		BarSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
        MACDIndicator dailyMacd = new MACDIndicator(dailyClosePrice, shortEma, longEma);
        EMAIndicator dailyMacdSignal = new EMAIndicator(dailyMacd, ema);
        for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
    		stockPriceEntity.setMacd(new BigDecimal(dailyMacd.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
    		stockPriceEntity.setMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).doubleValue()).setScale(3, RoundingMode.HALF_UP));
        }
		return;
	}
	
	public void generateDailyRsiTechnicalIndicator(String stockCode, List<StockPriceEntity> stockPriceEntityList, int shortRsi, int longRsi) throws Exception {
		BarSeries dailySeries = this.getStockTimeSeries(stockCode, stockPriceEntityList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		RSIIndicator shortRsiIndicator = new RSIIndicator(dailyClosePrice, shortRsi);
		RSIIndicator longRsiIndicator = new RSIIndicator(dailyClosePrice, longRsi);
        for(int i=0; i<stockPriceEntityList.size(); i++) {
        	StockPriceEntity stockPriceEntity = stockPriceEntityList.get(i);
        	stockPriceEntity.setShortRsi(new BigDecimal(shortRsiIndicator.getValue(i).doubleValue()).setScale(3,RoundingMode.HALF_UP));
    		stockPriceEntity.setLongRsi(new BigDecimal(longRsiIndicator.getValue(i).doubleValue()).setScale(3,RoundingMode.HALF_UP));
        }
		return;
	}
	
	public void generateDailyPriceDiff(List<StockPriceEntity> stockPriceEntityList) throws Exception {
		for(int i=1; i<stockPriceEntityList.size(); i++) {
			StockPriceEntity current = stockPriceEntityList.get(i);
			StockPriceEntity previous = stockPriceEntityList.get(i-1);
			BigDecimal diffPrice = MathUtils.getPriceDiff(previous.getClosePrice(), current.getClosePrice(), 2);
			current.setDiffPrice(diffPrice);
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
				if( dailyStockPrice.getHighPrice().compareTo(weeklyStockPrice.getHighPrice()) > 0 ) {
					weeklyStockPrice.setHighPrice(dailyStockPrice.getHighPrice());
				}
				if( dailyStockPrice.getLowPrice().compareTo(weeklyStockPrice.getLowPrice()) < 0 ) {
					weeklyStockPrice.setLowPrice(dailyStockPrice.getLowPrice());
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
    			if( dateStockPriceEntity.getHighPrice().compareTo(weeklyStockPriceEntity.getHighPrice()) > 0 ) {
    				weeklyStockPriceEntity.setHighPrice(dateStockPriceEntity.getHighPrice());
    			}
    			if( dateStockPriceEntity.getLowPrice().compareTo(weeklyStockPriceEntity.getLowPrice()) < 0 ) {
    				weeklyStockPriceEntity.setLowPrice(dateStockPriceEntity.getLowPrice());
    			}
    			if( dateStockPriceEntity.getDayVolume() != null ) {
    				weeklyStockPriceEntity.setDayVolume(weeklyStockPriceEntity.getDayVolume().add(dateStockPriceEntity.getDayVolume()));
    			}
    		}
    	}
    	return weeklyStockPriceEntity;
	}
	
	public void generateWeeklyMacdTechnicalIndicator(String stockCode, List<StockPriceEntity> dailyStockPriceList, int shortSma, int longSma, int ema) throws Exception {
		/*
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
        		//stockPriceEntity.setWeeklyMacd(new BigDecimal(weeklyMacd.getValue(index).toDouble()));
        		//stockPriceEntity.setWeeklyMacdSignal(new BigDecimal(weeklyMacdSignal.getValue(index).toDouble()));
        		//stockPriceEntity.setWeeklyMacdHistogram(stockPriceEntity.getWeeklyMacd().subtract(stockPriceEntity.getWeeklyMacdSignal()));
        	}
        }
        */
		return;
	}

	/*
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
	*/
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
        /*
		TimeSeries dailySeries = this.getStockTimeSeries(stock.getStockCode(), dailyStockPriceList);
		ClosePriceIndicator dailyClosePrice = new ClosePriceIndicator(dailySeries);
		
		SmoothedRSIIndicator rsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, 14);
		SmoothedRSIIndicator shortRsiIndicator = new SmoothedRSIIndicator(dailyClosePrice, 5);
		
		//MACDPercentageIndicator dailyMacd = new MACDPercentageIndicator(dailyClosePrice, 12, 26);
		MACDIndicator dailyMacd = new MACDIndicator(dailyClosePrice, 12, 26);
        EMAIndicator dailyMacdSignal = new EMAIndicator(dailyMacd, 9);
        FixedIndicator<Decimal> dailyMacdHistogram = new FixedIndicator<Decimal>();
        for(int i=0; i<dailyMacd.getTimeSeries().getTickCount(); i++) {
        	dailyMacdHistogram.addValue(dailyMacd.getValue(i).minus(dailyMacdSignal.getValue(i)));
        }
        
        //SMA
        SMAIndicator dailyShortSma = new SMAIndicator(dailyClosePrice, 10);
        SMAIndicator dailyMediumSma = new SMAIndicator(dailyClosePrice, 20);
        SMAIndicator dailyLongSma = new SMAIndicator(dailyClosePrice, 50);
        
		TimeSeries weeklySeries = this.getStockTimeSeries(stock.getStockCode(), weeklyStockPriceList);
		ClosePriceIndicator weeklyClosePrice = new ClosePriceIndicator(weeklySeries);
		//MACDPercentageIndicator weeklyMacd = new MACDPercentageIndicator(weeklyClosePrice, 12, 26);
		
		SmoothedRSIIndicator weeklyRsi = new SmoothedRSIIndicator(weeklyClosePrice, 14);
		MACDIndicator weeklyMacd = new MACDIndicator(weeklyClosePrice, 12, 26);
        EMAIndicator weeklyMacdSignal = new EMAIndicator(weeklyMacd, 9);
        FixedIndicator<Decimal> weeklyMacdHistogram = new FixedIndicator<Decimal>();
        for(int i=0; i<weeklyMacd.getTimeSeries().getTickCount(); i++) {
        	weeklyMacdHistogram.addValue(weeklyMacd.getValue(i).minus(weeklyMacdSignal.getValue(i)));
        }
        */
		StockPriceVo stockPriceVo = null, prevDailyStockPriceVo = null, prevWeeklyStockPriceVo = null;
		int currentDailyMacdTrend = 0, previousDailyMacdTrend = 0;
		//int currentWeeklyMacdTrend = 0, previousWeeklyMacdTrend = 0;
		int j=3;
		for(int i=5; i<dailyStockPriceList.size(); i++) {
			//System.out.println(i + " : " + dailyMacd.getValue(i) + " : " + dailyMacdSignal.getValue(i) + " : " + dailyMacdHistogram.getValue(i));
			stockPriceVo = new StockPriceVo(dailyStockPriceList.get(i));
			//MACD
			//stockPriceVo.setDailyMacd(new BigDecimal(dailyMacd.getValue(i).toDouble()).setScale(5, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMacdSignal(new BigDecimal(dailyMacdSignal.getValue(i).toDouble()).setScale(5, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMacdHistogram(new BigDecimal(dailyMacdHistogram.getValue(i).toDouble()).setScale(5, RoundingMode.HALF_UP));
        	
        	if( stockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) > 0 ) {
        		currentDailyMacdTrend = 1;
        	} else if( stockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 ) {
        		currentDailyMacdTrend = -1;
        	} else if( stockPriceVo.getDailyMacdHistogram().compareTo(BigDecimal.ZERO) == 0 ) {
        		currentDailyMacdTrend = previousDailyMacdTrend;
        	}
        	if( stockDataList.size() > 2 ) {
        		if( currentDailyMacdTrend != previousDailyMacdTrend ) {
        			stockPriceVo.setDailyMacdHistogramChange(stockPriceVo.getDailyMacdHistogram().subtract(prevDailyStockPriceVo.getDailyMacdHistogram()));	
        		}
        	}
        	//RSI
        	//stockPriceVo.setDailyLongRsi(new BigDecimal(rsiIndicator.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyShortRsi(new BigDecimal(shortRsiIndicator.getValue(i).toDouble()).setScale(3, RoundingMode.HALF_UP));
        	
        	//SMA
        	//stockPriceVo.setDailyShortSma(new BigDecimal(dailyShortSma.getValue(i).toDouble()).setScale(5, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyMediumSma(new BigDecimal(dailyMediumSma.getValue(i).toDouble()).setScale(5, RoundingMode.HALF_UP));
        	//stockPriceVo.setDailyLongSma(new BigDecimal(dailyLongSma.getValue(i).toDouble()).setScale(5, RoundingMode.HALF_UP));
        	/*
        	//Weekly Technical Indicator
        	if( j<weeklyStockPriceList.size() ) {
	        	if( dailyStockPriceList.get(i).getTradeDate().compareTo(weeklyStockPriceList.get(j).getTradeDate()) == 0 ) {
        			//System.out.println(j + " : " + weeklyMacd.getValue(j) + " : " + weeklyMacdSignal.getValue(j) + " : " + weeklyMacdHistogram.getValue(j));
	        		stockPriceVo.setWeeklyRsi(new BigDecimal(weeklyRsi.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	        		stockPriceVo.setWeeklyMacd(new BigDecimal(weeklyMacd.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	stockPriceVo.setWeeklyMacdSignal(new BigDecimal(weeklyMacdSignal.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	stockPriceVo.setWeeklyMacdHistogram(new BigDecimal(weeklyMacdHistogram.getValue(j).toDouble()).setScale(3, RoundingMode.HALF_UP));
	            	if( stockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) > 0 ) {
	            		currentWeeklyMacdTrend = 1;
	            	} else if( stockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) < 0 ) {
	            		currentWeeklyMacdTrend = -1;
	            	} else if( stockPriceVo.getWeeklyMacdHistogram().compareTo(BigDecimal.ZERO) == 0 ) {
	            		currentWeeklyMacdTrend = previousWeeklyMacdTrend;
	            	}
	            	if( weeklyStockPriceList.size() > 2 ) {
	            		if( currentWeeklyMacdTrend != previousWeeklyMacdTrend ) {
	            			stockPriceVo.setWeeklyMacdHistogramChange(stockPriceVo.getWeeklyMacdHistogram().subtract(prevWeeklyStockPriceVo.getWeeklyMacdHistogram()));	
	            		}
	            	}
	            	previousWeeklyMacdTrend = currentWeeklyMacdTrend;
	            	prevWeeklyStockPriceVo = stockPriceVo;
	            	j++;
	        	}
        	}
        	*/
        	prevDailyStockPriceVo = stockPriceVo;
        	previousDailyMacdTrend = currentDailyMacdTrend;
			stockDataList.add(stockPriceVo);
		}
        
        return stockDataList;
	}
	
	/**
	 * Get All Stock Index correlation
	 * Reference: Stockist.json
	 * @return
	 * @throws Exception
	 */
	public List<Object> getAllStockIndexCorrelationList() throws Exception {
		int size = 20;
		List<StockPriceEntity> hsiPriceList = this.getLastDailyStockPriceEntityList(StockEntity.HSI, size);
		List<StockPriceEntity> hsceiPriceList = this.getLastDailyStockPriceEntityList(StockEntity.HSCEI, size);
		
		List<Object> stockDataList = new ArrayList<Object>();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			if( stock.getStockCode().equals(StockEntity.HSI) || stock.getStockCode().equals(StockEntity.HSCEI) ) continue; 
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("code", stock.getTrimStockHexCode());
			dataMap.put("name", stock.getStockCname());
			/*
			dataMap.put("hsiratio", stock.getHsiRatio());
			dataMap.put("hsceiratio", stock.getHsceiRatio());
			List<StockPriceEntity> stockPriceList = this.getLastDailyStockPriceEntityList(stock.getStockCode(), size);
			if( stock.getHsiRatio().compareTo(BigDecimal.ZERO) > 0 ) {
				double corr = new PearsonsCorrelation().correlation(this.getDoubleArray(hsiPriceList), this.getDoubleArray(stockPriceList));
				dataMap.put("hsicor", String.format("%.3f", corr));
			} 
			if( stock.getHsceiRatio().compareTo(BigDecimal.ZERO) > 0 ) {
				double corr = new PearsonsCorrelation().correlation(this.getDoubleArray(hsceiPriceList), this.getDoubleArray(stockPriceList));
				dataMap.put("hsceicor", String.format("%.3f", corr));
			}
			BigDecimal diff = MathUtils.getPriceDiff(stockPriceList.get(size-2).getClosePrice(), stockPriceList.get(size-1).getClosePrice(), 2);
			dataMap.put("close", stockPriceList.get(size-1).getClosePrice());
			dataMap.put("diff", diff.toString());
			*/
			/*
			"signal":"2020-02-02",
			"type":"B"
			*/
			stockDataList.add(dataMap);
		}
		//stockMap.put("StockDataList", stockDataList);
		//stockMap.put("UpdateDate", hsiPriceList.get(hsiPriceList.size()-1).getTradeDate().toString());
		return stockDataList;
	}

	private double[] getDoubleArray(List<StockPriceEntity> priceList) {
		double[] priceArray = new double[priceList.size()];
		for(int i=0; i<priceList.size(); i++) {
			priceArray[i] = priceList.get(i).getClosePrice().doubleValue();
		}
		return priceArray;
	}	
}
 