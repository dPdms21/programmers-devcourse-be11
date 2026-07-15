package com.example.jpaboard.scheduler;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.repository.BoardRepository;
import com.example.jpaboard.domain.repository.CommentRepository;
import com.example.jpaboard.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// * @Scheduled - 정해진 시각/주기에 메서드를 자동 실행하는 배치 작업
// # 전제 조건 2가지
// - 메인 클래스에 @EnableScheduling이 있어야 함
// - 이 클래스가 스프링 빈이어야 함 (@Component)

// # @Scheduled 메서드의 형태: 파라미터 없음 + 반환 void
// - 정해진 시각에 "스프링이" 호출하는 것이라, 인자를 넘겨줄 주체도 반환값을 받을 주체도 없음

// # 실행 시점을 지정하는 3가지 방식
// - cron: "달력 기준" 특정 시각 (매일 09:00, 매주 월요일)
// - fixedDelay: "이전 실행이 끝난 뒤" ms 쉬고 다시 실행
// - fixedRate: "이전 실행이 시작된 뒤" ms 마다 실행
// * fixedDelay vs fixedRate: 작업이 오래 걸릴 때 차이가 남
//   예) 작업 3분 + 간격 5분 → fixedDelay 는 "끝나고 5분 뒤" (8분 주기), fixedRate 는 "시작 후 5분마다" (5분 주기)
//      겹치면 안 되는 작업(정리/집계)은 fixedDelay가 안전

// # 주의: 기본 스케줄러는 "스레드 1개"
// - 모든 @Scheduled 작업이 한 줄로 서서 순서대로 돔 → 한 작업이 오래 걸리면 다른 작업이 밀림
// - 작업이 많아지면 스레드 풀 설정(spring.task.scheduling.pool.size)으로 늘릴 수 있음

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardBatchScheduler {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // (1) 일일 현황 리포트 - cron 방식
    // cron 표현식 읽는 법 - "0 0 9 * * *" = 매일 09:00:00
    //
    //     "0   0   9   *   *   *"
    //      │   │   │   │   │   └ 요일 (0~7, 0과 7은 일요일. * = 모든 요일)
    //      │   │   │   │   └──── 월   (1~12)
    //      │   │   │   └──────── 일   (1~31)
    //      │   │   └──────────── 시   (0~23) → 9시
    //      │   └──────────────── 분   (0~59) → 0분
    //      └──────────────────── 초   (0~59) → 0초
    // - 스프링 cron은 "6자리" (맨 앞에 '초'가 있음) ex) 리눅스 crontab은 5자리(분부터) crontab 예제를 그대로 복사하면 한 칸씩 밀려 엉뚱한 시각에 돔
    // # 요일은 숫자로도, 영문으로도 쓸 수 있음 - 둘은 완전히 같은 뜻
    //   숫자: 0 또는 7=일, 1=월, 2=화, 3=수, 4=목, 5=금, 6=토   ("0 0 4 * * 1" = 매주 월요일 04:00)
    //   영문: SUN MON TUE WED THU FRI SAT (대소문자 무관)
    //   범위/목록: "MON-FRI"(평일), "1-5"(같은 뜻), "MON,WED,FRI" / "1,3,5"
    //      예) "0 0 9 * * 0-3" = 일(0)~수(3)요일 09:00  (0이 일요일임에 주의 - "월~수"가 아님!)
    //      예) "0 0 9 * * SUN-WED" = 같은 뜻을 영문으로 - 어느 요일인지 읽는 순간 명확
    //      조합도 가능: "0-3,6" (일~수 + 토)

    // * zone 옵션 - 시각의 기준 시간대를 정함
    // - 지정하지 않으면 "서버(JVM)의 시간대"를 따름 -> 해외 클라우드 서버(UTC)에 배포하면, "9시에 돌아라"가 한국 시간 18시에 도는 사고가 남
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void dailyReport() {
        // 배치는 AOP 로그가 없으므로 시작을 직접 남김
        log.info("[일일 리포트 배치 시작]");

        long members = memberRepository.count();
        long boards = boardRepository.count();
        long comments = commentRepository.count();

        log.info("[일일 리포트] 회원 {}명, 게시글 {}건, 댓글 {}건", members, boards, comments);
    }

    // * 고아 첨부파일 점검 - fixedDelay 방식
    // 고아 파일이란? - 디스크에는 있는데 어느 게시글도 참조하지 않는 파일
    // - 생기는 경로: FileService.deleteFile이 실패했을 때 (warn 로그 남기던 그 경우),
    //   또는 파일 저장 후 게시글 저장(트랜잭션)이 실패해 롤백됐을 때 등
    // - 파일은 트랜잭션 롤백 대상이 아니라서(DB가 아니니까) 이런 불일치가 조금씩 쌓일 수 있음
    // → 그래서 주기적으로 "DB와 디스크를 대조"하는 배치를 두는 것 (실무의 전형적인 정리 배치 패턴)

    // 왜 삭제하지 않고 warn 로그만 남기나?
    // - "지금 업로드 중"인 파일은 디스크에 먼저 생기고 게시글 저장은 그 다음
    // → 그 찰나에 배치가 돌면 정상 파일을 고아로 오판해 지워버릴 수 있음 (배치 삭제의 고전적 사고)

    // initialDelay / fixedDelay (단위: ms)
    // - initialDelay = 10초: 앱이 뜨고 10초 뒤 "첫 실행"
    // - fixedDelay = 1시간: 그 뒤로는 "이전 실행이 끝난 시점"부터 1시간 간격으로 반복
    @Scheduled(initialDelay = 10_000, fixedDelay = 3_600_000)
    public void reportOrphanFiles() {
        log.info("[고아 파일 점검 배치 시작]");

        File dir = new File(uploadDir).getAbsoluteFile();
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            log.info("[고아 파일 점검] 업로드 디렉토리가 비어 있음");

            return;
        }

        // DB가 참조 중인 파일 이름 집합을 만듦 (경로 형태가 제각각일 수 있어 "파일 이름"으로 비교)
        // List가 아니라 Set인 이유: contains 검사가 목록 크기와 무관하게 빠름 (파일 수 × 글 수 반복을 피함)
        Set<String> referenced = boardRepository.findAll().stream()
                .map(Board::getFilePath)
                .filter(Objects::nonNull)
                .map(path -> new File(path).getName())
                .collect(Collectors.toSet());

        int orphanCount = 0;

        for (File file : files) {
            if (!referenced.contains(file.getName())) {
                orphanCount++;
                log.warn("[고아 파일 발견] 어느 게시글도 참조하지 않음: {}", file.getName());
            }
        }

        log.info("[고아 파일 점검 완료] 전체 {}개 중 고아 {}개", files.length, orphanCount);
    }
}
