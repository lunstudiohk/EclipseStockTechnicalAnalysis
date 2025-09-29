package com.lunstudio.stocktechnicalanalysis.util;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class MathUtilsTest extends TestCase {

    public void testPositiveChange() {
        BigDecimal result = MathUtils.getPriceDiff(new BigDecimal("100"), new BigDecimal("110"), 2);
        assertEquals(new BigDecimal("10.00"), result);
    }

    public void testNegativeChange() {
        BigDecimal result = MathUtils.getPriceDiff(new BigDecimal("100"), new BigDecimal("90"), 2);
        assertEquals(new BigDecimal("-10.00"), result);
    }

    public void testInitialNull() {
        BigDecimal result = MathUtils.getPriceDiff(null, new BigDecimal("50"), 2);
        assertEquals(new BigDecimal("100.00"), result);
    }

    public void testFinalNull() {
        BigDecimal result = MathUtils.getPriceDiff(new BigDecimal("50"), null, 2);
        assertEquals(new BigDecimal("-100.00"), result);
    }

    public void testBothNull() {
        BigDecimal result = MathUtils.getPriceDiff(null, null, 2);
        assertNull(result);
    }
}
