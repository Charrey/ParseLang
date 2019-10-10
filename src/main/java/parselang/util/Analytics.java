package parselang.util;

import parselang.parser.ParseRuleStorage;
import parselang.parser.data.AST;
import parselang.parser.data.ASTElem;
import parselang.parser.data.ASTElemList;
import parselang.parser.data.Node;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Analytics {

    //Not made to be fast. Only for testing.
    public List<AST> searchByRoot(AST tree, Node root) {
        if (tree.getRoot().equals(root)) {
            return Collections.singletonList(tree);
        } else {
            return searchByRoot(tree.getChildren().stream(), root);
        }
    }

    //Not made to be fast. Only for testing.
    private List<AST> searchByRoot(Stream<ASTElem> tree, Node root) {
      Stream<List<AST>> a = tree.map(astElem -> {
          if (astElem instanceof AST) {
              return searchByRoot((AST) astElem, root);
          } else if (astElem instanceof ASTElemList) {
              return searchByRoot(((ASTElemList) astElem).stream(), root);
          } else {
              throw new UnsupportedOperationException();
          }
      });
      return a.map(Collection::stream).reduce(Stream.empty(), Stream::concat).collect(Collectors.toList());
    }
}
