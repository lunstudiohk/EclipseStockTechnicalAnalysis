package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=5201
 * @author alankam
 *
 */
public class BearishBreakaway extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishBreakaway(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishBreakaway;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-4));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-3));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo fourthCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo fifthCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() ) {
			if( secondCandlestick.isHollow() && secondCandlestick.getBottom().compareTo(firstCandlestick.getTop()) > 0 ) {
				if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
					if( fourthCandlestick.getClosePrice().compareTo(thirdCandlestick.getClosePrice()) > 0 ) {
						if( fifthCandlestick.isFilled() ) {
							if( fifthCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) < 0 ) {
								super.init(fifthCandlestick);
								super.candlestickEntity.setConfirmPrice(fifthCandlestick.getClosePrice());
								super.candlestickEntity.setStoplossPrice(fifthCandlestick.getHighPrice());
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}
