# Java 과제 정리

Java 기본 문법, 배열, 컬렉션, 파일 입출력, 객체지향, 추상 클래스, 인터페이스, 멀티스레드를 실습한 과제 모음이다.

각 과제는 문서와 실제 Java 구현 코드를 함께 확인할 수 있도록 정리한다.

## 기본 문법 · 조건문 · 반복문

| 과제        | 문서                                         | 코드                                                                               | 설명                                               |
| --------- | ------------------------------------------ | -------------------------------------------------------------------------------- | ------------------------------------------------ |
| 숫자 업다운 게임 | [up-down-game.md](./up-down-game.md)       | [🔗 UpDownGame.java](../../../assignments/src/main/java/UpDownGame.java)         | `Random`, `Scanner`, 조건문, 반복문을 활용해 숫자를 맞히는 콘솔 게임 |
| 자판기       | [vending-machine.md](./vending-machine.md) | [🔗 VendingMachine.java](../../../assignments/src/main/java/VendingMachine.java) | 돈 넣기, 음료 구매, 잔액 부족 처리, 종료 기능을 구현한 기본 자판기 프로그램    |

## 배열 · 2차원 배열

| 과제    | 문서                                             | 코드                                                                          | 설명                                                 |
| ----- | ---------------------------------------------- | --------------------------------------------------------------------------- | -------------------------------------------------- |
| 회원 관리 | [member-management.md](./member-management.md) | [🔗 membermanagement/](../../../assignments/src/main/java/membermanagement) | 2차원 배열로 회원 정보를 저장하고 추가, 조회, 수정, 삭제를 구현한 회원 관리 프로그램 |
| 빙고 게임 | [bingo-game.md](./bingo-game.md)               | [🔗 bingogame/](../../../assignments/src/main/java/bingogame)               | 2차원 배열을 활용해 사용자와 컴퓨터가 번갈아 숫자를 부르는 빙고 게임            |

## 컬렉션 · 파일 입출력

| 과제    | 문서                                       | 코드                                                                  | 설명                                                     |
| ----- | ---------------------------------------- | ------------------------------------------------------------------- | ------------------------------------------------------ |
| 가계부   | [account-book.md](./account-book.md)     | [🔗 accountbook/](../../../assignments/src/main/java/accountbook)   | `Map`과 `List`를 활용해 날짜별 지출 내역을 추가, 조회, 삭제하는 가계부 프로그램    |
| 가계부 2 | [account-book-2.md](./account-book-2.md) | [🔗 accountbook2/](../../../assignments/src/main/java/accountbook2) | File I/O를 활용해 날짜별 가계부 내역을 `.txt` 파일로 저장, 조회, 삭제하는 프로그램 |

## 자료구조 · 리스트 구현

| 과제 | 문서 | 코드 | 설명 |
|---|---|---|---|
| MyArrayList 구현 | [my-array-list.md](./my-array-list.md) | [🔗 myarraylist/](../../../assignments/src/main/java/myarraylist) | 배열 기반 리스트를 직접 구현하며 인덱스 접근과 맨 앞 삽입의 차이를 확인 |
| MyLinkedList 구현 | [my-linked-list.md](./my-linked-list.md) | [🔗 mylinkedlist/](../../../assignments/src/main/java/mylinkedlist) | 노드 연결 구조를 직접 구현하며 삽입, 삭제, 인덱스 접근의 동작 차이를 확인 |

## 객체지향 · 생성자 · 캡슐화

| 과제         | 문서                           | 코드                                                        | 설명                                         |
| ---------- | ---------------------------- | --------------------------------------------------------- | ------------------------------------------ |
| 반려동물 키우기   | [pet-game.md](./pet-game.md) | [🔗 petgame/](../../../assignments/src/main/java/petgame) | 클래스, 객체, 생성자, 캡슐화를 활용해 반려동물 상태를 관리하는 콘솔 게임 |
| 텍스트 RPG 전투 | [text-rpg.md](./text-rpg.md) | [🔗 textrpg/](../../../assignments/src/main/java/textrpg) | 객체끼리 공격하고 피해를 주고받는 턴제 RPG 전투 프로그램          |

## 객체지향 · 추상 클래스 · 인터페이스

| 과제      | 문서                                                 | 코드                                                                            | 설명                                                     |
| ------- | -------------------------------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------ |
| 자판기 2   | [vending-machine-2.md](./vending-machine-2.md)     | [🔗 vendingmachine2/](../../../assignments/src/main/java/vendingmachine2)     | 추상 클래스, 상속, 오버라이딩, 다형성을 활용해 자판기를 객체지향 구조로 개선           |
| 자판기 3   | [vending-machine-3.md](./vending-machine-3.md)     | [🔗 vendingmachine3/](../../../assignments/src/main/java/vendingmachine3)     | 인터페이스, 구현 클래스, 다형성을 활용해 자판기를 설계하고 추상 클래스 버전과 비교        |
| 회원 관리 2 | [member-management-2.md](./member-management-2.md) | [🔗 membermanagement2/](../../../assignments/src/main/java/membermanagement2) | `String[][]` 구조를 `Member` 객체 배열로 바꾸고 추상 클래스로 회원 등급을 구현 |
| 회원 관리 3 | [member-management-3.md](./member-management-3.md) | [🔗 membermanagement3/](../../../assignments/src/main/java/membermanagement3) | 인터페이스와 `default` 메서드를 활용해 회원 관리 구조를 구현하고 추상 클래스 버전과 비교 |

## 멀티스레드

| 과제     | 문서                               | 코드                                                            | 설명                                      |
| ------ | -------------------------------- | ------------------------------------------------------------- | --------------------------------------- |
| 달팽이 경주 | [snail-race.md](./snail-race.md) | 미구현 | 여러 달팽이를 각각 스레드로 실행해 동시에 경주하는 멀티스레드 프로그램 |

## 학습 흐름

1. 기본 문법, 조건문, 반복문으로 콘솔 프로그램을 구현한다.
2. 배열과 2차원 배열로 여러 데이터를 관리한다.
3. 컬렉션과 파일 입출력으로 데이터 저장 구조를 확장한다.
4. 배열 기반 리스트와 노드 기반 리스트를 직접 구현하며 자료구조의 동작 원리를 확인한다.
5. 클래스, 객체, 생성자, 캡슐화를 적용해 객체지향 구조를 연습한다.
6. 추상 클래스와 인터페이스를 각각 적용해 구조 차이를 비교한다.
7. 멀티스레드로 여러 작업이 동시에 실행되는 흐름을 확인한다.
