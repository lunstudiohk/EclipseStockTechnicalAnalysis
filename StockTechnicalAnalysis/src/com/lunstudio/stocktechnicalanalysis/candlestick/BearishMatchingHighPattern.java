package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandleStickVo;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BearishMatchingHighPattern extends BearishCandlestickPatterns implements CandlestickPattern {

	public BearishMatchingHighPattern(List<StockPriceEntity> stockPriceList) {
		super(stockPriceList);
		super.pattern = BearishPatterns.BearishMatchingHigh;
		return;
	}
	
	@Override
	public boolean isValid(Date tradeDate) throws Exception {
		int index = super.tradeDateMap.get(tradeDate);
		CandleStickVo firstCandlestick = new CandleStickVo(super.stockPriceList.get(index-1));
		CandleStickVo secondCandlestick = new CandleStickVo(super.stockPriceList.get(index));
		if( firstCandlestick.isHollow() && !firstCandlestick.isShortBody() ) {
			if( secondCandlestick.isHollow() ) {
				if( firstCandlestick.getClosePrice().compareTo(secondCandlestick.getOpenPrice()) > 0 ) {
					if( firstCandlestick.getDayVolume() == null || secondCandlestick.getDayVolume() == null || firstCandlestick.getDayVolume().compareTo(secondCandlestick.getDayVolume()) > 0 ) {
						if( CandleStickVo.isSamePrice(firstCandlestick.getClosePrice(), secondCandlestick.getClosePrice()) ) {
							super.init(secondCandlestick);
							super.candlestickEntity.setConfirmPrice(firstCandlestick.getBodyHalf());
							if( firstCandlestick.getDayHigh().compareTo(secondCandlestick.getDayHigh()) > 0 ) {
								super.candlestickEntity.setStoplossPrice(firstCandlestick.getDayHigh());
							} else {
								super.candlestickEntity.setStoplossPrice(secondCandlestick.getDayHigh());
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
