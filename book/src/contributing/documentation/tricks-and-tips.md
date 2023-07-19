# Tricks and Tips

It's a collection of useful tricks, tips and tutorials.
The list updates regularly as we find new tricks and tips.

You can also contribute to this list by adding your own tricks and tips,
just [contact us](../contact-us.md), and we'll add it to the list.

## Links to other pages

Links can be relative to the current page or absolute.

```md
[link-text](/absolute-path/to-file.html)
[link-text](relative-path/to-file.md)
```

You can also use `#` to specify heading id.

```md
[link-text](/absolute-path/to-file.html#heading-id)
[link-text](relative-path/to-file.md#heading-id)
```

```admonish note
Absolute path starts with `/` and can't refer to md files, instead use `.html` postfix.
```

## Using admonitions

You can use admonitions to present information in a more appealing way
using `admonish` preprocessor.

Use the following syntax to create admonitions:

<pre><code class="language-md hljs markdown">&#96;&#96;&#96;admonish &#60;type&#62; [title="&#60;title&#62;"] [class="&#60;class&#62;"]
&#60;content&#62;
&#96;&#96;&#96;
</code></pre>

See example below:

<pre><code class="language-md hljs markdown">&#96;&#96;&#96;admonish warning
This is a note.

It can contain multiple lines.

&#60;strong&#62;
    Or even contain html tags or any other markdown features.
&#60;/strong&#62;
&#96;&#96;&#96;
</code></pre>

<blockquote>

```admonish warning
This is a note.

It can contain multiple lines.

<strong>
    Or even contain html tags or any other markdown features.
</strong>
```

</blockquote>

See the list of available admonitions [here](https://squidfunk.github.io/mkdocs-material/reference/admonitions/#supported-types).

## Using emojis

You can use unicode shortcodes to insert emojis instead
of searching for them on the internet.

It's possible by using `emojicodes` preprocessor, which uses [gemoji assets](https://github.com/github/gemoji).
Use the following syntax to insert emojis:

<pre><code class="language-md hljs markdown">&#58;&#60;shortcode&#62;&#58;
</code></pre>

See example below:

<pre><code class="language-md hljs markdown">&#58;rocket&#58;
</code></pre>

> :rocket:

See the list of available emojis [here](https://github.com/github/gemoji/blob/master/db/emoji.json).

## Wide page mode

If you want to make a page wider **by removing right sidebar** with navigation,
you can put **special comment** at the _top of the page_.

```md
<!--- wide page --->
```

In this mode, the **page will be wider** and **will not have right sidebar** with navigation.

## Inserting themed SVG

You can insert svg images that will change color depending on the theme of the website.

To do so, we are using `template` preprocessor.

<pre><code class="language-md hljs markdown">&#60;span class="colored-fill"&#62;
&#123;&#123; #template &#60;path-to-svg&#62; &#125;&#125;
&#60;/span&#62;
</code></pre>

See example below:

<pre><code class="language-md hljs markdown">&#60;span class="colored-fill"&#62;
&#123;&#123; #template ../../assets/rocket.svg &#125;&#125;
&#60;/span&#62;
</code></pre>

<blockquote>

<span class="colored-fill">
{{ #template ../../assets/rocket.svg }}
</span>

</blockquote>

All svg images are stored in `assets` folder of documentation source directory,
and before using them, make sure to _delete color_ attributes from svg files.

```admonish note
Note that the path is relative to the current file.
```

## Inserting images

You can insert images from `assets` folder of documentation source directory
by using markdown syntax.

```md
![image](/assets/favicon.png)
```

There are also some other ways to insert images.

```md
![image](relative-path/to-file.png)
![image](https://example.com/image.png)
```

```admonish note

<blockquote>

![image](/assets/favicon.png)

</blockquote>
