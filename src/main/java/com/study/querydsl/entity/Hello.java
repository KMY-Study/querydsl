package com.study.querydsl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName    : com.study.querydsl.entity
 * fileName       : Hello
 * author         : kmy
 * date           : 12/18/23
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 12/18/23        kmy       최초 생성
 */
@Entity
@Getter
@Setter
public class Hello {
    @Id @GeneratedValue
    private Long id;
}
