Nine Square
=
Nine Square is an interactive [Sudoku](http://en.wikipedia.org/wiki/Sudoku) puzzle game. It has about a thousand easy puzzles and over ninety pieces of hard puzzles. There are no plan to include intermediate level puzzles yet :)

Rule
-
Fill in the grid so that every row, every column and every 3x3 box contains the digits 1 through 9.

Requirements
-
Nine Square was written in Swing with [Scala](http://www.scala-lang.org). It requires Scala libraries in addition to Java Development Kit to launch, maintain and for further development.

Nine Square was developed using the following tools and libraries,

 1. Scala 2.10.x, please refer [here](http://www.scala-lang.org/downloads) for information and download instructions.
 2. Simple Build Tool a.k.a sbt 0.12.x, please refer [here](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html) for setup instructions.
 3. Java Development Kit (JDK) 7, please refer [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html) for information and download instructions.

Setup and build
-
To setup an IntelliJ project,

    $ sbt gen-idea

To compile,

    $ sbt compile

To test,

    $ sbt test

To run web application demo,

    $ sbt
    > project web
    [web] $ run

To package and deploy as single JAR file,

    $ sbt one-jar

The final executable JAR file will be located in {project directory}/gui/target/scala-2.xx and is named gui_xxx-one-jar.jar

In addition to running these SBT commands from the command line, SBT console can be used to execute these commands. Please refer to SBT user guide.

To run the excutable JAR,

    $ java -jar gui_xxx-one-jar.jar

Background
-
Nine Square is a result of an on going effort to learn Functional Programming (FP) languages. After working with Java, an imperative Object Oriented (OO) language, for more than a decade, it is not easy to pick up a language from a different paradigm, and be effective about it. It challenges the mind to think differently and solves each problem in a different manner.

There are many obstacles along the way and fortunately, with the help from these people from the forums and website,

* http://stackoverflow.com/questions/15469303/how-do-i-accumulate-results-without-using-a-mutable-arraybuffer

* http://scala-programming-language.1934581.n4.nabble.com/25-lines-Sudoku-solver-in-Scala-td1987506.html

* http://stackoverflow.com/questions/17771573/suggestions-to-refactor-a-scala-function-with-multiple-exits/17783001

* http://norvig.com/sudoku.html

this project is completed. Completing this project has shown how immutable variables were  utilized and how to write in Scala idiom by using Scala functions effectively.

Quirks
-
There is a quirk running Nine Square with JDK 1.7 on top of OS X Mountain Lion.  Windows and Linux do not exhibit this behavior.

Pressed "F3" for "New Easy Game" for the first time generated a new puzzle. Pressed "F3" repeatedly did not work. If any button is clicked and pressed "F3" again, it worked for one time.

License
-
Copyright 2013 Lim, Teck Hooi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
