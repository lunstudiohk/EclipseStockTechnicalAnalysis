package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=4201
 * @author alankam
 *
 */
public class BearishThreeGapUps extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishThreeGapUps(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishThreeGapUps;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-3));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo fourthCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		
		if( secondCandlestick.getBottom().compareTo(firstCandlestick.getTop()) > 0 ) {
			if( thirdCandlestick.isHollow() && thirdCandlestick.getBottom().compareTo(secondCandlestick.getTop()) > 0 ) {
				if( fourthCandlestick.isHollow() && fourthCandlestick.getBottom().compareTo(thirdCandlestick.getTop()) > 0 ) {
					super.init(fourthCandlestick);
					super.candlestickEntity.setConfirmPrice(fourthCandlestick.getBodyMiddle());
					if( thirdCandlestick.getHighPrice().compareTo(fourthCandlestick.getHighPrice()) > 0 ) {
						super.candlestickEntity.setStoplossPrice(thirdCandlestick.getHighPrice());
					} else {
						super.candlestickEntity.setStoplossPrice(fourthCandlestick.getHighPrice());
					}
					return true;
				}
			}
		}
		return false;
	}

}
