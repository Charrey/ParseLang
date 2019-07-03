package parselang.parser.exceptions;

public class ParseErrorException extends Exception {

    public ParseErrorException(String originalString, int index) {
        super("No alternative at index " + index + " at character " + originalString.charAt(index));
    }
}
