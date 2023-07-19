# Prerequisites

Before you can build the Kiwi documentation, you need to install the required\
software.

## Download and Install MdBook

To build the Kiwi documentation, you need to install [MdBook]({{mdbook_url}})
first and its plugins:

- [template]({{mdbook_template_url}}) - adds mixins
- [admonish]({{mdbook_admonish_url}}) - adds admonitions
- [catppuccino]({{mdbook_catppuccino_url}}) - adds new themes
- [variables]({{mdbook_variables_url}}) - adds macros

Here is an examples how to cargo for Linux distributions:

**Ubuntu**

```bash
sudo apt install cargo
```

**Arch Linux**

```bash
sudo pacman -S cargo
```

**Fedora**

```bash
sudo dnf install cargo
```

And after installing cargo, you can install MdBook and its plugins:

```bash
cargo install mdbook
cargo install mdbook-template
cargo install mdbook-admonish
cargo install mdbook-catppuccino
cargo install mdbook-variables
```

## Clone the Kiwi repository

Clone the Kiwi repository branch called `docs`:

```bash
git clone -b docs https://github.com/KiwiLang/KiwiLang.git
cd KiwiLang
```

Or you can download it 
as a zip file as barbarian from [repository]({{github_repository}}/tree/docs).

```admonish note
Clone the `docs` branch, not the `master` branch.
```
