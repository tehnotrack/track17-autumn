package ru.track.io.vendor;

import junit.framework.TestCase;
import junitx.framework.FileAssert;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import ru.track.io.TaskImplementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;

import static junitx.framework.FileAssert.assertBinaryEquals;

@RunWith(Parameterized.class)
public class FileEncoderTest extends TestCase {

    @Parameters
    public static Collection<String> data() {
        return Arrays.asList(
                "xxx",
                "xx",
                "x",
                "xxx000",
                "xxx00",
                "xxx0"
        );
    }

    @NotNull
    private final String data;

    public FileEncoderTest(@NotNull String data) {
        this.data = data;
    }

//    @Test
//    public void testEncoderImplementation() throws Exception {
//        final Path p = Files.write(
//                createTempFile().toPath(),
//                data.getBytes(StandardCharsets.US_ASCII),
//                StandardOpenOption.WRITE
//        );
//
//        final File expected = (new ReferenceTaskImplementation()).encodeFile(p.toString(), null);
//        final File actual = (new TaskImplementation()).encodeFile(p.toString(), null);
//
//        assertEquals(
//                FileUtils.readFileToString(expected, StandardCharsets.US_ASCII),
//                FileUtils.readFileToString(actual, StandardCharsets.US_ASCII)
//        );
//    }

    private static @NotNull
    File createTempFile() throws IOException {
        final File f = File.createTempFile("test_tempfile_", ".tmp");
        f.deleteOnExit();
        return f;
    }

}