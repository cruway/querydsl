package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import study.querydsl.entity.Member;

import java.util.List;

// QuerydslPredicateExecutorはjoinができない
// Querydslに依存する
// 実務環境では限界がある
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryQuery, QuerydslPredicateExecutor<Member> {
    List<Member> findByUserName(String userName);

}
