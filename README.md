[![Build Status](https://travis-ci.org/eclipsesource/dockerizor.svg)](https://travis-ci.org/eclipsesource/dockerizor)

dockerizor
==========

[Gradle][gradle] plug-in to create a Docker image that includes an [Eclipse Virgo][Virgo] container.

During our preparations for the EclipseCon talk about our first Docker project, we spent quite some time
packaging Virgo containers inside Docker images.

We investigated how to improve the continuous delivery of Virgo powered applications using Gradle and Docker. The outcome is the Gradle Plugin ''dockerizor''.

In a first step we automated the generation of basic Virgo images.

### Using the Gradle Plugin

The following build snippet applies the [Gradle Plugin Dockerizor][dockerizor] to your build script:
 
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.eclipsesource.dockerizor:dockerizor:0.6.2"
  }
}

apply plugin: "com.eclipsesource.dockerizor"
```

### Building basic Virgo images

Both the Docker...

```groovy
dockerizor {
  maintainer = 'Florian Waibel <fwaibel@eclipsesource.com>'
  description = 'Docker image build with dockerizor'

  // docker client configuration
  uri = "http://localhost:4243"
  tag = "latest"

  javaImage = 'java:openjdk-6b36-jre'
}
```

...and Virgo specific configuration is done within the ```dockerizor``` block. 

```groovy
dockerizor {
  virgoFlavour = 'VJS'
  removeAdminConsole = true
  removeSplash = true

  imageName = 'virgo-jetty-server'
}
```

The snippet above creates a Docker image named 'virgo-jetty-server' with the Virgo flavor VJS (Virgo Jetty Server).

```sh
$ docker images | grep virgo-jetty-server
eclipsesource/virgo-jetty-server   3.6.4.RELEASE       0f900c762dcf        5 minutes ago       332.7 MB
```

The generated basic images for Virgo are available via [Docker Hub][dockerhub]:

 * Virgo Server for Apache Tomcat: https://registry.hub.docker.com/u/eclipsesource/virgo-tomcat-server/
 * Virgo Jetty Server: https://registry.hub.docker.com/u/eclipsesource/virgo-jetty-server/
 * Virgo RAP Server: https://registry.hub.docker.com/u/eclipsesource/virgo-rap-server/

[Virgo]: http://www.eclipse.org/virgo/ "Virgo"
[dockerhub]: https://hub.docker.com/ "Docker Hub"
[gradle]: http://gradle.org/ "Gradle"
[dockerizor]: https://plugins.gradle.org/plugin/com.eclipsesource.dockerizor "Gradle Plugin Dockerizor"
