package de.rayzs.vit.api.utils;

public class TimeConverter {

    private static final long
            SECOND = 1000,
            MINUTE = SECOND * 60,
            HOUR = MINUTE * 60,
            DAY = HOUR * 24,
            WEEK = DAY * 7,
            MONTH = DAY * 30;

    private static final String SEPERATOR_STR = ",";

    /**
     * Converts milliseconds into a hardcoded
     * String format. (w,d,h,m)
     */
    public static String timeToStrConverter(final long time) {

        long months = 0,
                weeks = 0,
                days = 0,
                hours = 0,
                minutes = 0,
                seconds = 0,
                ms = (System.currentTimeMillis() - time);


        while (ms >= SECOND) {
            if (ms >= MONTH) {
                months++;
                ms -= MONTH;
            } else if (ms >= WEEK) {
                weeks++;
                ms -= WEEK;
            } else if (ms >= DAY) {
                days++;
                ms -= DAY;
            } else if (ms >= HOUR) {
                hours++;
                ms -= HOUR;
            } else if (ms >= MINUTE) {
                minutes++;
                ms -= MINUTE;
            } else {
                seconds++;

                if (seconds >= 60) {
                    seconds = 0;
                    minutes++;
                }

                ms = ms - SECOND;
            }
        }


        final StringBuilder text = new StringBuilder();

        if (months > 0) {
            text.append(weeks).append("m");
        }

        if (weeks > 0) {
            if (months > 0) text.append(SEPERATOR_STR);
            text.append(weeks).append("w");
        }
        
        if (days > 0) {
            if (weeks > 0) text.append(SEPERATOR_STR);
            text.append(days).append("d");
        }

        if (hours > 0) {
            if (days > 0) text.append(SEPERATOR_STR);
            text.append(hours).append("h");
        }

        if (minutes > 0) {
            if (hours > 0) text.append(SEPERATOR_STR);
            text.append(minutes).append("m");
        }

        if (seconds > 0) {
            if (minutes > 0) text.append(SEPERATOR_STR);
            text.append(seconds).append("s");
        }

        return text.toString();
    }
}