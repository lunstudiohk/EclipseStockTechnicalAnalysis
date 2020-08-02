package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishAdvanceBlockPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishAdvanceBlockPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishAdvanceBlock;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() ) {
				if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getOpenPrice()) > 0 && secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) > 0 ) {

							if( secondCandlestick.getUpperShadowLength().compareTo(firstCandlestick.getUpperShadowLength()) > 0 ) {
								if( thirdCandlestick.isHollow() ) {
									if( thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getOpenPrice()) > 0 && thirdCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
										if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
											if( thirdCandlestick.getBodyLength().compareTo(firstCandlestick.getBodyLength()) < 0 && thirdCandlestick.getBodyLength().compareTo(secondCandlestick.getBodyLength()) < 0 ) {
												if( thirdCandlestick.getUpperShadowLength().compareTo(firstCandlestick.getUpperShadowLength()) > 0 ) {
													super.init(thirdCandlestick);
													super.candlestickEntity.setConfirmPrice(thirdCandlestick.getBodyMiddle());
													super.candlestickEntity.setStoplossPrice(thirdCandlestick.getHighPrice());
													return true;
												}
											}
										}
									}
								}					
							}
						}
					}
			}
		}
		return false;
	}

}
