package com.lhzkml.nowtest.core.network.retrofit

import com.lhzkml.nowtest.core.network.NtNetworkDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Retrofit] backed [NtNetworkDataSource]
 */
@Singleton
internal class RetrofitNtNetwork @Inject constructor() : NtNetworkDataSource
