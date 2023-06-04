<!--- wide page --->

# How to contribute

Hi! We are really excited that you are interested in contributing to Kiwi documentation.
Before submitting your contribution though, please make sure to take a moment and read through the following guidelines:

- [Pull request guidelines](#pull-request-guidelines)
- [Project structure](#project-structure)
- [Ways to contribute](#ways-to-contribute)

> Big thanks to [all contributors](contributors.md)!
> You are awesome and you make Kiwi better.

## Pull request guidelines

- The `master` branch is just a snapshot of the latest stable release.
  All development should be done in dedicated branches.
  **Do not submit PRs against the `master` branch.**
- For each kind of contribution, there is a dedicated branch.
  For example, if you want to contribute to the documentation, you should submit your PR against the `docs` branch.
- If you are submitting a PR for a bug fix, please make sure to include a test that would fail without your fix.
- If you are submitting a PR for a new feature, please make sure to include tests for it.
  
## Project structure

The project is structured as follows:

```text
├── book
│   └── <DOCUMENTATION SOURCE>
├── docs
│   └── <DOCUMENTATION OUTPUT>
├── gradle
│   └── <GRADLE FILES>
├── src
│   └── <KIWI COMPILER SOURCE>
├── .gitattributes
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── LICENSE
├── README.md
└── settings.gradle.kts
```

```admonish note
Note that you can't make changes to the `src` and `book` directories at the same time.
Instead, use different branches for each of them.
```

## Ways to contribute

There are many ways to contribute to the project:

<div class="docs-card-container">

```admonish quote title="Improve Kiwi documentation" class="docs-card"
<a class="docs-card-link" href="documentation/overview.html">
<div class="docs-card-visit">
    {{ #template ../assets/documentation.svg }}
    <span>Visit tutorial</span>
</div>

Add new posts, improve existing ones, translate to other languages.
</a>
```

```admonish quote title="Discord community" class="docs-card"
<a class="docs-card-link" href="{{discord_invite}}">
<div class="docs-card-visit">
    {{ #template ../assets/discord.svg }}
    <span>Join us!</span>
</div>

Join to discuss Kiwi, ask questions, and connect with other users.
</a>
```

</div>

## Other ways to contribute

If you are currently using Kiwi, you can do the following:

- [Report a bug](report-a-bug.md) - Did you find a bug in language or compiler? Let us know!
- [Request a feature](request-a-feature.md) - Do you miss something in Kiwi? We will discuss it with you.
- [Contact us](contact-us.md) - Do you have any questions? We will be happy to answer them.
