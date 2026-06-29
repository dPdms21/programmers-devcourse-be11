package com.example.spring.ch04;

// * 예외종류와 특징
// 자바에서 throw 할 수 있는 모든 것은 Throwable의 자손

//   Throwable
//    ├── Error                     (시스템 레벨의 심각한 오류 - 손대지 않음)
//    └── Exception
//         ├── (체크 예외)           Exception을 바로 상속 (IOException, SQLException ...)
//         └── RuntimeException     (언체크 예외) (NullPointerException, IllegalArgumentException ...)

// 핵심 구분선은 'RuntimeException을 상속했는가'
//  - 상속 안 함 -> 체크 예외   (컴파일러가 처리를 강제)
//  - 상속함     -> 언체크 예외 (컴파일러가 강제하지 않음)

import java.io.IOException;

public class Exception_02 {
    // 1) Error
    // - java.lang.Error의 자손. JVM/시스템 레벨의 비정상 상황(OutOfMemoryError, StackOverflowError 등)
    // - 애플리케이션 코드로 '복구'할 수 있는 성격이 아님. 그래서 잡으려고 시도하지 않음
    // - 잡아봤자 할 수 있는 게 없고, 보통 프로그램을 정상적으로 이어갈 수 없는 상황
    void errorExam() {
        // 아래처럼 끝없는 재귀를 호출하면 StackOverflowError(Error의 자손)가 남
//        errorExam();
        // -> 이런 건 try-catch로 다루는 대상이 아니라, '코드를 고쳐야 할' 문제
    }

    // 2) checked exception
    // - Exception의 자손이지만 RuntimeException은 상속하지 않은 예외(IOException, SQLException ...)
    // - 특징: 반드시 catch 하거나 throws로 선언해야 '컴파일' 됨 (컴파일러가 강제)
    //   -> 잡지도, 선언하지도 않으면 빨간 줄(컴파일 에러)이 뜸
    // - 의미: '발생할 수 있음을 미리 알리고, 호출하는 쪽이 대비하게 하라'는 의도
    //   주로 외부 자원(파일, DB, 네트워크)처럼 '내 잘못이 아니어도 실패할 수 있는' 상황에 쓰임
    void checkedExam() throws IOException { // <- throws로 선언하지 않으면 컴파일 안 됨
        readFile();
    }

    void readFile() throws IOException {
        throw new IOException("파일을 읽을 수 없음");
    }

    // 3) unchecked exception
    // - RuntimeException의 자손(NullPointerException, IllegalArgumentException, ArrayIndexOutOfBounds ...)
    // - 특징: catch/throws를 '강제하지 않음'. 선언하지 않아도 컴파일됨
    // - 의미: 대부분 '프로그램의 버그' 성격. 즉 미리 피할 수 있는(=코드를 고치면 되는) 문제
    //   그래서 일일이 catch로 대비하기보다, 발생하지 않도록 코드를 올바르게 작성하는 것이 우선
    void uncheckedExam() { // <- throws 선언이 없어도 됨
        String str = null;
//        str.length(); // -> NullPointerException (언체크). null 체크를 안 한 '버그'

        setAge(-1);
    }

    void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상 : " + age);
        }
    }

    // ====================== 정리: 언제 무엇을? ======================
    // - Error          : 다루지 않음 (잡지 않음)
    // - Checked        : '외부 요인으로 실패할 수 있는' 작업. 호출자가 복구를 시도할 여지가 있을 때
    //                    단, 복구할 수도 없는데 throws로 끝없이 떠넘기면 Exception_01의 안티패턴이 됨
    // - Unchecked      : '코드를 고치면 되는 버그' 또는 '복구 불가라 굳이 강제할 필요 없는' 상황
    //
    // 실무/스프링의 경향: 복구가 불가능한 체크 예외(SQLException 등)는 굳이 강제로 떠넘기지 말고
    //  언체크 예외로 '전환(wrapping)'해 던지는 방식을 즐겨 씀
    //  -> 그래야 코드가 불필요한 throws로 더럽혀지지 않으면서도 원인 정보는 보존됨
    //  (구체적인 '예외 복구 / 회피 / 전환' 전략은 이어지는 예제에서 다룸)

    public static void main(String[] args) {

    }
}
