parcelRequire=function(e,r,t,n){var i,o="function"==typeof parcelRequire&&parcelRequire,u="function"==typeof require&&require;function f(t,n){if(!r[t]){if(!e[t]){var i="function"==typeof parcelRequire&&parcelRequire;if(!n&&i)return i(t,!0);if(o)return o(t,!0);if(u&&"string"==typeof t)return u(t);var c=new Error("Cannot find module '"+t+"'");throw c.code="MODULE_NOT_FOUND",c}p.resolve=function(r){return e[t][1][r]||r},p.cache={};var l=r[t]=new f.Module(t);e[t][0].call(l.exports,p,l,l.exports,this)}return r[t].exports;function p(e){return f(p.resolve(e))}}f.isParcelRequire=!0,f.Module=function(e){this.id=e,this.bundle=f,this.exports={}},f.modules=e,f.cache=r,f.parent=o,f.register=function(r,t){e[r]=[function(e,r){r.exports=t},{}]};for(var c=0;c<t.length;c++)try{f(t[c])}catch(e){i||(i=e)}if(t.length){var l=f(t[t.length-1]);"object"==typeof exports&&"undefined"!=typeof module?module.exports=l:"function"==typeof define&&define.amd?define(function(){return l}):n&&(this[n]=l)}if(parcelRequire=f,i)throw i;return f}({"afUf":[function(require,module,exports) {
var global = arguments[3];
var e=arguments[3];function t(e){return e instanceof Map?e.clear=e.delete=e.set=function(){throw new Error("map is read-only")}:e instanceof Set&&(e.add=e.clear=e.delete=function(){throw new Error("set is read-only")}),Object.freeze(e),Object.getOwnPropertyNames(e).forEach(function(n){var i=e[n];"object"!=typeof i||Object.isFrozen(i)||t(i)}),e}var n=t,i=t;n.default=i;class r{constructor(e){void 0===e.data&&(e.data={}),this.data=e.data,this.isMatchIgnored=!1}ignoreMatch(){this.isMatchIgnored=!0}}function a(e){return e.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#x27;")}function s(e,...t){const n=Object.create(null);for(const i in e)n[i]=e[i];return t.forEach(function(e){for(const t in e)n[t]=e[t]}),n}const o="</span>",l=e=>!!e.kind;class c{constructor(e,t){this.buffer="",this.classPrefix=t.classPrefix,e.walk(this)}addText(e){this.buffer+=a(e)}openNode(e){if(!l(e))return;let t=e.kind;e.sublanguage||(t=`${this.classPrefix}${t}`),this.span(t)}closeNode(e){l(e)&&(this.buffer+=o)}value(){return this.buffer}span(e){this.buffer+=`<span class="${e}">`}}class u{constructor(){this.rootNode={children:[]},this.stack=[this.rootNode]}get top(){return this.stack[this.stack.length-1]}get root(){return this.rootNode}add(e){this.top.children.push(e)}openNode(e){const t={kind:e,children:[]};this.add(t),this.stack.push(t)}closeNode(){if(this.stack.length>1)return this.stack.pop()}closeAllNodes(){for(;this.closeNode(););}toJSON(){return JSON.stringify(this.rootNode,null,4)}walk(e){return this.constructor._walk(e,this.rootNode)}static _walk(e,t){return"string"==typeof t?e.addText(t):t.children&&(e.openNode(t),t.children.forEach(t=>this._walk(e,t)),e.closeNode(t)),e}static _collapse(e){"string"!=typeof e&&e.children&&(e.children.every(e=>"string"==typeof e)?e.children=[e.children.join("")]:e.children.forEach(e=>{u._collapse(e)}))}}class g extends u{constructor(e){super(),this.options=e}addKeyword(e,t){""!==e&&(this.openNode(t),this.addText(e),this.closeNode())}addText(e){""!==e&&this.add(e)}addSublanguage(e,t){const n=e.root;n.kind=t,n.sublanguage=!0,this.add(n)}toHTML(){return new c(this,this.options).value()}finalize(){return!0}}function h(e){return new RegExp(e.replace(/[-/\\^$*+?.()|[\]{}]/g,"\\$&"),"m")}function d(e){return e?"string"==typeof e?e:e.source:null}function f(...e){return e.map(e=>d(e)).join("")}function p(...e){return"("+e.map(e=>d(e)).join("|")+")"}function m(e){return new RegExp(e.toString()+"|").exec("").length-1}function b(e,t){const n=e&&e.exec(t);return n&&0===n.index}const E=/\[(?:[^\\\]]|\\.)*\]|\(\??|\\([1-9][0-9]*)|\\./;function x(e,t="|"){let n=0;return e.map(e=>{const t=n+=1;let i=d(e),r="";for(;i.length>0;){const e=E.exec(i);if(!e){r+=i;break}r+=i.substring(0,e.index),i=i.substring(e.index+e[0].length),"\\"===e[0][0]&&e[1]?r+="\\"+String(Number(e[1])+t):(r+=e[0],"("===e[0]&&n++)}return r}).map(e=>`(${e})`).join(t)}const w=/\b\B/,v="[a-zA-Z]\\w*",y="[a-zA-Z_]\\w*",N="\\b\\d+(\\.\\d+)?",R="(-?)(\\b0[xX][a-fA-F0-9]+|(\\b\\d+(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?)",_="\\b(0b[01]+)",k="!|!=|!==|%|%=|&|&&|&=|\\*|\\*=|\\+|\\+=|,|-|-=|/=|/|:|;|<<|<<=|<=|<|===|==|=|>>>=|>>=|>=|>>>|>>|>|\\?|\\[|\\{|\\(|\\^|\\^=|\\||\\|=|\\|\\||~",M=(e={})=>{const t=/^#![ ]*\//;return e.binary&&(e.begin=f(t,/.*\b/,e.binary,/\b.*/)),s({className:"meta",begin:t,end:/$/,relevance:0,"on:begin":(e,t)=>{0!==e.index&&t.ignoreMatch()}},e)},O={begin:"\\\\[\\s\\S]",relevance:0},A={className:"string",begin:"'",end:"'",illegal:"\\n",contains:[O]},L={className:"string",begin:'"',end:'"',illegal:"\\n",contains:[O]},I={begin:/\b(a|an|the|are|I'm|isn't|don't|doesn't|won't|but|just|should|pretty|simply|enough|gonna|going|wtf|so|such|will|you|your|they|like|more)\b/},j=function(e,t,n={}){const i=s({className:"comment",begin:e,end:t,contains:[]},n);return i.contains.push(I),i.contains.push({className:"doctag",begin:"(?:TODO|FIXME|NOTE|BUG|OPTIMIZE|HACK|XXX):",relevance:0}),i},B=j("//","$"),T=j("/\\*","\\*/"),S=j("#","$"),P={className:"number",begin:N,relevance:0},D={className:"number",begin:R,relevance:0},C={className:"number",begin:"\\b(0b[01]+)",relevance:0},H={className:"number",begin:N+"(%|em|ex|ch|rem|vw|vh|vmin|vmax|cm|mm|in|pt|pc|px|deg|grad|rad|turn|s|ms|Hz|kHz|dpi|dpcm|dppx)?",relevance:0},$={begin:/(?=\/[^/\n]*\/)/,contains:[{className:"regexp",begin:/\//,end:/\/[gimuy]*/,illegal:/\n/,contains:[O,{begin:/\[/,end:/\]/,relevance:0,contains:[O]}]}]},U={className:"title",begin:v,relevance:0},z={className:"title",begin:"[a-zA-Z_]\\w*",relevance:0},K={begin:"\\.\\s*[a-zA-Z_]\\w*",relevance:0},G=function(e){return Object.assign(e,{"on:begin":(e,t)=>{t.data._beginMatch=e[1]},"on:end":(e,t)=>{t.data._beginMatch!==e[1]&&t.ignoreMatch()}})};var V=Object.freeze({__proto__:null,MATCH_NOTHING_RE:w,IDENT_RE:v,UNDERSCORE_IDENT_RE:"[a-zA-Z_]\\w*",NUMBER_RE:N,C_NUMBER_RE:R,BINARY_NUMBER_RE:"\\b(0b[01]+)",RE_STARTERS_RE:k,SHEBANG:M,BACKSLASH_ESCAPE:O,APOS_STRING_MODE:A,QUOTE_STRING_MODE:L,PHRASAL_WORDS_MODE:I,COMMENT:j,C_LINE_COMMENT_MODE:B,C_BLOCK_COMMENT_MODE:T,HASH_COMMENT_MODE:S,NUMBER_MODE:P,C_NUMBER_MODE:D,BINARY_NUMBER_MODE:C,CSS_NUMBER_MODE:H,REGEXP_MODE:$,TITLE_MODE:U,UNDERSCORE_TITLE_MODE:z,METHOD_GUARD:K,END_SAME_AS_BEGIN:G});function W(e,t){"."===e.input[e.index-1]&&t.ignoreMatch()}function q(e,t){t&&e.beginKeywords&&(e.begin="\\b("+e.beginKeywords.split(" ").join("|")+")(?!\\.)(?=\\b|\\s)",e.__beforeBegin=W,e.keywords=e.keywords||e.beginKeywords,delete e.beginKeywords,void 0===e.relevance&&(e.relevance=0))}function X(e,t){Array.isArray(e.illegal)&&(e.illegal=p(...e.illegal))}function Z(e,t){if(e.match){if(e.begin||e.end)throw new Error("begin & end are not supported with match");e.begin=e.match,delete e.match}}function F(e,t){void 0===e.relevance&&(e.relevance=1)}const J=["of","and","for","in","not","or","if","then","parent","list","value"],Y="keyword";function Q(e,t,n=Y){const i={};return"string"==typeof e?r(n,e.split(" ")):Array.isArray(e)?r(n,e):Object.keys(e).forEach(function(n){Object.assign(i,Q(e[n],t,n))}),i;function r(e,n){t&&(n=n.map(e=>e.toLowerCase())),n.forEach(function(t){const n=t.split("|");i[n[0]]=[e,ee(n[0],n[1])]})}}function ee(e,t){return t?Number(t):te(e)?0:1}function te(e){return J.includes(e.toLowerCase())}function ne(e,{plugins:t}){function n(t,n){return new RegExp(d(t),"m"+(e.case_insensitive?"i":"")+(n?"g":""))}class i{constructor(){this.matchIndexes={},this.regexes=[],this.matchAt=1,this.position=0}addRule(e,t){t.position=this.position++,this.matchIndexes[this.matchAt]=t,this.regexes.push([t,e]),this.matchAt+=m(e)+1}compile(){0===this.regexes.length&&(this.exec=(()=>null));const e=this.regexes.map(e=>e[1]);this.matcherRe=n(x(e),!0),this.lastIndex=0}exec(e){this.matcherRe.lastIndex=this.lastIndex;const t=this.matcherRe.exec(e);if(!t)return null;const n=t.findIndex((e,t)=>t>0&&void 0!==e),i=this.matchIndexes[n];return t.splice(0,n),Object.assign(t,i)}}class r{constructor(){this.rules=[],this.multiRegexes=[],this.count=0,this.lastIndex=0,this.regexIndex=0}getMatcher(e){if(this.multiRegexes[e])return this.multiRegexes[e];const t=new i;return this.rules.slice(e).forEach(([e,n])=>t.addRule(e,n)),t.compile(),this.multiRegexes[e]=t,t}resumingScanAtSamePosition(){return 0!==this.regexIndex}considerAll(){this.regexIndex=0}addRule(e,t){this.rules.push([e,t]),"begin"===t.type&&this.count++}exec(e){const t=this.getMatcher(this.regexIndex);t.lastIndex=this.lastIndex;let n=t.exec(e);if(this.resumingScanAtSamePosition())if(n&&n.index===this.lastIndex);else{const t=this.getMatcher(0);t.lastIndex=this.lastIndex+1,n=t.exec(e)}return n&&(this.regexIndex+=n.position+1,this.regexIndex===this.count&&this.considerAll()),n}}if(e.compilerExtensions||(e.compilerExtensions=[]),e.contains&&e.contains.includes("self"))throw new Error("ERR: contains `self` is not supported at the top-level of a language.  See documentation.");return e.classNameAliases=s(e.classNameAliases||{}),function t(i,a){const s=i;if(i.isCompiled)return s;[Z].forEach(e=>e(i,a)),e.compilerExtensions.forEach(e=>e(i,a)),i.__beforeBegin=null,[q,X,F].forEach(e=>e(i,a)),i.isCompiled=!0;let o=null;if("object"==typeof i.keywords&&(o=i.keywords.$pattern,delete i.keywords.$pattern),i.keywords&&(i.keywords=Q(i.keywords,e.case_insensitive)),i.lexemes&&o)throw new Error("ERR: Prefer `keywords.$pattern` to `mode.lexemes`, BOTH are not allowed. (see mode reference) ");return o=o||i.lexemes||/\w+/,s.keywordPatternRe=n(o,!0),a&&(i.begin||(i.begin=/\B|\b/),s.beginRe=n(i.begin),i.endSameAsBegin&&(i.end=i.begin),i.end||i.endsWithParent||(i.end=/\B|\b/),i.end&&(s.endRe=n(i.end)),s.terminatorEnd=d(i.end)||"",i.endsWithParent&&a.terminatorEnd&&(s.terminatorEnd+=(i.end?"|":"")+a.terminatorEnd)),i.illegal&&(s.illegalRe=n(i.illegal)),i.contains||(i.contains=[]),i.contains=[].concat(...i.contains.map(function(e){return re("self"===e?i:e)})),i.contains.forEach(function(e){t(e,s)}),i.starts&&t(i.starts,a),s.matcher=function(e){const t=new r;return e.contains.forEach(e=>t.addRule(e.begin,{rule:e,type:"begin"})),e.terminatorEnd&&t.addRule(e.terminatorEnd,{type:"end"}),e.illegal&&t.addRule(e.illegal,{type:"illegal"}),t}(s),s}(e)}function ie(e){return!!e&&(e.endsWithParent||ie(e.starts))}function re(e){return e.variants&&!e.cachedVariants&&(e.cachedVariants=e.variants.map(function(t){return s(e,{variants:null},t)})),e.cachedVariants?e.cachedVariants:ie(e)?s(e,{starts:e.starts?s(e.starts):null}):Object.isFrozen(e)?s(e):e}var ae="10.7.3";function se(e){return Boolean(e||""===e)}function oe(e){const t={props:["language","code","autodetect"],data:function(){return{detectedLanguage:"",unknownLanguage:!1}},computed:{className(){return this.unknownLanguage?"":"hljs "+this.detectedLanguage},highlighted(){if(!this.autoDetect&&!e.getLanguage(this.language))return console.warn(`The language "${this.language}" you specified could not be found.`),this.unknownLanguage=!0,a(this.code);let t={};return this.autoDetect?(t=e.highlightAuto(this.code),this.detectedLanguage=t.language):(t=e.highlight(this.language,this.code,this.ignoreIllegals),this.detectedLanguage=this.language),t.value},autoDetect(){return!this.language||se(this.autodetect)},ignoreIllegals:()=>!0},render(e){return e("pre",{},[e("code",{class:this.className,domProps:{innerHTML:this.highlighted}})])}};return{Component:t,VuePlugin:{install(e){e.component("highlightjs",t)}}}}const le={"after:highlightElement":({el:e,result:t,text:n})=>{const i=ue(e);if(!i.length)return;const r=document.createElement("div");r.innerHTML=t.value,t.value=ge(i,ue(r),n)}};function ce(e){return e.nodeName.toLowerCase()}function ue(e){const t=[];return function e(n,i){for(let r=n.firstChild;r;r=r.nextSibling)3===r.nodeType?i+=r.nodeValue.length:1===r.nodeType&&(t.push({event:"start",offset:i,node:r}),i=e(r,i),ce(r).match(/br|hr|img|input/)||t.push({event:"stop",offset:i,node:r}));return i}(e,0),t}function ge(e,t,n){let i=0,r="";const s=[];function o(){return e.length&&t.length?e[0].offset!==t[0].offset?e[0].offset<t[0].offset?e:t:"start"===t[0].event?e:t:e.length?e:t}function l(e){r+="<"+ce(e)+[].map.call(e.attributes,function(e){return" "+e.nodeName+'="'+a(e.value)+'"'}).join("")+">"}function c(e){r+="</"+ce(e)+">"}function u(e){("start"===e.event?l:c)(e.node)}for(;e.length||t.length;){let t=o();if(r+=a(n.substring(i,t[0].offset)),i=t[0].offset,t===e){s.reverse().forEach(c);do{u(t.splice(0,1)[0]),t=o()}while(t===e&&t.length&&t[0].offset===i);s.reverse().forEach(l)}else"start"===t[0].event?s.push(t[0].node):s.pop(),u(t.splice(0,1)[0])}return r+a(n.substr(i))}const he={},de=e=>{console.error(e)},fe=(e,...t)=>{console.log(`WARN: ${e}`,...t)},pe=(e,t)=>{he[`${e}/${t}`]||(console.log(`Deprecated as of ${e}. ${t}`),he[`${e}/${t}`]=!0)},me=a,be=s,Ee=Symbol("nomatch"),xe=function(e){const t=Object.create(null),i=Object.create(null),a=[];let s=!0;const o=/(^(<[^>]+>|\t|)+|\n)/gm,l="Could not find the language '{}', did you forget to load/include a language module?",c={disableAutodetect:!0,name:"Plain text",contains:[]};let u={noHighlightRe:/^(no-?highlight)$/i,languageDetectRe:/\blang(?:uage)?-([\w-]+)\b/i,classPrefix:"hljs-",tabReplace:null,useBR:!1,languages:null,__emitter:g};function d(e){return u.noHighlightRe.test(e)}function f(e,t,n,i){let r="",a="";"object"==typeof t?(r=e,n=t.ignoreIllegals,a=t.language,i=void 0):(pe("10.7.0","highlight(lang, code, ...args) has been deprecated."),pe("10.7.0","Please use highlight(code, options) instead.\nhttps://github.com/highlightjs/highlight.js/issues/2277"),a=e,r=t);const s={code:r,language:a};O("before:highlight",s);const o=s.result?s.result:p(s.language,s.code,n,i);return o.code=s.code,O("after:highlight",o),o}function p(e,n,i,o){function c(e,t){const n=v.case_insensitive?t[0].toLowerCase():t[0];return Object.prototype.hasOwnProperty.call(e.keywords,n)&&e.keywords[n]}function g(){null!=R.subLanguage?function(){if(""===O)return;let e=null;if("string"==typeof R.subLanguage){if(!t[R.subLanguage])return void M.addText(O);e=p(R.subLanguage,O,!0,k[R.subLanguage]),k[R.subLanguage]=e.top}else e=m(O,R.subLanguage.length?R.subLanguage:null);R.relevance>0&&(A+=e.relevance),M.addSublanguage(e.emitter,e.language)}():function(){if(!R.keywords)return void M.addText(O);let e=0;R.keywordPatternRe.lastIndex=0;let t=R.keywordPatternRe.exec(O),n="";for(;t;){n+=O.substring(e,t.index);const i=c(R,t);if(i){const[e,r]=i;if(M.addText(n),n="",A+=r,e.startsWith("_"))n+=t[0];else{const n=v.classNameAliases[e]||e;M.addKeyword(t[0],n)}}else n+=t[0];e=R.keywordPatternRe.lastIndex,t=R.keywordPatternRe.exec(O)}n+=O.substr(e),M.addText(n)}(),O=""}function d(e){return e.className&&M.openNode(v.classNameAliases[e.className]||e.className),R=Object.create(e,{parent:{value:R}})}function f(e){return 0===R.matcher.regexIndex?(O+=e[0],1):(j=!0,0)}function E(e){const t=e[0],i=n.substr(e.index),a=function e(t,n,i){let a=b(t.endRe,i);if(a){if(t["on:end"]){const e=new r(t);t["on:end"](n,e),e.isMatchIgnored&&(a=!1)}if(a){for(;t.endsParent&&t.parent;)t=t.parent;return t}}if(t.endsWithParent)return e(t.parent,n,i)}(R,e,i);if(!a)return Ee;const s=R;s.skip?O+=t:(s.returnEnd||s.excludeEnd||(O+=t),g(),s.excludeEnd&&(O=t));do{R.className&&M.closeNode(),R.skip||R.subLanguage||(A+=R.relevance),R=R.parent}while(R!==a.parent);return a.starts&&(a.endSameAsBegin&&(a.starts.endRe=a.endRe),d(a.starts)),s.returnEnd?0:t.length}let x={};function w(t,a){const o=a&&a[0];if(O+=t,null==o)return g(),0;if("begin"===x.type&&"end"===a.type&&x.index===a.index&&""===o){if(O+=n.slice(a.index,a.index+1),!s){const t=new Error("0 width match regex");throw t.languageName=e,t.badRule=x.rule,t}return 1}if(x=a,"begin"===a.type)return function(e){const t=e[0],n=e.rule,i=new r(n),a=[n.__beforeBegin,n["on:begin"]];for(const r of a)if(r&&(r(e,i),i.isMatchIgnored))return f(t);return n&&n.endSameAsBegin&&(n.endRe=h(t)),n.skip?O+=t:(n.excludeBegin&&(O+=t),g(),n.returnBegin||n.excludeBegin||(O=t)),d(n),n.returnBegin?0:t.length}(a);if("illegal"===a.type&&!i){const e=new Error('Illegal lexeme "'+o+'" for mode "'+(R.className||"<unnamed>")+'"');throw e.mode=R,e}if("end"===a.type){const e=E(a);if(e!==Ee)return e}if("illegal"===a.type&&""===o)return 1;if(I>1e5&&I>3*a.index){throw new Error("potential infinite loop, way more iterations than matches")}return O+=o,o.length}const v=_(e);if(!v)throw de(l.replace("{}",e)),new Error('Unknown language: "'+e+'"');const y=ne(v,{plugins:a});let N="",R=o||y;const k={},M=new u.__emitter(u);!function(){const e=[];for(let t=R;t!==v;t=t.parent)t.className&&e.unshift(t.className);e.forEach(e=>M.openNode(e))}();let O="",A=0,L=0,I=0,j=!1;try{for(R.matcher.considerAll();;){I++,j?j=!1:R.matcher.considerAll(),R.matcher.lastIndex=L;const e=R.matcher.exec(n);if(!e)break;const t=w(n.substring(L,e.index),e);L=e.index+t}return w(n.substr(L)),M.closeAllNodes(),M.finalize(),N=M.toHTML(),{relevance:Math.floor(A),value:N,language:e,illegal:!1,emitter:M,top:R}}catch(B){if(B.message&&B.message.includes("Illegal"))return{illegal:!0,illegalBy:{msg:B.message,context:n.slice(L-100,L+100),mode:B.mode},sofar:N,relevance:0,value:me(n),emitter:M};if(s)return{illegal:!1,relevance:0,value:me(n),emitter:M,language:e,top:R,errorRaised:B};throw B}}function m(e,n){n=n||u.languages||Object.keys(t);const i=function(e){const t={relevance:0,emitter:new u.__emitter(u),value:me(e),illegal:!1,top:c};return t.emitter.addText(e),t}(e),r=n.filter(_).filter(M).map(t=>p(t,e,!1));r.unshift(i);const a=r.sort((e,t)=>{if(e.relevance!==t.relevance)return t.relevance-e.relevance;if(e.language&&t.language){if(_(e.language).supersetOf===t.language)return 1;if(_(t.language).supersetOf===e.language)return-1}return 0}),[s,o]=a,l=s;return l.second_best=o,l}const E={"before:highlightElement":({el:e})=>{u.useBR&&(e.innerHTML=e.innerHTML.replace(/\n/g,"").replace(/<br[ /]*>/g,"\n"))},"after:highlightElement":({result:e})=>{u.useBR&&(e.value=e.value.replace(/\n/g,"<br>"))}},x=/^(<[^>]+>|\t)+/gm,w={"after:highlightElement":({result:e})=>{u.tabReplace&&(e.value=e.value.replace(x,e=>e.replace(/\t/g,u.tabReplace)))}};function v(e){let t=null;const n=function(e){let t=e.className+" ";t+=e.parentNode?e.parentNode.className:"";const n=u.languageDetectRe.exec(t);if(n){const t=_(n[1]);return t||(fe(l.replace("{}",n[1])),fe("Falling back to no-highlight mode for this block.",e)),t?n[1]:"no-highlight"}return t.split(/\s+/).find(e=>d(e)||_(e))}(e);if(d(n))return;O("before:highlightElement",{el:e,language:n});const r=(t=e).textContent,a=n?f(r,{language:n,ignoreIllegals:!0}):m(r);O("after:highlightElement",{el:e,result:a,text:r}),e.innerHTML=a.value,function(e,t,n){const r=t?i[t]:n;e.classList.add("hljs"),r&&e.classList.add(r)}(e,n,a.language),e.result={language:a.language,re:a.relevance,relavance:a.relevance},a.second_best&&(e.second_best={language:a.second_best.language,re:a.second_best.relevance,relavance:a.second_best.relevance})}const y=()=>{if(y.called)return;y.called=!0,pe("10.6.0","initHighlighting() is deprecated.  Use highlightAll() instead."),document.querySelectorAll("pre code").forEach(v)};let N=!1;function R(){if("loading"===document.readyState)return void(N=!0);document.querySelectorAll("pre code").forEach(v)}function _(e){return e=(e||"").toLowerCase(),t[e]||t[i[e]]}function k(e,{languageName:t}){"string"==typeof e&&(e=[e]),e.forEach(e=>{i[e.toLowerCase()]=t})}function M(e){const t=_(e);return t&&!t.disableAutodetect}function O(e,t){const n=e;a.forEach(function(e){e[n]&&e[n](t)})}"undefined"!=typeof window&&window.addEventListener&&window.addEventListener("DOMContentLoaded",function(){N&&R()},!1),Object.assign(e,{highlight:f,highlightAuto:m,highlightAll:R,fixMarkup:function(e){return pe("10.2.0","fixMarkup will be removed entirely in v11.0"),pe("10.2.0","Please see https://github.com/highlightjs/highlight.js/issues/2534"),t=e,u.tabReplace||u.useBR?t.replace(o,e=>"\n"===e?u.useBR?"<br>":e:u.tabReplace?e.replace(/\t/g,u.tabReplace):e):t;var t},highlightElement:v,highlightBlock:function(e){return pe("10.7.0","highlightBlock will be removed entirely in v12.0"),pe("10.7.0","Please use highlightElement now."),v(e)},configure:function(e){e.useBR&&(pe("10.3.0","'useBR' will be removed entirely in v11.0"),pe("10.3.0","Please see https://github.com/highlightjs/highlight.js/issues/2559")),u=be(u,e)},initHighlighting:y,initHighlightingOnLoad:function(){pe("10.6.0","initHighlightingOnLoad() is deprecated.  Use highlightAll() instead."),N=!0},registerLanguage:function(n,i){let r=null;try{r=i(e)}catch(a){if(de("Language definition for '{}' could not be registered.".replace("{}",n)),!s)throw a;de(a),r=c}r.name||(r.name=n),t[n]=r,r.rawDefinition=i.bind(null,e),r.aliases&&k(r.aliases,{languageName:n})},unregisterLanguage:function(e){delete t[e];for(const t of Object.keys(i))i[t]===e&&delete i[t]},listLanguages:function(){return Object.keys(t)},getLanguage:_,registerAliases:k,requireLanguage:function(e){pe("10.4.0","requireLanguage will be removed entirely in v11."),pe("10.4.0","Please see https://github.com/highlightjs/highlight.js/pull/2844");const t=_(e);if(t)return t;throw new Error("The '{}' language is required, but not loaded.".replace("{}",e))},autoDetection:M,inherit:be,addPlugin:function(e){!function(e){e["before:highlightBlock"]&&!e["before:highlightElement"]&&(e["before:highlightElement"]=(t=>{e["before:highlightBlock"](Object.assign({block:t.el},t))})),e["after:highlightBlock"]&&!e["after:highlightElement"]&&(e["after:highlightElement"]=(t=>{e["after:highlightBlock"](Object.assign({block:t.el},t))}))}(e),a.push(e)},vuePlugin:oe(e).VuePlugin}),e.debugMode=function(){s=!1},e.safeMode=function(){s=!0},e.versionString=ae;for(const r in V)"object"==typeof V[r]&&n(V[r]);return Object.assign(e,V),e.addPlugin(E),e.addPlugin(le),e.addPlugin(w),e};var we=xe({});module.exports=we;
},{}],"tG1S":[function(require,module,exports) {
"use strict";var e=a(require("highlight.js/lib/core"));function a(e){return e&&e.__esModule?e:{default:e}}e.default.registerLanguage("kiwi",function(e){var a={className:"wrapper",begin:"(?<![ ])\\<(?!\\-)",end:"\\>",illegal:"[[](){}-+;?|&@#~!=/*^%:]",contains:[{className:"type",begin:"[a-zA-Z_][a-zA-Z0-9_]*"},{className:"compiletime-type",begin:"\\$[a-zA-Z_][a-zA-Z0-9_]*"},e.HASH_COMMENT_MODE]},n={className:"escape",begin:"\\\\[\\s\\S]",relevance:0},i={className:"reset",begin:"[^}]"},s={className:"format",begin:"\\$\\{",end:"\\}",contains:[i]},t={className:"format",begin:"\\{",end:"\\}",contains:[i,e.HASH_COMMENT_MODE]},l={className:"format",begin:"\\$"},c={className:"reset",begin:"(?<=\\$)[a-zA-Z_][a-zA-Z0-9_]*"},r={className:"string",begin:"'",end:"'",illegal:"\\n",contains:[n,s,l,c]},o={className:"string",begin:"f'",end:"'",illegal:"\\n",contains:[n,t,s,l,c]},g={className:"string",begin:"'''",end:"'''",contains:[n,s,l,c]},m={className:"string",begin:"f'''",end:"'''",contains:[n,t,s,l,c]},b={className:"string",begin:'"',end:'"',illegal:"\\n",contains:[n,s,l,c]},d={className:"string",begin:'f"',end:'"',illegal:"\\n",contains:[n,t,s,l,c]},N={className:"string",begin:'"""',end:'"""',contains:[n,s,l,c]},u={className:"string",begin:'f"""',end:'"""',contains:[n,t,s,l,c]};return{name:"Kiwi",case_insensitive:!0,keywords:{keyword:["package","import","as","trigger","if","else","for","while","when","do","break","continue","return","throw","try","catch","finally","fun","operator","infix","inline","extend","by","class","interface","object","enum","data","public","private","protected","internal","open","final","override","abstract","compiletime","interpret","in","!in"].join(" "),builtin:["print","log"].join(" "),literal:["true","false","none"].join(" "),operator:["+","-","*","/","%","==","!=","<","<=",">",">=","&&","||","!","=","+=","-=","*=","/=","%=","->","<-","++","--","(",")","[","]","{","}",".",":","?","?:"].join(" "),extension:["get","set","forEach","map","filter","reduce","fold","any","all","isEmpty","isNotEmpty","contains","containsAll","indexOf","lastIndexOf","toConstant","step","until","downTo","rangeTo","size","length","first","last","keys","values","indices","MAX_VALUE","MIN_VALUE"].join(" ")},contains:[e.HASH_COMMENT_MODE,{className:"package-name",begin:"(?<=package)[ ]+[a-zA-Z0-9_.]*(?!(\\n|#|as))"},{className:"package-name",begin:"(?<=import)[ ]+[a-zA-Z0-9_.*]*(?!(\\n|#|as))"},a,r,o,g,m,b,d,N,u,{className:"number",begin:"\\b0b[01]+"},{className:"number",begin:"\\b0o[0-7]+"},{className:"number",begin:"\\b0x[0-9a-fA-F]+"},{className:"number",begin:"\\b[0-9]+"},{className:"number",begin:"\\b[0-9]+\\.[0-9]*(e[+-]?\\d+)?(?![ ]*\\.)|\\b(?<!\\.)\\.[0-9]+(e[+-]?\\d+)?|\\b[0-9]+e[+-]?\\d+"},{className:"compiletime-type",begin:"(?<=(\\S:|->))[ ]*(?!\\$auto)\\$[a-zA-Z_][a-zA-Z0-9_]*(?!\\w*\\s*\\.)"},{className:"type",begin:"(?<=(\\S:|->))[ ]*(?!auto)[a-zA-Z_][a-zA-Z0-9_]*(?!\\w*\\s*\\.)"},{className:"keyword",begin:"((?!\\b)\\$a|\\ba)uto\\b"},{className:"parameter-delegation",begin:"(?<=by)[ ]+[a-zA-Z_][a-zA-Z0-9_]*"},{className:"compiletime-call",begin:"\\$[a-zA-Z_][a-zA-Z0-9_]*\\b"},{className:"ellipsis",begin:"(?<=\\s)\\.{3}"},{className:"selector",begin:"@[a-zA-Z_][a-zA-Z0-9_]*"},{className:"event",begin:"(?<=<-)[ ]*[a-zA-Z_][a-zA-Z0-9_]*"}]}}),window.addEventListener("load",function(a){document.querySelectorAll("code.language-kiwi").forEach(function(a){return e.default.highlightBlock(a)})});
},{"highlight.js/lib/core":"afUf"}]},{},["tG1S"], null)