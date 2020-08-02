package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=3206
 * @author alankam
 *
 */
public class BearishUniqueThreeMountainTop extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishUniqueThreeMountainTop(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishUniqueThreeMountainTop;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));	
		
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() && secondCandlestick.isShortBody() )  {
				if( secondCandlestick.getHighPrice().compareTo(firstCandlestick.getHighPrice()) > 0 ) {
					if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getOpenPrice()) > 0 ) {
						if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
							if( thirdCandlestick.isFilled() && thirdCandlestick.isShortBody() ) {
								if( thirdCandlestick.getClosePrice().compareTo(secondCandlestick.getClosePrice()) > 0 ) {
									super.init(thirdCandlestick);
									super.candlestickEntity.setConfirmPrice(thirdCandlestick.getClosePrice());
									super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
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
