package com.study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.querydsl.dto.MemberDto;
import com.study.querydsl.dto.QMemberDto;
import com.study.querydsl.dto.UserDto;
import com.study.querydsl.entity.Member;
import com.study.querydsl.entity.QMember;
import com.study.querydsl.entity.QTeam;
import com.study.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
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

    /*
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     */
    @Test
    @DisplayName("회원과 팀을 조인하면서 팀이름이 teamA인 팀만 조인, 회원은 모두 조회")
    public void join_on_filtering() throws Exception{
        //given
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("TeamA"))
//                .join(member.team, team).on(team.name.eq("TeamA")) //#1 inner join
//                .join(member.team, team) //#2
//                .where(team.name.eq("TeamA"))
                /*
                  on 절을 활용해 조인 대상을 필터링 할 때,
                  외부조인이 아니라 내부조인(inner join)을 사용하면,
                  where 절 에서 필터링 하는 것과 기능이 동일
                 */
                .fetch();

        for(Tuple tuple : result){
            System.out.println("tuple : " + tuple);
        }
        /*
        .leftJoin(member.team, team).on(team.name.eq("TeamA")) 경우
            tuple : [Member(id=1, username=member1, age=10), Team(id=1, name=TeamA)]
            tuple : [Member(id=2, username=member2, age=20), Team(id=1, name=TeamA)]
            tuple : [Member(id=3, username=member3, age=10), null]
            tuple : [Member(id=4, username=member4, age=20), null]
         */
    }

    /*
        연관관계 없는 엔티티 외부조인,,
     */
    @Test
    @DisplayName("회원의 이름이 팀 이름과 같은 대상 외부조인")
    public void join_on_no_relation() throws Exception{
        em.persist(new Member("TeamA"));
        em.persist(new Member("TeamB"));
        em.persist(new Member("TeamC"));

        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for(Tuple tuple : result){
            System.out.println("tuple : " + tuple);
        }
        /* 차이점.
            leftJoin(member.team, team)
            from(member).leftJoin(team).on(~~~)
         */

        /*
            tuple : [Member(id=1, username=member1, age=10), null]
            tuple : [Member(id=2, username=member2, age=20), null]
            tuple : [Member(id=3, username=member3, age=10), null]
            tuple : [Member(id=4, username=member4, age=20), null]
            tuple : [Member(id=5, username=TeamA, age=0), Team(id=1, name=TeamA)]
            tuple : [Member(id=6, username=TeamB, age=0), Team(id=2, name=TeamB)]
            tuple : [Member(id=7, username=TeamC, age=0), null]
         */
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    @DisplayName("fetch Join 미적용시")
    public void fetchJoinNo() throws Exception{
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("fetch join 미적용").isFalse();
    }

    @Test
    @DisplayName("fetch Join 적용시")
    public void fetchJoinUse() throws Exception{
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin() // <- 연관관계 다떙겨옴
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("fetch join 미적용").isTrue();
    }

    @Test
    @DisplayName("나이가 가장 많은 회원 조회")
    public void subQuery() throws Exception{

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(20,20);
    }
    @Test
    @DisplayName("나이가 평균이상인 회원 조회")
    public void subQueryGoe() throws Exception{

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(20,20);
    }
    @Test
    @DisplayName("in 활용")
    public void subQueryIn() throws Exception{

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(20,20);
    }

    @Test
    @DisplayName("select절 SubQuery 예제")
    public void selectSubQuery() throws Exception{
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub)
                )
                .from(member)
                .fetch();

        for(Tuple tuple : result){
            System.out.println("tutple :" + tuple);
        }
        /* 결과값
            tutple :[member1, 15.0]
            tutple :[member2, 15.0]
            tutple :[member3, 15.0]
            tutple :[member4, 15.0]
         */

        /*
            JPA JPQL의 서브쿼리는 ->>> 인라인뷰 SubQuery 안됨.. ( From절 SubQuery )
         */
        // 1. SubQuery를 Join으로 변경,,,권장
        // 2. 쿼리 분리
        // 3. nativeQuery.. JPA..

        // ** 데이터를 가져오는것에 대한 집중? concept..
    }
    
    @Test
    @DisplayName("회원의 나이별로 출력")
    public void basicCase() throws Exception{
        //given
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();
        //when
        //then
        for(String s : result){
            System.out.println("Str :: " + s);
        }
    }
    
    @Test
    @DisplayName("CaseBuilder를 이용한 case문 처리")
    public void complexCase() throws Exception{
        //given
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 31)).then("21~30살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();
        //when
        //then
        for(String s : result){
            System.out.println("Str :: " + s);
        }
    }

    /*
        복잡한 조건은 변수로 선언해서 재사용성을 높히거나, 결합도를 낮춘다
     */
    @Test
    @DisplayName("복잡조건 변수처리한 테스트")
    public void orderByUseCase() throws Exception{
        //given
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0,20)).then(2)
                .when(member.age.between(21,30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();
        //when
        //then
        for(Tuple tuple : result){
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = " +
                    rank);
        }
    }

    @Test
    @DisplayName("상수테스트")
    public void constant() throws Exception{
        //given
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
        //when
        //then
        for(Tuple t : result){
            System.out.println("Str :: " + t);
        }
    }
    
    @Test
    @DisplayName("쿼리결과와 여러타입의 concat테스트")
    public void concat() throws Exception{
        //given
        List<String> result = queryFactory
                .select(member.username.concat("___").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();
        //when
        //then
        for(String s : result){
            System.out.println("Str :: " + s);
        }
    }

    @Test
    @DisplayName("단일 타입의 리턴 Projection테스트")
    public void simpleProjection() throws Exception{
        //given
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
        //then
        for(String s : result){
            System.out.println("Str :: " + s);
        }
    }

    @Test
    @DisplayName("두가지 이상의 타입 리턴 Projection테스트")
    public void tupleProjection() throws Exception{
        //given
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        //then
        for(Tuple t : result){
            String name = t.get(member.username);
            Integer age = t.get(member.age);
            System.out.println("username : " + name);
            System.out.println("age : " + age);
        }
    }

    /*
        JPA DTO 조회시 new 사용(패키지명 명시)
        생성자 방식만 지원
     */
    @Test
    @DisplayName("JPQL에서의 DTO return Test")
    public void findDtoByJQPL(){
        List<MemberDto> resultList = em.createQuery("select new com.study.querydsl.dto.MemberDto(m.username, m.age) from Member m"
                , MemberDto.class).getResultList();
        for(MemberDto dto : resultList){
            System.out.println("memberDto ::" + dto);
        }
    }

    /*
        setter
     */
    @Test
    @DisplayName("QueryDSL에서의 DTO return Test01 - Property 접근 방법")
    public void findDtoByQuerydslBean(){
        List<MemberDto> resultList = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();
        for(MemberDto dto : resultList){
            System.out.println("memberDto ::" + dto);
        }
    }
    /*
        fields
     */
    @Test
    @DisplayName("QueryDSL에서의 DTO return Test02 - 필드 접근 방법")
    public void findDtoByQuerydslField(){
        List<MemberDto> resultList = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();
        for(MemberDto dto : resultList){
            System.out.println("memberDto ::" + dto);
        }
    }
    /*
        Constructor
     */
    @Test
    @DisplayName("QueryDSL에서의 DTO return Test03 - 생성자 접근 방법")
    public void findDtoByQuerydslConstructor(){
        List<MemberDto> resultList = queryFactory
                //타입을 잘맞추어야된다.
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();
        for(MemberDto dto : resultList){
            System.out.println("memberDto ::" + dto);
        }
    }

    /*
        1. 필드명이 매칭이 안될떄
        userDto ::UserDto(name=null, age=10)
        userDto ::UserDto(name=null, age=20)
        userDto ::UserDto(name=null, age=10)
        userDto ::UserDto(name=null, age=20)
        --> .as()로 해줘야된다.
        --> ExpressionUtils.as(member.username, "name") 로 감싸도된다.
        2. 서브쿼리시
        ExpressionUtils.as()를 사용한다.
        두번째 param은 alias!
     */
    @Test
    @DisplayName("QueryDSL에서의 DTO return Test01 - Property 접근 방법")
    public void findDtoByQuerydsl4(){
        QMember memberSub = new QMember("memberSub");
        List<UserDto> resultList = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
//                        ExpressionUtils.as(member.username, "name")
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub) , "age")
                        )
                )
                .from(member)
                .fetch();
        for(UserDto dto : resultList){
            System.out.println("userDto ::" + dto);
        }
    }

    @Test
    @DisplayName("QueryDSL에서의 DTO return Test03 - 생성자 접근 방법")
    public void findDtoByQuerydslConstructor1(){
        List<UserDto> resultList = queryFactory
                //타입을 잘맞추어야된다.
                .select(Projections.constructor(UserDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();
        for(UserDto dto : resultList){
            System.out.println("memberDto ::" + dto);
        }
    }


    @Test
    @DisplayName("QueryProjection테스트")
    public void findDtoByQueryProjection() throws Exception{
        //given
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        //when
        //then
        for(MemberDto dto : result){
            System.out.println("MemberDto :: " + dto);
        }
    }
    
    @Test
    @DisplayName("distinct 테스트")
    public void distinctTest() throws Exception{
        //given
        Integer age = queryFactory.select(member.age).distinct()
                .from(member)
                .where(member.age.eq(10))
                .fetchOne();
        //when
        //then
        assertThat(age).isEqualTo(10);
    }

    /*
        동적쿼리_BooleanBuilder
     */
    @Test
    @DisplayName("")
    public void dynamicQuery_BooleanBuilder() throws Exception{
        //given
        String usernameParam = "member1";
        Integer ageParam = 10; // 1. null <- searchMember1에서 동적으로 쿼리가능.

        List<Member> result = searchMember1(usernameParam, ageParam);

        //when
        //then
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        BooleanBuilder builder = new BooleanBuilder();
//        BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameParam)); // 2. 초기값 설정 가능

        if(usernameParam != null){
            builder.and(member.username.eq(usernameParam));
        }

        if(ageParam != null){
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();
    }

    @Test
    @DisplayName("")
    public void dynamicQuery_WhereParam() throws Exception{
        //given
        String usernameParam = "member1";
        Integer ageParam = 10; // 1. null <- searchMember1에서 동적으로 쿼리가능.

        List<Member> result = searchMember2(usernameParam, ageParam);

        //when
        //then
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory.selectFrom(member)
                .where(
//                         usernameEq(usernameParam)
//                        ,ageEq(ageParam)
                        allEq(usernameParam,ageParam)
                ) // where 조건에 null값인 경우 무시
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }
    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }

    private BooleanExpression allEq(String usernameParam, Integer ageParam){
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }

    
    @Test
    @DisplayName("")        
    public void bulkUpdate() throws Exception{

        /* 전
            member1 = 10 -> 비회원
            member2 = 20 -> 비회원
            member3 = 30 -> 유지
            member4 = 40 -> 유지
         */
        //given
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        //영속성 컨텍스트에 entity가 올라가있다..
        //bulk 연산은 DB에 바로 적재,, 영속성 컨텍스트와 불일치 상태,,

        /* 후
            member1 = 10 -> member1
            member2 = 20 -> member2
            member3 = 30 -> member3
            member4 = 40 -> member4
         */

        em.flush(); em.clear(); // bulk 연산후 영속성 컨테스트 초기화 필요,,, 디비와 일치시키기

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for(Member m : result){
            System.out.println(m);
        }


        //when
        //then
    }


}
