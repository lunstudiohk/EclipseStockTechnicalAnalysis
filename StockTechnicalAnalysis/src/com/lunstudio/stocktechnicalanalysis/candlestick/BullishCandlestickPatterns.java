package com.lunstudio.stocktechnicalanalysis.candlestick;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CandleStickVo;

public class BullishCandlestickPatterns {

	protected static BigDecimal two = BigDecimal.valueOf(2);
	protected static BigDecimal three = BigDecimal.valueOf(3);
	protected static BigDecimal onePercentage = BigDecimal.valueOf(0.01);
	protected static BigDecimal twoPercentage = BigDecimal.valueOf(0.02);
	
	public static enum BullishPatterns {
		BullishHammer			//鎚頭
		,BullishBeltHold		//多頭執帶
		,BullishEngulfing		//破腳穿頭
		,BullishHarami			//身懷六甲
		,BullishInvertedHammer	//反轉鎚頭
		,BullishPiercingLine	//曙光初現
		,BullishDojiStart		//底部星形十字
		,BullishMeetingLine		//好友反攻
		,BullishHomingPigeon	//飛鴿歸巢
		,BullishMatchingLow		//相同低價
		,BullishKicking			//看漲反沖
		,BullishMorningStar		//早晨之星
		,BullishAbandonedBaby	//底部棄嬰
		,BullishStickSandwich	//豎狀三明治
		,BullishThreeWhiteSoldiers	//三個白色武士
	}
	
	protected List<StockPriceEntity> stockPriceList = null;
	protected CandlestickEntity candlestickEntity = null;
	protected Map<Date, Integer> tradeDateMap = null;
	protected BullishPatterns pattern;
	
	public BullishCandlestickPatterns(List<StockPriceEntity> stockPriceList) {
		this.stockPriceList = stockPriceList;
		this.tradeDateMap = new HashMap<Date, Integer>();
		for(int i=0; i<stockPriceList.size(); i++) {
			tradeDateMap.put(stockPriceList.get(i).getTradeDate(), i);
		}
		return;
	}
	
	protected void init(CandleStickVo candlestick) {
		this.candlestickEntity = new CandlestickEntity();
		this.candlestickEntity.setStockCode(candlestick.getStockCode());
		this.candlestickEntity.setTradeDate(candlestick.getTradeDate());
		this.candlestickEntity.setType(CandlestickEntity.Buy);
		this.candlestickEntity.setCandlestickType(this.getBullishCandlestickPatternIndex());
		return;
	}
		
	public List<CandlestickEntity> getBullishCandlestickEntityList(Date tradeDate) throws Exception {
		List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
		for(BullishPatterns pattern : BullishPatterns.values() ) {
			//if( pattern != BullishPatterns.BullishThreeWhiteSoldiers ) continue;
			CandlestickPattern candlestickPattern = this.getBullishCandlestickPattern(pattern);
			if( candlestickPattern.isValid(tradeDate)) {
				candlestickList.add(candlestickPattern.getCandlestickEntity());
			}
		}
		return candlestickList;
	}
	
	public CandlestickEntity getCandlestickEntity() throws Exception {
		if( this.candlestickEntity == null ) {
			return null;
		} else {
			return this.candlestickEntity;
		}
	}
	
	public static String getBullishCandlestickPatternDesc(Integer pattern) {
		switch(pattern) {
		case 0: return "鎚頭";
		case 1: return "多頭執帶";
		case 2: return "破腳穿頭";
		case 3: return "身懷六甲";
		case 4: return "反轉鎚頭";
		case 5: return "曙光初現";
		case 6: return "底部星形十字";
		case 7: return "好友反攻";
		case 8: return "飛鴿歸巢";
		case 9: return "相同低價";
		case 10: return "看漲反沖";
		case 11: return "早晨之星";
		case 12: return "底部棄嬰";
		case 13: return "豎狀三明治";
		case 14: return "三個白色武士";
		default: return "";
		}
	}
	
	public CandlestickPattern getBullishCandlestickPattern(BullishPatterns pattern) {
		switch(pattern) {
		case BullishHammer:
			return new BullishHammerPattern(this.stockPriceList);
		case BullishBeltHold:
			return new BullishBeltHoldPattern(this.stockPriceList);
		case BullishEngulfing:
			return new BullishEngulfingPattern(this.stockPriceList);
		case BullishInvertedHammer:
			return new BullishInvertedHammerPattern(this.stockPriceList);
		case BullishHarami:
			return new BullishHaramiPattern(this.stockPriceList);
		case BullishPiercingLine:
			return new BullishPiercingLinePattern(this.stockPriceList);
		case BullishDojiStart:
			return new BullishDojiStartPattern(this.stockPriceList);
		case BullishMeetingLine:
			return new BullishMeetingLinePattern(this.stockPriceList);
		case BullishHomingPigeon:
			return new BullishHomingPigeonPattern(this.stockPriceList);
		case BullishMatchingLow:
			return new BullishMatchingLowPattern(this.stockPriceList);
		case BullishKicking:
			return new BullishKickingPattern(this.stockPriceList);
		case BullishMorningStar:
			return new BullishMorningStarPattern(this.stockPriceList);
		case BullishAbandonedBaby:
			return new BullishAbandonedBabyPattern(this.stockPriceList);
		case BullishStickSandwich:
			return new BullishStickSandwichPattern(this.stockPriceList);
		case BullishThreeWhiteSoldiers:
			return new BullishThreeWhiteSoldiersPattern(this.stockPriceList);
		default:
			return null;
		}
	}
	
	public Integer getBullishCandlestickPatternIndex() {
		for(int i=0; i<BullishPatterns.values().length; i++) {
			if( this.pattern == BullishPatterns.values()[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public static Integer getBullishCandlestickPatternIndex(BullishPatterns pattern) {
		for(int i=0; i<BullishPatterns.values().length; i++) {
			if( pattern == BullishPatterns.values()[i]) {
				return i;
			}
		}
		return -1;
	}
}
