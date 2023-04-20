# Machine Contribution Guide
Greetings! It's great to see that you have chosen to access this document.
I hope you find it helpful.

To begin with, we want to express our gratitude for your willingness to
contribute to Machine project. Your participation is highly valued and
much appreciated!

### Requirements
To begin with, we assume that you possess adequate knowledge in Java
programming and have selected your preferred IDE.
However, if you lack proficiency in Java programming, we recommend that
you refer to the official [Java documentation](https://docs.oracle.com/en/java/).

Before continuing, please ensure that you have **JDK 17 and
Kotlin** installed on your system.

Subsequently, to kickstart your contributions,
you will require a few essential tools.
While most of these tools come pre-installed with your IDE,
some may require additional installation:
- Gradle - *In case your IDE does not support Gradle,
  fret not, you can use the Gradle wrapper (gradlew or gradlew.bat)
  without the need for any installation or configuration.*
- Git

### Safety Rules
- Contributions must not include any malicious code intended to cause harm to the user or their device.
- Under no circumstances should contributions contain confidential tokens, passwords, personal API keys, GitHub tokens, or any similar sensitive information.
- Contributions should avoid slowing or making the server unstable.

### General Information
Adhere to the project's prescribed style guide,
which is comprehensively detailed in the `STYLE_GUIDE.md` file.

It is imperative to test your modifications rigorously.
Go beyond testing just your alterations; test for any possible
side effects that may have unintentionally resulted from your changes and
avoid the frustration of breaking existing functionality.

Finally, when you are ready to submit your pull request,
kindly conform to the provided template.

#### Nullness
All variables that are either not annotated or annotated with `@NotNull` are
presumed to be non-null.

#### Other
- Prevent Stream API if not dealing with large collections of data, use classic loop instead.

### Specific Information

#### API
The API is fashioned after its forerunners. It prioritizes brevity
to ensure simplicity for novices, while still offering ample
advanced features for more seasoned developers.

We derive inspiration for designing new APIs mainly from Bukkit,
Sponge, Minestom, and Krypton, credits where credits due.

The Machine API should be structured to evolve seamlessly
with new Minecraft versions. You should always weigh the potential use cases
against the prospective maintenance expenses. If the downsides outweigh the
benefits, it is perhaps not worth pursuing. In addition, it is crucial to
document your code thoroughly.

Plugin design should rely heavily on dependency injection,
and avoid the use of static accessors wherever feasible.
However, some exceptions may exist.

#### Server
The server is a bit more intricate than the API and does not impose
many of the same strict requirements, but there are still some guidelines
that you should attempt to follow.

The server is not designed to maintain backward compatibility for its
dependents as it would be prohibitively expensive to maintain and severely
limit the ability to evolve across Minecraft versions.

Moreover, when designing implementations, it is imperative to ensure that you:
- Follow the API specification thoroughly.
- Conduct comprehensive testing to ensure that your code operates
  precisely as intended.
- Avoid introducing any potentially problematic elements,
   such as unexpected exceptions.

### Licensing
In order to contribute code, it must be licensed under **GPLv3** by you.
We assume that any code you contribute is either your own or that you
have explicit permission to license it to us.

Third-party code may be accepted in the following circumstances:
- It is under a compatible license and part of a public, freely-available resource.
- You have been granted permission to include it.

### Lombok
Throughout the project, we utilize [Lombok](https://projectlombok.org/)
to minimize boilerplate code and enhance the readability of certain areas.
If you intend to use Lombok in your commitments, it is essential that you
use only annotations that do not modify how the code works, rather
adds functionality and reduce extra code.

**Banned Lombok Features** *(Annotations can't be detected by checkstyle so make sure
you don't use these in your code)*

| Banned Feature       | Reason
| -------------------- | ---
| `var` and `val`      | They make the code readability worse.
| `@NonNull`           | We use contact annotations for that, prevention of NPE should be manual.
| `@SneakyThrows`      | We handle all exceptions manually due to Machine exception handling.
| `@ExtensionMethod`   | High impact on code style, hard to track the original source.
| `@FieldDefaults`     | Modifies the code, makes the readability terrible.
| `@UtilityClass`      | Modifies the code, doesn't really help much.
| `@Helper`            | Makes the code readability worse, doesn't really help much.
| `@StandardException` | Makes the code readability worse, doesn't really help much.

Please be aware that we may choose to remove Lombok from the project
at some point in the future. Therefore, avoid overusing it when it is unnecessary.

### Code Contract Annotations
We use JetBrains code contract annotations to describe the behaviour of the code,
make sure you use them as well to keep the code consistent. Server implementation
doesn't need to be annotated precisely, for API it is required.

- Don't use `@NotNull` annotations if it's not to remove the warning, all variables
that are not marked with `@Nullable` are expected to be non-null. 
- Don't use `@Nls` and `@NonNls` annotations, localization for Machine is not planned.
- Use `@Contract` for methods where it's suitable.
- For unmodifiable collections use `@Unmodifiable`.
- From `ApiStatus` usage of only `@NonExtendable` and `@Internal` is allowed.
- `@Pattern` annotation is banned.
