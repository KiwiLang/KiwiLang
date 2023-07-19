package org.kiwilang.preprocessor

import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class ClassVersionProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(ClassVersion::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        for (element in roundEnv.getElementsAnnotatedWith(ClassVersion::class.java)) {
            val annotation = element.getAnnotation(ClassVersion::class.java)
            val version = annotation.version
            val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
            val className = element.simpleName.toString()
            val versionClassName = "${className}Version"
            val versionClass = processingEnv.filer.createClassFile("$packageName.$versionClassName")
            versionClass.openWriter().use { writer ->
                writer.appendln("package $packageName;")
                writer.appendln("")
                writer.appendln("public final class $versionClassName {")
                writer.appendln("    public static final String VERSION = \"$version\";")
                writer.appendln("}")
            }
        }
        return true
    }
}