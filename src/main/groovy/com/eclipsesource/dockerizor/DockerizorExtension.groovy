package com.eclipsesource.dockerizor

class DockerizorExtension {

    String maintainer
    String description

    String uri
    String repository
    String tag

    boolean dryRun

    String javaImage
    String virgoVersion
    String hudsonJobName
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

    Closure postDockerizeHook = {
    }

    def postProcessor(DockerizorTask task) {
        postDockerizeHook(task)
    }

    String getShortName() {
        switch (virgoFlavour) {
            case 'VJS':
                return "jetty-server"
            case 'VTS':
                return "tomcat-server"
            case 'VRS':
                return "nano"
            default:
                throw new IllegalArgumentException("Virgo flavour ${virgoFlavour} *not* supported")
        }
    }

    String getArchiveName() {
        switch (virgoFlavour) {
            case 'VJS':
                switch (virgoVersion) {
                    case 'latest':
                    return "virgo-jetty-server-latest"
                    default:
                    return "virgo-jetty-server-${virgoVersion}.zip"
                }
            case 'VTS':
                switch (virgoVersion) {
                    case 'latest':
                    return "virgo-tomcat-server-latest"
                    default:
                    return "virgo-tomcat-server-${virgoVersion}.zip"
                }
            case 'VRS':
                switch (virgoVersion) {
                    case 'latest':
                    return "virgo-nano-rap-latest"
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
                    logger.info "Using latest successful build for ${virgoFlavour}/3.7.0-SNAPSHOT."
                    return "https://hudson.eclipse.org/virgo/job/${hudsonJobName}/lastSuccessfulBuild/artifact/packaging/${shortName}/build/distributions/${archiveName}.zip"
                    default:
                    return "http://www.eclipse.org/downloads/download.php?file=/virgo/release/VP/${virgoVersion}/${archiveName}&mirror_id=580&r=1"
                }
            case 'VRS':
                switch (virgoVersion) {
                    case 'latest':
                    logger.info "Using latest successful build for ${virgoFlavour}/3.7.0-SNAPSHOT."
                    return "https://hudson.eclipse.org/virgo/job/${hudsonJobName}/lastSuccessfulBuild/artifact/packaging/${shortName}/build/distributions/${archiveName}.zip"
                    default:
                    throw new IllegalArgumentException("Virgo flavour ${virgoFlavour}/${virgoVersion} *not* supported")
                }
            default:
                throw new IllegalArgumentException("Virgo flavour ${virgoFlavour} *not* supported")
        }
    }
}
