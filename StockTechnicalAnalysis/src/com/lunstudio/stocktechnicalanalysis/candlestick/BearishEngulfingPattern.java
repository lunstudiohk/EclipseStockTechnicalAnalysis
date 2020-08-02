package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BearishEngulfingPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishEngulfingPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishEngulfing;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-2));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		//CandleStickVo thirdCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		//if( firstCandlestick.getDayVolume() == null || secondCandlestick.getDayVolume() == null || firstCandlestick.getDayVolume().compareTo(secondCandlestick.getDayVolume()) > 0 ) {
			if( firstCandlestick.isHollow() ) {
				if( secondCandlestick.isFilled() ) {
					if( secondCandlestick.getClosePrice().compareTo(firstCandlestick.getOpenPrice()) < 0 ) {
						if( secondCandlestick.getOpenPrice().compareTo(firstCandlestick.getClosePrice()) > 0 ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(secondCandlestick.getClosePrice());
							super.candlestickEntity.setStoplossPrice(secondCandlestick.getHighPrice());
							return true;
						}
					}
				}
			}
		//}		
		return false;
	}

}

