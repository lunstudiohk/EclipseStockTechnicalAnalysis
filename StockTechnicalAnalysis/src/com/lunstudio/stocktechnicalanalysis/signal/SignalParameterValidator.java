package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.StockPriceVo;

public class SignalParameterValidator {

	private static final Logger logger = LogManager.getLogger();

	public static boolean isRsiRangeValid(List<StockPriceVo> stockPriceVoList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getLowerDailyRsi() != null && signal.getUpperDailyRsi() != null ) {
			if( stockPriceVoList.get(tradeIndex).getDailyLongRsi().compareTo(signal.getLowerDailyRsi()) > 0 
					&& stockPriceVoList.get(tradeIndex).getDailyLongRsi().compareTo(signal.getUpperDailyRsi()) < 0 ) {
				//logger.info(String.format("Date: %s , [RSI: %s - %s]", this.stockPriceVoList.get(tradeIndex).getTradeDate(), signal.getLowerDailyRsi(), signal.getUpperDailyRsi()));
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSmaPriceDiffValid(List<StockPriceVo> stockPriceVoList, StockSignalEntity signal, Integer tradeIndex) {
		StockPriceVo vo = stockPriceVoList.get(tradeIndex);
		if( signal.getUpperDailySma() != null ) {
			if( signal.getUpperDailySma().intValue() == 10 ) {
				if( MathUtils.getPriceDiff(vo.getDailyShortSma(), vo.getClosePrice(), 2).compareTo(signal.getUpperPriceDiff()) > 0 ) {
					return true;
				}
			} else if( signal.getUpperDailySma().intValue() == 20 ) {
				if( MathUtils.getPriceDiff(vo.getDailyMediumSma(), vo.getClosePrice(), 2).compareTo(signal.getUpperPriceDiff()) > 0 ) {
					return true;
				}
			} else if( signal.getUpperDailySma().intValue() == 50 ) {
				if( MathUtils.getPriceDiff(vo.getDailyLongSma(), vo.getClosePrice(), 2).compareTo(signal.getUpperPriceDiff()) > 0 ) {
					return true;
				}
			}
		} else if( signal.getLowerDailySma() != null ) {
			if( signal.getLowerDailySma().intValue() == 10 ) {
				if( MathUtils.getPriceDiff(vo.getDailyShortSma(), vo.getClosePrice(), 2).compareTo(signal.getLowerPriceDiff()) < 0 ) {
					return true;
				}
			} else if( signal.getLowerDailySma().intValue() == 20 ) {
				if( MathUtils.getPriceDiff(vo.getDailyMediumSma(), vo.getClosePrice(), 2).compareTo(signal.getLowerPriceDiff()) < 0 ) {
					return true;
				}
			} else if( signal.getLowerDailySma().intValue() == 50 ) {
				if( MathUtils.getPriceDiff(vo.getDailyLongSma(), vo.getClosePrice(), 2).compareTo(signal.getLowerPriceDiff()) < 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isSmaTypeValid(List<StockPriceVo> stockPriceVoList, StockSignalEntity signal, Integer tradeIndex) {
		StockPriceVo vo = stockPriceVoList.get(tradeIndex);
		if( signal.getSmaType() != null ) {
			if( StockSignalEntity.SMA_SHORT_MEDIUM_LONG.compareTo(signal.getSmaType()) == 0 ) {
				if( vo.getDailyShortSma().compareTo(vo.getDailyMediumSma()) > 0 && vo.getDailyMediumSma().compareTo(vo.getDailyLongSma()) > 0 ) {
					return true;
				}
			} else if( StockSignalEntity.SMA_SHORT_LONG_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				if( vo.getDailyShortSma().compareTo(vo.getDailyLongSma()) > 0 && vo.getDailyLongSma().compareTo(vo.getDailyMediumSma()) > 0 ) {
					return true;
				}
			} else if( StockSignalEntity.SMA_MEDIUM_SHORT_LONG.compareTo(signal.getSmaType()) == 0 ) {
				if( vo.getDailyMediumSma().compareTo(vo.getDailyShortSma()) > 0 && vo.getDailyShortSma().compareTo(vo.getDailyLongSma()) > 0  ) {
					return true;
				}
			} else if( StockSignalEntity.SMA_MEDIUM_LONG_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				if( vo.getDailyMediumSma().compareTo(vo.getDailyLongSma()) > 0 && vo.getDailyLongSma().compareTo(vo.getDailyShortSma()) > 0 ) {
					return true;
				}
			} else if( StockSignalEntity.SMA_LONG_MEDIUM_SHORT.compareTo(signal.getSmaType()) == 0 ) {
				if( vo.getDailyLongSma().compareTo(vo.getDailyMediumSma()) > 0 && vo.getDailyMediumSma().compareTo(vo.getDailyShortSma()) > 0 ) {
					return true;
				}
			} else if( StockSignalEntity.SMA_LONG_SHORT_MEDIUM.compareTo(signal.getSmaType()) == 0 ) {
				if( vo.getDailyLongSma().compareTo(vo.getDailyShortSma()) > 0 && vo.getDailyShortSma().compareTo(vo.getDailyMediumSma()) > 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isCandlestickTypeValid(List<StockPriceVo> stockPriceVoList, StockSignalEntity signal, Integer tradeIndex) {
		
		if( signal.getCandlestickType() != null ) {
			if( StockSignalEntity.CANDLESTICK_HALLOW.compareTo(signal.getCandlestickType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getClosePrice().compareTo(stockPriceVoList.get(tradeIndex).getOpenPrice()) > 0 ) {
					//logger.info(String.format("Date: %s , [陽蠋] ", stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.CANDLESTICK_FILLED.compareTo(signal.getCandlestickType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getClosePrice().compareTo(stockPriceVoList.get(tradeIndex).getOpenPrice()) < 0 ) {
					//logger.info(String.format("Date: %s , [陰蠋] ", stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.CANDLESTICK_FILLED_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getOpenPrice().compareTo(stockPriceVoList.get(tradeIndex-1).getDayHigh()) > 0 && 
						stockPriceVoList.get(tradeIndex).getClosePrice().compareTo(stockPriceVoList.get(tradeIndex).getOpenPrice()) < 0 ) {
					//logger.info(String.format("Date: %s , [裂口] ", stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.CANDLESTICK_FILLED_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getOpenPrice().compareTo(stockPriceVoList.get(tradeIndex-1).getDayLow()) < 0 &&
						stockPriceVoList.get(tradeIndex).getClosePrice().compareTo(stockPriceVoList.get(tradeIndex).getOpenPrice()) < 0 ) {
					//logger.info(String.format("Date: %s , [大裂口] ", stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.CANDLESTICK_HALLOW_GAPUP.compareTo(signal.getCandlestickType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getOpenPrice().compareTo(stockPriceVoList.get(tradeIndex-1).getDayHigh()) > 0 &&
						stockPriceVoList.get(tradeIndex).getClosePrice().compareTo(stockPriceVoList.get(tradeIndex).getOpenPrice()) > 0	) {
					//logger.info(String.format("Date: %s , [裂口] ", stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.CANDLESTICK_HALLOW_GAPDOWN.compareTo(signal.getCandlestickType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getOpenPrice().compareTo(stockPriceVoList.get(tradeIndex-1).getDayLow()) < 0 &&
						stockPriceVoList.get(tradeIndex).getClosePrice().compareTo(stockPriceVoList.get(tradeIndex).getOpenPrice()) > 0 ) {
					//logger.info(String.format("Date: %s , [大裂口] ", stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			}
		}
		return false;
	}
	
	public static StockSignalEntity getValidSmaPeriodSignal(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getLowerPeriod() != null && signal2.getLowerPeriod() != null ) {
			if( signal1.getLowerPeriod().compareTo(signal2.getLowerPeriod()) > 0 ) {
				return signal2;
			} else {
				return signal1;
			}
		}
		return null;
	}
	
	public static StockSignalEntity getValidMacdPeriodSignal(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getLowerPeriod() != null && signal2.getLowerPeriod() != null ) {
			if( signal1.getLowerPeriod().compareTo(signal2.getLowerPeriod()) > 0 ) {
				return signal2;
			} else {
				return signal1;
			}
		}
		return null;
	}
	
	public static StockSignalEntity getValidRsiRangeSignal(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getUpperDailyRsi() != null && signal2.getUpperDailyRsi() != null ) {
			BigDecimal diff1 = signal1.getUpperDailyRsi().subtract(signal1.getLowerDailyRsi());
			BigDecimal diff2 = signal2.getUpperDailyRsi().subtract(signal2.getLowerDailyRsi());
			if( diff1.compareTo(diff2) <= 0 ) {
				return signal2;
			} else {
				return signal1;
			}
		}
		return null;
	}
	
	public static StockSignalEntity getValidLowerPriceDiffSignal(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getLowerPriceDiff() != null && signal2.getLowerPriceDiff() != null ) {
			if( signal1.getLowerPriceDiff().compareTo(signal2.getLowerPriceDiff()) > 0 ) {
				return signal2;
			} else {
				return signal1;
			}
		}
		return null;
	}
	
	public static StockSignalEntity getValidUpperPriceDiffSignal(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getUpperPriceDiff() != null && signal2.getUpperPriceDiff() != null ) {
			if( signal1.getUpperPriceDiff().compareTo(signal2.getUpperPriceDiff()) < 0 ) {
				return signal2;
			} else {
				return signal1;
			}
		}
		return null;
	}
	
	public static boolean isRsiTypeValid(List<StockPriceVo> stockPriceVoList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getRsiType() != null ) {
			if( StockSignalEntity.RSI_ABOVE.compareTo(signal.getRsiType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getDailyShortRsi().compareTo(stockPriceVoList.get(tradeIndex).getDailyLongRsi()) > 0 ) {
					//logger.info(String.format("Date: %s RSI-5 > RSI-14", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.RSI_BELOW.compareTo(signal.getRsiType()) == 0 ) {
				if( stockPriceVoList.get(tradeIndex).getDailyShortRsi().compareTo(stockPriceVoList.get(tradeIndex).getDailyLongRsi()) < 0 ) {
					//logger.info(String.format("Date: %s RSI-5 < RSI-14", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isMacdPriceNegativeDiffValid(List<StockPriceVo> stockPriceVoList, List<Integer> macdCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getLowerPriceDiff() != null ) {
			int macdIndex = macdCrossTradeIndexList.indexOf(tradeIndex);
			int prevTradeIndex = -1*macdCrossTradeIndexList.get(macdIndex-1);
			
			BigDecimal startPrice = stockPriceVoList.get(prevTradeIndex).getClosePrice();
			BigDecimal endPrice = stockPriceVoList.get(tradeIndex).getClosePrice();
			BigDecimal priceDiff = MathUtils.getPriceDiff(startPrice, endPrice, 3);
			if( priceDiff.compareTo(signal.getLowerPriceDiff()) < 0 ) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMacdPricePositiveDiffValid(List<StockPriceVo> stockPriceVoList, List<Integer> macdCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getUpperPriceDiff() != null ) {
			int macdIndex = macdCrossTradeIndexList.indexOf(-1*tradeIndex);
			int prevTradeIndex = macdCrossTradeIndexList.get(macdIndex-1);
			
			BigDecimal startPrice = stockPriceVoList.get(prevTradeIndex).getClosePrice();
			BigDecimal endPrice = stockPriceVoList.get(tradeIndex).getClosePrice();
			BigDecimal priceDiff = MathUtils.getPriceDiff(startPrice, endPrice, 3);
			if( priceDiff.compareTo(signal.getUpperPriceDiff()) > 0 ) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSmaBelowPeriodValid(List<StockPriceVo> stockPriceVoList, List<Integer> smaCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getLowerPeriod() != null ) {
			int smaIndex = smaCrossTradeIndexList.indexOf(tradeIndex);
			if( smaIndex == 0 ) {
				return false;
			}
			int smaCrossBelowIndex = -1*smaCrossTradeIndexList.get(smaIndex-1);
			if( tradeIndex - smaCrossBelowIndex > signal.getLowerPeriod() ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSmaAbovePeriodValid(List<StockPriceVo> stockPriceVoList, List<Integer> smaCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getLowerPeriod() != null ) {
			int smaIndex = smaCrossTradeIndexList.indexOf(-1*tradeIndex);
			if( smaIndex == 0 ) {
				return false;
			}
			int smaCrossAboveIndex = smaCrossTradeIndexList.get(smaIndex-1);
			if( tradeIndex - smaCrossAboveIndex > signal.getLowerPeriod() ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isMacdBelowPeriodValid(List<StockPriceVo> stockPriceVoList, List<Integer> macdCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getLowerPeriod() != null ) {
			int macdIndex = macdCrossTradeIndexList.indexOf(tradeIndex);
			if( macdIndex == 0 ) {
				return false;
			}
			int macdCrossDownIndex = -1*macdCrossTradeIndexList.get(macdIndex-1);
			if( tradeIndex - macdCrossDownIndex > signal.getLowerPeriod() ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isMacdAbovePeriodValid(List<StockPriceVo> stockPriceVoList, List<Integer> macdCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex) {
		if( signal.getLowerPeriod() != null ) {
			int macdIndex = macdCrossTradeIndexList.indexOf(-1*tradeIndex);
			if( macdIndex == 0 ) {
				return false;
			}
			int macdCrossAboveIndex = macdCrossTradeIndexList.get(macdIndex-1);
			if( tradeIndex - macdCrossAboveIndex > signal.getLowerPeriod() ) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDailyMacdTypeValid(List<StockPriceVo> stockPriceVoList, List<Integer> macdCrossTradeIndexList, StockSignalEntity signal, Integer tradeIndex, int macdIndex) {
		if( signal.getMacdType() != null ) {
			if( StockSignalEntity.MACD_ABOVE_ZERO == signal.getMacdType() ) {
				if( stockPriceVoList.get(tradeIndex).getDailyMacd().compareTo(BigDecimal.ZERO) > 0 ) {
					//logger.info(String.format("Date: %s MACD > 0", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.MACD_BELOW_ZERO == signal.getMacdType() ) {
				if( stockPriceVoList.get(tradeIndex).getDailyMacd().compareTo(BigDecimal.ZERO) < 0 ) {
					//logger.info(String.format("Date: %s MACD < 0", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
					return true;
				}
			} else if( StockSignalEntity.MACD_CROSS_ZERO == signal.getMacdType() ) {
				if( macdIndex > 0 ) {
					if( macdCrossTradeIndexList.get(macdIndex) > 0 ) {
						BigDecimal macdMin = SignalUtils.getMinDailyMacd(stockPriceVoList, -1*macdCrossTradeIndexList.get(macdIndex-1), tradeIndex);
						if( macdMin.compareTo(BigDecimal.ZERO) < 0 && stockPriceVoList.get(tradeIndex).getDailyMacd().compareTo(BigDecimal.ZERO) > 0 ) {
							//logger.info(String.format("Date: %s 0 > MACD > 0", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
							return true;
						}
					} else {
						BigDecimal macdMax = SignalUtils.getMaxDailyMacd(stockPriceVoList, macdCrossTradeIndexList.get(macdIndex-1), tradeIndex);
						if( macdMax.compareTo(BigDecimal.ZERO) > 0 && stockPriceVoList.get(tradeIndex).getDailyMacd().compareTo(BigDecimal.ZERO) < 0 ) {
							//logger.info(String.format("Date: %s 0 > MACD > 0", this.stockPriceVoList.get(tradeIndex).getTradeDate()));
							return true;
						}
					}
				}
			} else if( StockSignalEntity.MACD_HIGHER == signal.getMacdType() ) {
				if( macdIndex > 1 ) {
					if( macdCrossTradeIndexList.get(macdIndex) > 0 ) {
						BigDecimal currentMacd = stockPriceVoList.get(tradeIndex).getDailyMacd();
						BigDecimal previousMacd = stockPriceVoList.get(macdCrossTradeIndexList.get(macdIndex-2)).getDailyMacd();
						if( currentMacd.compareTo(previousMacd) > 0 ) {
							//logger.info(String.format("Date: %s %s > %s %s", this.stockPriceVoList.get(tradeIndex).getTradeDate(), currentMacd, this.stockPriceVoList.get(this.macdCrossTradeIndexList.get(macdIndex-2)).getTradeDate(), previousMacd ));
							return true;
						}
					} else {
						BigDecimal currentMacd = stockPriceVoList.get(tradeIndex).getDailyMacd();
						BigDecimal previousMacd = stockPriceVoList.get(-1*macdCrossTradeIndexList.get(macdIndex-2)).getDailyMacd();
						if( currentMacd.compareTo(previousMacd) > 0 ) {
							//logger.info(String.format("Date: %s %s > %s %s", this.stockPriceVoList.get(tradeIndex).getTradeDate(), currentMacd, this.stockPriceVoList.get(this.macdCrossTradeIndexList.get(macdIndex-2)).getTradeDate(), previousMacd ));
							return true;
						}
					}
				}
			} else if( StockSignalEntity.MACD_LOWER == signal.getMacdType() ) {
				if( macdIndex > 1 ) {
					if( macdCrossTradeIndexList.get(macdIndex) > 0 ) {
						BigDecimal currentMacd = stockPriceVoList.get(tradeIndex).getDailyMacd();
						BigDecimal previousMacd = stockPriceVoList.get(macdCrossTradeIndexList.get(macdIndex-2)).getDailyMacd();
						if( currentMacd.compareTo(previousMacd) < 0 ) {
							//logger.info(String.format("Date: %s %s < %s %s", this.stockPriceVoList.get(tradeIndex).getTradeDate(), currentMacd, this.stockPriceVoList.get(this.macdCrossTradeIndexList.get(macdIndex-2)).getTradeDate(), previousMacd));
							return true;
						}
					} else {
						BigDecimal currentMacd = stockPriceVoList.get(tradeIndex).getDailyMacd();
						BigDecimal previousMacd = stockPriceVoList.get(-1*macdCrossTradeIndexList.get(macdIndex-2)).getDailyMacd();
						if( currentMacd.compareTo(previousMacd) < 0 ) {
							//logger.info(String.format("Date: %s %s < %s %s", this.stockPriceVoList.get(tradeIndex).getTradeDate(), currentMacd, this.stockPriceVoList.get(this.macdCrossTradeIndexList.get(macdIndex-2)).getTradeDate(), previousMacd));
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public static List<StockSignalEntity> getLongestSmaPeriodSignal(List<StockSignalEntity> signalList) {
		List<StockSignalEntity> list = new ArrayList<StockSignalEntity>();
		StockSignalEntity longestPeriodSignal = null;
		for( StockSignalEntity signal : signalList) {
			if( longestPeriodSignal == null || longestPeriodSignal.getLowerPeriod().compareTo(signal.getLowerPeriod()) < 0 ) {
				longestPeriodSignal = signal;
			}
		}
		if( longestPeriodSignal != null ) {
			list.add(longestPeriodSignal);
		}
		return list;
	}
	
	public static StockSignalEntity getValidSmaUpperPriceDiff(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getUpperPriceDiff().compareTo(signal2.getUpperPriceDiff()) < 0 ) {
			return signal1;
		} else {
			return signal2;
		}
	}
	
	public static StockSignalEntity getValidSmaLowerPriceDiff(StockSignalEntity signal1, StockSignalEntity signal2) {
		if( signal1.getLowerPriceDiff().compareTo(signal2.getLowerPriceDiff()) > 0 ) {
			return signal1;
		} else {
			return signal2;
		}
	}
	
}
