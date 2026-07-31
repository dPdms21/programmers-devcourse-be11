package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.entity.Role;
import com.example.jpaboard.domain.repository.BoardRepository;
import com.example.jpaboard.dto.BoardUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    BoardRepository boardRepository;

    @Mock
    FileService fileService;

    @InjectMocks
    BoardService boardService;

    @Test
    void 작성자는_자신의_게시글을_수정할_수_있다() {
        Board board = createBoard("hong");

        BoardUpdateRequestDto request = new BoardUpdateRequestDto();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setFileFlag(false);

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        boardService.updateArticle(
                1L,
                request,
                "hong",
                Role.ROLE_USER
        );

        assertThat(board.getTitle())
                .isEqualTo("수정된 제목");

        assertThat(board.getContent())
                .isEqualTo("수정된 내용");

        assertThat(board.getFilePath())
                .isEqualTo("files/test.txt");

        verify(fileService, never())
                .deleteFile(board.getFilePath());

        verify(fileService, never())
                .storeFile(request.getFile());
    }

    @Test
    void 다른_일반_회원은_게시글을_수정할_수_없다() {
        Board board = createBoard("hong");

        BoardUpdateRequestDto request = new BoardUpdateRequestDto();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setFileFlag(false);

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        assertThatThrownBy(() ->
                boardService.updateArticle(
                        1L,
                        request,
                        "kim",
                        Role.ROLE_USER
                )
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("게시글을 수정하거나 삭제할 권한이 없습니다.");

        assertThat(board.getTitle())
                .isEqualTo("기존 제목");

        assertThat(board.getContent())
                .isEqualTo("기존 내용");

        verify(fileService, never())
                .deleteFile(board.getFilePath());
    }

    @Test
    void 관리자는_다른_회원의_게시글을_수정할_수_있다() {
        Board board = createBoard("hong");

        BoardUpdateRequestDto request = new BoardUpdateRequestDto();
        request.setTitle("관리자가 수정한 제목");
        request.setContent("관리자가 수정한 내용");
        request.setFileFlag(false);

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        boardService.updateArticle(
                1L,
                request,
                "admin",
                Role.ROLE_ADMIN
        );

        assertThat(board.getTitle())
                .isEqualTo("관리자가 수정한 제목");

        assertThat(board.getContent())
                .isEqualTo("관리자가 수정한 내용");
    }

    @Test
    void 작성자는_자신의_게시글을_삭제할_수_있다() {
        Board board = createBoard("hong");

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        boardService.deleteArticle(
                1L,
                "hong",
                Role.ROLE_USER
        );

        verify(boardRepository).delete(board);
        verify(fileService).deleteFile("files/test.txt");
    }

    @Test
    void 다른_일반_회원은_게시글을_삭제할_수_없다() {
        Board board = createBoard("hong");

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        assertThatThrownBy(() ->
                boardService.deleteArticle(
                        1L,
                        "kim",
                        Role.ROLE_USER
                )
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("게시글을 수정하거나 삭제할 권한이 없습니다.");

        verify(boardRepository, never()).delete(board);
        verify(fileService, never()).deleteFile(board.getFilePath());
    }

    @Test
    void 관리자는_다른_회원의_게시글을_삭제할_수_있다() {
        Board board = createBoard("hong");

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        boardService.deleteArticle(
                1L,
                "admin",
                Role.ROLE_ADMIN
        );

        verify(boardRepository).delete(board);
        verify(fileService).deleteFile("files/test.txt");
    }

    private Board createBoard(String userId) {
        return Board.builder()
                .userId(userId)
                .title("기존 제목")
                .content("기존 내용")
                .filePath("files/test.txt")
                .created(LocalDateTime.now())
                .build();
    }
}