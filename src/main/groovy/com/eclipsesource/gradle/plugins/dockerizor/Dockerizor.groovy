package com.eclipsesource.gradle.plugins.dockerizor

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import se.transmode.gradle.plugins.docker.DockerTask

class Dockerizor implements Plugin<Project> {
	void apply(Project project) {
		project.extensions.create("dockerizor", DockerizorExtension)
		project.dockerizor.with {
			virgoVersion = '3.6.3.RELEASE'
			virgoFlavour = 'VTS'
			removeAdminConsole = true
			removeSplash = true
			enableUserRegionOsgiConsole = false
			embeddedSpringVersion = '3.1.0.RELEASE'
			exposeHttpPort = true
		}

		project.task('dockerize', type: DockerTask) {
			group = 'docker'
			description = "Populates a Docker image with the configured Virgo container."

			doFirst {
				applicationName = project.dockerizor.imageName
				switch ( project.dockerizor.javaVersion ) {
					case "1.7":
						runCommand "sed 's/main\$/main universe/' -i /etc/apt/sources.list"
						runCommand "apt-get update && apt-get install -y software-properties-common python-software-properties"
						runCommand "add-apt-repository ppa:webupd8team/java -y"
						runCommand "apt-get update"
						runCommand "echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections"
						runCommand "apt-get install -y oracle-java7-installer"
						runCommand "apt-get clean"
						break
					default:
						throw new IllegalArgumentException("Java version ${javaVersion} *not* supported")
				}

				println "Building image from ${project.docker.baseImage}..."
				println "Adding Virgo Container (${project.dockerizor.virgoFlavour}) in version ${project.dockerizor.virgoVersion}..."
				def virgoHome = '/home/virgo'
				println "Installing Virgo into ${virgoHome}."
				runCommand ('useradd -m virgo')
				runCommand ("apt-get install -y curl bsdtar")
				runCommand ("curl -L '${project.dockerizor.downloadUrl}' | bsdtar --strip-components 1 -C ${virgoHome} -xzf -")

				if(project.dockerizor.removeAdminConsole) {
					switch (project.dockerizor.virgoFlavour) {
						case 'VJS':
						case 'VTS':
							runCommand "rm ${virgoHome}/pickup/org.eclipse.virgo.management.console_${project.dockerizor.virgoVersion}.jar"
							break
						default:
							println "Ignoring request to remove admin console from ${project.dockerizor.virgoFlavour}."
					}
				}

				if (project.dockerizor.removeSplash) {
					switch (project.dockerizor.virgoFlavour) {
						case 'VJS':
							runCommand "rm ${virgoHome}/pickup/org.eclipse.virgo.apps.splash-${project.dockerizor.virgoVersion}.jar"
							break
						case 'VTS':
							runCommand "rm ${virgoHome}/pickup/org.eclipse.virgo.apps.splash_${project.dockerizor.virgoVersion}.jar"
							runCommand "rm ${virgoHome}/pickup/org.eclipse.virgo.apps.repository_${project.dockerizor.virgoVersion}.par"
							break
						default:
							println "Ignoring request to remove splash from ${project.dockerizor.virgoFlavour}."
					}
				}

				if (project.dockerizor.enableUserRegionOsgiConsole) {
					switch (project.dockerizor.virgoFlavour) {
						case 'VJS':
						case 'VTS':
							setEnvironment ('DOCKER_HOSTNAME', project.dockerizor.hostname)
							println 'Exposing HTTP port 2501'
							exposePort 2501
							println "NOTE: The telnet.host has to be set with -h ${project.dockerizor.hostname} when starting this container!"
							println "      Otherwise the OSGi console will not be accessable"
							runCommand ("sed -i 's/telnet.enabled=false/telnet.enabled=true/' ${virgoHome}/repository/ext/osgi.console.properties")
							runCommand ("sed -i 's/telnet.host=localhost/telnet.host=${project.dockerizor.hostname}/' ${virgoHome}/repository/ext/osgi.console.properties")
							break
						default:
							println "Ignoring request to enable user region OSGi console for ${project.dockerizor.virgoFlavour}."
					}
				}

				switch (project.dockerizor.embeddedSpringVersion) {
					// TODO - automagically add or check if Spring is available in runtime dependencies
					case '3.1.0.RELEASE':
						println "Ignoring request to use default Spring version."
						break
					case '3.2.4.RELEASE':
						println "Replacing Spring 3.1.0.RELEASE with ${project.dockerizor.embeddedSpringVersion} in ${virgoHome}/repository/ext"
						runCommand ("rm -rf ${virgoHome}/repository/ext/org.springframework.*_3.1.0.RELEASE.*")
						project.configurations.runtime.each {
							if (it.canonicalPath.contains('org.springframework/org.springframework.')) {
								println it.canonicalPath
								addFile it, "${virgoHome}/repository/ext/"
							}
						}
						runCommand ("curl -L -o ${virgoHome}/repository/ext/org.springframework.spring-library-${project.dockerizor.embeddedSpringVersion}.libd \"http://ebr.springsource.com/repository/app/library/version/download?name=org.springframework.spring&version=${project.dockerizor.embeddedSpringVersion}&type=library\"")

						println "Replacing AspectJ 1.6.12.RELEASE with 1.7.2.RELEASE in ${virgoHome}/repository/ext and ${virgoHome}/plugins"
						runCommand ("rm -rf ${virgoHome}/plugins/com.springsource.org.aspectj.weaver_1.6.12.RELEASE.jar")
						runCommand ("rm -rf ${virgoHome}/repository/ext/com.springsource.org.aspectj.weaver_1.6.12.RELEASE.jar")
						project.configurations.runtime.each {
							if (it.canonicalPath.contains('com.springsource.org.aspectj.weaver')) {
								addFile it, "${virgoHome}/repository/ext/"
								addFile it, "${virgoHome}/plugins/"
							}
						}
						runCommand ("sed -i -e 's/org.aspectj\\.\\*;version.*/org.aspectj.*;version=\"[1.7.2.RELEASE,2.0.0)\",\\\\/' ${virgoHome}/configuration/org.eclipse.virgo.kernel.userregion.properties")
						runCommand ("sed -i -e 's/^com.springsource.org.aspectj.weaver,.*/com.springsource.org.aspectj.weaver,1.7.2.RELEASE,plugins\\/com.springsource.org.aspectj.weaver-1.7.2.RELEASE.jar,4,false/' ${virgoHome}/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info")
						break
					default:
						throw new IllegalArgumentException("Spring version ${project.dockerizor.embeddedSpringVersion} *not* supported")
				}

				if (project.dockerizor.exposeHttpPort) {
					switch (project.dockerizor.virgoFlavour) {
						case 'VJS':
						case 'VTS':
						case 'VRS':
							println 'Exposing HTTP port 8080'
							exposePort 8080
							break
						default:
							println "Ignoring request to expose HTTP port for ${project.dockerizor.virgoFlavour}."
					}
				}

				project.dockerizor.pickupFiles.each {
					println "Adding ${it} to pickup directory"
					addFile "${it}", "${virgoHome}/pickup/"
				}

				project.dockerizor.binFiles.each {
					println "Adding ${it} to bin directory"
					addFile "${it}", "${virgoHome}/bin/"
				}

				runCommand ("chmod u+x ${virgoHome}/bin/*.sh")

				def defaultCommand = ["${virgoHome}/bin/startup.sh"]
				setDefaultCommand defaultCommand
			}
		}
	}
}
