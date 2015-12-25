package com.eclipsesource.gradle.plugins.dockerizor

import org.gradle.api.Plugin
import org.gradle.api.Project

class Dockerizor implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("dockerizor", DockerizorExtension)
        project.dockerizor.with {
            // docker image
            maintainer = '<unconfigure maintainer>'
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

            closure = {  println "Running empty post processor" }
        }

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
                USER ("virgo")
                RUN ("curl -L '${project.dockerizor.downloadUrl}' | bsdtar --strip-components 1 -C ${virgoHome} -xzf -")

                if(project.dockerizor.removeAdminConsole) {
                    switch (project.dockerizor.virgoFlavour) {
                        case 'VJS':
                        case 'VTS':
                            RUN "rm ${virgoHome}/pickup/org.eclipse.virgo.management.console_*.jar"
                            break
                        default:
                            println "Ignoring request to remove admin console from ${project.dockerizor.virgoFlavour}."
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
                            println "Ignoring request to remove splash from ${project.dockerizor.virgoFlavour}."
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

                //                switch (project.dockerizor.embeddedSpringVersion) {
                //                    // TODO - automagically add or check if Spring is available in runtime dependencies
                //                    case '3.1.0.RELEASE':
                //                        if (project.dockerizor.virgoVersion == '3.6.4.RELEASE') {
                //                            println "Skipping request to add default Spring version."
                //                            break
                //                        }
                //                    case '3.2.4.RELEASE':
                //                        println "Replacing Spring 3.1.0.RELEASE with ${project.dockerizor.embeddedSpringVersion} in ${virgoHome}/repository/ext"
                //                        runCommand ("rm -rf ${virgoHome}/repository/ext/org.springframework.*_3.1.0.RELEASE.*")
                //                        project.configurations.runtime.each {
                //                            if (it.canonicalPath.contains('org.springframework/org.springframework.')) {
                //                                println it.canonicalPath
                //                                addFile it, "${virgoHome}/repository/ext/"
                //                            }
                //                        }
                //                        runCommand ("curl -L -o ${virgoHome}/repository/ext/org.springframework.spring-library-${project.dockerizor.embeddedSpringVersion}.libd \"http://ebr.springsource.com/repository/app/library/version/download?name=org.springframework.spring&version=${project.dockerizor.embeddedSpringVersion}&type=library\"")
                //
                //                        println "Replacing AspectJ 1.6.12.RELEASE with 1.7.2.RELEASE in ${virgoHome}/repository/ext and ${virgoHome}/plugins"
                //                        runCommand ("rm -rf ${virgoHome}/plugins/com.springsource.org.aspectj.weaver_1.6.12.RELEASE.jar")
                //                        runCommand ("rm -rf ${virgoHome}/repository/ext/com.springsource.org.aspectj.weaver_1.6.12.RELEASE.jar")
                //                        project.configurations.runtime.each {
                //                            if (it.canonicalPath.contains('com.springsource.org.aspectj.weaver')) {
                //                                addFile it, "${virgoHome}/repository/ext/"
                //                                addFile it, "${virgoHome}/plugins/"
                //                            }
                //                        }
                //                        runCommand ("sed -i -e 's/org.aspectj\\.\\*;version.*/org.aspectj.*;version=\"[1.7.2.RELEASE,2.0.0)\",\\\\/' ${virgoHome}/configuration/org.eclipse.virgo.kernel.userregion.properties")
                //                        runCommand ("sed -i -e 's/^com.springsource.org.aspectj.weaver,.*/com.springsource.org.aspectj.weaver,1.7.2.RELEASE,plugins\\/com.springsource.org.aspectj.weaver-1.7.2.RELEASE.jar,4,false/' ${virgoHome}/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info")
                //                        break
                //                    case '3.2.10.RELEASE':
                //                        if (project.dockerizor.virgoVersion == 'latest') {
                //                            println "Skipping request to add default Spring version."
                //                            break
                //                        }
                //                    default:
                //                        throw new IllegalArgumentException("Spring version ${project.dockerizor.embeddedSpringVersion} *not* supported")
                //                }

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
                CMD ("${virgoHome}/bin/startup.sh")

                logger.debug "Running custom post processer:"
                project.dockerizor.postProcessor(project.dockerize)
                logger.info "done"
            }
        }
    }
}
