# MdBook

In order to build the book we are using Rust's documentation engine.

```admonish note
Before get started, you must be familiar with [Markdown]({{markdown_url}}).
```

## Build the book

Make sure you've made the [prerequisites](prerequisites.md) first.

After that, you can build the book in source directory:

```bash
mdbook serve book --open
```

Check if all is fine by opening `http://localhost:3000` if it's not opened automatically.

## Structure

The book is structured as follows:

```text
book
├── dist
│   └── <javascript parcel files>
├── src
│   ├── assets
│   │   └── <images>
│   ├── <chapters>
│   │   └── <markdown files>
│   └── SUMMARY.md
├── theme
│   ├── <css files>
│   └── index.hbs
├── book.toml
└── <javascript files>
```

### `book.toml`

This file contains the configuration of the book:

- `book.title` - the title of the book
- `book.description` - the description of the book
- `booklanguage` - the language of the book
- `book.author` - the authors of the book
- `book.multilingual` - if the book is multilingual
- `book.src` - the source directory
- `build.build-dir` - the build directory
- `output.html` - the output options
- `preprocessor` - the plugins
- `preprocessor.variables.variables` - the variables of the book

_Add yourself to the `author` field if you want to contribute to the book._

### `src/SUMMARY.md`

This file contains the table of contents of the book. Each
page or chapter must be added to this file, otherwise it won't
be included in the book.

You can read more about the syntax of this file [here]({{mdbook_summary_url}}).

### `src/<chapters>`

This directory contains the chapters of the book. Each chapter
