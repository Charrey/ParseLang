package parselang.languages;

import parselang.parser.data.ParseRule;

import java.util.List;

/**
 * List of parse rules that form the baseline of a language
 */
public interface Language {

    List<ParseRule> getRules();

}
