package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=3211
 * @author alankam
 *
 */
public class BearishThreeInsideDown extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishThreeInsideDown(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishThreeInsideDown;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));	
		if( firstCandlestick.isHollow() ) {
			if( !firstCandlestick.isShortBody() ) {
				if( secondCandlestick.isFilled() ) {
					if( firstCandlestick.getOpenPrice().compareTo(secondCandlestick.getClosePrice()) < 0 ) {
						if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
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
		}
		return false;
	}

}
