import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {

    buildType(Package_1)
    buildType(Test)
    buildType(Build)
    buildTypesOrder = arrayListOf(Build, Test, Package_1)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
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
        root(DslContext.settingsRoot)
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
        root(DslContext.settingsRoot)
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
