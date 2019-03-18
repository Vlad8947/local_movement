package com.local_movement.core;

import java.text.DecimalFormat;

public class Converter {

    private static final String NUMBER_FORMAT_PATTERN ="#0.00";

    public static String length(long length) {
        final int thousandBytes = 1024;
        final int lengthMultiplierOfThousand = AppProperties.MultiplierOfByte.values().length;
        float result = (float) length;

        for (int multiplierOfThousand = 0; multiplierOfThousand < lengthMultiplierOfThousand; multiplierOfThousand++) {
            if (result < thousandBytes) {
                String template = new DecimalFormat(NUMBER_FORMAT_PATTERN).format(result) + " ";
                return template + AppProperties.MultiplierOfByte.values()[multiplierOfThousand].toString();
            }
            result /= thousandBytes;
        }
        return null;
    }

    public static String speedInSecond(long sizeInSecond) {
        return length(sizeInSecond) + "/s";
    }

}
