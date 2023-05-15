package net.lim.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class VersionUtilTest {
    private final static String TEST_MIN_SUPPORTED_VERSION = "2.05c";

    @BeforeAll
    public static void initMinVersion() {
        try (MockedStatic<ConfigReader> configReaderMockedStatic = Mockito.mockStatic(ConfigReader.class)) {
            configReaderMockedStatic.when(ConfigReader::getMinVersionSupported)
                    .thenReturn(TEST_MIN_SUPPORTED_VERSION);

            //execute to load static block
            VersionUtil.checkVersionSupported("2.05c");
        }
    }

    @Test
    public void testMinVersionIsLessThenCurrentButOnlyForLetter() {
        Assertions.assertTrue(VersionUtil.checkVersionSupported("2.05d"));

    }

    @Test
    public void testMinVersionIsLessThenCurrentMinor() {
        Assertions.assertTrue(VersionUtil.checkVersionSupported("2.06a"));
    }

    @Test
    public void testMinVersionIsLessThenCurrentMajor() {
        Assertions.assertTrue(VersionUtil.checkVersionSupported("3.01b"));
    }

    @Test
    public void testVersionsAreEquals() {
        Assertions.assertTrue(VersionUtil.checkVersionSupported(TEST_MIN_SUPPORTED_VERSION));
    }

    @Test
    public void testMinVersionIsGreaterThenCurrentButOnlyForLetter() {
        Assertions.assertFalse(VersionUtil.checkVersionSupported("2.05b"));
    }

    @Test
    public void testMinVersionIsGreaterThenCurrentMinor() {
        Assertions.assertFalse(VersionUtil.checkVersionSupported("2.04e"));
    }

    @Test
    public void testMinVersionIsGreaterThenCurrentMajor() {
        Assertions.assertFalse(VersionUtil.checkVersionSupported("1.99z"));
    }

    @Test
    public void testMinVersionLessNoLetterInCurrent() {
        Assertions.assertFalse(VersionUtil.checkVersionSupported("1.99"));
    }

    @Test
    public void testMinVersionGreaterNoLetterInCurrent() {
        Assertions.assertTrue(VersionUtil.checkVersionSupported("3.99"));
    }

    @Test
    public void testMinVersionGreaterMoreLetters() {
        Assertions.assertTrue(VersionUtil.checkVersionSupported("2.05ca"));
    }

    @Test
    public void testMinVersionLessMoreLetters() {
        Assertions.assertFalse(VersionUtil.checkVersionSupported("2.05bz"));
    }
}