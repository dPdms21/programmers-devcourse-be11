package com.example.spring.testcode;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
class ProductDaoTest {
    @Autowired
    private ProductDao dao;

    @BeforeAll
    static void beforeAll() {
        System.out.println("전체 테스트 시작");
    }

    @BeforeEach
    void setUp() {
        dao.deleteAll();
    }

    private Product newProduct(String id, String name, int price) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);

        return p;
    }

    @DisplayName("상품 추가")
    @Test
    void add() {
        assertEquals(0, dao.getCount());

        dao.add(newProduct("1", "연필", 1500));

        assertEquals(1, dao.getCount());
    }

    @Test
    void get() {
        Product p = newProduct("2", "지우개", 1000);
        dao.add(p);

        Product d = dao.get("2");

        assertEquals(p.getName(), d.getName());
        assertEquals(p.getPrice(), d.getPrice());
    }

    @DisplayName("상품 수정")
    @Test
    void update() {
        dao.add(newProduct("1", "연필", 1500));
        dao.update(newProduct("1", "싸인펜", 1000));

        Product p = dao.get("1");

        assertEquals("싸인펜", p.getName());
        assertEquals(1000, p.getPrice());
    }

    @DisplayName("상품 삭제")
    @Test
    void delete() {
        dao.add(newProduct("1", "연필", 1500));

        dao.delete("1");

        assertEquals(0, dao.getCount());
        assertThrows(
                NoSuchElementException.class,
                () -> dao.get("1")
        );
    }

    @Test
    void add_여러개_개수_증가() {
        assertEquals(0, dao.getCount());

        dao.add(newProduct("1", "연필", 1500));
        assertEquals(1, dao.getCount());

        dao.add(newProduct("2", "지우개", 1000));
        assertEquals(2, dao.getCount());

        dao.add(newProduct("3", "볼펜", 2000));
        assertEquals(3, dao.getCount());
    }

    @Test
    void add_중복_id_예외() {
        dao.add(newProduct("3", "샤프", 5000));

        assertThrows(
                IllegalStateException.class,
                () -> dao.add(newProduct("3", "샤프", 5000))
        );
    }

    @Test
    void get_없는_id_예외() {
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> dao.get("없는_id")
        );

        assertEquals(
                "없는 id: 없는_id",
                exception.getMessage()
        );
    }

    @Disabled("일부러 틀린 기대값을 넣은 학습용 실패 예제")
    @Test
    void 일부러_실패하는_테스트() {
        dao.add(newProduct("4", "볼펜", 2000));
        assertEquals(2, dao.getCount());
    }
}