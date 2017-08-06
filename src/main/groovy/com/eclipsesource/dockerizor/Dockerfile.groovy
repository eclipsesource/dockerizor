package com.eclipsesource.dockerizor

import java.util.List

import org.gradle.api.GradleException

class Dockerfile {

    File dockerfile
    List<String> commands = []

    void init(File dockerfile) {
        this.dockerfile = dockerfile
        boolean success = dockerfile.createNewFile()
        if (!success) {
            // try to delete and recreate (existing) Dockerfile
            dockerfile.delete()
            success = dockerfile.createNewFile()
        }
        if (!success) {
            throw new GradleException("Dockerizor Plugin Error: Failed to create Dockerfile: '" + this.dockerfile + "'.")
        }
    }

    void addCommand(String command) {
        commands.add(command)
    }

    void write() {
        dockerfile.withWriter { out ->
            commands.each { out.println it }
        }
    }
}
