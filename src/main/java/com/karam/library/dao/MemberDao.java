package com.karam.library.dao;

import com.karam.library.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberDao {
    void save(Member member);
    Optional<Member> findById(long id);
    List<Member> findAll();
    Optional<Member> findByEmail(String email);
}
