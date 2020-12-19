/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.lang.ast.literal.Literal;
import jall.lang.ast.literal.ObjectLiteral;
import jall.lang.ast.literal.StringLiteral;

public class IO {
    public void print(ObjectLiteral arg0) {
        System.out.print(arg0);
    }

    public void println(ObjectLiteral arg0) {
        System.out.println(arg0.toString());
    }

    public void dump(StringLiteral fileFullName, ObjectLiteral value) {
        jall.util.Utility.writeToFile(fileFullName.toString(), Literal.toJSON(value));
    }

}
