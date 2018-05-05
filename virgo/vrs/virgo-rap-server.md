# Virgo RAP Server

[Virgo][Virgo] from [EclipseRT][EclipseRT] is a completely module-based Java application server that is designed to run enterprise Java applications and Spring-powered applications with a high degree of flexibility and reliability. It offers a simple yet comprehensive platform to develop, deploy, and service enterprise Java applications.

## Build your Docker image

To create an application based on this image you can simply drop your application into the ``pickup`` folder.
The following Dockerfile would add your application bundle to the Docker container:

```Dockerfile
FROM eclipsesource/virgo-rap-server

ADD my-app.jar /home/virgo/pickup/
```

Change into the directory that contains your Dockerfile and build your image.

```shell
$ docker build .
```

#3 Running the Container

The following command starts the Virgo RAP Server in a Docker container and exposes the port ``8080``.

```shell
$ docker run -d --name="virgo-rap-server" --publish=8080:8080 -t eclipsesource/virgo-rap-server:latest
```

## Accessing your Web Application

The context path of the application is specified in the OSGi metadata of your application bundle.

## Customize the Virgo Container

The Virgo container is built with [Dockerizer][Dockerizor]:

```groovy
dockerizor {
    virgoFlavour = 'VRS'
    removeAdminConsole = true
    removeSplash = true

    virgoVersion = 'latest'
    imageName = 'virgo-rap-server'
}
```

> *Note:* The Admin Console and the Splash screen have been removed from the container.

[Virgo]: http://eclipse.org/virgo
[EclipseRT]: http://eclipse.org/rt
[Dockerizor]: https://github.com/eclipsesource/dockerizor
