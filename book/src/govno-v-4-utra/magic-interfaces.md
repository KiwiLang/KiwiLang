# Magic Interfaces

В Kiwi есть некоторые интерфейсы, которые автоматически реализовываются компилятором.
Такие интерфейсы называются магическими.

```admonish info
Такие интерфейсы относятся к Interpret вариациям, так как
реализовывают compile-time атрибуты в экземпляре класса.
```

Как пример магического интерфейса можно привести `FieldKnown` интерфейс.
Он реализуется для всех классов с модификатором `final`.

```kiwi
final class A {
    a: Int
}  # A implements FieldKnown
```

Такой модификатор добавляет в класс `A` поле `__fields__` типа `List<Tuple<String, TypeAlias, *>>`.

```kiwi
for (fieldName, fieldType, fieldValue in A.__fields__) {
    print(fieldName, fieldType, fieldValue)
}
```

Пример использования:

```kiwi
extend interpret class <T: FieldKnown> T : toStringInterface {
    override interpret fun toString() -> String {
        result: String
        for (fieldName, fieldType, fieldValue in this.__fields__) {
            result += fieldName + ": " + fieldValue.toString() + ","
        }
        return result
    }
}
```

P.S: `extend` - это ключевое слово, которое позволяет расширять классы во время компиляции.

В данном примере мы расширяем класс `T` интерфейсом `toStringInterface`, что позволяет нам
выводить в консоль любой класс используя `print`, который реализует `FieldKnown` интерфейс.

```kiwi
final class A {
    a: Int,
    b: String
}

fun main() {
    a: A(1, "2")
    print(a)  # a: 1, b: 2,
}
```

Однако как уже было сказано, такая логика применима только к `final` классам.

```kiwi
class A {
    a: Int
}

fun main() {
    a: A(1)
    print(a)  # error: A doesn't implement FieldKnown, so it doesn't implement toStringInterface
}
```

```admonish note
Обратите внимание, что так как мы наследуемся от интерпретируемого интерфейса,
то и класс мы должны пометить как интерпретируемый.
```