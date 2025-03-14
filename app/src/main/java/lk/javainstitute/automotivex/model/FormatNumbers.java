package lk.javainstitute.automotivex.model;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatNumbers {



        public static String formatQuantity(double qty) {
            if (qty % 1 == 0) {
                // If there is no decimal part, display as an integer
                return new DecimalFormat("#").format(qty);
            } else {
                // Otherwise, format the number with 3 decimal places
                return new DecimalFormat("#.000").format(qty);
            }
        }

        public static String formatPrice(double price) {
            // Create a DecimalFormat for formatting the price
            DecimalFormat priceFormat = new DecimalFormat("#,##0.00");

            // Return the formatted price as a string
            return priceFormat.format(price);
        }

    public static String formatRatings(double price) {
        DecimalFormat priceFormat = new DecimalFormat("###0.0");
        return priceFormat.format(price);
    }

        public static String formatPriceWithoutComma(double price) {
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            return decimalFormat.format(price);
        }

        public static String formatPriceWithCurrencyCode(double price) {
            // Create a DecimalFormat for formatting the price
            DecimalFormat priceFormat = new DecimalFormat("#,##0.00");

            // Return the formatted price as a string
            return "LKR " + priceFormat.format(price);
        }

        public static String formatPresentage(double value) {
            // Round to 2 decimal places
            double roundedValue = Math.round(value * 100.0) / 100.0;

            // Convert to string with two decimal points
            String formattedValue = String.format("%.2f", roundedValue);

            // Remove unnecessary trailing zeros
            if (formattedValue.endsWith(".00")) {
                return formattedValue.substring(0, formattedValue.length() - 3); // Remove .00
            } else if (formattedValue.endsWith("0")) {
                return formattedValue.substring(0, formattedValue.length() - 1); // Remove last 0
            }

            return formattedValue;
        }

        public static Date getDateObject(String dateString) throws ParseException {
            // Define the date format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Parse the string to a Date object
            return formatter.parse(dateString);
        }

}
