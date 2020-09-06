package settings

import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object TheNextFaceBook : Project({
    vcsRoot(TheNextFaceBookRoot)
    buildType(ValidateBuildConfig)
    buildType(Compile)
    buildType(Test)
    buildType(Package)
    buildTypesOrder = arrayListOf(ValidateBuildConfig, Compile, Test, Package)
})

object TheNextFaceBookRoot : GitVcsRoot(
        {
            name = "TheNextFaceBookRoot"
            url = "https://github.com/marcobehler/thenextfacebook.git"
        }
)

object ValidateBuildConfig : BuildType({
    name = "Validate BuildConfig"
    vcs {
        root(TheNextFaceBookRoot)
    }
    steps {
        maven {
            goals = "clean test"
            pomLocation = ".teamcity/pom.xml"
        }
    }
})

object Compile : BuildType({
    name = "Build"

    vcs {
        root(TheNextFaceBookRoot)
    }

    steps {
        maven {
            goals = "clean compile"
        }
    }

    triggers {
        vcs {
        }
    }

    dependencies {
        snapshot(ValidateBuildConfig) {
        }
    }

    requirements {
        noLessThanVer("teamcity.agent.jvm.version", "11")
    }
})

object Test : BuildType({
    name = "Test"

    vcs {
        root(TheNextFaceBookRoot)
    }

    steps {
        maven {
            goals = "test"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${Compile.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(Compile) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})


object Package : BuildType({
    name = "Package"

    artifactRules = "+:target/*.jar"

    vcs {
        root(TheNextFaceBookRoot)
    }

    steps {
        maven {
            goals = "package"
            runnerArgs = "-Dmaven.test.skip=true"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${Test.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(Test) {
            onDependencyFailure = FailureAction.CANCEL
        }
    }
})

