package parselang.parser.exceptions;

public class ParseErrorException extends Exception {

    private final int index;

    public ParseErrorException(String originalString, int index) {
        super("No alternative at index " + index + " at " + whichCharacter(originalString, index));
        this.index = index;
    }

    private static String whichCharacter(String originalString, int index) {
        if (originalString.isEmpty()) {
            return "start of input";
        } else if (index == originalString.length()) {
            return "end of input";
        } else {
            if (index == originalString.length() + 1) {
                return "EOF";
            }
            return index == -1 ? "-1" : String.valueOf(originalString.charAt(index));
        }
    }

    public int getIndex() {
        return index;
    }
}
