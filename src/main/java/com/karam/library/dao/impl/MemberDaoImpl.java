package com.karam.library.dao.impl;

import com.karam.library.dao.MemberDao;
import com.karam.library.model.Book;
import com.karam.library.model.Member;
import com.karam.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class MemberDaoImpl implements MemberDao {
    @Override
    public void save(Member member) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(member);
            session.getTransaction().commit();
        }
    }

    @Override
    public Optional<Member> findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.find(Member.class, id));
        }
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            members = session.createQuery("FROM Member", Member.class).getResultList();
        }
        return members;
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                            "FROM Member WHERE isbn LIKE :isbn",
                            Member.class
                    )
                    .setParameter("isbn", "%" + email + "%")
                    .uniqueResultOptional();
        }
    }
}
