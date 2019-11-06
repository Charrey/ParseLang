package parselang.intermediate.fruity;

import parselang.intermediate.fruity.parameters.FruityParameter;
import parselang.intermediate.fruity.sentences.FruitySentence;

import java.util.LinkedList;
import java.util.List;

public class FruityFunction {

    private String name = "defaultFruityFunctionName";
    private String comment = "no-comment";
    private List<FruityParameter> parameters = new LinkedList<>();
    private List<FruitySentence> sentences = new LinkedList<>();


    public String toString() {
        StringBuilder sb = new StringBuilder("method " + name + makeParameters(parameters) + "\t//" + comment + "\n");
        for (FruitySentence sentence : sentences) {
            sb.append("\t").append(sentence).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String makeParameters(List<FruityParameter> args) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < args.size() - 1; i++) {
            sb.append(args.get(i)).append(", ");
        }
        if (!args.isEmpty()) {
            sb.append("o_").append(args.get(args.size() - 1)).append(")");
        } else {
            sb.append(")");
        }
        return sb.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void addParam(FruityParameter param) {
        parameters.add(param);
    }

    public void addUsefulParam(FruityParameter param) {
        parameters.add(param);
    }

    public void addSentences(List<FruitySentence> content) {
        sentences.addAll(content);
    }

    public FruityFunction addSentence(FruitySentence content) {
        sentences.add(content);
        return this;
    }

    public List<FruitySentence> getSentences() {
        return sentences;
    }

    public FruityParameter getParam(int i) {
        return parameters.get(i);
    }
}
