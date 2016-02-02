package com.eclipsesource.dockerizor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import com.eclipsesource.dockerizor.DockerizorExtension;
import com.eclipsesource.dockerizor.DockerizorTask;

import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

class DockerizorTest {

	@Test
	public void dockerizorShouldBeDockerizorExtension() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.eclipsesource.dockerizor'

		assertTrue(project.dockerizor instanceof DockerizorExtension)
	}

	@Test
	public void dockerizeShouldBeDockerTask() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.eclipsesource.dockerizor'

		assertTrue(project.tasks.dockerize instanceof DockerizorTask)
	}
	
}
