# Java 과제 정리

Java 기본 문법, 배열, 컬렉션, 파일 입출력, 객체지향, 자료구조, 그래프 탐색, 추상 클래스, 인터페이스, 멀티스레드를 실습한 과제 모음이다.

각 과제는 문서와 실제 Java 구현 코드를 함께 확인할 수 있도록 정리한다.

## 기본 문법 · 조건문 · 반복문

| 과제        | 문서                                         | 코드                                                                               | 설명                                               |
| --------- | ------------------------------------------ | -------------------------------------------------------------------------------- | ------------------------------------------------ |
| 숫자 업다운 게임 | [up-down-game.md](./up-down-game.md)       | [🔗 UpDownGame.java](../../../assignments/src/main/java/updowngame)         | `Random`, `Scanner`, 조건문, 반복문을 활용해 숫자를 맞히는 콘솔 게임 |
| 자판기       | [vending-machine.md](./vending-machine.md) | [🔗 VendingMachine.java](../../../assignments/src/main/java/vendingmachine) | 돈 넣기, 음료 구매, 잔액 부족 처리, 종료 기능을 구현한 기본 자판기 프로그램    |

## 배열 · 2차원 배열

| 과제    | 문서                                             | 코드                                                                          | 설명                                                 |
| ----- | ---------------------------------------------- | --------------------------------------------------------------------------- | -------------------------------------------------- |
| 회원 관리 | [member-management.md](./member-management.md) | [🔗 membermanagement/](../../../assignments/src/main/java/membermanagement) | 2차원 배열로 회원 정보를 저장하고 추가, 조회, 수정, 삭제를 구현한 회원 관리 프로그램 |
| 빙고 게임 | [bingo-game.md](./bingo-game.md)               | [🔗 bingogame/](../../../assignments/src/main/java/bingogame)               | 2차원 배열을 활용해 사용자와 컴퓨터가 번갈아 숫자를 부르는 빙고 게임            |

## 컬렉션 · 파일 입출력

| 과제    | 문서                                       | 코드                                                                  | 설명                                                     |
| ----- | ---------------------------------------- | ------------------------------------------------------------------- | ------------------------------------------------------ |
| 가계부   | [account-book.md](./account-book.md)     | [🔗 accountbook/](../../../assignments/src/main/java/accountbook)   | `Map`과 `List`를 활용해 날짜별 지출 내역을 추가, 조회, 삭제하는 가계부 프로그램    |
| 가계부 2 | [account-book-2.md](./account-book-2.md) | 미구현 | File I/O를 활용해 날짜별 가계부 내역을 `.txt` 파일로 저장, 조회, 삭제하는 프로그램 |

## 자료구조 · 직접 구현

| 과제            | 문서                                 | 코드                                                              | 설명                                          |
| ------------- | ---------------------------------- | --------------------------------------------------------------- | ------------------------------------------- |
| ArrayList 구현  | [array-list.md](./array-list.md)   | [🔗 arraylist/](../../../assignments/src/main/java/arraylist)   | 배열 기반 리스트를 직접 구현하며 인덱스 접근과 맨 앞 삽입의 차이를 확인   |
| LinkedList 구현 | [linked-list.md](./linked-list.md) | [🔗 linkedlist/](../../../assignments/src/main/java/linkedlist) | 노드 연결 구조를 직접 구현하며 삽입, 삭제, 인덱스 접근의 동작 차이를 확인 |
| HashMap 구현    | [hashmap.md](./hashmap.md)         | [🔗 hashmap/](../../../assignments/src/main/java/hashmap)       | 배열, 해시 함수, 체이닝을 활용해 키와 값의 저장, 조회, 삭제 구조를 구현 |
| 트리 구현         | [tree.md](./tree.md)               | [🔗 tree/](../../../assignments/src/main/java/tree)             | 이진 검색 트리를 구현하고 전위, 중위, 후위 순회의 방문 순서를 확인     |
| TreeMap 구현    | [treemap.md](./treemap.md)         | [🔗 treemap/](../../../assignments/src/main/java/treemap)       | 이진 검색 트리를 기반으로 키의 정렬, 조회, 삽입, 삭제 구조를 구현     |

## 객체지향 · 생성자 · 캡슐화

| 과제         | 문서                           | 코드                                                        | 설명                                         |
| ---------- | ---------------------------- | --------------------------------------------------------- | ------------------------------------------ |
| 반려동물 키우기   | [pet-game.md](./pet-game.md) | [🔗 petgame/](../../../assignments/src/main/java/petgame) | 클래스, 객체, 생성자, 캡슐화를 활용해 반려동물 상태를 관리하는 콘솔 게임 |
| 텍스트 RPG 전투 | [text-rpg.md](./text-rpg.md) | [🔗 textrpg/](../../../assignments/src/main/java/textrpg) | 객체끼리 공격하고 피해를 주고받는 턴제 RPG 전투 프로그램          |

## 객체지향 · 추상 클래스 · 인터페이스 · 컬렉션

| 과제      | 문서                                                 | 코드                                                                            | 설명                                                     |
| ------- | -------------------------------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------ |
| 자판기 2   | [vending-machine-2.md](./vending-machine-2.md)     | [🔗 vendingmachine2/](../../../assignments/src/main/java/vendingmachine2)     | 추상 클래스, 상속, 오버라이딩, 다형성을 활용해 자판기를 객체지향 구조로 개선           |
| 자판기 3   | [vending-machine-3.md](./vending-machine-3.md)     | [🔗 vendingmachine3/](../../../assignments/src/main/java/vendingmachine3)     | 인터페이스, 구현 클래스, 다형성을 활용해 자판기를 설계하고 추상 클래스 버전과 비교        |
| 회원 관리 2 | [member-management-2.md](./member-management-2.md) | [🔗 membermanagement2/](../../../assignments/src/main/java/membermanagement2) | `String[][]` 구조를 `Member` 객체 배열로 바꾸고 추상 클래스로 회원 등급을 구현 |
| 회원 관리 3 | [member-management-3.md](./member-management-3.md) | [🔗 membermanagement3/](../../../assignments/src/main/java/membermanagement3) | 인터페이스와 `default` 메서드를 활용해 회원 관리 구조를 구현하고 추상 클래스 버전과 비교 |
| 회원 관리 4 | [member-management-4.md](./member-management-4.md) | [🔗 membermanagement4/](../../../assignments/src/main/java/membermanagement4) | `List<Member>`, 제네릭, `enum`, 예외 처리를 적용해 회원 관리 구조를 확장   |

## 그래프 탐색

| 과제  | 문서                 | 코드                                                | 설명                                                    |
| --- | ------------------ | ------------------------------------------------- | ----------------------------------------------------- |
| BFS | [bfs.md](./bfs.md) | [🔗 bfs/](../../../assignments/src/main/java/bfs) | 인접 리스트, `Queue`, 방문 배열을 활용해 가까운 정점부터 탐색하는 너비 우선 탐색 구현 |
| DFS | [dfs.md](./dfs.md) | [🔗 dfs/](../../../assignments/src/main/java/dfs) | 인접 리스트, 재귀, `Stack`, 방문 배열을 활용해 한 경로를 끝까지 탐색하는 깊이 우선 탐색 구현    |

## 멀티스레드

| 과제        | 문서                                       | 코드                                                                    | 설명                                                                                 |
| --------- | ---------------------------------------- | --------------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| 달팽이 경주    | [snail-race.md](./snail-race.md)         | [🔗 snailrace/](../../../assignments/src/main/java/snailrace)         | 여러 달팽이를 각각 스레드로 실행하고 `synchronized`, `join()`을 활용해 경주 결과를 제어하는 프로그램    |
| 스레드 실행 제어 | [thread-control.md](./thread-control.md) | [🔗 threadcontrol/](../../../assignments/src/main/java/threadcontrol) | `sleep()`, `interrupt()`, `yield()`, `join()`을 실험하며 스레드 상태와 실행 제어 방식을 확인           |
| 세마포어 게임   | [semaphore-game.md](./semaphore-game.md) | [🔗 semaphoregame/](../../../assignments/src/main/java/semaphoregame) | `Semaphore`의 `acquire()`, `release()`, `tryAcquire()`를 활용해 던전의 동시 입장 인원을 제한하는 프로그램 |


## 학습 흐름

1. 기본 문법, 조건문, 반복문으로 콘솔 프로그램을 구현한다.
2. 배열과 2차원 배열로 여러 데이터를 관리한다.
3. 컬렉션과 파일 입출력으로 데이터 저장 구조를 확장한다.
4. 배열 기반 리스트와 노드 기반 리스트를 직접 구현해 데이터 저장 방식의 차이를 확인한다.
5. 해시 테이블과 체이닝을 구현하며 `HashMap`의 저장 및 탐색 원리를 이해한다.
6. 이진 검색 트리와 순회를 구현하며 트리 구조와 재귀 동작을 이해한다.
7. 이진 검색 트리를 기반으로 키를 정렬하는 `TreeMap`의 동작 원리를 확인한다.
8. 클래스, 객체, 생성자, 캡슐화를 적용해 객체지향 구조를 연습한다.
9. 추상 클래스와 인터페이스를 각각 적용해 구조 차이를 비교한다.
10. 컬렉션, 제네릭, `enum`, 예외 처리를 적용해 회원 관리 구조를 확장한다.
11. 인접 리스트와 방문 배열로 그래프를 표현하고, `Queue` 기반 BFS와 재귀 및 `Stack` 기반 DFS의 탐색 방식을 비교한다.
12. 멀티스레드 실행, 스레드 제어, 동기화, 세마포어를 적용해 여러 작업의 동시 실행과 공유 자원 접근을 제어한다.
