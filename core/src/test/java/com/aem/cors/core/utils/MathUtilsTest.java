package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.MathUtils.randomDoubleInRange;
import static com.aem.cors.core.utils.MathUtils.randomLongInRange;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MathUtilsTest {

    @Test
    void testRandomLongInRange() {
        for (int i = 0; i < 50; i++) {
            Long result = randomLongInRange(10, 20);
            assertThat(result >= 10 && result < 20, is(true));
        }
    }

    @Test
    void testRandomLongInRangeSameBounds() {
        assertThat(randomLongInRange(5, 5), is(5L));
    }

    @Test
    void testRandomDoubleInRangeIsRoundedToNearest() {
        for (int i = 0; i < 50; i++) {
            Double result = randomDoubleInRange(0, 100, 5);
            assertThat(result % 5 == 0, is(true));
            assertThat(result >= 0 && result <= 100, is(true));
        }
    }
}
