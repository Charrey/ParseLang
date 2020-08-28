package parselang.parser;

import parselang.parser.data.*;

public class TreeFixer {
    public ASTElem fix(ASTElem tree) {
        if (tree instanceof AST) {
            AST treeAST = (AST) tree;
            if (treeAST.getRoot() instanceof Terminal) {
                return treeAST;
            }
            if (((NonTerminal) treeAST.getRoot()).getSpecialStatus() == NonTerminal.SpecialStatus.NONE) {
                for (int i = 0; i < ((AST) tree).getChildren().size(); i++) {
                    treeAST.setChild(i, fix(treeAST.getChild(i)));
                }
            } else {
                if (treeAST.getChildren().size() == 0) {
                    return new ASTElemList();
                } else {
                    ASTElemList childsList = ((ASTElemList)fix(treeAST.getLastChild()));
                    if (treeAST.getChildren().size() > 2) {
                        ASTElemList listToAdd = new ASTElemList();
                        for (ASTElem child : treeAST.getChildren().subList(0, treeAST.getChildren().size()-1)) {
                            listToAdd.add(fix(child));
                        }
                        childsList.prepend(listToAdd);
                    } else { //equal to 2
                        for (ASTElem child : treeAST.getChildren().subList(0, treeAST.getChildren().size()-1)) {
                            childsList.prepend(fix(child));
                        }
                    }
                    return childsList;
                }
            }
            return treeAST;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
