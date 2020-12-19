/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import jall.exceptions.ParserErrorException;
import jall.inference.*;
import jall.lang.ErrorReporter.ErrorType;
import jall.lang.Token.TokenType;
import jall.lang.ast.Identifier;
import jall.lang.ast.Program;
import jall.lang.ast.command.*;
import jall.lang.ast.declaration.*;
import jall.lang.ast.expression.*;
import jall.lang.ast.literal.*;
import jall.lang.ast.operator.LogicalOperator;
import jall.lang.ast.operator.Operator;
import jall.lang.ast.sentence.*;
import jall.lang.type.TypeDenoter;
import jall.util.ListIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Parser {
    private Program program = null;
    private ListIterator<Token> tokens = null;
    private PreProcessor preProcessor = null;
    private Token currentToken = null;
    private ErrorReporter err = null;

    public Parser(PreProcessor preProcessor, ErrorReporter err) {
        this.preProcessor = preProcessor;
        this.err = err;
    }

    private void accept(TokenType expectedTokenType) {
        if (currentToken.getType().equals(expectedTokenType))
            acceptIt();
        else {
            throwError(expectedTokenType);
        }
    }

    private void acceptIt() {
        currentToken = tokens.next();
    }

    private void throwError(TokenType expectedTokenType) {
        String msg = getTokenErrorMsg(currentToken);
        msg += " Expected token type: " + expectedTokenType.toString() + ".";
        err.log(new ErrorReporter.Error(msg, ErrorType.Parsing));
        throw new ParserErrorException(msg);
    }

    private void throwError(String msg) {
        msg += " " + getTokenErrorMsg(currentToken);
        err.log(new ErrorReporter.Error(msg, ErrorType.Parsing));
        throw new ParserErrorException(msg);
    }

    private String getTokenErrorMsg(Token token) {
        return "File: '" + token.getSourceFile().toString() + "'. Line " + token.getLine() + " near '" + token.getText() + "'.";
    }

    // Parse Methods
    public Program parse() throws IOException {
        preProcessor.process();
        this.tokens = new ListIterator<Token>(preProcessor.getTokenStream());

        currentToken = tokens.next();
        return program = parseProgram();
    }

    private Program parseProgram() {
        Command command = parseCommand();
        if (currentToken.getType() != TokenType.EOT)
            err.log(new ErrorReporter.Error("Sentence ended unexpectedly.", ErrorType.Parsing));
        return new Program(command);
    }

    // Parse Commands
    private Command parseCommand() {
        Command left = parseSingleCommand();
        Command parent = left;
        while (tokens.hasNext() && currentToken.getType() != TokenType.RightCurl) {
            Command right = parseSingleCommand();
            if (right == null)
                throwError("Syntax error.");
            parent = new SequentialCommand(left, right);
            left.setParent(parent);
            right.setParent(parent);
            left = parent;
        }
        return left;
    }

    private Command parseSingleCommand() {
        Command command = null;
        switch (currentToken.getType()) {
            case While:
                command = parseWhileCommand();
                break;
            case For:
                command = parseForCommand();
                break;
            case If:
                command = parseIfCommand();
                break;
            case Return:
                command = parseReturnCommand();
                break;
            case Break:
                command = parseBreakCommand();
                break;
            case Final:
                command = parseAssignDeclarationCommand();
                break;
            case Identifier:
                currentToken = tokens.goFoward(1);
                // Assign Declaration
                if (currentToken.getType() == TokenType.Assign) {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    command = parseAssignDeclarationCommand();
                }
                // Array Declaration of array
                else if (currentToken.getType() == TokenType.RightSquareBracket && tokens.peek(-1).getType() == TokenType.LeftSquareBracket
                        && tokens.peek(2).getType() == TokenType.Assign) {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    command = parseAssignDeclarationCommand();
                }
                // Declaration Command
                else if (currentToken.getType() == TokenType.SemiComma) {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    command = parseDeclarationCommand();
                }
                // Array Declaration
                else if (currentToken.getType() == TokenType.RightSquareBracket && tokens.peek(-1).getType() == TokenType.LeftSquareBracket) {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    command = parseDeclarationCommand();
                } else {
                    currentToken = tokens.getBack(1);

                    // Infers if it is an assignment or call
                    int i = 0;
                    Token nextToken = tokens.peek(i);

                    for (; nextToken.getType() != TokenType.SemiComma; i++) {

                        if (nextToken.getType() == TokenType.Assign)
                            break;
                        nextToken = tokens.peek(i);

                        // Checks for end of file or parse error
                        if (nextToken == null)
                            throwError(TokenType.SemiComma);

                    }
                    if (nextToken.getType() == TokenType.Assign) {
                        currentToken = tokens.getBack(1);
                        tokens.goFoward(1);
                        command = parseAssignCommand();
                    } else {
                        currentToken = tokens.getBack(1);
                        tokens.goFoward(1);
                        command = parseCallCommand();
                    }
                }
                break;
            // Declaration Command
            default:
                command = parseDeclarationCommand();
                if (command.getType() == Command.Type.Declaration) {
                    DeclarationCommand dCommand = (DeclarationCommand) command;
                    if (dCommand.getDeclaration() == null)
                        command = null;
                }
                break;
        }
        return command;
    }

    private Command parseAssignDeclarationCommand() {
        boolean isFinal = false;
        if (currentToken.getType() == TokenType.Final) {
            acceptIt();
            isFinal = true;
        }
        Declaration declaration = parseReferenceDeclaration();
        declaration.setFinal(isFinal);
        accept(TokenType.Assign);
        Expression expression = parseExpression();
        accept(TokenType.SemiComma);
        return new AssignDeclarationCommand(declaration, expression);
    }

    private Command parseAssignCommand() {
        Command command = parseClearAssignCommand();
        accept(TokenType.SemiComma);
        return command;
    }

    private Command parseCallCommand() {
        NameExpression[] nameExpressions = parseNameExpressionList();

        // Checks if last is a call expression
        if (nameExpressions[nameExpressions.length - 1].getType() != Expression.Type.Call)
            throwError("Incomplete call command.");

        accept(TokenType.SemiComma);
        return new CallCommand(nameExpressions);
    }

    private IfCommand parseIfCommand() {
        accept(TokenType.If);
        IfCommand ifCommand = parseIfCommandBlock();
        if (currentToken.getType() == TokenType.Else) {
            acceptIt();
            if (currentToken.getType() == TokenType.If)
                ifCommand.setElseCommand(parseIfCommand());
            else
                ifCommand.setElseCommand(parseElseCommandBlock());
        }
        return ifCommand;
    }

    private Command parseReturnCommand() {
        accept(TokenType.Return);
        Expression expression = null;
        if (tokens.peek(1).getType() != TokenType.SemiComma)
            expression = parseExpression();
        else
            expression = new VoidExpression();
        accept(TokenType.SemiComma);
        return new ReturnCommand(expression);
    }

    private Command parseWhileCommand() {
        accept(TokenType.While);
        accept(TokenType.LeftParen);
        Expression expression = parseExpression();
        accept(TokenType.RightParen);
        accept(TokenType.LeftCurl);
        Command command = parseCommand();
        accept(TokenType.RightCurl);
        Command whileCommand = new WhileCommand(expression, command);
        command.setParent(whileCommand);
        return whileCommand;
    }

    private Command parseForCommand() {
        accept(TokenType.For);
        accept(TokenType.LeftParen);
        Command initCommand = parseSingleCommand();
        if (tokens.peek(-2).getType() == TokenType.SemiComma) {
            currentToken = tokens.getBack(2);
            tokens.goFoward(1);
        }
        accept(TokenType.SemiComma);
        Expression expression = parseExpression();
        accept(TokenType.SemiComma);
        Command incrementCommand = parseClearAssignCommand();
        accept(TokenType.RightParen);
        accept(TokenType.LeftCurl);
        Command bodyCommand = parseCommand();
        accept(TokenType.RightCurl);
        Command forCommand = new ForCommand(initCommand, expression, incrementCommand, bodyCommand);
        initCommand.setParent(forCommand);
        incrementCommand.setParent(forCommand);
        if (bodyCommand != null)
            bodyCommand.setParent(forCommand);
        return forCommand;
    }

    private Command parseClearAssignCommand() {
        NameExpression[] nameExpressions = parseNameExpressionList();

        // Checks if last is a reference expression
        if (nameExpressions[nameExpressions.length - 1].getType() != Expression.Type.Reference)
            throwError("Incomplete assign command.");

        accept(TokenType.Assign);
        Expression expression = parseExpression();
        return new AssignCommand(nameExpressions, expression);
    }

    private Command parseBreakCommand() {
        accept(TokenType.Break);
        accept(TokenType.SemiComma);
        return new BreakCommand();
    }

    private Command parseDeclarationCommand() {
        Declaration declaration = parseDeclaration();
        if (declaration != null) {
            if (declaration.getType() != Declaration.DeclarationType.ProcedureFunction)
                accept(TokenType.SemiComma);
        }
        return new DeclarationCommand(declaration);
    }

    private IfCommand parseIfCommandBlock() {
        accept(TokenType.LeftParen);
        Expression expression = parseExpression();
        accept(TokenType.RightParen);
        accept(TokenType.LeftCurl);
        Command command = parseCommand();
        accept(TokenType.RightCurl);
        IfCommand ifCommand = new IfCommand(expression, command);
        command.setParent(ifCommand);
        return ifCommand;
    }

    private IfCommand parseElseCommandBlock() {
        accept(TokenType.LeftCurl);
        Command command = parseCommand();
        accept(TokenType.RightCurl);
        Expression expression = new BooleanExpression(new BooleanLiteral(true));
        return new IfCommand(expression, command);
    }

    // Terminal Symbols
    private Literal parseLiteral() {
        Literal literal = null;
        switch (currentToken.getType()) {
            case BooleanLiteral:
                literal = parseBooleanLiteral();
                break;
            case StringLiteral:
                literal = parseStringLiteral();
                break;
            case IntegerLiteral:
                literal = parseIntegerLiteral();
                break;
            case DoubleLiteral:
                literal = parseDoubleLiteral();
                break;
            case NullLiteral:
                literal = parseNullLiteral();
                break;
        }
        return literal;
    }

    private IntLiteral parseIntegerLiteral() {
        Token token = currentToken.clone();
        accept(TokenType.IntegerLiteral);
        IntLiteral literal = new IntLiteral(Integer.parseInt(token.getText()));
        return literal;
    }

    private DoubleLiteral parseDoubleLiteral() {
        Token token = currentToken.clone();
        accept(TokenType.DoubleLiteral);
        DoubleLiteral literal = new DoubleLiteral(Double.parseDouble(token.getText()));
        return literal;
    }

    private StringLiteral parseStringLiteral() {
        Token token = currentToken.clone();
        accept(TokenType.StringLiteral);
        String text = token.getText();
        // TODO fix getting text from token
        if (text.length() > 0)
            text = text.substring(1, text.length() - 1);
        StringLiteral literal = new StringLiteral(text);
        return literal;
    }

    private BooleanLiteral parseBooleanLiteral() {
        Token token = currentToken.clone();
        accept(TokenType.BooleanLiteral);
        boolean value = preProcessor.getLexicon().isTrueBooleanLiteral(token.getText());
        BooleanLiteral literal = new BooleanLiteral(value);
        return literal;
    }

    private NullLiteral parseNullLiteral() {
        accept(Token.TokenType.NullLiteral);
        return new NullLiteral();
    }

    private Operator parseOperator() {
        Token token = currentToken.clone();
        if (token.getType() == TokenType.Operator || token.getType() == TokenType.NegationOperator)
            acceptIt();
        else
            accept(TokenType.Operator);
        return Operator.createOperator(token);
    }

    private Identifier parseIdentifier() {
        Token token = currentToken.clone();
        accept(TokenType.Identifier);
        Identifier identifier = new Identifier(token);
        return identifier;
    }

    private TypeDenoter parseTypeDenoter() {
        Identifier identifier = parseIdentifier();
        int dimensions = 0;
        while (currentToken.getType() == TokenType.LeftSquareBracket) {
            acceptIt();
            dimensions++;
            accept(TokenType.RightSquareBracket);
        }
        TypeDenoter typeDenoter = TypeDenoter.createTypeDenoter(identifier, dimensions);
        return typeDenoter;
    }

    // Parse Declarations
    private Declaration parseDeclaration() {
        Declaration declaration = null;
        switch (currentToken.getType()) {
            case Rule:
                declaration = parseRuleDeclaration();
                break;
            case Fact:
                declaration = parseFactDeclaration();
                break;
            case Function:
                declaration = parseFunctionDeclaration();
                break;
            case Sentence:
                declaration = parseSentenceDeclaration();
                break;
            case Struct:
                declaration = parseStructDeclaration();
                break;
            case function:
                declaration = parseFunctionProcedureDeclaration();
                break;
            case Identifier:
                declaration = parseReferenceDeclaration();
                break;
        }
        // Sequential
        if (currentToken.getType() == TokenType.Comma)
            parseSequentialDeclaration();
        return declaration;
    }

    private Declaration parseFunctionProcedureDeclaration() {
        ReferenceDeclaration[] argsDeclarations = new ReferenceDeclaration[0];
        Command command = null;
        accept(TokenType.function);
        TypeDenoter typeDenoter = parseTypeDenoter();

        // Constructors
        Identifier identifier = null;
        boolean declaredTypeLess = false;
        if (currentToken.getType() == TokenType.Identifier)
            identifier = parseIdentifier();
        else {
            currentToken = tokens.getBack(2);
            tokens.goFoward(1);
            identifier = parseIdentifier();
            declaredTypeLess = true;
        }

        accept(TokenType.LeftParen);

        if (currentToken.getType() == TokenType.Identifier)
            argsDeclarations = parseDeclarationReferenceList();

        accept(TokenType.RightParen);
        accept(TokenType.LeftCurl);

        if (currentToken.getType() != TokenType.RightCurl)
            command = parseCommand();

        accept(TokenType.RightCurl);
        return new ProcedureFunctionDeclaration(typeDenoter, identifier, argsDeclarations, command, declaredTypeLess);
    }

    private ReferenceDeclaration[] parseDeclarationReferenceList() {
        List<ReferenceDeclaration> declarations = new ArrayList<ReferenceDeclaration>();
        declarations.add((ReferenceDeclaration) parseReferenceDeclaration());
        while (currentToken.getType() == TokenType.Comma) {
            acceptIt();
            declarations.add((ReferenceDeclaration) parseReferenceDeclaration());
        }
        ReferenceDeclaration[] aReferences = new ReferenceDeclaration[declarations.size()];
        return declarations.toArray(aReferences);
    }

    private Declaration parseSequentialDeclaration() {
        Declaration declaration = null;
        if (currentToken.getType() == TokenType.Comma) {
            while (currentToken.getType() == TokenType.Comma) {
                acceptIt();
                declaration = new SequentialDeclaration(declaration, parseDeclaration());
            }
        }
        return declaration;
    }

    private Declaration parseReferenceDeclaration() {
        TypeDenoter typeDenoter = parseTypeDenoter();
        Identifier identifier = parseIdentifier();
        return new ReferenceDeclaration(typeDenoter, identifier);
    }

    private Declaration parseStructDeclaration() {
        Identifier identifier = null;
        accept(TokenType.Struct);
        TypeDenoter typeDenoter = parseTypeDenoter();
        accept(TokenType.LeftCurl);
        Declaration[] declarations = parseStructFieldDeclarations();
        accept(TokenType.RightCurl);
        if (currentToken.getType() == TokenType.Identifier)
            identifier = parseIdentifier();
        return new StructDeclaration(typeDenoter, declarations, identifier);
    }

    private Declaration[] parseStructFieldDeclarations() {
        List<Declaration> declarations = new ArrayList<Declaration>();
        Declaration fieldDeclaration = parseDeclaration();
        if (fieldDeclaration != null)
            declarations.add(fieldDeclaration);
        while (currentToken.getType() == TokenType.SemiComma) {
            acceptIt();
            fieldDeclaration = parseDeclaration();
            if (fieldDeclaration != null)
                declarations.add(fieldDeclaration);
        }
        Declaration[] aDeclarations = new Declaration[declarations.size()];
        return declarations.toArray(aDeclarations);
    }

    private Declaration parseSentenceDeclaration() {
        Identifier typeIdentifier = new Identifier(currentToken.clone());
        accept(TokenType.Sentence);
        Identifier identifier = parseIdentifier();
        accept(TokenType.LeftCurl);
        Sentence sentence = parseSentence();
        accept(TokenType.RightCurl);
        return new SentenceDeclaration(identifier, sentence, TypeDenoter.createTypeDenoter(typeIdentifier, 0));
    }

    private Declaration parseFunctionDeclaration() {
        Identifier typeIdentifier = new Identifier(currentToken.clone());
        accept(TokenType.Function);
        Function function = parseFunction();
        accept(TokenType.Becomes);
        Constant constant = parseConstant();
        return new FunctionDeclaration(function, constant, TypeDenoter.createTypeDenoter(typeIdentifier, 0));
    }

    private Declaration parseFactDeclaration() {
        Identifier typeIdentifier = new Identifier(currentToken.clone());
        accept(TokenType.Fact);
        Predicate fact = parseFact();
        return new FactDeclaration(fact, TypeDenoter.createTypeDenoter(typeIdentifier, 0));
    }

    private Declaration parseRuleDeclaration() {
        Identifier typeIdentifier = new Identifier(currentToken.clone());
        accept(TokenType.Rule);
        Identifier identifier = parseIdentifier();
        accept(TokenType.LeftCurl);
        Rule rule = new Rule(parseSentence(), identifier);
        accept(TokenType.RightCurl);
        return new RuleDeclaration(rule, TypeDenoter.createTypeDenoter(typeIdentifier, 0));
    }

    // Parse Expressions
    private Expression parseExpression() {
        PrecedenceClimbing processor = new PrecedenceClimbing(preProcessor.getLexicon().getOperatorPrecedence());
        return processor.process();
    }

    private Expression parsePrimaryExpression() {
        Expression expression = null;
        switch (currentToken.getType()) {
            case BooleanLiteral:
                expression = parseBooleanExpression();
                break;
            case StringLiteral:
                expression = parseStringExpression();
                break;
            case IntegerLiteral:
                expression = parseIntegerExpression();
                break;
            case DoubleLiteral:
                expression = parseDoubleExpression();
                break;
            case NullLiteral:
                expression = parseNullExpression();
                break;
            case LeftParen:
                expression = parseParenExpression();
                break;
            case New:
                expression = parseNewExpression();
                break;
            case Identifier:
                acceptIt();
                // Reference expression
                if (currentToken.getType() == TokenType.Operator) {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    expression = parseReferenceExpression();
                }
                // Call Expression
                else if (currentToken.getType() == TokenType.LeftParen) {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    expression = parseCallExpression();
                } else {
                    currentToken = tokens.getBack(1);
                    if (currentToken.getType() != TokenType.Operator) {
                        currentToken = tokens.getBack(1);
                        tokens.goFoward(1);
                        expression = parseReferenceDereferenceExpression();
                    }
                }
                break;
            case NegationOperator:
            case Operator:
                expression = parseUnaryExpression();
        }
        return expression;
    }

    private Expression parseParenExpression() {
        Expression expression = null;
        accept(TokenType.LeftParen);
        expression = parseExpression();
        accept(TokenType.RightParen);
        return expression;
    }

    private Expression parseCallExpression() {
        Identifier identifier = parseIdentifier();
        accept(TokenType.LeftParen);
        Expression[] expressions = parseExpressionList();
        accept(TokenType.RightParen);
        Expression[] indexExpressions = new Expression[0];
        if (currentToken.getType() == TokenType.LeftSquareBracket)
            indexExpressions = parseBracketExpressionList();
        return new CallExpression(identifier, expressions, indexExpressions);
    }

    private Expression parseUnaryExpression() {
        Operator operator = parseOperator();
        if (!operator.isUnaryOperator())
            throwError("Expecting unary operator. '" + tokens.peek(-2).getText() + "' found.");
        Expression expression = parsePrimaryExpression();
        return new UnaryExpression(operator, expression);
    }

    private Expression parseDoubleExpression() {
        return new DoubleExpression(parseDoubleLiteral());
    }

    private Expression parseIntegerExpression() {
        return new IntegerExpression(parseIntegerLiteral());
    }

    private Expression parseStringExpression() {
        return new StringExpression(parseStringLiteral());
    }

    private Expression parseReferenceDereferenceExpression() {
        Expression expression = parseNameExpression();
        if (currentToken.getType() == TokenType.Period) {
            List<NameExpression> nameExpressions = new ArrayList<NameExpression>();
            nameExpressions.add((NameExpression) expression);
            while (currentToken.getType() == TokenType.Period) {
                acceptIt();
                nameExpressions.add((NameExpression) parseNameExpression());
            }
            NameExpression[] aNameExpressions = new NameExpression[nameExpressions.size()];
            expression = new DereferenceExpression(nameExpressions.toArray(aNameExpressions));
        }
        return expression;
    }

    private Expression parseReferenceExpression() {
        Identifier identifier = parseIdentifier();
        Expression[] indexExpressions = new Expression[0];
        if (currentToken.getType() == TokenType.LeftSquareBracket)
            indexExpressions = parseBracketExpressionList();
        return new ReferenceExpression(identifier, indexExpressions);
    }

    private Expression parseNameExpression() {
        Expression nameExpression = null;
        if (currentToken.getType() == TokenType.Identifier) {
            if (tokens.peek(0).getType() == TokenType.LeftParen)
                nameExpression = parseCallExpression();
            else
                nameExpression = parseReferenceExpression();
        }
        return nameExpression;
    }

    private Expression parseBooleanExpression() {
        return new BooleanExpression(parseBooleanLiteral());
    }

    private Expression parseNullExpression() {
        accept(TokenType.NullLiteral);
        return new NullExpression();
    }

    private NameExpression[] parseNameExpressionList() {
        Expression expression = parseNameExpression();
        List<NameExpression> nameExpressions = new ArrayList<NameExpression>();
        nameExpressions.add((NameExpression) expression);
        while (currentToken.getType() == TokenType.Period) {
            acceptIt();
            nameExpressions.add((NameExpression) parseNameExpression());
        }
        NameExpression[] aNameExpressions = new NameExpression[nameExpressions.size()];
        return nameExpressions.toArray(aNameExpressions);
    }

    private Expression[] parseExpressionList() {
        List<Expression> expressions = new ArrayList<Expression>();
        Expression expression = parseExpression();
        if (expression != null) {
            expressions.add(expression);
            while (currentToken.getType() == TokenType.Comma) {
                acceptIt();
                expressions.add(parseExpression());
            }
        }
        Expression[] aExpressions = new Expression[expressions.size()];
        return expressions.toArray(aExpressions);
    }

    private Expression[] parseBracketExpressionList() {
        List<Expression> expressions = new ArrayList<Expression>();
        accept(TokenType.LeftSquareBracket);
        expressions.add(parseExpression());
        accept(TokenType.RightSquareBracket);
        while (currentToken.getType() == TokenType.LeftSquareBracket) {
            acceptIt();
            expressions.add(parseExpression());
            accept(TokenType.RightSquareBracket);
        }
        Expression[] aExpressions = new Expression[expressions.size()];
        return expressions.toArray(aExpressions);
    }

    private Expression parseNewExpression() {
        accept(TokenType.New);
        Identifier identifier = parseIdentifier();
        Expression[] expressionsArgs = null;
        boolean isArray = false;
        if (currentToken.getType() == TokenType.LeftParen) {
            acceptIt();
            expressionsArgs = parseExpressionList();
            accept(TokenType.RightParen);
        } else if (currentToken.getType() == TokenType.LeftSquareBracket) {
            expressionsArgs = parseBracketExpressionList();
            isArray = true;
        }

        return new NewExpression(identifier, expressionsArgs, isArray);
    }

    // Parse FOL Sentences
    private Sentence parseSentence() {
        Sentence left = parsePrimarySentence();
        while (currentToken.getType() == TokenType.Operator) {
            Operator operator = parseOperator();
            left = new BinarySentence(left, parsePrimarySentence(), (LogicalOperator) operator);
        }
        return left;
    }

    private Sentence parsePrimarySentence() {
        Sentence sentence = null;
        switch (currentToken.getType()) {
            case Identifier:
                sentence = parseAtomicSentence();
                break;
            case LeftParen:
                currentToken = tokens.goFoward(0);
                if (currentToken.getType() == TokenType.QuestionMark) {
                    currentToken = tokens.getBack(1);
                    tokens.goFoward(1);
                    sentence = parseAtomicSentence();
                } else {
                    currentToken = tokens.getBack(1);
                    tokens.goFoward(1);
                    sentence = parseComplexSentence();
                }
                break;
            default:
                sentence = parseComplexSentence();
        }
        return sentence;
    }

    private Sentence parseBindingSentence() {
        accept(TokenType.LeftParen);
        Term term1 = parseTerm();
        Operator operator = parseOperator();
        Term term2 = parseTerm();
        BindingSentence left = new BindingSentence(term1, term2, operator);
        while (currentToken.getType() == TokenType.Operator) {
            operator = parseOperator();
            left = new BindingSentence(term1, parseTerm(), operator);
        }
        accept(TokenType.RightParen);
        return left;
    }

    private Sentence parseComplexSentence() {
        ComplexSentence sentence = null;
        switch (currentToken.getType()) {
            case NegationOperator:
                sentence = (NegationSentence) parseNegationSentence();
                break;
            case Quantifier:
                sentence = (QuantifiedSentence) parseQuantifiedSentence();
                break;
            case LeftParen:
                sentence = (ParenSentence) parseParenSentence();
                break;
            default:
                String msg = "Error on parsing Complex sentence " + currentToken.getText() + " on line: " + currentToken.getLine();
                throwError(msg);
        }
        return sentence;
    }

    private Sentence parseQuantifiedSentence() {
        QuantifiedSentence.Quantifier quantifier = parseQuantifier();
        accept(TokenType.LeftParen);
        Variable[] vars = parseVariableList();
        accept(TokenType.SemiComma);
        Sentence sentence = parseSentence();
        accept(TokenType.RightParen);
        return new QuantifiedSentence(vars, sentence, quantifier);
    }

    private Sentence parseParenSentence() {
        accept(TokenType.LeftParen);
        Sentence sentence = parseSentence();
        accept(TokenType.RightParen);
        return new ParenSentence(sentence);
    }

    private Sentence parseNegationSentence() {
        accept(TokenType.NegationOperator);
        Sentence sentence = parseSentence();
        return new NegationSentence(sentence);
    }

    private Sentence parseAtomicSentence() {
        AtomicSentence sentence = null;
        if (currentToken.getType() == TokenType.Identifier)
            sentence = parsePredicate();
        else if (currentToken.getType() == TokenType.LeftParen)
            sentence = (AtomicSentence) parseBindingSentence();
        else {
            BooleanLiteral literal = parseBooleanLiteral();
            sentence = new BooleanSentence(literal);
        }
        return sentence;
    }

    private QuantifiedSentence.Quantifier parseQuantifier() {
        QuantifiedSentence.Quantifier quantifier = QuantifiedSentence.getQuantifier(currentToken.getText());
        return quantifier;
    }

    private Term parseTerm() {
        Term term = null;
        switch (currentToken.getType()) {
            case QuestionMark:
                term = parseVariable();
                break;
            case Identifier:
                acceptIt();
                if (currentToken.getType() == TokenType.LeftParen) {
                    currentToken = tokens.getBack(1);
                    tokens.goFoward(1);
                    term = parseFunction();
                } else {
                    currentToken = tokens.getBack(2);
                    tokens.goFoward(1);
                    term = parseConstant();
                }
                break;
            // Constant again. Might need to refactor this.
            default:
                acceptIt();
                currentToken = tokens.getBack(2);
                tokens.goFoward(1);
                term = parseConstant();
        }
        return term;
    }

    private Function parseFunction() {
        Identifier identifier = parseIdentifier();
        accept(TokenType.LeftParen);
        Term[] terms = parseTermList();
        accept(TokenType.RightParen);
        return new Function(identifier, terms);
    }

    private Constant parseConstant() {
        Constant constant = null;

        if (currentToken.getType() == Token.TokenType.Identifier) {
            Identifier identifier = parseIdentifier();
            constant = new Constant(identifier);
        } else {
            Literal literal = parseLiteral();
            constant = new Constant(literal);
        }
        return constant;
    }

    private Variable parseVariable() {
        accept(TokenType.QuestionMark);
        Identifier identifier = parseIdentifier();
        return new Variable(identifier);
    }

    private Predicate parseFact() {
        return parsePredicate();
    }

    private Predicate parsePredicate() {
        Identifier identifier = parseIdentifier();
        Term[] terms = new Term[0];
        if (currentToken.getType() == TokenType.LeftParen) {
            acceptIt();
            terms = parseTermList();
            accept(TokenType.RightParen);
        }
        return new Predicate(identifier, terms);
    }

    private Term[] parseTermList() {
        List<Term> terms = new ArrayList<Term>();
        terms.add(parseTerm());
        while (currentToken.getType() == TokenType.Comma) {
            acceptIt();
            terms.add(parseTerm());
        }
        Term[] aTerms = new Term[terms.size()];
        return terms.toArray(aTerms);
    }

    private Variable[] parseVariableList() {
        List<Variable> vars = new ArrayList<Variable>();
        accept(TokenType.QuestionMark);
        vars.add(parseVariable());
        while (currentToken.getType() == TokenType.Comma) {
            acceptIt();
            accept(TokenType.QuestionMark);
            vars.add(parseVariable());
        }
        Variable[] aVars = new Variable[vars.size()];
        return vars.toArray(aVars);
    }

    public ErrorReporter getErrorReporter() {
        return err;
    }

    // Auxiliary parse methods
    public void setErrorReporter(ErrorReporter err) {
        this.err = err;
    }

    public PreProcessor getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(PreProcessor preProcessor) {
        this.preProcessor = preProcessor;
        this.tokens = new ListIterator<Token>(preProcessor.getTokenStream());
    }

    public Program getProgram() {
        return program;
    }

    private class PrecedenceClimbing {
        private String[] operatorPrecedence = null;

        public PrecedenceClimbing(String[] operatorPrecedence) {
            this.operatorPrecedence = operatorPrecedence;
        }

        public Expression process() {
            return parseExpression();
        }

        private Expression parseExpression1(Expression left, int minPrecedence) {
            Expression right = null;
            Operator operator = null;

            while (currentToken.getType() == TokenType.Operator && getOperatorPrecedence(currentToken.getText()) >= minPrecedence) {
                operator = parseOperator();
                right = parsePrimaryExpression();
                while (currentToken.getType() == TokenType.Operator &&
                        Operator.isBinaryOperator(currentToken.getText()) &&
                        getOperatorPrecedence(currentToken.getText()) >= getOperatorPrecedence(operator.getText())) {
                    right = parseExpression1(right, getOperatorPrecedence(currentToken.getText()));
                }
                left = new BinaryExpression(left, right, operator);
            }

            return left;
        }

        private Expression parseExpression() {
            return parseExpression1(parsePrimaryExpression(), 0);
        }

        private int getOperatorPrecedence(String symbol) {
            int i = 0;
            for (i = 0; i < operatorPrecedence.length; i++) {
                if (operatorPrecedence[i].equals(symbol))
                    break;
            }
            return operatorPrecedence.length - i;
        }

    }

}
