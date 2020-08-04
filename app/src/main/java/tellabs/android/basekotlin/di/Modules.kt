package tellabs.android.basekotlin.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tellabs.android.basekotlin.data.db.AppDatabase
import tellabs.android.basekotlin.data.remote.createWebService
import tellabs.android.basekotlin.data.remote.provideOkHttpClient
import tellabs.android.basekotlin.data.pref.PreferencesHelper
import tellabs.android.basekotlin.data.remote.AuthInterceptor
import tellabs.android.basekotlin.data.repository.TeamRepository
import tellabs.android.basekotlin.data.repository.TeamRepositoryImpl
import tellabs.android.basekotlin.data.remote.service.TeamService
import tellabs.android.basekotlin.presentation.comunicate.ComunicateViewModel
import tellabs.android.basekotlin.presentation.home.PairedDeviceViewModel

val networkModule = module {
    single { PreferencesHelper(androidContext()) }
    single { provideOkHttpClient(get(), get()) }
    single { createWebService<TeamService>(get()) }
    single { AuthInterceptor(get()) }
    //add this line if u use rxJava instead courotine
//    single { AppSchedulerProvider() as SchedulerProvider }
}

val viewModelModule = module {
    viewModel { PairedDeviceViewModel() }
    viewModel { ComunicateViewModel() }
}

val dataBaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "app-database").build()
    }
    single { get<AppDatabase>().teamDao() }
}


val repositoryModule = module {

    single<TeamRepository> { TeamRepositoryImpl(get(), get()) }
}

val myAppModule = listOf(networkModule, repositoryModule,viewModelModule, dataBaseModule)