# Java LMS 학습 메모

## 컬렉션

### 컬렉션에 객체 저장

`List` 같은 컬렉션에는 객체도 저장할 수 있다.   
객체를 그대로 출력하면 기본 `toString()` 결과가 호출되어 클래스명과 해시값 형태로 보일 수 있다.   
객체의 필드값을 원하는 형태로 출력하려면 `toString()`을 오버라이딩해야 한다.

### `List<Integer>`를 `int[]`로 변환

`List<Integer>`는 `stream()`을 사용해 요소를 순차적으로 처리할 수 있다.   
`mapToInt()`는 각 `Integer` 요소를 기본형 `int`로 변환하며,   
`Integer::intValue`는 각 요소의 `intValue()` 메소드를 호출하는 메소드 참조 표현이다.   
마지막에 `toArray()`를 호출하면 변환된 값을 `int[]`로 반환한다.
