mermaid.initialize({startOnLoad: true});
document.querySelectorAll('.mermaid').forEach(function (switchElement) {
    mermaid.init(undefined, switchElement);
});
document.querySelectorAll('.mermaid-outer').forEach(
    function (outer) {
        let inner = outer.nextElementSibling;
        while (inner && inner.classList.contains('mermaid-inner')) {
            outer.innerHTML += inner.outerHTML;
            inner.remove();
            inner = inner.nextElementSibling;
        }
    });