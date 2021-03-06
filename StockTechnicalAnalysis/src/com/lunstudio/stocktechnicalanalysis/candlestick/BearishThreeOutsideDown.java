package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=3212
 * @author alankam
 *
 */
public class BearishThreeOutsideDown extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishThreeOutsideDown(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishThreeOutsideDown;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() ) {
			if( secondCandlestick.isFilled() ) {
				if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getOpenPrice()) < 0 ) {
					if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) > 0 ) {
						if( thirdCandlestick.isFilled() ) {
							if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
								super.init(thirdCandlestick);
								super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
								super.candlestickEntity.setStoplossPrice(thirdCandlestick.getHighPrice());
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
