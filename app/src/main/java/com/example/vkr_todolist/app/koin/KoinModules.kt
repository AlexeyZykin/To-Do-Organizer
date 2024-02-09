package com.example.vkr_todolist.app.koin

import com.example.vkr_todolist.cache.koin.cacheModule
import com.example.vkr_todolist.data.koin.dataModule
import com.example.vkr_todolist.domain.koin.domainModule
import com.example.vkr_todolist.presentation.koin.presentationModule

val koinModules = listOf(
    presentationModule,
    domainModule,
    dataModule,
    cacheModule
)