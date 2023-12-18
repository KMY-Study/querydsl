package com.study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.entity.Hello;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import com.study.querydsl.entity.QHello;

@SpringBootTest
@Transactional
//@Commit <-
class QuerydslApplicationTests {

    @Autowired
//    @PersistenceContext < --- JPA 표준스펙
    EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = new QHello("h");

        Hello result = query.selectFrom(qHello)
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);

        Assertions.assertThat(result.getId()).isEqualTo(hello.getId());

    }

}
