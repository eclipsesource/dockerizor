package com.eclipsesource.dockerizor

import org.gradle.api.Plugin
import org.gradle.api.Project

class Dockerizor implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("dockerizor", DockerizorExtension)
        project.dockerizor.with {
            // docker image
            maintainer = '<unconfigured maintainer>'
            description = '<no description>'

            // docker client configuration
            uri = "http://localhost:4243"
            repository = "<unconfigured repository>"
            tag = "latest"

            dryRun = false

            javaImage = 'java:openjdk-6b36-jre'
            virgoVersion = '3.6.4.RELEASE'
            hudsonJobName = 'gradle-build'
            virgoFlavour = 'VTS'
            virgoHome = '/home/virgo'
            removeAdminConsole = true
            removeSplash = true
            enableUserRegionOsgiConsole = false
            embeddedSpringVersion = '3.1.0.RELEASE'
            exposeHttpPort = true
            hostname = "unconfigured_hostname"

            postDockerizeHook = {  project.logger.debug "Running empty post processor" }
        }

        project.configurations { endorsed { transitive = false } }
        project.configurations { repositoryExt { transitive = false } }
        project.configurations { repositoryUsr { transitive = false } }

        project.task('dockerize', type: DockerizorTask) {
            group = 'docker'
            description = "Populates a Docker image with the configured Virgo container."

            doFirst {
                logger.info "Creating custom image FROM ${project.dockerizor.javaImage}"
                FROM ("${project.dockerizor.javaImage}")
                MAINTAINER ("${project.dockerizor.maintainer}")
                LABEL ("Description=\"${project.dockerizor.description}\"")

                logger.info "Installing Virgo runtime (${project.dockerizor.virgoFlavour}) version ${project.dockerizor.virgoVersion}..."
                def virgoHome = project.dockerizor.virgoHome
                logger.info "Installing Virgo into ${virgoHome}."
                RUN ("apt-get update")
                RUN ("apt-get install -y curl bsdtar")
                RUN ("useradd -m virgo")
                logger.info "Using Virgo download URL: '${project.dockerizor.downloadUrl}'"
                RUN ("curl -L '${project.dockerizor.downloadUrl}' | bsdtar --strip-components 1 -C ${virgoHome} -xzf -")

                if(project.dockerizor.removeAdminConsole) {
                    switch (project.dockerizor.virgoFlavour) {
                        case 'VJS':
                        case 'VTS':
                            RUN "rm ${virgoHome}/pickup/org.eclipse.virgo.management.console_*.jar"
                            break
                        default:
                            logger.warn "Ignoring request to remove admin console from ${project.dockerizor.virgoFlavour}."
                    }
                }

                if (project.dockerizor.removeSplash) {
                    switch (project.dockerizor.virgoFlavour) {
                        case 'VJS':
                        case 'VTS':
                            RUN "rm -f ${virgoHome}/pickup/org.eclipse.virgo.apps.splash_*.jar"
                        // splash bundle is differently named in VJS in 3.6.x stream
                            RUN "rm -f ${virgoHome}/pickup/org.eclipse.virgo.apps.splash-*.jar"
                        // only available in VTS
                            RUN "rm -f ${virgoHome}/pickup/org.eclipse.virgo.apps.repository_*.par"
                            break
                        default:
                            logger.warn "Ignoring request to remove splash from ${project.dockerizor.virgoFlavour}."
                    }
                }

                if (project.dockerizor.enableUserRegionOsgiConsole) {
                    switch (project.dockerizor.virgoFlavour) {
                        case 'VJS':
                        case 'VTS':
                            if(!project.dockerizor.hostname) {
                                throw new IllegalArgumentException("'hostname' not set in dockerizor plugin")
                            }
                            ENV ('DOCKER_HOSTNAME', project.dockerizor.hostname)
                            EXPOSE 2501
                            logger.info "NOTE: The telnet.host has to be set with -h ${project.dockerizor.hostname} when starting this container!"
                            logger.info "      Otherwise the OSGi console will not be accessable"
                            RUN ("sed -i 's/telnet.enabled=false/telnet.enabled=true/' ${virgoHome}/repository/ext/osgi.console.properties")
                            RUN ("sed -i 's/telnet.host=localhost/telnet.host=${project.dockerizor.hostname}/' ${virgoHome}/repository/ext/osgi.console.properties")
                            break
                        default:
                            logger.warn "Ignoring request to enable user region OSGi console for ${project.dockerizor.virgoFlavour}."
                    }
                }

                if (project.dockerizor.exposeHttpPort) {
                    switch (project.dockerizor.virgoFlavour) {
                        case 'VJS':
                        case 'VTS':
                        case 'VRS':
                            EXPOSE 8080
                            break
                        default:
                            logger.warn "Ignoring request to expose HTTP port for ${project.dockerizor.virgoFlavour}."
                    }
                }

                logger.info "Processing pickup files: ${project.dockerizor.pickupFiles}:"
                project.dockerizor.pickupFiles.each {
                    ADD ("${it}", "${virgoHome}/pickup/")
                }
                logger.info "done"

                logger.info "Processing bin files: ${project.dockerizor.binFiles}:"
                project.dockerizor.binFiles.each {
                    ADD ("${it}", "${virgoHome}/bin/")
                }
                logger.info "done"

                RUN ("chmod u+x ${virgoHome}/bin/*.sh")

                logger.info "Provisioning Virgo endorsed:"
                project.configurations.endorsed.each {
                    logger.debug "Adding dependency to endorsed: " + it
                    ADD (it, "${virgoHome}/lib/endorsed/")
                }
                logger.info "done"

                logger.info "Provisioning Virgo repository ext/usr:"
                project.configurations.repositoryExt.each {
                    logger.debug "Adding dependency to repository/ext: " + it
                    ADD (it, "${virgoHome}/repository/ext/")
                }
                project.configurations.repositoryUsr.each {
                    logger.debug "Adding dependency to repository/usr: " + it
                    ADD (it, "${virgoHome}/repository/usr/")
                }
                logger.info "done"

                logger.info "Running custom post processor:"
                project.dockerizor.postProcessor(project.dockerize)
                logger.info "done"

                RUN ("chown -R virgo:virgo ${virgoHome}")
                USER ("virgo")

                CMD ("${virgoHome}/bin/startup.sh")
            }
            doLast() { logger.info "Successful dockerized '${project.dockerizor.repository}'." }
        }
    }
}
