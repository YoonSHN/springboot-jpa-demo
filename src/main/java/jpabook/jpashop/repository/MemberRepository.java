package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    @Autowired//Springjpa가 @persistenceContext를 지원해줌
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findALl(){
        List<Member> result = em.createQuery("select m From Member m", Member.class).getResultList();

        return result;
    }
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name= : name", Member.class)
                .setParameter("name",name)
                .getResultList();
    }
}
