package parselang.languages;

import parselang.parser.data.ParseRule;

import java.util.List;

public interface Language {

    List<ParseRule> getRules();

}
