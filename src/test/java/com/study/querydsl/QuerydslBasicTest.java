package com.study.querydsl;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.entity.Member;
import com.study.querydsl.entity.QMember;
import com.study.querydsl.entity.QTeam;
import com.study.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.querydsl.entity.QMember.member;
import static com.study.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

//    @Autowired
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    @DisplayName("실행전 먼저 실행")
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 10, teamB);
        Member member4 = new Member("member4", 20, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    @DisplayName("JPQL을 이용한 테스트")
    public void startJPQL(){
        //member1
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName("QueryDSL을 이용한 테스트")
    public void startQuerydsl(){
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    //.and Chaining
    @Test
    @DisplayName("Search1 where내 .and() 사용")
    public void search(){
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    //.where내 파라미터로 주어도 .and Chaining과 동일하게 적용됨.
    @Test
    @DisplayName("Search1 where내 ,를 이용")
    public void searchAndParam(){
        Member findMember = queryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /*
        member.username.eq("member1") // username = 'member1'
        member.username.ne("member1") //username != 'member1'
        member.username.eq("member1").not() // username != 'member1'

        member.username.isNotNull() //이름이 is not null

        member.age.in(10, 20) // age in (10,20)
        member.age.notIn(10, 20) // age not in (10, 20)
        member.age.between(10,30) //between 10, 30

        member.age.goe(30) // age >= 30
        member.age.gt(30) // age > 30
        member.age.loe(30) // age <= 30
        member.age.lt(30) // age < 30

        member.username.like("member%") //like 검색
        member.username.contains("member") // like ‘%member%’ 검색
        member.username.startsWith("member") //like ‘member%’ 검색
     */

    @Test
    @DisplayName("result종류 테스트")
    public void resultFetch(){
//        List<Member> fetch = queryFactory.selectFrom(member)
//                .fetch();
//
//        Member fetchone = queryFactory.selectFrom(member)
//                .fetchOne();
//
//        Member fetchFirst = queryFactory.selectFrom(member)
//                .fetchFirst();
        // *****************************************************************
        // *****************************************************************
        // *****************************************************************
        // deprecated!! fetchResults() -> fetch()
        // *****************************************************************
//        QueryResults<Member> fetchResults = queryFactory.selectFrom(member)
//                .fetchResults();
//        fetchResults.getTotal();
//        List<Member> content = fetchResults.getResults();

        /*
            QueryResult.getOffset() or .getLimit() 사용하지 않는 경우라면 fetch()
            성능적 이점이 있다.
         */

        // *****************************************************************
        // *****************************************************************
        // *****************************************************************
        // deprecated!! fetchCount() -> fetch().size()
        // *****************************************************************
        // *****************************************************************
        // *****************************************************************
        // For large result sets this may come at a severe performance **penalty**.
//        long total = queryFactory.selectFrom(member)
//                .fetchCount();

        int size = queryFactory.selectFrom(member)
                .fetch().size();

    }

    @Test
    @DisplayName("sort활용 테스트")
    public void sort(){
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsFirst())
                .fetch();

        Member member5 = result.get(1);
        Member member6 = result.get(2);
        Member memberNull = result.get(0);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    @DisplayName("paging처리, offset,limit")
    public void paging(){
        List<Member> fetch = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 하나를 스킵?
                .limit(2)
                .fetch();

        assertThat(fetch.size()).isEqualTo(2);
    }

    //전체 조회시?
    @Test
    @DisplayName("QueryResults 활용 테스트")
    public void paging2(){
        QueryResults<Member> fetchResults = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(fetchResults.getTotal()).isEqualTo(4);
        assertThat(fetchResults.getLimit()).isEqualTo(2);
        assertThat(fetchResults.getOffset()).isEqualTo(1);
        assertThat(fetchResults.getResults().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("집합 function 테스트")
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(60);
        assertThat(tuple.get(member.age.avg())).isEqualTo(15);
        assertThat(tuple.get(member.age.max())).isEqualTo(20);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    @DisplayName("팀의 이름과 각 팀의 평균 연령을 구해라")
    public void groupby() throws Exception{
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
//                .having() <- 가능
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("TeamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("TeamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(15);

    }

    @Test
    @DisplayName("팀 A에 소속된 모든 회원")
    public void join() throws Exception{
        List<Member> result = queryFactory.selectFrom(member)
                .leftJoin(member.team, team) // join, innerjoin, leftjoin etc...
                .where(team.name.eq("TeamA"))
                .fetch();
        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /*
     * 연관관계없는 조인 테스트
     *
     * left,outer join --> X 무적건 inner join?
     * 외부조인 불가능 -> on 으로 외부조인 가능
     */
    @Test
    @DisplayName("회원의 이름과 팀 이름과 같은 회원조회")
    public void theta_join() throws Exception{
        em.persist(new Member("TeamA"));
        em.persist(new Member("TeamB"));
        em.persist(new Member("TeamC"));

        List<Member> result = queryFactory.select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("TeamA", "TeamB");
    }
}
