package parselang.intermediate;

import parselang.intermediate.dataholders.*;
import parselang.parser.ParseRuleStorage;
import parselang.parser.data.*;
import parselang.util.Analytics;
import parselang.util.DeclarationTree;
import parselang.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

import static parselang.parser.ParseRuleStorage.nonTerm;

public class ASTWriter {

    public Program write(String programName, String originalString, AST tree, ParseRuleStorage storage) {
        List<AST> declarations = new Analytics().searchByRoot(tree, nonTerm("Declaration"));
        Program program = new Program(programName);
        for (AST declaration : declarations) {
            Pair<ParseRule, ParseRule> associatedRules = storage.getAddedRulesHistory(declaration);
            DeclarationTree declTree = new DeclarationTree(originalString, declaration);
            DeclUse sentences = (DeclUse) generateDeclUseFromASTOrASTElemList(originalString, declaration.getChild(11), storage);
            Declaration toAdd = new Declaration(declTree.name, storage.getIDbyRule(associatedRules.getValue().getOrigin()), storage.getIDbyRule(associatedRules.getKey().getOrigin()), declTree.superNonTerminal, declTree.retrievedNodes, sentences);
            program.addDeclaration(toAdd);
        }
        return program;
    }

    private Argument generateDeclUseFromASTOrASTElemList(String originalString, ASTElem elem, ParseRuleStorage storage) {
        if (elem instanceof AST) {
            if (((AST) elem).getRoot() instanceof Terminal) {
                return new ConstantString(((AST) elem).subString(originalString));
            } else {
                return generateDeclUse(originalString, (AST) elem, storage);
            }
        } else if (elem instanceof ASTElemList) {
            return generateDeclUse(originalString, (ASTElemList) elem, storage);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private ArgumentList generateDeclUse(String originalString, ASTElemList astElems, ParseRuleStorage storage) {
        return new ArgumentList(astElems.stream().map(astElem -> generateDeclUseFromASTOrASTElemList(originalString, astElem, storage)).collect(Collectors.toList()), ArgumentList.Nature.STAR);
    }

    private DeclUse generateDeclUse(String originalString, AST tree, ParseRuleStorage storage) {
        int ruleUsed = 0;
        try {
            ruleUsed = storage.getIDbyRule(tree.getRule().getOrigin());
        } catch (NullPointerException foo){
            System.out.println();
        }
        if (tree.getRoot() instanceof Terminal) {
            return new DeclUse(tree.subString(originalString));
        }
        ArgumentList arguments = new ArgumentList(tree.getChildren().stream().map(astElem -> generateDeclUseFromASTOrASTElemList(originalString, astElem, storage)).collect(Collectors.toList()), ArgumentList.Nature.RHS);
        return new DeclUse(ruleUsed, arguments);
    }
}
