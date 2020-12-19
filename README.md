## Notice
```
Copyright (C) 2013 Lucas Batista.
All rights reserved.

The software in this package is published under the terms of the BSD
style license a copy of which has been included with this distribution in
the LICENSE.txt file.
```

## What is Jall?
Jall stands for Just Another Logic Language. Jall is a scripting language whose scope is to
develop logic agents using both logic and procedural paradigms. Jall supports native FOL sentences
that can be expressed easily and naturally, and a powerful logic engine that can be accessed through Jall standard procedures.
Jall also features most common procedural language constructs, such as functions, loops, conditionals, expressions, etc...

## How to run Jall?
Jall can be run by executing the Jall.jar executable jar and passing the jall source code file as an argument.

### Prerequisites:
Java virtual machine (JVM).

### Installation example in windows:

*	Create the folder "C:\Jall", where "C" is your hard drive unit.
*	Place the file "release\Jall.jar" and the folder scripts under this folder.
*	Open windows Command prompt.
*	Make the "jall" folder as the current directory, the command line should start showing the "C:\jall?>" prefix.
*	Execute the following command: java -jar Jall.jar method=file filename="C:\jall\scripts\helloWorld.jall"
*	To run another file just pass the absolute file path as the value of argument "filename".	

## Reasoning Example:
Please run file "reasoning.jall" under the "scripts" folder following the instruction above.

## RELEASES
05/06/2013 - Alpha release - Functional release. Able to execute Jall scripts, and contains some standard
procedures. Able to do Foward Chaining Reasoning.