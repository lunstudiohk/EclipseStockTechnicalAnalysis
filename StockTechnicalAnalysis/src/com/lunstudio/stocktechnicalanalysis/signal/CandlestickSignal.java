package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class CandlestickSignal extends BaseSignal {

	public CandlestickSignal(SignalIndicatorPattern pattern) {
		super(pattern);
		return;
	}

	public static BaseSignal[] getGapPattern(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.GapUp);
		signals[1] = new SmaSignal(SignalIndicatorPattern.GapDown);
		
		StockPriceEntity prevStockPrice = stockPriceList.get(0);
		for(int i=1; i<stockPriceList.size(); i++) {
			StockPriceEntity currentStockPrice = stockPriceList.get(i);
			
			if( currentStockPrice.getOpenPrice().compareTo(prevStockPrice.getHighPrice()) > 0 ) {
				signals[0].tradeIndexList.add(i);
			} else if( currentStockPrice.getOpenPrice().compareTo(prevStockPrice.getLowPrice()) < 0 ) {
				signals[1].tradeIndexList.add(i);
			}
				
			prevStockPrice = stockPriceList.get(i);
		}
		return signals;
	}
	
	public static BaseSignal[] getCandlestickType(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.WhiteCandlestick);
		signals[1] = new SmaSignal(SignalIndicatorPattern.BlackCandlestick);
		for(int i=0; i<stockPriceList.size(); i++) {
			CandleStickVo candlestick = new CandleStickVo(stockPriceList.get(i));
			if( candlestick.isHollow() ) {
				signals[0].tradeIndexList.add(i);
			} else if( candlestick.isFilled() ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getCandlestickSize(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.LongCandlestick);
		signals[1] = new SmaSignal(SignalIndicatorPattern.ShortCandlestick);
		for(int i=0; i<stockPriceList.size(); i++) {
			CandleStickVo candlestick = new CandleStickVo(stockPriceList.get(i));
			if( candlestick.isLongCandlestick() ) {
				signals[0].tradeIndexList.add(i);
			} else if( candlestick.isShortCandlestick() ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	
	public static BaseSignal[] getCandlestickBodySize(List<StockPriceEntity> stockPriceList) throws Exception {
		SmaSignal[] signals = new SmaSignal[2];
		signals[0] = new SmaSignal(SignalIndicatorPattern.LongCandlestickBody);
		signals[1] = new SmaSignal(SignalIndicatorPattern.ShortCandlestickBody);
		for(int i=0; i<stockPriceList.size(); i++) {
			CandleStickVo candlestick = new CandleStickVo(stockPriceList.get(i));
			if( candlestick.isLongBody() ) {
				signals[0].tradeIndexList.add(i);
			} else if( candlestick.isShortBody() ) {
				signals[1].tradeIndexList.add(i);
			}
		}
		return signals;
	}
	public static BaseSignal[] getCandlestickPattern(List<StockPriceEntity> stockPriceList) throws Exception {
		SignalIndicatorPattern[] patterns = { 
				SignalIndicatorPattern.BearishBeltHold, SignalIndicatorPattern.BullishBeltHold, SignalIndicatorPattern.BullishHammer, 
				SignalIndicatorPattern.BearishDojiStar, SignalIndicatorPattern.BullishDojiStar, SignalIndicatorPattern.BullishEngulfing,
				SignalIndicatorPattern.BullishHarami, SignalIndicatorPattern.BullishHaramiCross, SignalIndicatorPattern.BullishMeetingLines,
				SignalIndicatorPattern.BullishSeparatingLines, SignalIndicatorPattern.BullishTasukiLine, SignalIndicatorPattern.BullishHomingPigeon,
				SignalIndicatorPattern.BullishInvertedHammer, SignalIndicatorPattern.BullishKicking, SignalIndicatorPattern.BullishMatchingLow,
				SignalIndicatorPattern.BullishPiercing, SignalIndicatorPattern.BullishRisingWindow, SignalIndicatorPattern.BullishTurnUp,
				SignalIndicatorPattern.BullishTweezersBottom, SignalIndicatorPattern.BullishGappingUpDoji, SignalIndicatorPattern.BullishSouthernDoji,
				SignalIndicatorPattern.BullishTakuriLine
		};
		int index = 0;
		SmaSignal[] signals = new SmaSignal[patterns.length];
		for(SignalIndicatorPattern pattern : patterns) {
			signals[index] = new SmaSignal(pattern);
			if( pattern == SignalIndicatorPattern.BullishRisingWindow 
					|| pattern == SignalIndicatorPattern.BullishGappingUpDoji ) {
				signals[index++].isTriggerSignal = false;
			} else {
				signals[index++].isTriggerSignal = true;
			}
		}
		
		for(int i=2; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( isBearishBeltHold(stockPrice) ) {
				signals[0].tradeIndexList.add(i);
			}
			if( isBullishBeltHold(stockPrice) ) {
				signals[1].tradeIndexList.add(i);
			}
			if( isBullishHammer(stockPrice) ) {
				signals[2].tradeIndexList.add(i);
			}
			if( isBearishDojiStar(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[3].tradeIndexList.add(i);
			}
			if( isBullishDojiStar(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[4].tradeIndexList.add(i);
			}
			if( isBullishEngulfing(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[5].tradeIndexList.add(i);
			}
			if( isBullishHarami(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[6].tradeIndexList.add(i);
			}
			if( isBullishHaramiCross(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[7].tradeIndexList.add(i);
			}
			if( isBullishMeetingLines(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[8].tradeIndexList.add(i);
			}
			if( isBullishSeparatingLines(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[9].tradeIndexList.add(i);
			}
			if( isBullishTasukiLine(stockPriceList.get(i-2), stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[10].tradeIndexList.add(i);
			}
			if (isBullishHomingPigeon(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[11].tradeIndexList.add(i);
			}
			if( isBullishInvertedHammer(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[12].tradeIndexList.add(i);
			}
			if( isBullishKicking(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[13].tradeIndexList.add(i);
			}
			if( isBullishMatchingLow(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[14].tradeIndexList.add(i);
			}
			if( isBullishPiercing(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[15].tradeIndexList.add(i);
			}
			if( isBullishRisingWindow(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[16].tradeIndexList.add(i);
			}
			if( isBullishTurnUp(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[17].tradeIndexList.add(i);
			}
			if( isBullishTweezersBottom(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[18].tradeIndexList.add(i);
			}
			if( isBullishGappingUpDoji(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[19].tradeIndexList.add(i);
			}
			if( isBullishSouthernDoji(stockPriceList.get(i-1), stockPriceList.get(i)) ) {
				signals[20].tradeIndexList.add(i);
			}
			if( isBullishTakuriLine(stockPriceList.get(i)) ) {
				signals[21].tradeIndexList.add(i);
			}
			
		}
		
		/*
		System.out.print("[" + signals[8].tradeIndexList.size() + "] = ");
		for(int i:signals[8].tradeIndexList) {
			System.out.print(stockPriceList.get(i).getTradeDate() + " ; ");
		}
		System.out.println("");
		*/
		return signals;
	}
	
	//One-line pattern Start
	private static boolean isBearishBeltHold(StockPriceEntity stockPrice) throws Exception {
		CandleStickVo candlestick = new CandleStickVo(stockPrice);
		if( candlestick.isLongCandlestick() ) {
			if( candlestick.isFilled() ) {
				if( candlestick.isNoUpperShadow() ) {
					if( candlestick.isShortLowerShadow() ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean isBullishBeltHold(StockPriceEntity stockPrice) throws Exception {
		CandleStickVo candlestick = new CandleStickVo(stockPrice);
		if( candlestick.isLongCandlestick() ) {
			if( candlestick.isHollow() ) {
				if( candlestick.isNoLowerShadow() ) {
					if( candlestick.isShortUpperShadow() ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean isBullishHammer(StockPriceEntity stockPrice) throws Exception {
		CandleStickVo candlestick = new CandleStickVo(stockPrice);
		if( candlestick.getUpperShadowLength().compareTo(candlestick.getBodyLength()) < 0 ) {
			if( candlestick.getLowerShadowLength().compareTo(candlestick.getBodyLength().multiply(BigDecimal.valueOf(2))) > 0 ) {
				if( candlestick.isMedianCandlestick() ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isBullishTakuriLine(StockPriceEntity stockPrice) throws Exception {
		CandleStickVo candlestick = new CandleStickVo(stockPrice);
		if( candlestick.isLongCandlestick() ) {
			if( candlestick.isNoUpperShadow() ) {
				if( candlestick.getLowerShadowLength().compareTo(candlestick.getBodyLength().multiply(BigDecimal.valueOf(3))) > 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	
	//One-line pattern End
	
	
	
	//Two-Line pattern start
	private static boolean isBearishDojiStar(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( firstCandlestick.isHollow() && firstCandlestick.isLongCandlestick() ) {
			if( secondCandlestick.isDoji() ) {
				if( firstCandlestick.getTop().compareTo(secondCandlestick.getBottom()) < 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isBullishDojiStar(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( firstCandlestick.isFilled() && firstCandlestick.isLongCandlestick() ) {
			if( secondCandlestick.isDoji() ) {
				if( firstCandlestick.getBottom().compareTo(secondCandlestick.getTop()) > 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isBullishEngulfing(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isHollow() && secondCandlestick.isMedianCandlestick() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishHarami(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( firstCandlestick.isFilled() && firstCandlestick.isMedianCandlestick() ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishHaramiCross(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( firstCandlestick.isFilled() && firstCandlestick.isMedianCandlestick() ) {
			if( secondCandlestick.isDoji() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishMeetingLines(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( firstCandlestick.isFilled() && firstCandlestick.isLongCandlestick() ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) == 0  ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isBullishSeparatingLines(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isFilled() && firstCandlestick.isLongCandlestick() ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) == 0  ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isBullishTasukiLine(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2, StockPriceEntity stockPrice3) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		CandleStickVo thirdCandlestick = new CandleStickVo(stockPrice3);
		
		if( secondCandlestick.isFilled() && secondCandlestick.getHighPrice().compareTo(firstCandlestick.getLowPrice()) > 0 && secondCandlestick.isLongCandlestick() ) {
			if( thirdCandlestick.isHollow() ) {
				if( thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) >= 0 && thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
					if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
						if( thirdCandlestick.isLongCandlestick() ) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishHomingPigeon(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isFilled() ) {
				if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) > 0 && firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isBullishInvertedHammer(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isShortBody() ) {
				if( secondCandlestick.getLowerShadowLength().compareTo(secondCandlestick.getBodyLength()) < 0
						&& secondCandlestick.getUpperShadowLength().compareTo(secondCandlestick.getBodyLength().multiply(BigDecimal.valueOf(2.5))) > 0 ) {
					if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) <= 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishKicking(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isFilled() && firstCandlestick.isLongCandlestick() ) {
			if( firstCandlestick.isNoUpperShadow() && firstCandlestick.isNoLowerShadow() ) {
				if( secondCandlestick.isHollow() && secondCandlestick.isLongCandlestick() ) {
					if( secondCandlestick.isNoUpperShadow() && secondCandlestick.isNoLowerShadow() ) {
						if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getHighPrice()) >= 0 ) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishMatchingLow(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isFilled() && firstCandlestick.isLongCandlestick() && firstCandlestick.isNoLowerShadow() ) {
			if( secondCandlestick.isFilled() && secondCandlestick.isNoLowerShadow() ) {
				if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getOpenPrice()) < 0 ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) == 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishPiercing(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isHollow() ) {
				if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) <= 0 ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getBodyMiddle()) >= 0 ) {
						if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getOpenPrice()) <= 0 ) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishRisingWindow(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.isHollow() ) {
			if( (firstCandlestick.getOpenPrice().compareTo(firstCandlestick.getShortSma()) < 0 && firstCandlestick.getClosePrice().compareTo(firstCandlestick.getShortSma()) > 0)
					|| (firstCandlestick.getOpenPrice().compareTo(firstCandlestick.getMediumSma()) < 0 && firstCandlestick.getClosePrice().compareTo(firstCandlestick.getMediumSma()) > 0) 
					|| (firstCandlestick.getOpenPrice().compareTo(firstCandlestick.getLongSma()) < 0 && firstCandlestick.getClosePrice().compareTo(firstCandlestick.getLongSma()) > 0) ) {				
				if( secondCandlestick.isHollow() ) {
					if( secondCandlestick.getLowPrice().compareTo(firstCandlestick.getHighPrice()) > 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static boolean isBullishTurnUp(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( (firstCandlestick.getTop().compareTo(firstCandlestick.getShortSma()) < 0 && firstCandlestick.getHighPrice().compareTo(firstCandlestick.getShortSma()) > 0)
				|| (firstCandlestick.getTop().compareTo(firstCandlestick.getMediumSma()) < 0 && firstCandlestick.getHighPrice().compareTo(firstCandlestick.getMediumSma()) > 0) 
				|| (firstCandlestick.getTop().compareTo(firstCandlestick.getLongSma()) < 0 && firstCandlestick.getHighPrice().compareTo(firstCandlestick.getLongSma()) > 0) ) {				
			if( secondCandlestick.isHollow() ) {
				if( (secondCandlestick.getLowPrice().compareTo(firstCandlestick.getShortSma()) < 0 && secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getShortSma()) > 0)
						|| (secondCandlestick.getLowPrice().compareTo(firstCandlestick.getMediumSma()) < 0 && secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getMediumSma()) > 0) 
						|| (secondCandlestick.getLowPrice().compareTo(firstCandlestick.getLongSma()) < 0 && secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getLongSma()) > 0) ) {
						return true;

				}
			}
		}
		return false;
	}
	private static boolean isBullishTweezersBottom(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( firstCandlestick.getLowPrice().compareTo(secondCandlestick.getLowPrice()) == 0 ) {
			return true;
		}
		return false;
	}
	private static boolean isBullishGappingUpDoji(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		
		if( secondCandlestick.getLowPrice().compareTo(firstCandlestick.getHighlowMedian()) > 0 ) {
			if( secondCandlestick.isDoji() ) {
				return true;
			}
		}
		return false;
	}
	private static boolean isBullishSouthernDoji(StockPriceEntity stockPrice1, StockPriceEntity stockPrice2) throws Exception {
		CandleStickVo firstCandlestick = new CandleStickVo(stockPrice1);
		CandleStickVo secondCandlestick = new CandleStickVo(stockPrice2);
		if( secondCandlestick.isDoji() ) {
			if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getBottom()) < 0 ) {
				if( secondCandlestick.getHighPrice().compareTo(firstCandlestick.getLowPrice()) > 0 ) {
					if( secondCandlestick.getHighPrice().compareTo(secondCandlestick.getShortSma()) <= 0
							|| secondCandlestick.getHighPrice().compareTo(secondCandlestick.getMediumSma()) <= 0
							|| secondCandlestick.getHighPrice().compareTo(secondCandlestick.getLongSma()) <= 0 ) {
						return true;
					}
				}
			}

		}
		return false;
	}
	
	//Two-Line pattern end
	
	
	
}
