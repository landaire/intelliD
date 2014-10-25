D Language plugin for JetBrains IntelliJ

## Project Status

These are my near-future goals in order of importance:

[X] Lexer
[X] Highlighter
[X] Brace Matcher
[] Project SDK setup
[] Compiling
[] Code completion
[] Debugging

Based off of [intelliD by elendl-](https://github.com/elendel-/intelliD)

This project consists of two modules:

- Dlang, the IntelliJ plugin
- plugin_tooling

plugin_tooling was created by Bruno Medeiros as part of [DDT], an Eclipse-based IDE for D.

The following changes to DDT were necessary to get the project up faster:

- Totally ditched the tests since I originally just wanted the lexer and didn't want to fix the tests yet
- Backported to JDK 6 as the module made heavy use of JDK 7 and JDK 6 or lower is required for IntelliJ
 plugin development


## Notes
 I have NOT tested some of the JDK 7 -> JDK 6 changes. Most (if not all!) of these changes were just refactoring
 to get rid of the use of java.nio module usage.

 I'm not a super great Java developer. I probably messed something up. Lots of the changes were
 changes from using the java.nio.file.Path class to just using java.io.File. If you think something
 related to my backport is broken, check out e646aa2bc25a30be30f4a5a3932a7830274faf85! The commit
 is *very* messed up because I accidentally committed a bunch of binary files without noticing, but
 just look at the Java source files that changed.
