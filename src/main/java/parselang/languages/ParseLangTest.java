package parselang.languages;

import parselang.parser.data.ParseRule;

import java.util.LinkedList;
import java.util.List;

import static parselang.parser.ParseRuleStorage.*;

public class ParseLangTest implements Language {

    @Override
    public List<ParseRule> getRules() {
        List<ParseRule> rules = new LinkedList<>();
        rules.add(new ParseRule("Level1").addRhs(nonTerm("Level2")));
        rules.add(new ParseRule("Level2").addRhs(nonTerm("Level3"), term("a")));
        rules.add(new ParseRule("Level3").addRhs(term("b"), nonTerm("Level4")));
        rules.add(new ParseRule("Level4").addRhs(star(term("c"), term("d"), nonTerm("Level5"))));
        rules.add(new ParseRule("Level5").addRhs(star(term("e"))));
        return rules;
    }
}
