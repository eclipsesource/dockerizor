[![Build Status](https://travis-ci.org/eclipsesource/dockerizor.svg)](https://travis-ci.org/eclipsesource/dockerizor)

dockerizor
==========

Gradle plug-in to create a Docker image that includes an [Virgo][Eclipse Virgo] container.

During our preparations for the EclipseCon talk about our first Docker project, we spent quite some time
packaging Virgo containers inside Docker images.

We investigated how to improve the continuous delivery of Virgo powered applications using Gradle and Docker. The outcome is the Gradle Plugin 'dockerizor'.

In a first step we automated the generation of basic Virgo images.

### Building basic Virgo images

In your build script you specify Docker related configuration via the 'gradle-docker'[gradle-docker] [] Plugin.

	apply plugin: 'docker'
	docker {
		baseImage = 'ubuntu:14.04'
		maintainer = 'Florian Waibel <fwaibel@eclipsesource.com>'

		version = 'latest'

		useApi true
		hostUrl 'http://localhost:4243'
	}

In addition to this you can specify the Virgo specific configuration via the 'dockerizor' Plugin:

    apply plugin: 'dockerizor'
    
    dockerizor {
        virgoFlavour = 'VJS'
        removeAdminConsole = true
        removeSplash = true
        		
        virgoVersion = 'latest'
        imageName = 'virgo-jetty-server'
    }

The snippet above will create a Docker image named 'virgo-jetty-server' with the Virgo flavor VJS (Virgo Jetty Server).

	$ docker images | grep virgo-jetty-server

The generated basic images for Virgo are available via [dockerhub][Docker Hub]:

 * Virgo Server for Apache Tomcat: https://registry.hub.docker.com/u/eclipsesource/virgo-tomcat-server/
 * Virgo Jetty Server: https://registry.hub.docker.com/u/eclipsesource/virgo-jetty-server/
 * Virgo RAP Server: https://registry.hub.docker.com/u/eclipsesource/virgo-rap-server/

[Virgo]: http://www.eclipse.org/virgo/ "Virgo"
[gradle-docker]: https://github.com/Transmode/gradle-docker "gradle-docker"
[dockerhub]: https://hub.docker.com/ "Docker Hub"