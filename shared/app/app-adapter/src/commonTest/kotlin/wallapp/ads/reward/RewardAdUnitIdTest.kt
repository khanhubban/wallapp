package wallapp.ads.reward

import kotlin.test.Test
import kotlin.test.assertFailsWith


class RewardAdUnitIdTest {

    @Test fun `RewardAdUnitId() throws exception for empty ID`() {
        assertFailsWith<IllegalArgumentException> {
            RewardAdUnitId("")
        }
    }
}