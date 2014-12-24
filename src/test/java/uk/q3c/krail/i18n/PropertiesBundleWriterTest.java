package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.testutil.TestUserOptionModule;
import uk.q3c.util.ResourceUtils;
import uk.q3c.util.testutil.TestResource;
import util.FileTestUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestUserOptionModule.class})
public class PropertiesBundleWriterTest {

    @Inject
    PropertiesBundleWriter writer;

    @Inject
    PatternUtility utility;


    @Test
    public void write() throws IOException {
        //given
        File testOutDir = new File(ResourceUtils.userTempDirectory(), "testOut");
        if (testOutDir.exists()) {
            FileUtils.deleteQuietly(testOutDir);
        }
        File targetDir = new File(ResourceUtils.userTempDirectory(), "testOut/codeModel");
        writer.setOptionWritePath(targetDir);
        Set<Locale> locales = new LinkedHashSet<>();
        locales.add((Locale.ITALIAN));
        locales.add((Locale.GERMAN));
        File referenceFile_de = new File(TestResource.testResourceRootDir("krail"), "TestLabels_de.properties_ref");
        File targetFile_de = new File(targetDir, "TestLabels_de.properties");
        File referenceFile_it = new File(TestResource.testResourceRootDir("krail"), "TestLabels_it.properties_ref");
        File targetFile_it = new File(targetDir, "TestLabels_it.properties");
        //when
        utility.writeOut(writer, TestLabelKey.class, locales, Optional.empty());
        //then
        assertThat(FileTestUtil.compare(referenceFile_de, targetFile_de, 1)).isEqualTo(Optional.empty());
        assertThat(FileTestUtil.compare(referenceFile_it, targetFile_it, 1)).isEqualTo(Optional.empty());
    }
}