[![Build Status](https://travis-ci.org/eclipsesource/dockerizor.svg)](https://travis-ci.org/eclipsesource/dockerizor)

dockerizor
==========

Gradle plug-in to create a Docker image that includes an Eclipse Virgo container

During our preparations for the EclipseCon talk about our first Docker project, we spent quite some time
packaging Virgo containers inside Docker images.

We investigating how to improve the continuous delivery of Virgo powered applications using Gradle and Docker. The outcome is the Gradle Plugin 'dockerizor'.

In your build script you specify Docker related configuration via the 'gradle-docker' [https://github.com/Transmode/gradle-docker] Plugin.
