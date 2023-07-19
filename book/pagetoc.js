// Un-active everything when you click it
Array.prototype.forEach.call(document.getElementsByClassName("pagetoc")[0].children, function (el) {
    el.onclick = () => {
        Array.prototype.forEach.call(document.getElementsByClassName("pagetoc")[0].children, function (el) {
            el.classList.remove("active")
        })
        el.classList.add("active")
    }
})

const updateFunction = function () {
    let id
    const elements = document.getElementsByClassName("header")
    Array.prototype.forEach.call(elements, function (el) {
        if (window.pageYOffset >= el.offsetTop) {
            id = el
        }
    })

    Array.prototype.forEach.call(document.getElementsByClassName("pagetoc")[0].children, function (el) {
        if (id === undefined) return
        el.classList.remove("active")
    })

    Array.prototype.forEach.call(document.getElementsByClassName("pagetoc")[0].children, function (el) {
        if (id === undefined) return
        if (id.href.localeCompare(el.href) === 0) {
            el.classList.add("active")
        }
    })
}

// Populate sidebar on load
window.addEventListener('load', function () {
    const pagetoc = document.getElementsByClassName("pagetoc")[0]
    const elements = document.getElementsByClassName("header")
    Array.prototype.forEach.call(elements, function (el) {
        const link = document.createElement("a");
        let indent = ""
        switch (el.parentElement.tagName) {
            case "H2":
                indent = "20px"
                break
            case "H3":
                indent = "40px"
                break
            case "H4":
                indent = "60px"
                break
            case "H5":
                indent = "80px"
                break
            case "H6":
                indent = "100px"
                break
            default:
                break
        }
        link.appendChild(document.createTextNode(el.text))
        link.style.paddingLeft = indent
        link.href = el.href
        pagetoc.appendChild(link)
    })
    updateFunction.call()
})

window.addEventListener("scroll", updateFunction)