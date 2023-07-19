package org.kiwilang.preprocessor

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClassVersion(val version: String)
