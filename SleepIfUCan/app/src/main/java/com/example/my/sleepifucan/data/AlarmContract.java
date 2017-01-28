package com.example.my.sleepifucan.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MY on 2017-01-18.
 */

public class AlarmContract {
    public static final String CONTENT_AUTHORITY = "com.example.my.sleepifucan";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ALARM = "alarm";

    public static final class AlarmEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ALARM).build();

        public static final String TABLE_NAME = "alarm";

        public static final String COLUMN_CLOCK = "clock";
        public static final String COLUMN_MINUTE = "minute";
        public static final String COLUMN_DAY = "day";

        public static final String COLUMN_REPEAT = "repeat";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_URI = "path";
        public static final String COLUMN_VOLUME = "volume";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_SWITCH = "switch";

        public static Uri buildAlarmUriWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }
    }
}
