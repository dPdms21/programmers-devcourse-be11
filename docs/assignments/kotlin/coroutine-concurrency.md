# 코루틴 복습 - 동시성

하나의 파일에 연습 함수를 순서대로 추가하면서 코루틴을 이용해 대기 작업을 겹쳐 실행하는 방법을 확인한다.

---

## 1. 구현 내용

상품 정보를 서버에서 조회하는 상황을 가정하고, 서버 응답 시간은 `delay()`로 표현한다.

```text
[문제 3] 순차 조회
상품1 / 상품2 / 상품3
걸린 시간: 1521ms

[문제 4] 동시 조회
상품1 / 상품2 / 상품3
걸린 시간: 512ms
```

두 경우 모두 동일한 결과를 출력하지만 실행 시간에는 차이가 발생한다.

이번 실습에서는 순차 실행과 동시 실행의 차이를 직접 확인하고, 코루틴을 이용해 여러 대기 작업을 동시에 처리하는 방법을 학습한다.

---

## 2. 학습 목표

| 개념                  | 활용                                  |
| ------------------- | ----------------------------------- |
| `suspend`           | 일시 중단 가능한 함수를 정의한다.                 |
| `delay`             | 스레드를 차단하지 않고 일정 시간 대기한다.            |
| `runBlocking`       | 일반 코드에서 코루틴 실행 영역을 만든다.             |
| `launch`            | 반환 결과가 필요하지 않은 코루틴을 실행한다.           |
| `async` / `await`   | 반환 결과가 필요한 작업을 동시에 실행하고 결과를 기다린다.   |
| `awaitAll`          | 여러 `Deferred`의 결과를 한 번에 기다린다.       |
| `coroutineScope`    | suspend 함수 내부에서 여러 코루틴을 구조적으로 실행한다. |
| `cancelAndJoin`     | 실행 중인 코루틴을 취소하고 종료까지 기다린다.          |
| `withTimeoutOrNull` | 작업에 시간 제한을 적용한다.                    |
| 구조화된 동시성            | 부모와 자식 코루틴 사이의 취소와 예외 전파를 확인한다.     |

---

## 3. 준비

### 3.1 의존성 확인

`build.gradle.kts`에 코루틴 의존성을 추가한다.

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}
```

의존성을 추가한 뒤 Gradle 설정을 새로고침한다.

---

### 3.2 실습 파일 생성

`src/main/kotlin/CoroutinePractice.kt`를 생성한다.

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun fetchName(id: Int): String {
    delay(500)
    return "상품$id"
}

suspend fun fetchPrice(id: Int): Int {
    delay(500)
    return id * 1000
}

fun main() {
}
```

`fetchName()`은 서버에서 상품 이름을 가져오는 상황을 가정하고 500ms 동안 대기한다.

`fetchPrice()`는 상품 가격을 가져오는 상황을 가정한다.

실행 시간은 `measureTimeMillis()`를 이용해 측정한다.

```kotlin
val time = measureTimeMillis {
    // 실행 시간을 측정할 코드
}

println("걸린 시간: ${time}ms")
```

---

## 4. 문제

### 문제 1. `suspend`가 필요한 이유

**목표**

`suspend` 함수가 필요한 이유와 `delay()`와 `Thread.sleep()`의 차이를 확인한다.

**확인 내용**

1. `fetchName()`에서 `suspend`를 제거한다.
2. 컴파일 오류를 확인한다.
3. 다시 `suspend`를 추가한다.
4. `suspend`를 제거한 상태에서 `delay(500)`을 `Thread.sleep(500)`으로 변경해 컴파일 여부를 확인한다.

```kotlin
suspend fun fetchName(id: Int): String {
    delay(500)
    return "상품$id"
}
```

`delay()`는 suspend 함수이므로 suspend 함수 또는 코루틴 영역에서 호출해야 한다.

따라서 일반 함수에서 `delay()`를 직접 호출할 수 없다.

반면 `Thread.sleep()`은 Java의 일반 함수이므로 suspend 함수가 아니어도 호출할 수 있다.

```kotlin
fun fetchName(id: Int): String {
    Thread.sleep(500)
    return "상품$id"
}
```

두 방식은 모두 일정 시간 대기하지만 동작 방식이 다르다.

`delay()`는 현재 코루틴을 일시 중단하고 스레드를 다른 작업에 사용할 수 있도록 한다.

`Thread.sleep()`은 지정한 시간 동안 현재 스레드를 차단한다.

---

### 문제 2. `runBlocking`과 `launch`

**목표**

`launch()`가 새로운 코루틴을 시작한 뒤 부모 코드의 실행을 계속하는 흐름을 확인한다.

```kotlin
fun ex2() = runBlocking {
    println("A")

    launch {
        delay(1000)
        println("B")
    }

    println("C")
}
```

실행 결과는 다음 순서가 된다.

```text
A
C
B
```

`launch()`는 코루틴을 시작하고 즉시 다음 코드로 진행한다.

따라서 `delay(1000)`이 포함된 자식 코루틴의 `B`보다 부모 코루틴의 `C`가 먼저 출력된다.

`runBlocking()`은 내부에서 실행한 자식 코루틴이 완료될 때까지 호출한 스레드를 차단하므로 `B`도 프로그램 종료 전에 출력된다.

`runBlocking()`을 제거하면 `launch()`를 호출할 `CoroutineScope`가 없어지므로 그대로 사용할 수 없다.

---

### 문제 3. 순차 실행

**목표**

suspend 함수를 연속해서 호출해도 자동으로 동시 실행되지 않는다는 것을 확인한다.

```kotlin
fun ex3() = runBlocking {
    val time = measureTimeMillis {
        val a = fetchName(1)
        val b = fetchName(2)
        val c = fetchName(3)

        println("$a / $b / $c")
    }

    println("걸린 시간: ${time}ms")
}
```

각 `fetchName()`이 500ms를 사용하므로 총 실행 시간은 약 1500ms가 된다.

```text
fetchName(1)
    ↓ 완료
fetchName(2)
    ↓ 완료
fetchName(3)
```

`suspend`는 함수가 일시 중단될 수 있음을 의미하지만 함수를 자동으로 동시에 실행시키지는 않는다.

두 번째 호출은 첫 번째 호출이 끝난 뒤 시작하며, 세 번째 호출 역시 두 번째 호출이 끝난 뒤 실행된다.

---

### 문제 4. `async`와 `await`

**목표**

세 개의 작업을 동시에 실행해 약 1500ms가 걸리던 작업을 약 500ms로 줄인다.

```kotlin
fun ex4() = runBlocking {
    val time = measureTimeMillis {
        val a = async {
            fetchName(1)
        }

        val b = async {
            fetchName(2)
        }

        val c = async {
            fetchName(3)
        }

        println(
            "${a.await()} / ${b.await()} / ${c.await()}"
        )
    }

    println("걸린 시간: ${time}ms")
}
```

`async()`는 결과를 바로 반환하는 대신 `Deferred<T>`를 반환한다.

```text
Deferred<String>
```

세 작업을 모두 `async()`로 실행한 뒤 `await()`를 호출하면 각 작업이 동시에 대기 상태로 진행된다.

```text
fetchName(1) ───── 500ms ─────┐
fetchName(2) ───── 500ms ─────┼─ 결과 반환
fetchName(3) ───── 500ms ─────┘
```

따라서 전체 실행 시간은 약 500ms가 된다.

다음과 같이 `async()`를 호출하자마자 `await()`하면 동시 실행의 효과를 얻을 수 없다.

```kotlin
val a = async {
    fetchName(1)
}.await()

val b = async {
    fetchName(2)
}.await()

val c = async {
    fetchName(3)
}.await()
```

첫 번째 결과를 기다린 뒤 두 번째 작업을 시작하기 때문에 다시 순차 실행이 된다.

---

### 문제 5. `awaitAll`

**목표**

여러 작업을 컬렉션 형태로 생성하고 한 번에 결과를 기다린다.

```kotlin
fun ex5() = runBlocking {
    val time = measureTimeMillis {
        val names =
            (1..10)
                .map { id ->
                    async {
                        fetchName(id)
                    }
                }
                .awaitAll()

        println(names)
    }

    println("걸린 시간: ${time}ms")
}
```

`map()`을 이용해 10개의 `Deferred<String>`을 생성한다.

```text
List<Deferred<String>>
```

`awaitAll()`은 모든 `Deferred`의 완료를 기다린 뒤 결과를 리스트로 반환한다.

```text
List<String>
```

각 작업이 500ms 동안 동시에 진행되므로 순차 실행 시 약 5000ms가 필요한 작업을 약 500ms에 처리할 수 있다.

동시 실행 수가 증가한다고 항상 동일한 실행 시간이 보장되는 것은 아니지만, 해당 실습처럼 독립적인 대기 작업에서는 실행 시간이 개별 대기 시간에 가까워지는 것을 확인할 수 있다.

---

### 문제 6. `coroutineScope`

**목표**

suspend 함수 내부에서 여러 코루틴을 동시에 실행하는 구조를 만든다.

먼저 다음과 같이 작성하면 컴파일 오류가 발생한다.

```kotlin
suspend fun fetchProduct(id: Int): String {
    val name = async {
        fetchName(id)
    }

    val price = async {
        fetchPrice(id)
    }

    return "${name.await()} (${price.await()}원)"
}
```

`async()`는 `CoroutineScope`의 확장 함수이므로 일반 suspend 함수의 본문에서 직접 사용할 수 없다.

`coroutineScope()`를 이용해 새로운 코루틴 스코프를 만든다.

```kotlin
suspend fun fetchProduct(
    id: Int
): String =
    coroutineScope {
        val name =
            async {
                fetchName(id)
            }

        val price =
            async {
                fetchPrice(id)
            }

        "${name.await()} (${price.await()}원)"
    }
```

실행 코드는 다음과 같다.

```kotlin
fun ex6() = runBlocking {
    val time =
        measureTimeMillis {
            println(
                fetchProduct(1)
            )
        }

    println(
        "걸린 시간: ${time}ms"
    )
}
```

상품 이름과 가격 조회가 동시에 실행되므로 약 500ms가 소요된다.

```text
상품1 (1000원)
```

`runBlocking()`은 일반 코드에서 코루틴 영역으로 진입할 때 사용한다.

이미 suspend 함수나 코루틴 내부에서 추가적인 자식 코루틴을 실행할 때는 `coroutineScope()`를 사용한다.

`coroutineScope()`의 마지막 식은 블록의 반환값이 된다.

---

### 문제 7. 취소와 타임아웃

#### 7.1 코루틴 취소

**목표**

실행 중인 코루틴을 취소하고 종료될 때까지 기다린다.

```kotlin
fun ex7() = runBlocking {
    val job =
        launch {
            repeat(10) { i ->
                println(
                    "작업 중 $i"
                )

                delay(200)
            }

            println(
                "이 줄은 실행되지 않는다"
            )
        }

    delay(500)

    job.cancelAndJoin()

    println(
        "취소 완료"
    )
}
```

`cancelAndJoin()`은 코루틴에 취소를 요청하고 실제 종료가 완료될 때까지 기다린다.

`delay()`는 취소 가능한 suspend 함수이므로 취소 요청을 확인하면 실행을 중단한다.

---

#### 7.2 타임아웃

3초가 걸리는 작업을 만든다.

```kotlin
suspend fun fetchSlow(): String {
    delay(3000)
    return "느린 응답"
}
```

`withTimeoutOrNull()`을 이용해 1초 제한을 적용한다.

```kotlin
val result =
    withTimeoutOrNull(1000) {
        fetchSlow()
    }

println(
    "결과: $result"
)
```

시간 안에 완료되지 않으면 `null`을 반환한다.

```text
결과: null
```

시간 내에 완료되면 정상 결과를 반환한다.

```kotlin
val ok =
    withTimeoutOrNull(1000) {
        fetchName(1)
    }

println(
    "여유 있을 때: $ok"
)
```

`withTimeoutOrNull()`은 제한 시간을 초과한 내부 코루틴을 취소한다.

`withTimeout()`은 제한 시간을 초과하면 예외를 발생시키고, `withTimeoutOrNull()`은 `null`을 반환한다.

```kotlin
val name =
    withTimeoutOrNull(1000) {
        fetchName(1)
    } ?: "이름 없음"
```

실패를 예외로 처리해야 하는 경우에는 `withTimeout()`을 사용하고, 기본값이나 대체 흐름으로 처리할 경우에는 `withTimeoutOrNull()`을 사용할 수 있다.

---

### 문제 8. `Thread.sleep()`과 `delay()`

**목표**

코루틴 내부에서 스레드를 차단하는 작업을 사용했을 때 동시 실행에 어떤 영향을 주는지 확인한다.

문제 5의 `fetchName()`을 다음과 같이 변경한다.

```kotlin
suspend fun fetchName(
    id: Int
): String {

    Thread.sleep(500)

    return "상품$id"
}
```

기존에는 다음과 같이 `delay()`를 사용했다.

```kotlin
suspend fun fetchName(
    id: Int
): String {

    delay(500)

    return "상품$id"
}
```

`delay()`는 현재 코루틴을 일시 중단하고 스레드를 다른 작업에 사용할 수 있도록 한다.

반면 `Thread.sleep()`은 현재 실행 중인 스레드 자체를 차단한다.

따라서 코루틴을 여러 개 실행하더라도 실행 중인 스레드가 차단되면 기대한 동시성을 얻지 못할 수 있다.

코루틴을 사용한다고 모든 코드가 자동으로 비차단 방식으로 동작하는 것은 아니다.

내부에서 사용하는 DB, 네트워크, 파일 I/O 등의 API가 blocking 방식인지도 함께 고려해야 한다.

코루틴 취소 역시 협조적으로 동작한다.

`delay()`와 같은 취소 가능한 suspend 함수는 취소 상태를 확인하지만, `Thread.sleep()`처럼 코루틴 취소와 무관한 blocking 코드는 같은 방식으로 즉시 중단되지 않는다.

---

### 문제 9. 구조화된 동시성

**목표**

자식 코루틴 하나에서 예외가 발생했을 때 부모와 다른 자식 코루틴에 어떤 영향을 주는지 확인한다.

```kotlin
fun ex9() = runBlocking {
    try {
        coroutineScope {
            launch {
                delay(1000)

                println(
                    "느린 작업 완료"
                )
            }

            launch {
                delay(100)

                throw RuntimeException(
                    "일부러 낸 오류"
                )
            }
        }
    } catch (
        e: Exception
    ) {
        println(
            "예외를 받았다: ${e.message}"
        )
    }
}
```

두 번째 자식 코루틴이 100ms 후 예외를 발생시킨다.

예외는 부모인 `coroutineScope()`로 전달되고, 같은 스코프에 속한 다른 자식 코루틴도 취소된다.

따라서 다음 문장은 실행되지 않는다.

```text
느린 작업 완료
```

외부의 `catch`에서는 자식 코루틴에서 발생한 예외를 처리할 수 있다.

```text
예외를 받았다: 일부러 낸 오류
```

이처럼 코루틴은 부모와 자식의 실행 관계를 구조적으로 관리한다.

하나의 자식 실패가 다른 자식의 실행을 취소하지 않아야 하는 경우에는 `supervisorScope()`와 같은 별도 구조를 사용할 수 있다.

---

## 5. 핵심 비교

### `launch`와 `async`

| 구분    | `launch`          | `async`       |
| ----- | ----------------- | ------------- |
| 주요 목적 | 결과가 필요하지 않은 작업 실행 | 결과가 필요한 작업 실행 |
| 반환값   | `Job`             | `Deferred<T>` |
| 결과 받기 | 별도 결과 없음          | `await()`     |
| 활용 예  | 로그 처리, 독립 작업      | 여러 API 결과 조합  |

---

### `delay`와 `Thread.sleep`

| 구분         | `delay`    | `Thread.sleep` |
| ---------- | ---------- | -------------- |
| 종류         | suspend 함수 | 일반 blocking 함수 |
| 스레드 차단     | 하지 않음      | 차단함            |
| 코루틴 취소와 연동 | 가능         | 직접 연동되지 않음     |
| 코루틴 환경     | 적합         | 주의 필요          |

---

### `runBlocking`과 `coroutineScope`

| 구분       | `runBlocking`  | `coroutineScope`          |
| -------- | -------------- | ------------------------- |
| 주요 목적    | 일반 코드에서 코루틴 실행 | suspend 함수 내부에서 자식 코루틴 실행 |
| 스레드 차단   | 함              | 하지 않음                     |
| 사용 위치    | 코루틴 외부 진입 지점   | 이미 코루틴 내부인 경우             |
| 자식 완료 대기 | 함              | 함                         |

---

### 순차 실행과 동시 실행

순차 실행은 앞의 작업 결과가 끝난 뒤 다음 작업을 시작한다.

```text
작업 1 ────── 500ms
                  작업 2 ────── 500ms
                                      작업 3 ────── 500ms

총 약 1500ms
```

동시 실행은 여러 작업을 먼저 시작한 뒤 결과를 기다린다.

```text
작업 1 ────── 500ms
작업 2 ────── 500ms
작업 3 ────── 500ms

총 약 500ms
```

독립적인 대기 작업을 동시에 실행할 수 있는 경우 전체 대기 시간을 줄일 수 있다.

---

## 6. 최종 체크리스트

* [ ] `suspend` 함수에서 `delay()`를 사용할 수 있는 이유를 설명할 수 있다.
* [ ] `delay()`와 `Thread.sleep()`의 차이를 설명할 수 있다.
* [ ] `runBlocking()`의 역할을 설명할 수 있다.
* [ ] `launch()`를 이용한 실행 흐름을 이해한다.
* [ ] suspend 함수를 연속 호출하면 순차 실행된다는 것을 확인한다.
* [ ] `async()`로 여러 작업을 먼저 실행한 뒤 `await()`할 수 있다.
* [ ] `async().await()`를 즉시 호출하면 다시 순차 실행될 수 있음을 이해한다.
* [ ] `awaitAll()`을 이용해 여러 작업의 결과를 함께 기다릴 수 있다.
* [ ] suspend 함수 내부에서 `coroutineScope()`를 이용해 자식 코루틴을 실행할 수 있다.
* [ ] `cancelAndJoin()`으로 코루틴을 취소하고 종료를 기다릴 수 있다.
* [ ] `withTimeoutOrNull()`로 실행 시간 제한을 적용할 수 있다.
* [ ] blocking 코드를 코루틴에서 그대로 사용했을 때의 영향을 이해한다.
* [ ] 구조화된 동시성에서 자식 예외가 부모와 형제 코루틴에 전파되는 흐름을 이해한다.
* [ ] 독립적인 자식 실패가 다른 작업에 영향을 주지 않아야 할 경우 `supervisorScope()`를 고려할 수 있다.

---

## 7. 정리

코루틴의 `suspend`는 함수를 자동으로 동시에 실행하게 만드는 기능이 아니라 실행 중 일시 중단이 가능하도록 만드는 문법이다.

여러 독립적인 작업을 동시에 수행하려면 `async`, `launch` 등의 코루틴 빌더를 사용해야 한다.

반환 결과가 필요한 여러 작업은 `async()`로 먼저 실행한 뒤 `await()` 또는 `awaitAll()`을 이용해 결과를 기다린다.

suspend 함수 내부에서 여러 자식 코루틴을 실행할 때는 `coroutineScope()`를 사용할 수 있으며, 이를 통해 부모와 자식 사이의 실행과 취소 관계가 구조적으로 관리된다.

`delay()`는 스레드를 차단하지 않지만 `Thread.sleep()`은 스레드를 직접 차단하므로 코루틴 코드에서도 blocking 작업의 사용 여부를 구분해야 한다.

취소와 타임아웃 역시 코루틴의 구조 안에서 처리되며, 자식 코루틴에서 발생한 예외는 부모와 같은 스코프의 다른 자식에게 영향을 줄 수 있다.

이를 통해 순차 실행과 동시 실행의 차이, 코루틴 스코프, 취소, 타임아웃, 구조화된 동시성의 기본 흐름을 확인한다.
