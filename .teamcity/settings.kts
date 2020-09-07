import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

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
    vcsRoot(TheNextFaceBookRoot)
    buildType(Compile)
    buildType(Test)
    buildType(Package)

    buildTypesOrder = arrayListOf(Compile, Test, Package)
}

object TheNextFaceBookRoot : GitVcsRoot(
    {
        name = "TheNextFaceBookRoot"
        url = "https://github.com/marcobehler/thenextfacebook.git"
    }
)

object Compile : BuildType({
    name = "Compile"

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

