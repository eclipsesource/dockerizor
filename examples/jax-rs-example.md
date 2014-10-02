## Dockerize an OSGi JAX-RS Connector Example

This example demonstrates how to build Docker images for Virgo powered applications with [Dockerizor][Dockerizor].

The code is based on the DS example from the [OSGi - JAX-RS Connector][jax-rs-connector] project.

### Build the Docker Image

Please note: The following step requires that you have a Docker daemon running locally on port 4243.

    $ ./gradlew build dockerize

This will create the Docker image ``dockerizor-examples/jax-rs-example``.

### Running the Container

To start the previously dockerized application  

    docker run -d --name="jax-rs-example" --publish=8080:8080 --publish=9090:9090 -t dockerizor-examples/jax-rs-example

The command runs the ``jax-rs-example`` and publishes the ports ``8080`` and ``9090``.

### Accessing the JAX-RS Application

The example exposes the OSGi HttpService on port 9090 with the rest service:

http://localhost:9090/services/osgi-jax-rs/

    JAX-RS and OSGi are a lovely couple.

http://localhost:9090/services/product/1

    Product name: Pencil
    Product Description :
    Simple writing instrument    

The Virgo admin console is available at the default HTTP port of Virgo:

http://localhost:8080/admin

### Customizing the Docker Image

  [Virgo]: http://eclipse.org/virgo
  [EclipseRT]: http://eclipse.org/rt
  [Dockerizor]: https://github.com/eclipsesource/dockerizor
  [jax-rs-connector]: https://github.com/hstaudacher/osgi-jax-rs-connector
