package wallapp.data.favorite

import wallapp.content.model.Id

object FavoriteArbitrator {

    /**
     *
     */
    fun arbitrateFavorites(serverIds: List<Id>?, localIds: List<Id>?): List<Id>? {
        if (serverIds == null) return localIds
        if (localIds == null) return serverIds
        return (serverIds + localIds).distinct()
    }

}