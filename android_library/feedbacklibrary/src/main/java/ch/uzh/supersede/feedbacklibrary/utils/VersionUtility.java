package ch.uzh.supersede.feedbacklibrary.utils;

import ch.uzh.supersede.feedbacklibrary.BuildConfig;

public final class VersionUtility {
    private static final int BASE_VERSION = 1;
    private static final int MAX_VERSION = 6;

    private VersionUtility() {
    }

    public static int getDateVersion() {
        //        1	21-23	21.05 - 08.06	45
        //        2	24-26	09.06 - 29.06	22
        //        3	27-29	30.06 - 20.07	45
        //        4	30-32	21.07 - 10.08	22
        //        5	33-35	11.08 - 31.08	12
        int modifier = 0;
        if (BuildConfig.DEBUG) {
            modifier = 1;
        }

        if (DateUtility.getYear() <= 2018) {
            if ((DateUtility.getMonth() == 8 && DateUtility.getDay() >= 31) || DateUtility.getMonth() >= 9) {
                return BASE_VERSION + 4 + modifier;
            } else if ((DateUtility.getMonth() == 8 && DateUtility.getDay() >= 10) || DateUtility.getMonth() >= 9) {
                return BASE_VERSION + 3 + modifier;
            } else if ((DateUtility.getMonth() == 7 && DateUtility.getDay() >= 20) || DateUtility.getMonth() >= 8) {
                return BASE_VERSION + 2 + modifier;
            } else if ((DateUtility.getMonth() == 6 && DateUtility.getDay() >= 29) || DateUtility.getMonth() >= 7) {
                return BASE_VERSION + 1 + modifier;
            }
            return BASE_VERSION + modifier;
        } else {
            return MAX_VERSION;
        }
    }
}
