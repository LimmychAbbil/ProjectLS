package net.lim.util;

public class VersionUtil {

    private static final int minSupportedMajor;
    private static final int minSupportedMinor;
    private static final String minSupportedSubVersion;

    static {
        minSupportedMajor = getMajorVersionFromVersionString(ConfigReader.minVersionSupported);
        minSupportedMinor = getMinorVersionFromVersionString(ConfigReader.minVersionSupported);
        minSupportedSubVersion = getSubVersionFromVersionString(ConfigReader.minVersionSupported);
    }


    public static boolean checkVersionSupported(String clientVersion) {
        int majorVersion = getMajorVersionFromVersionString(clientVersion);
        int minorVersion = getMinorVersionFromVersionString(clientVersion);
        String subVersion = getSubVersionFromVersionString(clientVersion);
        if (majorVersion < minSupportedMajor) return false;
        else if (minorVersion < minSupportedMinor) return false;
        else return isSubVersionGreaterThenMin(subVersion);
    }


    private static int getMajorVersionFromVersionString(String version) {
        return Integer.parseInt(
                version.substring(0, version.indexOf('.')));
    }

    private static int getMinorVersionFromVersionString(String version) {
        return Integer.parseInt(
                version.substring(version.indexOf(".") + 1, findFirstLetterOccurence(version)));

    }

    private static String getSubVersionFromVersionString(String version) {
        return version.substring(findFirstLetterOccurence(version));
    }

    private static int findFirstLetterOccurence(String version) {
        for (int i = 0; i < version.length(); i++) {
            if (Character.isLetter(version.charAt(i))) {
                return i;
            }
        }
        return -1;

    }

    private static boolean isSubVersionGreaterThenMin(String subVersion) {
        if (subVersion.length() < minSupportedSubVersion.length()) {
            return false;
        } else return (subVersion.compareTo(minSupportedSubVersion) >= 0);
    }
}
