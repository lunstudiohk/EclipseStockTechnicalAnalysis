package com.lunstudio.stocktechnicalanalysis.signal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

public class BaseSignal {

	protected List<Integer> tradeIndexList;
	protected SignalIndicatorPattern pattern;
	protected Boolean isTriggerSignal = false;
	
	protected BigDecimal param1;
	protected BigDecimal param2;
	
	public enum SignalIndicatorPattern {
		MacdCrossAboveSignal
		, MacdCrossAboveSignalConverge
		, MacdCrossAboveSignalDiverge
		, MacdCrossAboveSignalParallel
		, MacdCrossBelowSignal
		, MacdCrossBelowSignalConverge
		, MacdCrossBelowSignalDiverge
		, MacdCrossBelowSignalParallel

		, MacdCrossAboveZero
		, MacdCrossBelowZero
		, MacdAboveZero
		, MacdBelowZero
		
		, MacdSignalCrossAboveZero
		, MacdSignalCrossBelowZero
		, MacdSignalAboveZero
		, MacdSignalBelowZero
		
		, ShortRsiCrossAboveLongRsi
		, ShortRsiCrossBelowLongRsi
		, ShortRsiAboveLongRsi
		, ShortRsiBelowLongRsi
		, ShortRsiRange
		, ShortRsiRangeUpTrend
		, ShortRsiRangeDownTrend
		, LongRsiRange
		, LongRsiRangeUpTrend
		, LongRsiRangeDownTrend
		
		, ClosePriceCrossAboveShortSma
		, ClosePriceCrossBelowShortSma
		, ClosePriceCrossAboveMediumSma
		, ClosePriceCrossBelowMediumSma
		, ClosePriceCrossAboveLongSma
		, ClosePriceCrossBelowLongSma
		
		, ShortSmaCrossAboveMediumSma
		, ShortSmaCrossBelowMediumSma
		, ShortSmaCrossAboveLongSma
		, ShortSmaCrossBelowLongSma
		, MediumSmaCrossAboveLongSma
		, MediumSmaCrossBelowLongSma
		
		, ShortSmaAboveMediumSmaAboveLongSma
		, ShortSmaAboveLongSmaAboveMediumSma
		, ShortSmaAboveMediumLongSma
		, ShortSmaBelowMediumLongSma
		, MediumSmaAboveShortSmaAboveLongSma
		, MediumSmaAboveLongSmaAboveShortSma
		, MediumSmaAboveShortLongSma
		, MediumSmaBelowShortLongSma
		, LongSmaAboveShortSmaAboveMediumSma
		, LongSmaAboveMediumSmaAboveShortSma
		, LongSmaAboveShortMediumSma
		, LongSmaBelowShortMediumSma
		, ClosePriceAboveShortSma 
		, ClosePriceBelowShortSma
		, ClosePriceAboveMediumSma
		, ClosePriceBelowMediumSma
		, ClosePriceAboveLongSma
		, ClosePriceBelowLongSma
		, ShortSmaRebound	//Day low below SMA-10 and Close Above SMA-10
		, MediumSmaRebound
		, LongSmaRebound
		, ShortSmaResist
		, MediumSmaResist
		, LongSmaResist
		
		, BollingerRebound
		, BollingerResist
		, BollingerBelowLower
		, BollingerAboveUpper
		
		, VolumeAboveShortSma
		, VolumeBelowShortSma
		, VolumeAboveMediumSma
		, VolumeBelowMediumSma
		, VolumeAboveLongSma
		, VolumeBelowLongSma
		
		, ConfirmCloseAbove
		, ConfirmCloseBelow
		, ConfirmCloseAboveHigh
		, ConfirmCloseBelowLow
		, ConfirmVolumeAbove
		, ConfirmVolumeBelow
		
		, GapUp
		, GapDown
		, WhiteCandlestick
		, BlackCandlestick
		, LongCandlestick
		, LongCandlestickBody
		, ShortCandlestick
		, ShortCandlestickBody
		
		, BearishBeltHold
		
		, BullishBeltHold
		, BullishHammer
		, BullishGappingUpDoji
		, BullishSouthernDoji
		, BullishTakuriLine
		
		, BearishDojiStar
		
		, BullishDojiStar
		, BullishEngulfing
		, BullishHarami
		, BullishHaramiCross
		, BullishMeetingLines
		, BullishSeparatingLines
		, BullishTasukiLine
		, BullishHomingPigeon
		, BullishInvertedHammer
		, BullishKicking
		, BullishMatchingLow
		, BullishPiercing
		, BullishRisingWindow
		, BullishTurnUp
		, BullishTweezersBottom
	}
	
	public String getSignalDescInt() {
		for(int i=0; i<SignalIndicatorPattern.values().length; i++) {
			if( SignalIndicatorPattern.values()[i] == pattern ) {
				return String.format("[%s]", i);
			}
		}
		return "";
	}
	
	public String getSignalDesc() {
		switch(pattern) {
		case MacdCrossAboveSignal:
			return "MACD 升穿 Signal";
		case MacdCrossAboveSignalConverge:
			return "MACD 升穿 Signal(收斂)";
		case MacdCrossAboveSignalDiverge:
			return "MACD 升穿 Signal(分歧)";
		case MacdCrossAboveSignalParallel:
			return "MACD 升穿 Signal(平行)";
		case MacdCrossAboveZero:
			return "MACD 升穿 0";
		case MacdAboveZero:
			return "MACD > 0";
		case MacdSignalCrossAboveZero:
			return "MACD Signal 升穿 0";
		case MacdSignalAboveZero:
			return "MACD Signal > 0";
		case MacdCrossBelowSignal:
			return "MACD 跌穿 Signal";
		case MacdCrossBelowSignalConverge:
			return "MACD 跌穿 Signal(收斂)";
		case MacdCrossBelowSignalDiverge:
			return "MACD 跌穿 Signal(分歧)";
		case MacdCrossBelowSignalParallel:
			return "MACD 跌穿 Signal(平行)";
		case MacdCrossBelowZero:
			return "MACD 跌穿 0";
		case MacdBelowZero:
			return "MACD < 0";
		case MacdSignalCrossBelowZero:
			return "MACD Signal 跌穿 0";
		case MacdSignalBelowZero:
			return "MACD Signal < 0";
		case ShortRsiCrossAboveLongRsi:
			return "RSI-5 升穿 RSI-14";
		case ShortRsiCrossBelowLongRsi:
			return "RSI-5 跌穿 RSI-14";
		case ShortRsiAboveLongRsi:
			return "RSI-5 > RSI-14";
		case ShortRsiBelowLongRsi:
			return "RSI-5 < RSI-14";
		case ShortRsiRange:
			return String.format("RSI-5 於 %d 至 %d 之間", param1.intValue(), param2.intValue());
		case ShortRsiRangeUpTrend:
			return String.format("RSI-5 向上於 %d 至 %d 之間", param1.intValue(), param2.intValue());
		case ShortRsiRangeDownTrend:
			return String.format("RSI-5 向下於 %d 至 %d 之間", param1.intValue(), param2.intValue());
		case LongRsiRange:
			return String.format("RSI-14 於 %d 至 %d 之間", param1.intValue(), param2.intValue());
		case LongRsiRangeUpTrend:
			return String.format("RSI-14 向上於 %d 至 %d 之間", param1.intValue(), param2.intValue());
		case LongRsiRangeDownTrend:
			return String.format("RSI-14 向下於 %d 至 %d 之間", param1.intValue(), param2.intValue());
		case ClosePriceCrossAboveShortSma:
			return "收市 升穿 SMA-10";
		case ClosePriceCrossBelowShortSma:
			return "收市 跌穿 SMA-10";
		case ClosePriceCrossAboveMediumSma:
			return "收市 升穿 SMA-20";
		case ClosePriceCrossBelowMediumSma:
			return "收市 跌穿 SMA-20";
		case ClosePriceCrossAboveLongSma:
			return "收市 升穿 SMA-50";
		case ClosePriceCrossBelowLongSma:
			return "收市 跌穿 SMA-50";
		case ClosePriceAboveShortSma:
			if( param1 != null && param2 != null ) {
				return String.format("收市 高過 SMA-10 (%d%% 至 %d%%)", param1.intValue(), param2.intValue());
			} else {
				return "收市高過 SMA-10";
			}
		case ClosePriceBelowShortSma:
			if( param1 != null && param2 != null ) {
				return String.format("收市 低過 SMA-10 (%d%% 至 %d%%)", param1.intValue(), param2.intValue());
			} else {
				return "收市低過 SMA-10";
			}
		case ClosePriceAboveMediumSma:
			if( param1 != null && param2 != null ) {
				return String.format("收市 高過 SMA-20 (%d%% 至 %d%%)", param1.intValue(), param2.intValue());
			} else {
				return "收市高過 SMA-20";
			}
		case ClosePriceBelowMediumSma:
			if( param1 != null && param2 != null ) {
				return String.format("收市 低過 SMA-20 (%d%% 至 %d%%)", param1.intValue(), param2.intValue());
			} else {
				return "收市低過 SMA-20";
			}
		case ClosePriceAboveLongSma:
			if( param1 != null && param2 != null ) {
				return String.format("收市 高過 SMA-50 (%d%% 至 %d%%)", param1.intValue(), param2.intValue());
			} else {
				return "收市高過 SMA-50";
			}
		case ClosePriceBelowLongSma:
			if( param1 != null && param2 != null ) {
				return String.format("收市 低過 SMA-50 (%d%% 至 %d%%)", param1.intValue(), param2.intValue());
			} else {
				return "收市低過 SMA-50";
			}
		case ShortSmaCrossAboveMediumSma:
			return "SMA-10 升穿 SMA-20";
		case ShortSmaCrossBelowMediumSma:
			return "SMA-10 跌穿 SMA-20";
		case ShortSmaCrossAboveLongSma:
			return "SMA-10 升穿 SMA-50";
		case ShortSmaCrossBelowLongSma:
			return "SMA-10 跌穿 SMA-50";
		case MediumSmaCrossAboveLongSma:
			return "SMA-20 升穿 SMA-50";
		case MediumSmaCrossBelowLongSma:
			return "SMA-20 跌穿 SMA-50";
		case ShortSmaAboveMediumSmaAboveLongSma:
			return "SMA-10 > SMA-20 > SMA-50";
		case ShortSmaAboveLongSmaAboveMediumSma:
			return "SMA-10 > SMA-50 > SMA-20";
		case ShortSmaAboveMediumLongSma:
			return "SMA-10 > SMA-20 & SMA-50";
		case ShortSmaBelowMediumLongSma:
			return "SMA-10 < SMA-20 & SMA-50";
		case MediumSmaAboveShortSmaAboveLongSma:
			return "SMA-20 > SMA-10 > SMA-50";
		case MediumSmaAboveLongSmaAboveShortSma:
			return "SMA-20 > SMA-50 > SMA-10";
		case MediumSmaAboveShortLongSma:
			return "SMA-20 > SMA-10 & SMA-50";
		case MediumSmaBelowShortLongSma:
			return "SMA-20 < SMA-10 & SMA-50";
		case LongSmaAboveShortSmaAboveMediumSma:
			return "SMA-50 > SMA-10 > SMA-20";
		case LongSmaAboveMediumSmaAboveShortSma:
			return "SMA-50 > SMA-20 > SMA-10";
		case LongSmaAboveShortMediumSma:
			return "SMA-50 > SMA-10 & SMA-20";
		case LongSmaBelowShortMediumSma:
			return "SMA-50 < SMA-10 > SMA-20";
		case VolumeAboveShortSma:
			return "Vol > SMA-10";
		case VolumeBelowShortSma:
			return "Vol < SMA-10";
		case VolumeAboveMediumSma:
			return "Vol > SMA-20";
		case VolumeBelowMediumSma:
			return "Vol < SMA-20";
		case VolumeAboveLongSma:
			return "Vol > SMA-50";
		case VolumeBelowLongSma:
			return "Vol < SMA-50";
		case ShortSmaRebound:
			return "SMA-10 反彈";
		case MediumSmaRebound:
			return "SMA-20 反彈";
		case LongSmaRebound:
			return "SMA-50 反彈";
		case ShortSmaResist:
			return "SMA-10 阻力";
		case MediumSmaResist:
			return "SMA-20 阻力";
		case LongSmaResist:
			return "SMA-50 阻力";
		case BollingerRebound:
			return "保力加反彈";
		case BollingerResist:
			return "保力加阻力";
		case BollingerBelowLower:
			return "保力加穿底";
		case BollingerAboveUpper:
			return "保力加破頂";
		case GapUp:
			return "Gap-Up";
		case GapDown:
			return "Gap-Down";
		case WhiteCandlestick:
			return "陽蠋";
		case BlackCandlestick:
			return "陰蠋";
		case LongCandlestick:
			return "大蠋";
		case ShortCandlestick:
			return "小蠋";
		case LongCandlestickBody:
			return "大蠋身";
		case ShortCandlestickBody:
			return "小蠋身";
		case BearishDojiStar:
			return "Bearish Doji Star";
		case BearishBeltHold:
			return "Bearish Belt Hold";
		case BullishDojiStar:
			return "Bullish Doji Star";
		case BullishBeltHold:
			return "Bullish Belt Hold";
		case BullishHammer:
			return "Bullish Hammer";
		case BullishEngulfing:
			return "Bullish Engulfing";
		case BullishHarami:
			return "Bullish Harami";
		case BullishHaramiCross:
			return "Bullish Harami Cross";
		case BullishMeetingLines:
			return "Bullish Meeting Lines";
		case BullishSeparatingLines:
			return "Bullish Separating Lines";
		case BullishTasukiLine:
			return "Bullish Tasuki Line";
		case BullishHomingPigeon:
			return "Bullish Homing Pigeon";
		case BullishInvertedHammer:
			return "Bullish Inverted Hammer";
		case BullishKicking:
			return "Bullish Kicking";
		case BullishMatchingLow:
			return "Bullish Matching Low";
		case BullishPiercing:
			return "Bullish Piercing";
		case BullishRisingWindow:
			return "Bullish Rising Window";
		case BullishTurnUp:
			return "Bullish Turn Up";
		case BullishTweezersBottom:
			return "Bullish Tweezers Bottom";
		case BullishGappingUpDoji:
			return "Bullish Gapping Up Doji";
		case BullishSouthernDoji:
			return "Bullish Southern Doji";
		case BullishTakuriLine:
			return "Bullish Takuri Line";
		case ConfirmCloseAbove:
			return "翌日升";
		case ConfirmCloseBelow:
			return "翌日跌";
		case ConfirmCloseAboveHigh:
			return "翌日高收";
		case ConfirmCloseBelowLow:
			return "翌日低收";
		case ConfirmVolumeAbove:
			return "翌日量升";
		case ConfirmVolumeBelow:
			return "翌日量跌";
		//default:
			//return null;
		}
		return null;
	}
	
	public BaseSignal(SignalIndicatorPattern pattern) {
		this.pattern = pattern;
		this.tradeIndexList = new ArrayList<Integer>();
		return;
	}
	
	public List<Integer> getTradeIndex() {
		return this.tradeIndexList;
	}
	
	public static List<BaseSignal[]> getCandlestickSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(CandlestickSignal.getGapPattern(stockPriceList));
		signals.add(CandlestickSignal.getCandlestickSize(stockPriceList));
		signals.add(CandlestickSignal.getCandlestickBodySize(stockPriceList));
		signals.add(CandlestickSignal.getCandlestickType(stockPriceList));
		signals.add(CandlestickSignal.getCandlestickPattern(stockPriceList));
		return signals;
	}
	
	public static List<BaseSignal[]> getVolumeSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(VolumeSignal.getVolumeSma(stockPriceList));
		return signals;
	}
	
	public static List<BaseSignal[]> getRsiSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(RsiSignal.getShortRsiCrossLongRsiSignalPattern(stockPriceList));
		signals.add(RsiSignal.getShortRsiRangeSignalPattern(stockPriceList));
		signals.add(RsiSignal.getLongRsiRangeSignalPattern(stockPriceList));
		return signals;
	}
		
	public static List<BaseSignal[]> getMacdSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(MacdSignal.getMacdCrossSignalPattern(stockPriceList));
		signals.add(MacdSignal.getMacdZeroPattern(stockPriceList));
		signals.add(MacdSignal.getMacdSignalZeroPattern(stockPriceList));
		return signals;
	}
		
	public static List<BaseSignal[]> getSmaSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		
		signals.add(SmaSignal.getClosePriceCrossShortSma(stockPriceList));
		signals.add(SmaSignal.getClosePriceCrossMediumSma(stockPriceList));
		signals.add(SmaSignal.getClosePriceCrossLongSma(stockPriceList));
		
		signals.add(SmaSignal.getShortSmaCrossMediumSma(stockPriceList));
		signals.add(SmaSignal.getShortSmaCrossLongSma(stockPriceList));
		signals.add(SmaSignal.getMediumSmaCrossLongSma(stockPriceList));
		return signals;
	}
	
	public static List<BaseSignal[]> getConfirmSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(ConfirmSignal.getNextClose(stockPriceList));
		signals.add(ConfirmSignal.getNextVolume(stockPriceList));
		return signals;
	}
	
	public static List<BaseSignal[]> getBollingerSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(BollingerSignal.getBollingerReboundResist(stockPriceList));
		return signals;
	}
	
	public static List<BaseSignal[]> getAllSignalList(List<StockPriceEntity> stockPriceList) throws Exception {
		List<BaseSignal[]> signals = new ArrayList<BaseSignal[]>();
		signals.add(RsiSignal.getShortRsiCrossLongRsiSignalPattern(stockPriceList));
		signals.add(RsiSignal.getShortRsiRangeSignalPattern(stockPriceList));
		signals.add(RsiSignal.getLongRsiRangeSignalPattern(stockPriceList));
		
		signals.add(MacdSignal.getMacdCrossSignalPattern(stockPriceList));
		signals.add(MacdSignal.getMacdZeroPattern(stockPriceList));
		signals.add(MacdSignal.getMacdSignalZeroPattern(stockPriceList));
		
		signals.add(SmaSignal.getClosePriceCrossShortSma(stockPriceList));
		signals.add(SmaSignal.getClosePriceCrossMediumSma(stockPriceList));
		signals.add(SmaSignal.getClosePriceCrossLongSma(stockPriceList));
		signals.add(SmaSignal.getShortSmaCrossMediumSma(stockPriceList));
		signals.add(SmaSignal.getShortSmaCrossLongSma(stockPriceList));
		signals.add(SmaSignal.getMediumSmaCrossLongSma(stockPriceList));
		return signals;
	}
	
}
