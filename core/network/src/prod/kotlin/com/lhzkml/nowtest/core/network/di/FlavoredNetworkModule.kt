package com.lhzkml.nowtest.core.network.di

import com.lhzkml.nowtest.core.network.NtNetworkDataSource
import com.lhzkml.nowtest.core.network.retrofit.RetrofitNtNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {

    @Binds
    fun binds(impl: RetrofitNtNetwork): NtNetworkDataSource
}
