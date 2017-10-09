package ru.track.io.vendor;

import junit.framework.TestCase;
import junitx.framework.FileAssert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
    public static Collection<Sample> data() {
        return Arrays.asList(
                new Sample("xxx"),
                new Sample("xx"),
                new Sample("x"),
                new Sample("xxx000"),
                new Sample("xxx00"),
                new Sample("xxx0"),
                /* edge cases for 4 KiB buffer */
                new Sample(StringUtils.repeat("x", 4097)),
                new Sample(StringUtils.repeat("x", 4098)),
                new Sample(StringUtils.repeat("x", 4099)),
                /* edge cases for 8 KiB buffer */
                new Sample(StringUtils.repeat("x", 8193)),
                new Sample(StringUtils.repeat("x", 8194)),
                new Sample(StringUtils.repeat("x", 8195)),
                /* jpeg header -- mixed endianness */
                new Sample(new byte[]{(byte) 0xd8, (byte) 0xff, (byte) 0xe0, (byte) 0xff}),
                new Sample(new byte[]{(byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe0})
        );
    }

    @NotNull
    private final Sample data;

    public FileEncoderTest(@NotNull Sample data) {
        this.data = data;
    }

    @Test
    public void testEncoderImplementation() throws Exception {
        final Path p = Files.write(
                createTempFile().toPath(),
                data.get(),
                StandardOpenOption.WRITE
        );

        final File expected = (new ReferenceTaskImplementation()).encodeFile(p.toString(), null);
        final File actual = (new TaskImplementation()).encodeFile(p.toString(), null);

        if (!data.isPrintable()) {
            assertBinaryEquals(expected, actual);
        } else {
            assertEquals(
                    FileUtils.readFileToString(expected, StandardCharsets.US_ASCII),
                    FileUtils.readFileToString(actual, StandardCharsets.US_ASCII)
            );
        }
    }

    @NotNull
    private static File createTempFile() throws IOException {
        final File f = File.createTempFile("test_tempfile_", ".tmp");
        f.deleteOnExit();
        return f;
    }


    private static class Sample {

        private final byte[] raw;
        private final String printable;

        Sample(@NotNull byte[] raw) {
            this.raw = raw;
            this.printable = null;
        }

        Sample(@NotNull String printable) {
            this.printable = printable;
            this.raw = null;
        }

        byte[] get() {
            return printable != null ? printable.getBytes() : raw;
        }

        boolean isPrintable() {
            return printable != null;
        }

    }

}