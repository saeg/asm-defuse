# ASM-DefUse

[ASM](http://asm.ow2.org/) powered by definitions/uses analysis

[![Maven Central](https://img.shields.io/maven-central/v/br.usp.each.saeg/asm-defuse.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/br.usp.each.saeg/asm-defuse)

ASM-DefUse extends [ASM](http://asm.ow2.org/) analysis API with control- and data-flow algorithms for definition/uses analysis.

## Requirements

* Java 6

## Setup

If you're using Maven just add this new dependency:

```xml
<dependency>
    <groupId>br.usp.each.saeg</groupId>
    <artifactId>asm-defuse</artifactId>
    <version>${asm-defuse.version}</version>
</dependency>
```

## Control-flow analysis

The following code exemplify how to use `FlowAnalyzer` class
```java
MethodNode mn = ... // A regular MethodNode from ASM tree API
FlowAnalyzer<BasicValue> analyzer = new FlowAnalyzer<BasicValue>(new BasicInterpreter());
analyzer.analyze("package/ClassName", mn);

int[][] successors = analyzer.getSuccessors();
int[][] predecessors = analyzer.getPredecessors();
int[][] basicBlocks = analyzer.getBasicBlocks();
int[] leaders = analyzer.getLeaders();

for (int i = 0; i < mn.instructions.size(); i++) {
  // successors[i] array contains the indexes of the successors of instruction i
  System.out.println("Instruction " + i + " has " + successors[i].length + " successors");

  // predecessors[i] array contains the indexes of the predecessors of instruction i
  System.out.println("Instruction " + i + " has " + predecessors[i].length + " predecessors");

  System.out.println("Instruction " + i + " belongs to basic block " + leaders[i]);
}
for (int i = 0; i < basicBlocks.length; i++) {
  System.out.println("Basic block " + i + " contains " + basicBlocks[i].length + " instructions");
}
```

## Data-flow analysis

The following code exemplify how to use `DefUseAnalyzer` class
```java
MethodNode mn = ... // A regular MethodNode from ASM tree API
DefUseAnalyzer analyzer = new DefUseAnalyzer();
analyzer.analyze("package/ClassName", mn);

Variable[] variables = analyzer.getVariables();
DefUseFrame[] frames = analyzer.getDefUseFrames();

System.out.println("This method contains " + variables.length + " variables");
for (int i = 0; i < mn.instructions.size(); i++) {
  System.out.println("Instruction " + i + " contains definitions of " + frames[i].getDefinitions());
  System.out.println("Instruction " + i + " contains usage of " + frames[i].getUses());
}
```

## Definition-Use Chain

The following code exemplify how to compute `DefUseChain`
```java
MethodNode mn = ... // A regular MethodNode from ASM tree API
DefUseInterpreter interpreter = new DefUseInterpreter();
FlowAnalyzer<Value> flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
DefUseAnalyzer analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);
analyzer.analyze("package/ClassName", mn);

Variable[] variables = analyzer.getVariables();
DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
    analyzer.getDefUseFrames(),
    analyzer.getVariables(),
    flowAnalyzer.getSuccessors(),
    flowAnalyzer.getPredecessors());

System.out.println("This method contains " + chains.length + " Definition-Use Chains");
for (int i = 0; i < chains.length; i++) {
  DefUseChain chain = chains[i];
  System.out.println("Instruction " + chain.def + " define variable " + variables[chain.var]);
  System.out.println("Instruction " + chain.use + " uses variable " + variables[chain.var]);
  // There is a path between chain.def and chain.use that not redefine chain.var
}
```
