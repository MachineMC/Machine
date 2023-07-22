# Machine Contribution Guide
To begin with, we want to express our gratitude for your willingness to
contribute to Machine project. Every contribution is highly valued and
much appreciated!

### Requirements
- JDK 17
- Gradle - *you can use the Gradle wrapper (gradlew or gradlew.bat)
  without the need for any installation or configuration*
- Git

### General Information
- Adhere to the project's style guide, which is comprehensively
described in the [STYLE_GUIDE.md](STYLE_GUIDE.md) file.
- Test your modifications carefully, make sure they don't cause
side effects that may cause unintentional behaviour.
- Contributions must not include any malicious code intended to cause harm to
  the end-user and contain confidential tokens, passwords, personal API keys,
  or any similar information.
- Finally, when you are ready to submit your pull request, make
  sure to follow the provided template.

#### Nullness
All variables that are either not annotated or annotated with `@NotNull` are
expected to be non-null.
Null value is allowed only for variables marked with `@Nullable`.
We encourage the use of the Optional for returning when dealing with nullable values.

### Specific Information

#### API
The API is designed after its forerunners. It prioritizes simplicity to
ensure ease for beginners, while offering enough advanced features for
more experienced developers. We draw inspiration for the design of new APIs
primarily from Bukkit, Sponge, Minestom, and Krypton.

The Machine API should be structured to evolve seamlessly
with new Minecraft versions. You should always weigh the potential use cases
against the maintenance expenses. If the downsides outweigh the
benefits, it is most likely not worth implementing the idea.

Make sure that all of your API code is documented with the correct use of code
contract annotations.

#### Server
The server is more complex than the API and does not follow many of the
same strict requirements, but there are still some guidelines
that you should attempt to follow.

The server is not designed to maintain backwards compatibility as it would
be expensive to maintain and severely limit the ability to evolve
across Minecraft versions.

When designing implementations, ensure that you:
- Follow the API specification.
- Test it to make sure that your code operates as intended.
- Avoid introducing any potentially problematic elements,
  such as unexpected exceptions.

### Licensing
In order to contribute code, it must be licensed under [**GPLv3**](LICENSE);
it is either your property, or you have express permission to provide
and license it to us.

Third-party code may be accepted in the following circumstances:
- It is under a compatible license and part of a public, freely-available resource.
- You have been granted permission to include it.

All code files should start with the [Machine project header](HEADER.txt).
For Java files, this is done automatically when the project is built.

### Lombok
Throughout the project, we use [Lombok](https://projectlombok.org/)
to minimize boilerplate code and improve its readability.
If you intend to use Lombok in your commitments, use only annotations
that do not modify how the code works, rather reduce boilerplate code.

**Banned Lombok Features**: `var`, `val`, `@NonNull`, `@SneakyThrows`,
`@ExtensionMethod`, `@FieldDefaults`, `@UtilityClass`, `@Helper`

Please be aware that we may decide to remove Lombok from the project
at some point in the future. Therefore, avoid overusing it when it is unnecessary.

### Code Contract Annotations
We use JetBrains code contract annotations to describe the behaviour of the code,
make sure you use them as well to keep the code consistent. Server implementation
doesn't need to be annotated precisely, for API it is required.

- Don't use `@NotNull` annotations if it's not to remove the warnings, all
  variables that are not marked with `@Nullable` are expected to be non-null.
- Don't use `@Nls` and `@NonNls` annotations, localization for Machine is not planned.
- Use `@Contract` for methods where it's suitable.
- Mark unmodifiable collections with `@Unmodifiable`.
- `@Pattern` annotation is banned.
