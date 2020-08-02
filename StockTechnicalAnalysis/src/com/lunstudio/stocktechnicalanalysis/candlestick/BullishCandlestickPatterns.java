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

/**
 * https://www.candlesticker.com/BullishPatterns.aspx?lang=en
 * @author alankam
 *
 */
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
		,BullishHaramiCross		//身懷六甲星
		,BullishInvertedHammer	//反轉鎚頭
		,BullishPiercingLine	//曙光初現
		,BullishDojiStart		//底部星形十字
		,BullishMeetingLine		//好友反攻
		,BullishHomingPigeon	//飛鴿歸巢
		,BullishKicking			//看漲反沖
		,BullishMorningStar		//早晨之星
		
		,BullishAbandonedBaby	//底部棄嬰
		,BullishStickSandwich	//豎狀三明治
		,BullishThreeWhiteSoldiers	//三個白色武士
		
		,BullishOneWhiteSoldier //牛勢一白兵
		,BullishDownsideGapTwoRabbits //牛勢向下跳空兩兔子
		,BullishUniqueThreeRiverBottom //牛勢獨特三河底
		,BullishDescentBlock //牛勢下降乏力
		,BullishDeliberationBlock //牛勢步步為營
		,BullishTwoRabbits //牛勢兩兔子
		,BullishThreeInsideUp //牛勢內困三紅
		,BullishThreeOutsideUp //牛勢外側三紅
		,BullishThreeStarsInTheSouth //牛勢南方三星
		,BullishSqueezeAlert //牛勢擠壓警報
		,BullishThreeGapDowns //牛勢三個向下跳空缺口
		,BullishConcealingBabySwallow //牛勢閨中乳燕
		,BullishBreakaway //牛勢分離
		,BullishLadderBottom //牛勢梯底
		,BullishAfterBottomGapUp //牛勢觸底上跳
		
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
		this.candlestickEntity.setPriceType(candlestick.getPriceType());
		this.candlestickEntity.setCandlestickType(this.getBullishCandlestickPatternIndex());
		return;
	}
		
	public List<CandlestickEntity> getBullishCandlestickEntityList(Date tradeDate) throws Exception {
		List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
		for(BullishPatterns pattern : BullishPatterns.values() ) {
			//Debug Only
			if( pattern != BullishPatterns.BullishHammer ) continue;
			
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
		case 4: return "身懷六甲星";
		case 5: return "反轉鎚頭";
		case 6: return "曙光初現";
		case 7: return "底部星形十字";
		case 8: return "好友反攻";
		case 9: return "飛鴿歸巢";
		case 10: return "看漲反沖";
		case 11: return "早晨之星";
		case 12: return "底部棄嬰";
		case 13: return "豎狀三明治";
		case 14: return "三個白色武士";
		case 15: return "牛勢一白兵";
		case 16: return "牛勢向下跳空兩兔子";
		case 17: return "牛勢獨特三河底";
		case 18: return "牛勢下降乏力";
		case 19: return "牛勢步步為營";
		case 20: return "牛勢兩兔子";
		case 21: return "牛勢內困三紅";
		case 22: return "牛勢外側三紅";
		case 23: return "牛勢南方三星";
		case 24: return "牛勢擠壓警報";
		case 25: return "牛勢三個向下跳空缺口";
		case 26: return "牛勢閨中乳燕";
		case 27: return "牛勢分離";
		case 28: return "牛勢梯底";
		case 29: return "牛勢觸底上跳";
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
		case BullishHaramiCross:
			return new BullishHaramiCrossPattern(this.stockPriceList);
		case BullishPiercingLine:
			return new BullishPiercingLinePattern(this.stockPriceList);
		case BullishDojiStart:
			return new BullishDojiStartPattern(this.stockPriceList);
		case BullishMeetingLine:
			return new BullishMeetingLinePattern(this.stockPriceList);
		case BullishHomingPigeon:
			return new BullishHomingPigeonPattern(this.stockPriceList);
		case BullishKicking:		//To-Be-Retest ===================================================
			return new BullishKickingPattern(this.stockPriceList);
		case BullishMorningStar:
			return new BullishMorningStarPattern(this.stockPriceList);
		case BullishAbandonedBaby:
			return new BullishAbandonedBabyPattern(this.stockPriceList);
		case BullishStickSandwich:
			return new BullishStickSandwichPattern(this.stockPriceList);
		case BullishThreeWhiteSoldiers:
			return new BullishThreeWhiteSoldiersPattern(this.stockPriceList);
		
		case BullishOneWhiteSoldier:
			
		case BullishDownsideGapTwoRabbits:
			return new BullishDownsideGapTwoRabbits(this.stockPriceList);
		case BullishUniqueThreeRiverBottom:
		case BullishDescentBlock:
		case BullishDeliberationBlock:
		case BullishTwoRabbits:
		case BullishThreeInsideUp:
		case BullishThreeOutsideUp:
		case BullishThreeStarsInTheSouth:
		case BullishSqueezeAlert:
		case BullishThreeGapDowns:
		case BullishConcealingBabySwallow:
		case BullishBreakaway:
		case BullishLadderBottom:
		case BullishAfterBottomGapUp:
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
