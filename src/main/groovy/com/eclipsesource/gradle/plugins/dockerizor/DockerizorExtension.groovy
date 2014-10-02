package com.eclipsesource.gradle.plugins.dockerizor

import se.transmode.gradle.plugins.docker.DockerTask

class DockerizorExtension {
	String javaVersion
	String virgoVersion
	String virgoFlavour
	String virgoHome
	String hostname
	Boolean removeAdminConsole
	Boolean removeSplash
	Boolean enableUserRegionOsgiConsole
	Boolean exposeHttpPort

	String[] pickupFiles
	String[] binFiles
	String embeddedSpringVersion
	String imageName

	Closure closure = {
	}

	def postProcessor(DockerTask task) {
		closure(task)
	}

	String getArchiveName() {
		switch (virgoFlavour) {
			case 'VJS':
				switch (virgoVersion) {
					case 'latest':
					return "virgo-jetty-server-3.7.0.CI-latest"
					default:
					return "virgo-jetty-server-${virgoVersion}.zip"
				}
			case 'VTS':
				switch (virgoVersion) {
					case 'latest':
					return "virgo-tomcat-server-3.7.0.CI-latest"
					default:
					return "virgo-tomcat-server-${virgoVersion}.zip"
				}
			case 'VRS':
				switch (virgoVersion) {
					case 'latest':
					return "virgo-nano-rap-3.7.0.CI-latest"
					default:
					throw new IllegalArgumentException("Virgo flavour ${virgoFlavour}/${virgoVersion} *not* supported")
				}
			default:
				throw new IllegalArgumentException("Virgo flavour ${virgoFlavour} *not* supported")
		}
	}

	String getDownloadUrl() {
		switch (virgoFlavour) {
			case 'VJS':
			case 'VTS':
				switch (virgoVersion) {
					case 'latest':
					println "Using latest successful build for ${virgoFlavour}/3.7.0.CI."
					return "https://hudson.eclipse.org/virgo/view/Ripplor%20Repositories/job/virgo.packaging.snapshot/lastSuccessfulBuild/artifact/build-packaging/target/artifacts/${archiveName}.zip"
					default:
					return "http://www.eclipse.org/downloads/download.php?file=/virgo/release/VP/${virgoVersion}/${archiveName}&mirror_id=580&r=1"
				}
			case 'VRS':
				switch (virgoVersion) {
					case 'latest':
					println "Using latest successful build for ${virgoFlavour}/3.7.0.CI."
					return "https://hudson.eclipse.org/virgo/view/Ripplor%20Repositories/job/virgo.packaging.snapshot/lastSuccessfulBuild/artifact/build-packaging/target/artifacts/${archiveName}.zip"
					default:
					throw new IllegalArgumentException("Virgo flavour ${virgoFlavour}/${virgoVersion} *not* supported")
				}
			default:
				throw new IllegalArgumentException("Virgo flavour ${virgoFlavour} *not* supported")
		}
	}
}
