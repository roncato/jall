/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import jall.exceptions.InvalidPreprocessorTokenException;
import jall.lang.SourceFile.SourceOption;
import jall.lang.Token.TokenType;
import jall.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PreProcessor {

    private final SourceFile mainFile;
    private final Lexicon lexicon;
    private Set<SourceFile> importedFiles;
    private List<Token> tokenStream;
    private int currentTokenIndex = 0;
    private Token currentToken;
    private SourceFile referenceFile;
    public PreProcessor(SourceFile mainFile, Lexicon lexicon) {
        importedFiles = new HashSet<SourceFile>();
        importedFiles.add(mainFile);
        this.mainFile = mainFile;
        this.lexicon = lexicon;
        this.tokenStream = new LinkedList<Token>();
        referenceFile = mainFile;
    }

    public void process() throws IOException {
        currentTokenIndex = 0;
        currentToken = null;
        tokenStream = getTokens(mainFile);
        while (currentTokenIndex < tokenStream.size()) {
            currentToken = tokenStream.get(currentTokenIndex);
            if (currentToken.getType() == TokenType.Preprocessor)
                processPreprocessorCommand(currentToken);
            currentTokenIndex++;
        }
    }

    private void processPreprocessorCommand(Token currentToken) throws IOException {
        String prefix = lexicon.getPreprocessorPrefix();
        String text = currentToken.getText().substring(prefix.length(), currentToken.getText().length() - prefix.length() + 1);
        switch (getPreprocessorTokenType(text)) {
            case Import:
                processImport();
                break;
            case Include:
                processInclude();
        }
    }

    private void processImport() throws IOException {
        // Get File Name, decrements current Token position and remove tokens
        String fileName = lexicon.getStringFromStringLiteral(tokenStream.get(currentTokenIndex + 1).getText());
        Utility.removeFromList(tokenStream, currentTokenIndex, currentTokenIndex + 1);

        // File name
        fileName = getAbsoluteFileName(fileName);

        // Creates source file
        SourceFile sourceFile = new SourceFile(fileName, SourceOption.File);
        if (!importedFiles.contains(sourceFile)) {
            importedFiles.add(sourceFile);
            List<Token> tokens = getTokens(sourceFile);
            tokens.remove(tokens.size() - 1); // Remove last EOT token
            tokenStream.addAll(currentTokenIndex, tokens);
            currentTokenIndex--;
            referenceFile = sourceFile;
        }
    }

    private void processInclude() throws IOException {
        // Get File Name, decrements current Token position and remove tokens
        String fileName = lexicon.getStringFromStringLiteral(tokenStream.get(currentTokenIndex + 1).getText());
        Utility.removeFromList(tokenStream, currentTokenIndex, currentTokenIndex + 1);

        // File name
        fileName = getAbsoluteFileName(fileName);

        // Creates source file
        SourceFile sourceFile = new SourceFile(fileName, SourceOption.File);
        List<Token> tokens = getTokens(sourceFile);
        tokens.remove(tokens.size() - 1); // Remove last EOT token
        tokenStream.addAll(currentTokenIndex, tokens);
        currentTokenIndex--;
        referenceFile = sourceFile;
    }

    private String getAbsoluteFileName(String fileName) {

        String rootPath = referenceFile.getFile().getParentFile().getAbsolutePath() + "\\";

        // Checks if relative path
        File file = new File(fileName);
        if (!file.exists()) {
            fileName = rootPath + fileName;
            file = new File(fileName);
            if (!file.exists())
                throw new RuntimeException("File not found: " + fileName + ".");
            else if (!file.isAbsolute())
                fileName = rootPath + fileName;
        } else if (!file.isAbsolute())
            fileName = rootPath + fileName;
        return fileName;
    }

    private PreprocessorTokenType getPreprocessorTokenType(String text) {
        PreprocessorTokenType preProcessorToken;
        if (text.equals("import")) {
            preProcessorToken = PreprocessorTokenType.Import;
        } else if (text.equals("include")) {
            preProcessorToken = PreprocessorTokenType.Include;
        } else
            throw new InvalidPreprocessorTokenException("Invalid preprocessor token '" + text + "'.");

        return preProcessorToken;
    }

    private List<Token> getTokens(SourceFile sourceFile) {
        Scanner scanner = null;
        scanner = new Scanner(sourceFile, lexicon);
        scanner.scan();
        return scanner.getTokens();
    }

    public List<Token> getTokenStream() {
        return tokenStream;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public enum PreprocessorTokenType {
        Import,
        Include
    }

}
