import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

plugins {
    id("io.freefair.lombok") version "9.1.0"
}

dependencies {
    labyProcessor()
    api(project(":api"))
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}