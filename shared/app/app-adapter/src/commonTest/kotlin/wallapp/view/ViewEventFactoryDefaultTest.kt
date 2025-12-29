package wallapp.view

import kotlinx.coroutines.test.TestScope
import wallapp.account.data.AccountDataRepositoryNoOp
import wallapp.app.AppStateManagerNoOp
import wallapp.content.model.Id.ArtistId
import wallapp.data.favorite.FavoriteItemsRepositoryNoOp
import wallapp.data.following.FollowingRepositoryNoOp
import wallapp.entitlement.EntitlementRepositoryUnlockAll
import wallapp.inappreview.InAppReviewRequestManagerNoOp
import wallapp.network.NetworkRefreshManagerNoOp
import wallapp.onboarding.OnboardingManagerNoOp
import wallapp.permission.SystemPermissionManagerMock
import wallapp.pixel.alert.AlertManagerNoOp
import wallapp.pixel.globaloverlay.GlobalOverlayManagerNoOp
import wallapp.profileimage.ProfileImageManagerNoOp
import wallapp.purchase.PurchaseManagerNoOp
import wallapp.system.navigation.SystemNavigatorNoOp
import kotlin.test.Test
import kotlin.test.assertEquals


class ViewEventFactoryDefaultTest {

    private fun createViewEventFactory(): ViewEventFactoryDefault {
        return ViewEventFactoryDefault(
            appStateManager = AppStateManagerNoOp(),
            favoriteItemsRepository = FavoriteItemsRepositoryNoOp,
            followingRepository = FollowingRepositoryNoOp(),
            onboardingManager = OnboardingManagerNoOp(),
            purchaseManager = PurchaseManagerNoOp(),
            systemNavigator = SystemNavigatorNoOp,
            alertManager = AlertManagerNoOp,
            profileImageManager = ProfileImageManagerNoOp,
            accountDataRepository = AccountDataRepositoryNoOp,
            coroutineScopeIo = TestScope(),
            systemPermissionManager = SystemPermissionManagerMock(),
            globalOverlayManager = GlobalOverlayManagerNoOp,
            entitlementRepository = EntitlementRepositoryUnlockAll(),
            networkRefreshManager = NetworkRefreshManagerNoOp,
            inAppReviewRequestManager = InAppReviewRequestManagerNoOp,
        )
    }

    @Test fun createNavigateToScreen_id_equality() {
        val factory = createViewEventFactory()

        val artistId = ArtistId("artistId")
        val first = factory.createNavigateToScreen(artistId)
        val second = factory.createNavigateToScreen(artistId)

        assertEquals(first, second)
    }

    @Test fun createOnClickFavorite_id_equality() {
        val factory = createViewEventFactory()

        val artistId = ArtistId("artistId")
        val first = factory.createOnClickFavorite(artistId)
        val second = factory.createOnClickFavorite(artistId)

        assertEquals(first, second)
    }
}