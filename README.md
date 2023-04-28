![banner](.github/assets/text_banner_dark.png#gh-dark-mode-only)
![banner](.github/assets/text_banner_light.png#gh-light-mode-only)

# Machine

[![license](https://img.shields.io/github/license/machinemc/machine?style=for-the-badge&color=657185)](../LICENSE)

Machine is a Minecraft Server software that is completely free and open source.
It was created from scratch without any code from Mojang.

One key difference between vanilla Minecraft servers and Machine is that our
software does not come with any default features. However, we offer a
comprehensive API that allows you to easily create anything you desire.

**[!]** Right now Machine is heavily in development and is not ready to use in
a production environment.

# Table of contents
* [Why Machine](#why-machine)
* [Building](#building)
* [Credits](#credits)
* [Contributing](#contributing)
* [License](#license)

## Why Machine

Machine does not come with any default features,
but instead offers a complete API that allows users to easily create their
own custom features and modifications. This flexibility is beneficial for users who require high
performance and greater control over their Minecraft servers.

Machine is a free and open source Minecraft Server software,
which means that anyone can download, use, modify, and distribute its code
without any cost or restriction.

Machine's codebase is clean and straightforward, making it easier for
users to understand how the server works and to modify it as needed.

**Advantages:**
* Remove the overhead of vanilla features
* Multi-threaded
* Open-source
* Modern API

**Disadvantages:**
* Does not work with Bukkit plugins
* Does not work with older clients
* Bad for vanilla experience
* It takes longer to develop something playable

## Building
* Configure ITJ Gradle to use JDK 17.
* Reload all gradle projects & run newly created task `buildAll` in group `build` of the root Machine project.

## Credits
* The [contributors](https://github.com/MachineMC/Machine/graphs/contributors) of the project. ❤️
* [The Minecraft Coalition](https://wiki.vg/) and [`#mcdevs`](https://github.com/mcdevs) - protocol and file formats research.
* [The Minecraft Wiki](https://minecraft.gamepedia.com/Minecraft_Wiki) for all their useful info.
* [Minestom](https://github.com/Minestom) and [Krypton](https://github.com/KryptonMC) projects for inspiration when designing both api and implementation.

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) file fore more information about contributing and project guidelines.

## License
Machine is free software licensed under the [GPUv3 license](LICENSE).

![bottom](.github/assets/bottom.png)