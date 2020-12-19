/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

import jall.lang.ast.Identifier;
import jall.lang.ast.literal.Literal;
import jall.lang.ast.literal.SentenceLiteral;
import jall.lang.ast.literal.StructLiteral;
import jall.lang.ast.sentence.Rule;
import jall.lang.ast.sentence.Sentence;

import java.util.HashSet;
import java.util.Set;

public class KnowledgeBase {

    private Set<Predicate> facts = null;
    private Set<Rule> rules = null;
    public KnowledgeBase() {
        facts = new HashSet<Predicate>();
        rules = new HashSet<Rule>();
    }

    public static KnowledgeBase createKnowledgeBase(StructLiteral domain) {
        KnowledgeBase kb = new KnowledgeBase();

        Set<Identifier> identifiers = domain.getScope().getIdentifiers();

        for (Identifier identifer : identifiers) {
            Literal literal = domain.getScope().retrieve(identifer).getLiteral();
            if (literal.getType() == Literal.Type.Sentence) {
                Sentence sentence = ((SentenceLiteral) literal).getValue();
                kb.tell(sentence);
            }
        }

        return kb;
    }

    public void tell(Sentence sentence) {
        switch (sentence.getType()) {
            case Predicate:
                tell((Predicate) sentence);
                break;
            case Rule:
                tell((Rule) sentence);
                break;
            default:
        }
    }

    public void tell(Predicate fact) {
        facts.add(fact);
    }

    public void tell(Rule rule) {
        rules.add(rule);
    }

    public boolean ask(Sentence sentence) {
        boolean ask = false;
        switch (sentence.getType()) {
            case Predicate:
                ask = ask((Predicate) sentence);
                break;
            case Rule:
                ask = ask((Rule) sentence);
                break;
            default:
        }
        return ask;
    }

    public boolean ask(Predicate fact) {
        return facts.contains(fact);
    }

    public boolean ask(Rule rule) {
        return rules.contains(rule);
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public Set<Predicate> getFacts() {
        return facts;
    }

}
