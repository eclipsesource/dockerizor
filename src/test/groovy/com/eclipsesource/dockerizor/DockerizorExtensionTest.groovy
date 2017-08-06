package com.eclipsesource.dockerizor
import static org.junit.Assert.assertThat

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.endsWith
import static org.junit.Assert.assertThat

import org.junit.Ignore
import org.junit.Test

class DockerizorExtensionTest {

    @Test
    void shouldSupportLatestVersionOfVirgoRapServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'latest',
                virgoFlavour:'VRS'
                )

        assertThat(uut.archiveName, containsString("virgo-nano-rap"))
        assertThat(uut.archiveName, containsString("latest"))
    }

    @Test
    void shouldSupportMilestoneDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.M03',
                virgoFlavour:'VTS'
                )

        assertThat(uut.downloadUrl, containsString("VTS"))
        assertThat(uut.downloadUrl, containsString("milestone"))
    }

    @Test
    void shouldSupportMilestoneDownloadOfVirgoJettyServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.M03',
                virgoFlavour:'VJS'
                )

        assertThat(uut.downloadUrl, containsString("VJS"))
        assertThat(uut.downloadUrl, containsString("milestone"))
    }

    @Test
    void shouldSupport36ReleaseDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.6.4.RELEASE',
                virgoFlavour:'VTS'
                )

        assertThat(uut.downloadUrl, containsString("VP"))
        assertThat(uut.downloadUrl, containsString("release"))
    }

    @Test
    void shouldSupport36ReleaseDownloadOfVirgoJettyServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.6.4.RELEASE',
                virgoFlavour:'VJS'
                )

        assertThat(uut.downloadUrl, containsString("VP"))
        assertThat(uut.downloadUrl, containsString("release"))
    }

    @Test
    @Ignore
    void shouldSupport37ReleaseDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.RELEASE',
                virgoFlavour:'VTS'
                )

        assertThat(uut.downloadUrl, containsString("VTS"))
        assertThat(uut.downloadUrl, containsString("release"))
    }

    @Test
    @Ignore
    void shouldSupport37ReleaseDownloadOfVirgoJettyServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.RELEASE',
                virgoFlavour:'VJS'
                )

        assertThat(uut.downloadUrl, containsString("VJS"))
        assertThat(uut.downloadUrl, containsString("release"))
    }

    /*
     *   https://hudson.eclipse.org/virgo/job/gradle-build/lastSuccessfulBuild/artifact/packaging/tomcat-server/build/distributions/virgo-tomcat-server-latest.zip
     */
    @Test
    void shouldSupportLatestDevelopmentVersionDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'latest',
                virgoFlavour:'VTS',
                hudsonJobName:'gradle-build'
                )

        println "Resulting download URL ==> ${uut.downloadUrl}"

        assertThat(uut.downloadUrl, containsString("/gradle-build/lastSuccessfulBuild/"))
        assertThat(uut.downloadUrl, endsWith("/virgo-tomcat-server-latest.zip"))
    }

    /*
     *  https://hudson.eclipse.org/virgo/job/gradle-build/782/artifact/packaging/tomcat-server/build/distributions/virgo-tomcat-server-3.7.0.D-20161218094003.zip
     */
    @Test
    void shouldSupportSpecificDevelopmentVersionDownloadWithSpecificBuildNumberOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.D-20161218094003',
                virgoFlavour:'VTS',
                hudsonJobName:'gradle-build',
                hudsonJobNumber:'782'
                )

        println "Resulting download URL ==> ${uut.downloadUrl}"

        assertThat(uut.downloadUrl, containsString("/gradle-build/782/"))
        assertThat(uut.downloadUrl, endsWith("/virgo-tomcat-server-3.7.0.D-20161218094003.zip"))
    }

    /*
     *  https://hudson.eclipse.org/virgo/job/gradle-build/782/artifact/packaging/tomcat-server/build/distributions/virgo-tomcat-server-latest.zip
     */
    @Test
    void shouldSupportLatestDevelopmentVersionDownloadWithSpecificBuildNumberOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'latest',
                virgoFlavour:'VTS',
                hudsonJobName:'gradle-build',
                hudsonJobNumber:'782'
                )

        println "Resulting download URL ==> ${uut.downloadUrl}"

        assertThat(uut.downloadUrl, containsString("/gradle-build/782/"))
        assertThat(uut.downloadUrl, endsWith("/virgo-tomcat-server-latest.zip"))
    }

    /*
     *
     */
    @Test
    void shouldSupportSpecificDevelopmentVersionDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.D-20161218094003',
                virgoFlavour:'VTS',
                hudsonJobName:'gradle-build'
                )

        println "Resulting download URL ==> ${uut.downloadUrl}"

        assertThat(uut.downloadUrl, containsString("/gradle-build/lastSuccessfulBuild/"))
        assertThat(uut.downloadUrl, endsWith("/virgo-tomcat-server-3.7.0.D-20161218094003.zip"))
    }
}

