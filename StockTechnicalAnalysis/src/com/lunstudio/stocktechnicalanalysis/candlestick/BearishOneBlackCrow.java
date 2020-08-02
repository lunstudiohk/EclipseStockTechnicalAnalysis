package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.candlestick.BearishCandlestickPatterns.BearishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

/**
 * @see https://www.candlesticker.com/Pattern.aspx?lang=en&Pattern=2211
 * @author alankam
 *
 */
public class BearishOneBlackCrow extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishOneBlackCrow(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishOneBlackCrow;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		
		if( firstCandlestick.isHollow() ) {
			if( secondCandlestick.isFilled() )  {
				if( secondCandlestick.getHighPrice().compareTo(firstCandlestick.getClosePrice()) < 0 ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getLowPrice()) < 0 ) {
						super.init(secondCandlestick);
						super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
						super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
						return true;
					}
				}
			}
		}
		return false;
	}

}
