package com.eclipsesource.dockerizor
import static org.junit.Assert.assertThat

import static org.hamcrest.CoreMatchers.containsString
import static org.junit.Assert.assertThat

import org.junit.Ignore
import org.junit.Test

class DockerizorExtensionTest {

    @Test
    public void shouldSupportLatestVersionOfVirgoRapServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'latest',
                virgoFlavour:'VRS'
                )

        assertThat(uut.archiveName, containsString("virgo-nano-rap"));
        assertThat(uut.archiveName, containsString("latest"));
    }

    @Test
    public void shouldSupportMilestoneDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.M03',
                virgoFlavour:'VTS'
                )

        assertThat(uut.downloadUrl, containsString("VTS"));
        assertThat(uut.downloadUrl, containsString("milestone"));
    }

    @Test
    public void shouldSupportMilestoneDownloadOfVirgoJettyServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.M03',
                virgoFlavour:'VJS'
                )

        assertThat(uut.downloadUrl, containsString("VJS"));
        assertThat(uut.downloadUrl, containsString("milestone"));
    }

    @Test
    public void shouldSupport36ReleaseDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.6.4.RELEASE',
                virgoFlavour:'VTS'
                )

        assertThat(uut.downloadUrl, containsString("VP"));
        assertThat(uut.downloadUrl, containsString("release"));
    }

    @Test
    public void shouldSupport36ReleaseDownloadOfVirgoJettyServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.6.4.RELEASE',
                virgoFlavour:'VJS'
                )

        assertThat(uut.downloadUrl, containsString("VP"));
        assertThat(uut.downloadUrl, containsString("release"));
    }

    @Test
    @Ignore
    public void shouldSupport37ReleaseDownloadOfVirgoTomcatServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.RELEASE',
                virgoFlavour:'VTS'
                )

        assertThat(uut.downloadUrl, containsString("VTS"));
        assertThat(uut.downloadUrl, containsString("release"));
    }

    @Test
    @Ignore
    public void shouldSupport37ReleaseDownloadOfVirgoJettyServer() {
        DockerizorExtension uut = new DockerizorExtension (
                virgoVersion:'3.7.0.RELEASE',
                virgoFlavour:'VJS'
                )

        assertThat(uut.downloadUrl, containsString("VJS"));
        assertThat(uut.downloadUrl, containsString("release"));
    }
}
