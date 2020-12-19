/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.sentence;

import jall.exceptions.NotProperFormRuleException;
import jall.inference.Predicate;
import jall.inference.Substitution;
import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;
import jall.lang.ast.operator.LogicalOperator;

import java.util.ArrayList;
import java.util.List;

public class Rule extends Sentence {

    private Identifier identifier = null;
    private Sentence sentence = null;

    // Cached collections
    private Predicate[] premises = null;
    private Predicate conclusion = null;

    public Rule(Sentence sentence, Identifier identifier) {
        this.sentence = sentence;
        this.identifier = identifier;
        this.type = Type.Rule;
    }

    public Sentence getPremissesSentence() throws NotProperFormRuleException {
        Sentence premisses = null;
        switch (sentence.getType()) {
            case Binary:
                premisses = getPremises((BinarySentence) sentence);
                break;
        }
        return premisses;
    }

    public Sentence getConclusionSentence() throws NotProperFormRuleException {
        Sentence conclusion = null;
        switch (sentence.getType()) {
            case Binary:
                conclusion = getConclusion((BinarySentence) sentence);
                break;
        }
        return conclusion;
    }

    private void buildPremisesList() throws NotProperFormRuleException {
        List<Predicate> predicates = new ArrayList<Predicate>();
        Sentence premissesSentence = getPremissesSentence();
        buildPremisesList(premissesSentence, predicates);
        Predicate[] aPredicates = new Predicate[predicates.size()];
        premises = predicates.toArray(aPredicates);
    }

    private void buildPremisesList(Sentence sentence, List<Predicate> predicates) {
        switch (sentence.getType()) {
            case Binary:
                buildPremisesList(((BinarySentence) sentence).getLeft(), predicates);
                buildPremisesList(((BinarySentence) sentence).getRight(), predicates);
                break;
            case Negation:
                buildPremisesList(((NegationSentence) sentence).getSentence(), predicates);
                break;
            case Paren:
                buildPremisesList(((ParenSentence) sentence).getSentence(), predicates);
                break;
            case Quantified:
                buildPremisesList(((QuantifiedSentence) sentence).getSentence(), predicates);
                break;
            case Predicate:
                predicates.add((Predicate) sentence);
                break;
            default:
        }
    }

    private void buildConclusion() throws NotProperFormRuleException {
        Sentence conclusionSentence = getConclusionSentence();
        conclusion = (Predicate) conclusionSentence;
    }

    private Sentence getPremises(BinarySentence binarySentence) throws NotProperFormRuleException {
        Sentence premises = null;
        LogicalOperator lOperator = binarySentence.getOperator();
        if (lOperator.isImply())
            premises = binarySentence.getLeft();
        else
            throw new NotProperFormRuleException("Rule not in canonical definite clause form.");

        // Checks operators
        analysePremises(premises);

        return premises;
    }

    private Sentence getConclusion(BinarySentence binarySentence) throws NotProperFormRuleException {
        Sentence conclusion = null;
        LogicalOperator lOperator = binarySentence.getOperator();
        if (lOperator.isImply())
            conclusion = binarySentence.getRight();
        else
            throw new NotProperFormRuleException("Rule not in canonical definite clause form.");
        return conclusion;
    }

    private void analysePremises(Sentence premises) throws NotProperFormRuleException {
        if (premises.type == Sentence.Type.Binary) {
            BinarySentence binarySentence = (BinarySentence) premises;
            LogicalOperator lOperator = binarySentence.getOperator();
            if (!lOperator.isAND())
                throw new NotProperFormRuleException("Rule not in canonical definite clause form.");
        }
    }

    public Sentence infer(Substitution substitution) {
        // TODO Rule::infer
        return null;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Sentence clone() {
        return new Rule(sentence.clone(), identifier.clone());
    }

    @Override
    public String toString() {
        return "{" + identifier.toString() + "=" + sentence.toString() + "}";
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public Predicate[] getPremises() throws NotProperFormRuleException {
        if (premises == null)
            buildPremisesList();
        return premises;
    }

    public Predicate getConclusion() throws NotProperFormRuleException {
        if (conclusion == null)
            buildConclusion();
        return conclusion;
    }

}
