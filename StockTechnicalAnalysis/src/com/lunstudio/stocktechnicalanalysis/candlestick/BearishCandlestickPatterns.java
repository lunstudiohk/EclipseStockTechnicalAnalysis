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

public class BearishCandlestickPatterns {

	protected static BigDecimal two = BigDecimal.valueOf(2);
	protected static BigDecimal three = BigDecimal.valueOf(3);
	protected static BigDecimal onePercentage = BigDecimal.valueOf(0.01);
	protected static BigDecimal twoPercentage = BigDecimal.valueOf(0.02);
	
	public enum BearishPatterns {
		BearishHangingMan			//吊人
		,BearishBeltHold			//空頭執帶
		,BearishEngulfing			//穿頭破腳
		,BearishHarami				//頂部身懷六甲
		,BearishShootingStar		//射擊之星
		,BearishDarkCloudCover		//烏雲蓋頂
		,BearishDojiStar			//頂部星形十字
		
		,BearishMeetingLine			//淡友反攻
		,BearishDescendingHawk		//落鷹盤旋
		,BearishMatchingHigh		//相同頂價
		,BearishKicking				//看跌反沖
		,BearishEveningStar			//黃昏之星
		
		,BearishAbandonedBaby		//頂部棄嬰
		,BearishUpsideGapTwoCrows	//向上跳空雙烏鴉
		,BearishThreeBlackCrows		//三飛烏鴉
		,BearishAdvanceBlock		//大敵當前
		,BearishDeliberationBlock	//步步為營
		
		,BearishTwoCrows			//雙飛烏鴉
	}
	
	protected List<StockPriceEntity> stockPriceList = null;
	protected CandlestickEntity candlestickEntity = null;
	protected Map<Date, Integer> tradeDateMap = null;
	protected BearishPatterns pattern;
	
	public BearishCandlestickPatterns(List<StockPriceEntity> stockPriceList) {
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
		this.candlestickEntity.setType(CandlestickEntity.Sell);
		this.candlestickEntity.setCandlestickType(this.getBearishCandlestickPattern());
		return;
	}
	
	public List<CandlestickEntity> getBearishCandlestickEntityList(Date tradeDate) throws Exception {
		List<CandlestickEntity> candlestickList = new ArrayList<CandlestickEntity>();
		for(BearishPatterns pattern : BearishPatterns.values() ) {
			//if( pattern != BearishPatterns.BearishThreeInsideDown ) continue;
			CandlestickPattern candlestickPattern = this.getBearishCandlestickPattern(pattern);
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
	
	public CandlestickPattern getBearishCandlestickPattern(BearishPatterns pattern) {
		switch(pattern) {
		case BearishHangingMan:
			return new BearishHangingManPattern(this.stockPriceList);
		case BearishBeltHold:
			return new BearishBeltHoldPattern(this.stockPriceList);
		case BearishEngulfing:
			return new BearishEngulfingPattern(this.stockPriceList);
		case BearishHarami:
			return new BearishHaramiPattern(this.stockPriceList);
		case BearishShootingStar:
			return new BearishShootingStarPattern(this.stockPriceList);
		case BearishDarkCloudCover:
			return new BearishDarkCloudCoverPattern(this.stockPriceList);
		case BearishDojiStar:
			return new BearishDojiStarPattern(this.stockPriceList);
		case BearishMeetingLine:
			return new BearishMeetingLinePattern(this.stockPriceList);
		case BearishDescendingHawk:
			return new BearishDescendingHawkPattern(this.stockPriceList);
		case BearishMatchingHigh:
			return new BearishMatchingHighPattern(this.stockPriceList);
		case BearishKicking:
			return new BearishKickingPattern(this.stockPriceList);
		case BearishEveningStar:
			return new BearishEveningStarPattern(this.stockPriceList);
		case BearishAbandonedBaby:
			return new BearishAbandonedBabyPattern(this.stockPriceList);
		case BearishUpsideGapTwoCrows:
			return new BearishUpsideGapTwoCrowsPattern(this.stockPriceList);
		case BearishThreeBlackCrows:
			return new BearishThreeBlackCrowsPattern(this.stockPriceList);
		case BearishAdvanceBlock:
			return new BearishAdvanceBlockPattern(this.stockPriceList);
		case BearishDeliberationBlock:
			return new BearishDeliberationBlockPattern(this.stockPriceList);
		case BearishTwoCrows:
			return new BearishTwoCrowsPattern(this.stockPriceList);
		default:
			return null;
		}
	}
	
	public static String getBearishCandlestickPatternDesc(Integer pattern) {
		switch(pattern) {
		case 0: return "鎚頭";
		case 1: return "空頭執帶";
		case 2: return "穿頭破腳";
		case 3: return "頂部身懷六甲";
		case 4: return "射擊之星";
		case 5: return "烏雲蓋頂";
		case 6: return "頂部星形十字";
		case 7: return "淡友反攻";
		case 8: return "落鷹盤旋";
		case 9: return "相同頂價";
		case 10: return "看跌反沖";
		case 11: return "黃昏之星";
		case 12: return "頂部棄嬰";
		case 13: return "向上跳空雙烏鴉";
		case 14: return "三飛烏鴉";
		case 15: return "大敵當前";
		case 16: return "步步為營";
		case 17: return "雙飛烏鴉";
		default: return "";
		}
	}
	public Integer getBearishCandlestickPattern() {
		for(int i=0; i<BearishPatterns.values().length; i++) {
			if( this.pattern == BearishPatterns.values()[i]) {
				return i;
			}
		}
		return -1;
	}
}
