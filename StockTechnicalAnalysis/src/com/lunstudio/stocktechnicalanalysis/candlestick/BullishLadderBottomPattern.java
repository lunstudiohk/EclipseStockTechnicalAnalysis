package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishLadderBottomPattern extends BullishCandlestickPatterns implements CandlestickPattern {
	
	public BullishLadderBottomPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		//super.pattern = BullishPatterns.BullishLadderBottom;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-4));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-3));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo forthCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo fifthCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isFilled() ) {
			if( secondCandlestick.isFilled() ) {
				//if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
					if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
						if( thirdCandlestick.isFilled() ) {
							//if( secondCandlestick.getOpenPrice().compareTo(thirdCandlestick.getOpenPrice()) > 0 ) {
								if( secondCandlestick.getClosePrice().compareTo(thirdCandlestick.getClosePrice()) > 0 ) {
									if( forthCandlestick.isFilled() ) {
										if( forthCandlestick.isLongUpperShadow() && forthCandlestick.isShortLowerShadow() ) {
											if( thirdCandlestick.getClosePrice().compareTo(forthCandlestick.getClosePrice()) > 0 ) {
												if( fifthCandlestick.isHollow() ) {
													if( fifthCandlestick.isGapUp(forthCandlestick) ) {
														super.init(fifthCandlestick);
														super.candlestickEntity.setConfirmPrice(fifthCandlestick.getClosePrice());
														super.candlestickEntity.setStoplossPrice(fifthCandlestick.getDayLow());
														return true;
													}
												}
											}
										}
									}
								}
							//}
						}
					//}
				}
			}
		}
		
		return false;
	}
}
