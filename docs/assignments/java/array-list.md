# 배열로 ArrayList 직접 만들기

## 1. 목표

배열 기반 리스트를 직접 구현하면서 인덱스 접근과 맨 앞 삽입의 차이를 이해한다.

`get`은 인덱스로 바로 접근할 수 있기 때문에 빠르다. 반면 `addFirst`는 기존 요소를 한 칸씩 뒤로 밀어야 하므로 상대적으로 느리다.

이 과제에서는 배열 기반 리스트의 내부 동작을 직접 구현하며 `ArrayList`의 기본 원리를 확인한다.

---

## 2. 준비물

`MyArrayListTest.java` 스켈레톤 코드를 IDE에 추가한다.

`main` 메서드는 미리 작성되어 있으므로 `TODO` 부분만 단계별로 채운다. 각 단계를 구현한 뒤 프로그램을 실행하여 결과를 확인한다.

---

## 3. 핵심 개념

* 데이터를 `String[] arr`에 물리적으로 나란히 저장한다.
* `size`는 현재 저장된 데이터 개수를 의미한다.
* 인덱스 접근은 `arr[index]`로 바로 접근하므로 빠르다.
* 맨 앞 삽입은 기존 요소를 모두 한 칸씩 뒤로 밀어야 하므로 느리다.
* 배열은 크기가 고정되어 있으므로 공간이 부족하면 더 큰 배열을 새로 만들어 기존 데이터를 옮겨야 한다.

---

## 4. 단계별 구현

### Step 1. 필드 이해

다음 두 필드의 역할을 확인한다.

```java
private String[] arr = new String[10];
private int size = 0;
```

`arr`는 실제 데이터를 저장하는 배열이다.

`size`는 현재 배열에 저장된 데이터 개수를 의미한다.

---

### Step 2. `addLast(String data)` 구현

리스트의 마지막 위치에 데이터를 추가한다.

구현 조건은 다음과 같다.

1. 공간이 부족한지 확인한다.
2. `size` 위치에 `data`를 저장한다.
3. `size`를 1 증가시킨다.

확인 기준은 다음과 같다.

```text
"가", "나", "다"를 addLast 한 뒤 size()가 3이면 성공
```

---

### Step 3. `get(int index)` 구현

전달받은 `index` 위치의 값을 반환한다.

구현 조건은 다음과 같다.

1. `arr[index]` 값을 반환한다.

---

### Step 4. `size()` 구현

현재 저장된 데이터 개수를 반환한다.

구현 조건은 다음과 같다.

1. `size` 값을 반환한다.

확인 기준은 다음과 같다.

```text
size = 3
0,1,2 = 가, 나, 다
```

---

### Step 5. `ensureCapacity()` 구현

배열이 가득 찼을 때 배열 크기를 2배로 늘린다.

구현 조건은 다음과 같다.

1. `size`와 `arr.length`가 같은지 확인한다.
2. 배열이 가득 찼다면 `Arrays.copyOf()`를 사용해 배열 크기를 2배로 늘린다.

```java
arr = Arrays.copyOf(arr, arr.length * 2);
```

배열은 한 번 생성하면 크기를 바꿀 수 없다. 따라서 더 많은 데이터를 저장하려면 더 큰 배열을 새로 만들고 기존 데이터를 복사해야 한다.

---

### Step 6. `addFirst(String data)` 구현

리스트의 맨 앞에 데이터를 추가한다.

구현 조건은 다음과 같다.

1. `ensureCapacity()`를 호출한다.
2. 마지막 요소부터 앞으로 이동하며 기존 요소를 한 칸씩 뒤로 민다.
3. `arr[0]`에 새 데이터를 저장한다.
4. `size`를 1 증가시킨다.

확인 기준은 다음과 같다.

```text
"가", "나", "다" 뒤에 addFirst("앞")을 호출하면
0번 인덱스는 "앞", 1번 인덱스는 "가"가 된다.
```

생각해볼 점은 다음과 같다.

```text
요소가 100만 개일 때 addFirst를 호출하면 몇 번의 이동이 발생하는가?
```

---

## 5. 선택 도전 과제

1. **중간 삽입**: `insert(int index, String data)`를 구현해 원하는 위치에 데이터 삽입
2. **중간 삭제**: `remove(int index)`를 구현해 원하는 위치의 데이터 삭제
3. **인덱스 검증**: 잘못된 인덱스가 들어오면 예외 처리
4. **전체 출력**: 현재 저장된 데이터를 순서대로 출력하는 `printAll()` 메서드 추가

---

## 6. 제출 및 확인

`MyArrayListTest.java`를 실행했을 때 다음과 같이 출력되면 기본 기능 구현이 완료된 것이다.

```text
size = 3
0,1,2 = 가, 나, 다
addFirst 후 0,1 = 앞, 가
size = 4
```

---

## 7. 스켈레톤 코드

```java
public class A_collections_list_ex_array {  
    public static void main(String[] args) {  
        MyArrayList list = new MyArrayList();  
  
        // --- Step 2~4 확인 ---  
        list.addLast("가");  
        list.addLast("나");  
        list.addLast("다");  
        System.out.println("size = " + list.size());                 // 기대: 3  
        System.out.println("0,1,2 = " + list.get(0) + ", "  
                + list.get(1) + ", "  
                + list.get(2));                // 기대: 가, 나, 다  
  
        // --- Step 6 확인 ---        list.addFirst("앞");  
        System.out.println("addFirst 후 0,1 = " + list.get(0) + ", " + list.get(1)); // 기대: 앞, 가  
        System.out.println("size = " + list.size());                 // 기대: 4  
    }  
}  
  
  
class MyArrayList {  
  
    // [Step 1] 필드 (작성돼 있음): 데이터를 담을 배열과 현재 개수  
    private String[] arr = new String[10];  
    private int size = 0;  
  
    // [Step 2] 맨 뒤에 추가  
    void addLast(String data) {  
        // TODO: 빈 끝자리(size 위치)에 data를 넣고, size를 1 늘리세요.  
    }  
  
    // [Step 3] 인덱스로 꺼내기  
    String get(int index) {  
        // TODO: index 위치의 값을 반환하세요.  
        return null;  
    }  
  
    // [Step 4] 현재 개수  
    int size() {  
        // TODO: size를 반환하세요.  
        return 0;  
    }  
  
    // [Step 5] 공간이 꽉 찼으면 배열을 2배로 늘리기  
    private void ensureCapacity() {  
        // TODO: size가 arr.length와 같으면  
        //       arr = Arrays.copyOf(arr, arr.length * 2); 로 교체하세요.  
    }  
  
    // [Step 6] 맨 앞에 추가  ★핵심★  
    void addFirst(String data) {  
        // TODO: 1) ensureCapacity() 호출  
        //       2) for 문으로 맨 뒤에서부터 arr[i] = arr[i - 1] 한 칸씩 밀기  
        //       3) arr[0] = data;  그리고 size++  
    }  
  
    // [도전] index 위치에 삽입  
    void insert(int index, String data) {  
        // TODO (도전): index 뒤의 요소들을 한 칸씩 밀고, 그 자리에 data를 넣으세요.  
    }  
  
    // [도전] index 위치 삭제  
    void remove(int index) {  
        // TODO (도전): index 뒤의 요소들을 한 칸씩 앞으로 당기고 size--  
    }  
}
```
