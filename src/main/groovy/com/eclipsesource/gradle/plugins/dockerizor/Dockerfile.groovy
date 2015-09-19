package com.eclipsesource.gradle.plugins.dockerizor

import java.util.List;

import org.gradle.api.GradleException

class Dockerfile {

    File dockerfile
    List<String> commands = []

    void init(File dockerfile) {
        this.dockerfile = dockerfile
        boolean success = dockerfile.createNewFile()
        if (!success) {
            throw new GradleException("Failed to create Dockerfile")
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
