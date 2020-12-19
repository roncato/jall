/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

import jall.exceptions.NotProperFormRuleException;
import jall.lang.ast.sentence.Rule;

import java.util.*;

public class InferenceEngine implements Runnable {

    private KnowledgeBase kb = null;

    private Set<Predicate> facts = null;
    private Set<Predicate> queries = null;

    public InferenceEngine() {
        facts = new HashSet<Predicate>();
        queries = new HashSet<Predicate>();
    }

    public InferenceEngine(KnowledgeBase kb) {
        this();
        this.kb = kb;
    }

    public InferenceEngine(KnowledgeBase kb, Set<Predicate> facts,
                           Set<Predicate> queries) {
        this.kb = kb;
        this.facts = facts;
        this.queries = queries;
    }

    @Override
    public void run() {
        for (Predicate fact : facts) {
            try {
                fowardChain(fact);
            } catch (NotProperFormRuleException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public Answer[] answer() {

        ArrayList<Answer> answers = new ArrayList<Answer>();

        for (Predicate query : queries) {
            SubstitutionSet theta = null;
            for (Predicate fact : kb.getFacts()) {
                SubstitutionSet theta2 = unify(query, fact);
                if (theta2 != null) {
                    if (theta == null)
                        theta = new SubstitutionSet();
                    theta = SubstitutionSet.union(theta, theta2);
                }
            }
            Answer answer = new Answer(query, theta);
            answers.add(answer);
        }
        Answer[] aAnswers = new Answer[answers.size()];
        if (answers.size() > 0)
            aAnswers = answers.toArray(aAnswers);
        return aAnswers;
    }

    private SubstitutionSet unify(Predicate p1, Predicate p2) {
        return unify(new FOLList(p1), new FOLList(p2));
    }

    private SubstitutionSet unify(FOLList e1, FOLList e2) {
        if (e1.isAtom() && e2.isAtom()) {
            if (e1.equals(e2))
                return new SubstitutionSet();
            if (e1.isVariable()) {
                Variable v1 = e1.asVariable();
                if (e2.occurs(v1))
                    return null;
                SubstitutionSet theta = new SubstitutionSet();
                theta.add(e2.substitute(v1));
                return theta;
            }
            if (e2.isVariable()) {
                Variable v2 = e2.asVariable();
                if (e1.occurs(v2))
                    return null;
                SubstitutionSet theta = new SubstitutionSet();
                theta.add(e1.substitute(v2));
                return theta;
            }
            return null;
        }

        FOLList f1 = e1.first();
        FOLList t1 = e1.remaining();
        FOLList f2 = e2.first();
        FOLList t2 = e2.remaining();

        SubstitutionSet z1 = unify(f1, f2);

        if (z1 == null)
            return null;


        FOLList g1 = t1.apply(z1);
        FOLList g2 = t2.apply(z1);

        SubstitutionSet z2 = unify(g1, g2);

        if (z2 == null)
            return null;

        return SubstitutionSet.compose(z1, z2);

    }

    private void fowardChain(Predicate p) throws NotProperFormRuleException {
        if (kb.ask(p))
            return;
        else
            kb.tell(p);
        for (Rule rule : kb.getRules()) {
            Predicate[] premises = rule.getPremises();
            for (Predicate premise : premises) {
                SubstitutionSet theta = unify(premise, p);
                if (theta != null)
                    findAndInfer(new LinkedList<Predicate>(Arrays.asList(premises)), rule.getConclusion(), theta);
            }
        }
    }

    private void findAndInfer(LinkedList<Predicate> premises, Predicate conclusion, SubstitutionSet theta) throws NotProperFormRuleException {
        if (premises.isEmpty())
            fowardChain(conclusion.substitute(theta));
        else {
            Set<Predicate> facts = new HashSet<Predicate>();
            facts.addAll(kb.getFacts());
            for (Predicate fact : facts) {
                Predicate first = premises.getFirst();
                SubstitutionSet theta2 = unify(fact, first.substitute(theta));
                if (theta2 != null)
                    findAndInfer(new LinkedList<Predicate>(premises.subList(1, premises.size())), conclusion, SubstitutionSet.compose(theta, theta2));
            }
        }
    }

    protected void backwardChain() {
        //TODO: backwardChain
    }

    public KnowledgeBase getKnowledgeBase() {
        return kb;
    }

    public void setKnowledgeBase(KnowledgeBase kb) {
        this.kb = kb;
    }
}
