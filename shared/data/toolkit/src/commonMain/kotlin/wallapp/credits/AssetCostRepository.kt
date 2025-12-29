package wallapp.credits

import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.RemixId

interface AssetCostRepository {
    fun getRemixCost(id: RemixId): Int
    fun getDesignCost(id: DesignId): Int
}