package parselang.parser.exceptions;

public class ParseErrorException extends Exception {

    public ParseErrorException(String originalString, int index) {
        super("No alternative at index " + index + " at " + whichCharacter(originalString, index));
    }

    private static String whichCharacter(String originalString, int index) {
        if (originalString.isEmpty()) {
            return "start of input";
        } else if (index == originalString.length()) {
            return "end of input";
        } else {
            return String.valueOf(originalString.charAt(index));
        }
    }
}
