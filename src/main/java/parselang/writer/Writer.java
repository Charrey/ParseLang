package parselang.writer;

import parselang.intermediate.dataholders.Program;
import parselang.parser.ParseRuleStorage;

import java.io.File;
import java.io.IOException;

public abstract class Writer {


    public abstract void writeToFile(Program program, File target, ParseRuleStorage storage) throws IOException;

    public abstract String writeToString(Program program, ParseRuleStorage storage);
}
