/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.inference.*;
import jall.lang.ast.Identifier;
import jall.lang.ast.literal.*;
import jall.lang.ast.sentence.Sentence;
import jall.lang.interpreter.entities.Reference;

import java.util.HashSet;
import java.util.Set;

public class FOL {

    public ObjectLiteral runRadio(StructLiteral domain, ProcedureFunctionLiteral objectiveFunction) {
        return null;
    }

    public ObjectLiteral reason(StructLiteral domain, StructLiteral facts, StructLiteral queries) {
        // Creates knowledge base
        KnowledgeBase kb = KnowledgeBase.createKnowledgeBase(domain);

        // Gets case specific elements
        Set<Predicate> factsSet = getPredicates(facts);
        Set<Predicate> queriesSet = getPredicates(queries);

        // Runs inference engine
        InferenceEngine engine = new InferenceEngine(kb, factsSet, queriesSet);
        engine.run();
        Answer[] answers = engine.answer();

        // Answer
        ObjectLiteral obj = null;

        // Prepares answer
        if (answers != null) {
            ArrayLiteral arrLiteral = new ArrayLiteral();
            for (Answer answer : answers) {
                StructLiteral answerStruct = new StructLiteral();
                ArrayLiteral elementArray = new ArrayLiteral();
                arrLiteral.getValue().add(answerStruct);

                Reference query = new Reference(new Identifier("query"));
                query.setLiteral(new StringLiteral(answer.query.toString()));
                answerStruct.getScope().enter(query.getIdentifier(), query);
                if (answer.theta != null) {
                    for (Substitution subst : answer.theta) {
                        StructLiteral struct = new StructLiteral();
                        Reference substitutor = new Reference(new Identifier("substitutor"));
                        Reference substituted = new Reference(new Identifier("substituted"));

                        substitutor.setLiteral(new StringLiteral(subst.substitutor.toString()));
                        substituted.setLiteral(new StringLiteral(subst.substituted.toString()));

                        struct.getScope().enter(substitutor.getIdentifier(), substitutor);
                        struct.getScope().enter(substituted.getIdentifier(), substituted);
                        elementArray.getValue().add(struct);
                    }
                    Reference array = new Reference(new Identifier("theta"));
                    array.setLiteral(elementArray);
                    answerStruct.getScope().enter(array.getIdentifier(), array);
                }
            }
            obj = new ObjectLiteral(arrLiteral);
        }

        return obj;
    }

    private Set<Predicate> getPredicates(StructLiteral structLiteral) {

        Set<Predicate> predicates = new HashSet<Predicate>();

        for (Identifier identifer : structLiteral.getScope().getIdentifiers()) {
            Literal literal = structLiteral.getScope().retrieve(identifer).getLiteral();
            if (literal.getType() == Literal.Type.Sentence) {
                Sentence sentence = ((SentenceLiteral) literal).getValue();
                if (sentence.getType() == Sentence.Type.Predicate)
                    predicates.add((Predicate) sentence);
            }
        }
        return predicates;
    }

}
