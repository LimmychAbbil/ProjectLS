package net.lim.util;

public class VersionUtil {

    private static final int minSupportedMajor;
    private static final int minSupportedMinor;
    private static final String minSupportedSubVersion;

    static {
        minSupportedMajor = getMajorVersionFromVersionString(ConfigReader.getMinVersionSupported());
        minSupportedMinor = getMinorVersionFromVersionString(ConfigReader.getMinVersionSupported());
        minSupportedSubVersion = getSubVersionFromVersionString(ConfigReader.getMinVersionSupported());
    }


    public static boolean checkVersionSupported(String clientVersion) {
        int majorVersion = getMajorVersionFromVersionString(clientVersion);
        int minorVersion = getMinorVersionFromVersionString(clientVersion);
        String subVersion = getSubVersionFromVersionString(clientVersion);
        if (majorVersion < minSupportedMajor) return false;
        else if (majorVersion == minSupportedMajor && minorVersion < minSupportedMinor) return false;
        else if (majorVersion == minSupportedMajor && minorVersion == minSupportedMinor) {
            return isSubVersionGreaterThenMin(subVersion);
        } else return true;
    }


    private static int getMajorVersionFromVersionString(String version) {
        return Integer.parseInt(
                version.substring(0, version.indexOf('.')));
    }

    private static int getMinorVersionFromVersionString(String version) {
        int firstLetterOccurrence = findFirstLetterOccurrence(version);
        if (firstLetterOccurrence == -1) {
            return Integer.parseInt(
                    version.substring(version.indexOf(".") + 1));
        } else {
            return Integer.parseInt(
                    version.substring(version.indexOf(".") + 1, firstLetterOccurrence));
        }

    }

    private static String getSubVersionFromVersionString(String version) {
        int firstLetterOccurrence = findFirstLetterOccurrence(version);
        if (firstLetterOccurrence == -1) {
            return "";
        } else {
            return version.substring(findFirstLetterOccurrence(version));
        }
    }

    private static int findFirstLetterOccurrence(String version) {
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
