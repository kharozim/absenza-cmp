package id.neo.hr.di

import org.koin.core.module.Module

/**
 * Modul dependency injection untuk binding platform spesifik (Android & iOS).
 */
expect val platformModule: Module
