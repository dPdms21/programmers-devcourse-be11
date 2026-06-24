package com.example.spring.testcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
class ProductDaoTest {
    @Autowired
    private ProductDao dao;

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

    @Test
    void add_중복_id_예외() {
        dao.add(newProduct("3", "샤프", 5000));

        Executable action = new Executable() {
            @Override
            public void execute() {
                dao.add(newProduct("3", "샤프", 5000));
            }
        };

        assertThrows(IllegalStateException.class, action);
    }

    @Test
    void get_없는_id_예외() {
        Executable action = new Executable() {
            @Override
            public void execute() {
                dao.get("없는_id");
            }
        };

        assertThrows(NoSuchElementException.class, action);
    }

    @Disabled("일부러 틀린 기대값을 넣은 학습용 실패 예제")
    @Test
    void 일부러_실패하는_테스트() {
        dao.add(newProduct("4", "볼펜", 2000));
        assertEquals(2, dao.getCount());
    }
}