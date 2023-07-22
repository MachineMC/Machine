# Machine Style Guide

This guide provides instructions on the code style to follow when making
contributions to the Machine project. Its purpose is to prohibit poor
coding practices and suggest ways to minimize the need for extra maintenance.

Generally, we adhere to the formal [Sun code conventions for Java](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html),
subject to the following changes:
- Documentation Changes:
  - Documentation for constructors, fields, and packages is not mandatory.
  - Overridden methods don't need to be documented for classes that are not final.
  - Comments and documentation must be indented to the same level as the surrounding code.
- Code Changes:
  - Length:
    - The maximum line length is now 120 characters.
    - There is no maximum limit on the length of a method.
    - Only one statement is permitted per line.
    - There is no maximum parameter limit.
  - Switches:
    - Variable assignments inside switch cases are allowed.
    - Default case is not enforced.
    - Default case has to be the last case in the switch block.
  - Variables:
    - Multiple variables can be declared on a single line.
    - A local variable or parameter can have the same name as a field in the same class.
    - Local variables that do not change must be declared as final.
    - Enhanced for loop variables has to be declared as final.
    - Unused local variables are not permitted.
  - Annotations:
    - Override annotations are required.
    - Annotations have to follow compact, no array style.
  - Overloaded methods must be declared adjacent to each other.
  - Public, mutable, non-static fields are allowed.
  - Empty statements are not allowed.
  - Star imports are allowed.
  - Unnecessary parentheses are not permitted.
  - Boolean expressions have to be as simple as possible.
  - If a class overrides either the equals or hashCode methods, it must override both methods.
  - Capitalize acronyms in field, method, and class names, unless the name begins with the acronym.
    - Examples of naming to avoid: `getUuid`, `ID`, `setNbt`.
    - Examples of good naming: `getUUID`, `id`, `setNBT`.

To perform a style check on your code, execute the `other/checkstyleMain` task within the module containing the code.

### Recommendations
- It is recommended to minimize the use of labels as excessive labels can make it difficult to keep track of the nesting context.
- When chaining arguments, it is recommended to wrap them at the dot (.) to improve code readability;
  for example:
  ```java
  System.out.println(message
      .substring(0, 5)
      .toUpperCase()
      .concat(" ")
      .concat(message.substring(6))
  ```
- When splitting method or constructor arguments across multiple lines, it is recommended to keep the first argument on the same line as the method declaration;
  for example:
  ```java
  public ServerWorld(final File folder,
                     final Machine server,
                     final NamespacedKey name,
                     final DimensionType dimensionType,
                     final WorldType worldType,
                     final long seed) {
  ```
- When applying multiple annotations to the same element and spreading them across multiple lines,
  it is recommended to arrange them in ascending order based on their length:
  ```java
  @Something
  @SomethingLonger
  @SomethingEvenLonger
  private int myVariable;
  ```

- When creating getters and setters, getter method should always be declared before the setter method; when using Lombok `@Getter` and `@Setter` annotations for fields, it is recommended to place both annotations on the same line.