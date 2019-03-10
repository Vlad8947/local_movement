package com.local_movement.core;

public class SizeConverter {

    public static String size(long size) {
        final int thousandBytes = 1024;
        final int lengthMultiplierOfThousand = AppProperties.MultiplierOfByte.values().length;

        for (int multiplierOfThousand = 0;
             multiplierOfThousand < lengthMultiplierOfThousand;
             multiplierOfThousand++) {
            if (size < thousandBytes) {
                String template = size + " ";
                if (multiplierOfThousand == 1) {
                    return template + AppProperties.MultiplierOfByte.B.toString();
                } else {
                    return template + AppProperties.MultiplierOfByte.values()[multiplierOfThousand].toString();
                }
            }
            size /= thousandBytes;
        }
        return null;
    }

    public static String speedInSecond(long sizeInSecond) {
        return size(sizeInSecond) + "/s";
    }

}
