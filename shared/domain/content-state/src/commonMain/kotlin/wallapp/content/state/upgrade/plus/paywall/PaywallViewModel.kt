package wallapp.content.state.upgrade.plus.paywall

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorViewStateMapper
import wallapp.content.state.pager.PagerPersistableStateController
import wallapp.content.state.upgrade.PaywallViewEvent
import wallapp.content.state.upgrade.PaywallViewEventSink
import wallapp.content.state.upgrade.PlusPlan
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.coroutine.collectIn
import wallapp.data.purchase.Purchasable
import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.LicenseStateType
import wallapp.license.state.isLicensedAny
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewStateOk
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.purchase.PurchaseManager
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.screen.screen
import wallapp.system.platform.PlatformFeature
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.viewmodel.ViewModel

class PaywallViewModel(
    private val argument: PaywallScreenArgument,
    private val entitlementRepository: EntitlementRepository,
    private val errorViewStateMapper: ErrorViewStateMapper,
    private val paywallViewStateMapper: PaywallViewStateMapper,
    private val purchaseManager: PurchaseManager,
    private val appStateManager: AppStateManager,
    private val viewEventFactory: ViewEventFactory,
    alertManager: AlertManager,
    strings: StringRepository,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    companion object {
        private const val UseTabs = true
    }

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        MutableStateFlow(ScreenSystemBarController.TranslucentStatusBar())
    }

    private val initialSubscriptionPlan: SubscriptionPlan
        get() = argument.subscriptionPlan ?: SubscriptionPlan.PlusBasic

    private val subscriptionExpired: Boolean
        get() = argument.subscriptionExpired

    private val selectedSubscription = MutableStateFlow(initialSubscriptionPlan)
    private val selectedPlusPlan: MutableStateFlow<PlusPlan> = MutableStateFlow(PlusPlan.MONTHLY)

    private var purchaseInitiated = false

    private val licenseState: LicenseStateType
        get() = entitlementRepository.licenseState.value

    private val paywallViewEventSink: PaywallViewEventSink = { event: PaywallViewEvent ->
        when (event) {
            PaywallViewEvent.ActionButtonViewEvent -> {
                if (PlatformFeature.NativeManageSubscriptionSupported && licenseState.isLicensedAny()) {
                    appStateManager.navigateToManageSubscription()
                } else {
                    initiatePurchase()
                }
            }
            is PaywallViewEvent.PlusPlanViewEvent -> {
                selectedPlusPlan.value = event.plusPlan
            }
            is PaywallViewEvent.SubscriptionPlanViewEvent -> {
                selectedSubscription.value = event.subscriptionPlan
            }
        }
    }

    private fun initiatePurchase() {
        viewModelScope.launch {
            val purchasable = selectedPurchasable ?: return@launch
            viewEventFactory.createOnClickInitiatePurchase(
                purchasable = purchasable,
                isSubscription = true,
            ).invoke()
            purchaseInitiated = true
        }
    }

    private val purchasablePlusMonthly: Flow<Purchasable.SubscriptionUnlimitedMonthly?>
        get() = purchaseManager.purchasablePlusUnlimitedMonthlyUserVisible
    private val purchasablePlusAnnual: Flow<Purchasable.SubscriptionUnlimitedAnnual?>
        get() = purchaseManager.purchasablePlusUnlimitedAnnualUserVisible
    private val purchasableAdFreeMonthly: Flow<Purchasable.SubscriptionStandardMonthly?>
        get() = purchaseManager.purchasablePlusStandardMonthlyUserVisible
    private val showErrorScreen: Flow<Boolean> = combine(
        purchasablePlusMonthly,
        purchasablePlusAnnual,
        purchasableAdFreeMonthly,
    ) { purchasablePlusMonthly, purchasablePlusAnnual, purchasableAdFreeMonthly ->
        purchasablePlusMonthly == null || purchasableAdFreeMonthly == null
    }

    private val onCloseButtonViewEventHandler = ViewEventHandler.createOnClick {
        appStateManager.navigateBack()
        // TODO: Retry the purchase
    }

    private val pagerPersistableStateController = PagerPersistableStateController(viewModelScope)

    @Suppress("UNCHECKED_CAST")
    private val actionButtonViewEventHandler = ViewEventHandler.Event(
        eventSink = paywallViewEventSink as ViewEventSink,
        event = PaywallViewEvent.ActionButtonViewEvent,
    )

    private var selectedPurchasable: Purchasable? = null

    private fun createViewState(
        purchasablePlusMonthly: Purchasable.SubscriptionUnlimitedMonthly,
        purchasablePlusAnnual: Purchasable.SubscriptionUnlimitedAnnual?,
        purchasableAdFreeMonthly: Purchasable.SubscriptionStandardMonthly,
        selectedSubscription: SubscriptionPlan,
        selectedPlusPlan: PlusPlan = this.selectedPlusPlan.value,
        licenseState: LicenseStateType,
    ): PaywallViewState {
        selectedPurchasable = when (selectedSubscription) {
            SubscriptionPlan.PlusBasic -> {
                purchasableAdFreeMonthly
            }
            SubscriptionPlan.PlusUnlimited -> {
                when (selectedPlusPlan) {
                    PlusPlan.ANNUAL -> {
                        purchasablePlusAnnual
                    }
                    PlusPlan.MONTHLY -> {
                        purchasablePlusMonthly
                    }
                }
            }
        }
        val subscribedToCurrentSelectedSubscription = selectedPurchasable?.isSubscribedBasedOn(licenseState) == true

        return paywallViewStateMapper.mapPaywallViewState(
            purchasablePlusMonthly,
            purchasablePlusAnnual,
            purchasableAdFreeMonthly,
            subscriptionExpired,
            initialSubscriptionPlan,
            actionButtonViewEventHandler,
            selectedPlan = selectedPlusPlan,
            paywallViewEventSink = paywallViewEventSink,
            showAsModalSheet = PlatformFeature.SupportModalSheetBehaviour && argument.screen.showAsModalSheet,
            planSelectionLastPagerStateUpdateSink = if (UseTabs) {
                pagerPersistableStateController.pagerPersistableStateWrapper.lastPagerStateUpdateSink
            } else {
                null
            },
            subscribedToCurrentSelectedSubscription = subscribedToCurrentSelectedSubscription,
            currentlySubscribedToAny = licenseState.isLicensedAny(),
            selectedSubscriptionPlan = selectedSubscription,
        )
    }

    @FlowInterop.Enabled
    override val viewState: StateFlow<PaywallViewState> =
        combine(
            appStateManager.isUiReady,
            selectedSubscription,
            selectedPlusPlan,
            purchasablePlusMonthly,
            purchasablePlusAnnual,
            purchasableAdFreeMonthly,
            showErrorScreen,
            pagerPersistableStateController.lastPagerStateUpdateFlow,
            entitlementRepository.licenseState,
        ) { isUiReady, selectedSubscription, selectedPlan, purchasablePlusMonthly, purchasablePlusAnnual, purchasableAdFreeMonthly, showErrorScreen, _, licenseState ->
            if (!isUiReady) {
                return@combine PaywallViewState.Loading
            }

            if (showErrorScreen) {
                return@combine PaywallViewState.Error(
                    errorViewStateMapper.createBillingErrorViewState(onCloseButtonViewEventHandler),
                )
            }

            createViewState(
                purchasablePlusMonthly!!,
                purchasablePlusAnnual,
                purchasableAdFreeMonthly!!,
                selectedSubscription,
                selectedPlan,
                licenseState,
            )
        }.stateIn(
            initialValue = PaywallViewState.Loading,
        )

    fun Purchasable.isSubscribedBasedOn(licenseState: LicenseStateType): Boolean {
        return when (licenseState) {
            LicenseStateType.AdFree -> {
                this is Purchasable.SubscriptionStandardMonthly
            }
            LicenseStateType.Plus -> {
                this is Purchasable.SubscriptionUnlimitedMonthly || this is Purchasable.SubscriptionUnlimitedAnnual
            }
            LicenseStateType.Unlicensed -> false
        }
    }

    init {
        entitlementRepository.licenseState.collectIn(viewModelScope) { licenseState ->
            if (purchaseInitiated && licenseState.isLicensedAny()) {
                appStateManager.navigateBack()
            }
        }

        if (subscriptionExpired) {
            alertManager.show(
                AlertViewStateOk(
                    title = strings.subscriptionExpiredTitle,
                    message = strings.subscriptionExpiredMessage
                )
            )
        }
    }
}
