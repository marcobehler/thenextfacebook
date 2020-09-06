package settings

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MarcoBehler : Project({


    id("MarcoBehler")


    buildType(Package_1)
    buildType(Test)
    buildType(Build)
    buildTypesOrder = arrayListOf(Build, Package_1, Test)
})

object MarcoBehlerRoot : GitVcsRoot(
    {
        name = "MarcoBehlerRoot"
      url = "https://github.com/marcobehler/thenextfacebook.git"
    }
)

object Build : BuildType({
    name = "Build"

    vcs {
        root(MarcoBehlerRoot)
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

    requirements {
        noLessThanVer("teamcity.agent.jvm.specification", "11")
    }
})

object Package_1 : BuildType({
    id("Package")
    name = "Package"

    artifactRules = "+:target/*.jar"

    vcs {
        root(MarcoBehlerRoot)
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

object Test : BuildType({
    name = "Test"

    vcs {
        root(MarcoBehlerRoot)
    }

    steps {
        maven {
            goals = "test"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${Build.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(Build) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})
