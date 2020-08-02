package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=3215
 * @author alankam
 *
 */
public class BearishSqueezeAlert extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishSqueezeAlert(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishSqueezeAlert;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		
		
		if( firstCandlestick.isHollow() ) {
			if( secondCandlestick.getTop().compareTo(firstCandlestick.getTop()) < 0 ) {
				if( secondCandlestick.getBottom().compareTo(firstCandlestick.getBottom()) > 0 ) {
					if( thirdCandlestick.getTop().compareTo(secondCandlestick.getTop()) < 0 ) {
						if( thirdCandlestick.getBottom().compareTo(secondCandlestick.getBottom()) > 0 ) {
							super.init(thirdCandlestick);
							super.candlestickEntity.setConfirmPrice(thirdCandlestick.getBottom());
							if( thirdCandlestick.getHighPrice().compareTo(secondCandlestick.getHighPrice()) > 0 ) {
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getHighPrice());
							} else {
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
