## How-To build dockerizor

We use [Gradle](http://gradle.org/) to build the Gradle Plugin [dockerizor](https://github.com/eclipsesource/dockerizor).

    ::::sh
    $ git clone https://github.com/eclipsesource/dockerizor.git
    $ cd dockerizor
    $ ./gradlew build

Publish the plugin to your local [Maven](https://maven.apache.org/) repository.

    ::::sh
    $ ./gradlew clean build publishToMavenLocal

Publish the plugin to [Gradle Plugins](https://plugins.gradle.org/)

    ::::sh
    $ ./gradlew clean build publishPlugins
