package com.eclipsesource.dockerizor
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class DockerizorTaskTest {

    Project project
    Task task

    File dockerfile

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.eclipsesource.dockerizor'
        task = project.tasks['dockerize']
        project.dockerizor.dryRun = true
        dockerfile = new File(task.outputDir, "Dockerfile")
    }

    @Test
    public void dockerizorTaskShouldCreateDockerWorkingDirectory() {
        task.build()

        assertTrue task.outputDir.exists()
    }

    @Test
    public void dockerizorTaskShouldCreateDockerfile() {
        task.build()

        assertTrue dockerfile.exists()
    }

    @Test
    public void dockerizorTaskShouldUseDefaultUri() {
        task.build()

        assertEquals('unix:///var/run/docker.sock', task.uri)
    }

}
