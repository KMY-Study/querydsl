package com.study.querydsl.repository;

import com.study.querydsl.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest(){
        Member member = new Member("member1", 100);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();

        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);

    }

    @Test
    public void basic_querydsl_Test(){
        Member member = new Member("member1", 100);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();

        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

        List<Member> result1 = memberJpaRepository.findAll_querydsl();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUsername_querydsl("member1");
        assertThat(result2).containsExactly(member);

    }

}