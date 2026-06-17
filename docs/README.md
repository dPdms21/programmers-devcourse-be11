# Documentation

과제 문서와 실습 이해를 돕기 위한 보조자료를 정리한다.

## Directory Guide

| Path                                            | Description           |
| ----------------------------------------------- | --------------------- |
| [assignments/java/](./assignments/java)         | Java 과제 설명 및 구현 안내 문서 |
| [practice/collections/](./practice/collections) | Java 실습 보조자료          |

## 과제 보조자료

| 주제                        | 자료                                                                       | 설명                                                      |
| ------------------------- | ------------------------------------------------------------------------ | ------------------------------------------------------- |
| HashMap 버킷과 체이닝           | [🔗 이미지](./assignments/java/images/hashmap/hashmap-bucket-chaining.png)  | 버킷 배열과 각 버킷에 연결된 노드의 체이닝 구조를 이해하기 위해 만든 자료              |
| HashMap 삭제 과정             | [🔗 이미지](./assignments/java/images/hashmap/hashmap-remove-process.png)   | 체이닝된 연결 리스트에서 첫 번째, 중간, 마지막 노드를 삭제하는 과정을 이해하기 위해 만든 자료  |
| HashMap resize와 rehashing | [🔗 이미지](./assignments/java/images/hashmap/hashmap-resize-rehashing.png) | HashMap 과제를 수행하며 버킷 배열 확장과 기존 데이터 재배치 과정을 이해하기 위해 만든 자료 |

## 실습 보조자료

| 주제        | 자료                                                                  | 설명                                                                 |
| --------- | ------------------------------------------------------------------- | ------------------------------------------------------------------ |
| 이중 연결 리스트 | [🔗 시각화](./practice/collections/doubly-linked-list-visualizer.html) | `prev`와 `next`를 이용한 노드 연결 구조를 확인하기 위해 제공된 시각화 자료                   |
| HashMap 체이닝 | [🔗 시각화](./practice/collections/hashmap-chaining-visualizer.html) | 해시 함수를 통한 버킷 선택과 충돌 발생 시 노드가 체이닝되는 과정을 확인하는 시각화 자료 |
| TreeMap   | [🔗 시각화](./practice/collections/treemap-visualizer.html)            | 이진 검색 트리 기반의 `put`, `get`, `remove` 동작과 중위 순회 결과를 단계별로 확인하는 시각화 자료 |

## 관리 기준

* `assignments/`에는 과제 설명과 과제 수행 과정에서 만든 보조자료를 정리한다.
* `practice/`에는 수업 중 실습과 직접 연결된 보조자료를 정리한다.
