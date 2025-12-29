package wallapp.di.module

import org.koin.dsl.module
import wallapp.di.Factory
import wallapp.di.NamedScope
import wallapp.search.SearchCategorySpecFactory
import wallapp.search.SearchInputDefaults
import wallapp.search.SearchInputDefaultsImpl
import wallapp.search.SearchQueryManager
import wallapp.search.SearchQueryManagerDefault
import wallapp.search.SearchQueryResultPatcher
import wallapp.search.SearchQueryResultPatcherDefault
import wallapp.search.SearchResultManager
import wallapp.search.SearchResultManagerDefault
import wallapp.search.SearchSessionManager
import wallapp.search.SearchSessionManagerDefault
import wallapp.search.content.SearchContentRepository
import wallapp.search.content.SearchContentRepositoryDefault
import wallapp.search.network.repository.NetworkSearchContentRepository
import wallapp.search.network.repository.NetworkSearchContentRepositoryConfig
import wallapp.search.network.repository.NetworkSearchContentRepositoryConfigDefault
import wallapp.search.network.repository.NetworkSearchContentRepositoryNetwork
import wallapp.search.sort.SearchResultSorter
import wallapp.search.sort.SearchResultSorterDefault

@Suppress("RemoveExplicitTypeArguments")
val SearchModule = module {
    single<NetworkSearchContentRepository> { Factory.networkSearchContentRepository(this) }
    single<NetworkSearchContentRepositoryConfig> { NetworkSearchContentRepositoryConfigDefault(get()) }
    single<NetworkSearchContentRepositoryNetwork> { NetworkSearchContentRepositoryNetwork(get(), get(), get()) }
    single<SearchCategorySpecFactory> { SearchCategorySpecFactory(get()) }
    single<SearchContentRepository> { get<SearchContentRepositoryDefault>() }
    single<SearchContentRepositoryDefault> { SearchContentRepositoryDefault(get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<SearchInputDefaults> { SearchInputDefaultsImpl(get()) }
    single<SearchQueryManager> { get<SearchQueryManagerDefault>() }
    single<SearchQueryManagerDefault> { SearchQueryManagerDefault(get(), get(), get(), get(), get()) }
    single<SearchQueryResultPatcher> { get<SearchQueryResultPatcherDefault>() }
    single<SearchQueryResultPatcherDefault> { SearchQueryResultPatcherDefault(get(), get()) }
    single<SearchResultManager> { get<SearchResultManagerDefault>() }
    single<SearchResultManagerDefault> { SearchResultManagerDefault(get(), get(), get()) }
    single<SearchResultSorter> { SearchResultSorterDefault(get()) }
    single<SearchSessionManager> { get<SearchSessionManagerDefault>() }
    single<SearchSessionManagerDefault> { SearchSessionManagerDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
}