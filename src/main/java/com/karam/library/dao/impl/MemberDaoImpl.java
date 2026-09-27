package com.karam.library.dao.impl;

import com.karam.library.dao.MemberDao;
import com.karam.library.model.Member;

import java.util.List;
import java.util.Optional;

public class MemberDaoImpl implements MemberDao {
    @Override
    public void save(Member member) {

    }

    @Override
    public Optional<Member> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        return List.of();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }
}
