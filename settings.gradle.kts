dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven("https://jitpack.io")
        maven("https://repository.map.naver.com/archive/maven")
    }
}
rootProject.name = "Senty"
include(":app")