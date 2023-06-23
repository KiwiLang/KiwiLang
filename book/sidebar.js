window.addEventListener("load", (_) => {
    document.querySelector('.sidebar-scrollbox').querySelectorAll('ol > li').forEach((item) => {
        let nextItem = item.nextElementSibling
        if (nextItem && !nextItem.classList.contains('chapter-item')) {
            if (item.querySelector('a')) {
                let strong = item.querySelector('a > strong')
                strong.innerHTML = '⋄ ' + strong.innerHTML
                nextItem.style.display = 'none'
                strong.classList.add('chapter-mark')
                item.classList.add('chapter-list-item-static')
                return
            }
            let strong = item.querySelector('div > strong')
            let state = sessionStorage.getItem(strong.innerHTML)
            if (state && state === "display") {
                nextItem.style.display = 'inherit'
                strong.innerHTML = '▾ ' + strong.innerHTML
            }
            else {
                nextItem.style.display = 'none'
                strong.innerHTML = '▸ ' + strong.innerHTML
            }
            strong.classList.add('chapter-mark')
            item.onclick = (_) => {
                if (nextItem.style.display === 'none') {
                    nextItem.style.display = 'inherit'
                    strong.innerHTML = '▾ ' + strong.innerHTML.slice(2)
                    sessionStorage.setItem(strong.innerHTML.slice(2), "display")
                }
                else {
                    nextItem.style.display = 'none'
                    strong.innerHTML = '▸ ' + strong.innerHTML.slice(2)
                    sessionStorage.removeItem(strong.innerHTML.slice(2))
                }
            }
            item.classList.add('chapter-list-item')
        }
    })
    let item = document.querySelector('.chapter li a.active')
    let parent = item.parentNode
    if (parent && parent.classList.contains('chapter-list-item-static')) {
        parent.nextElementSibling.style.display = 'inherit'
    }
    while (item) {
        let prevItem = item.previousElementSibling
        if (item.tagName === 'LI' && !item.classList.contains('chapter-item')) {
            item.style.display = 'inherit'
            if (!prevItem.classList.contains('chapter-list-item-static')) {
                let strong = prevItem.querySelector('strong')
                strong.innerHTML = '▾ ' + strong.innerHTML.slice(2)
            }
        }
        item = item.parentNode;
    }
    document.querySelector('main').childNodes.forEach((element) => {
        if (element && element.nodeType === Node.COMMENT_NODE) {
            let content = element.textContent.replace(/^[ -]*|[ -]*$/g, '');
            if (content === 'wide page') {
                document.querySelector('main').classList.add('wide-page')
            }
        }
    })
})


