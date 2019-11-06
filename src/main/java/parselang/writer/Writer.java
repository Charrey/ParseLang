package parselang.writer;

import parselang.intermediate.dataholders.Program;
import parselang.parser.ParseRuleStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class Writer {


    public void writeToFile(Program program, File target, ParseRuleStorage storage) throws FileNotFoundException {
        String toWrite = writeToString(program, storage);
        try (PrintWriter out = new PrintWriter(target.toPath().resolve(program.getName() + ".fruity").toFile())) {
            out.println(toWrite);
        }
    }

    public abstract String writeToString(Program program, ParseRuleStorage storage);
}
