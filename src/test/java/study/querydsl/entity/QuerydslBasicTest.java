package study.querydsl.entity;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import study.querydsl.common.EnableQueryLog;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@DataJpaTest
@EnableQueryLog
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = Team.builder()
                .teamName("teamA")
                .build();
        Team teamB = Team.builder()
                .teamName("teamB")
                .build();
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .team(teamA)
                .build();
        Member member2 = Member.builder()
                .userName("member2")
                .age(20)
                .team(teamA)
                .build();
        Member member3 = Member.builder()
                .userName("member3")
                .age(30)
                .team(teamB)
                .build();
        Member member4 = Member.builder()
                .userName("member4")
                .age(40)
                .team(teamB)
                .build();

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() throws Exception {
        Member findMember = em.createQuery("select m from Member m " +
                        "where m.userName = :userName", Member.class)
                .setParameter("userName", "member1")
                .getSingleResult();

        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.userName.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    @Test
    public void search() throws Exception {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.userName.eq("member1")
                        .and(member.age.between(10, 30)))
                .fetchOne();

        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() throws Exception {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.userName.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUserName()).isEqualTo("member1");
    }

    @Test
    public void resultFetch() throws Exception {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        /*Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();*/

        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        /*QueryResults<Member> memberQueryResults = queryFactory
                .selectFrom(member)
                .fetchResults();*/ // Deprecated(現在使わない. fetchをおすすめ)
        long count = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();
    }

    @Test
    public void sort() throws Exception {
        em.persist(
                Member.builder()
                        .userName(null)
                        .age(100)
                        .build()
        );
        em.persist(
                Member.builder()
                        .userName("member5")
                        .age(100)
                        .build()
        );
        em.persist(
                Member.builder()
                        .userName("member6")
                        .age(100)
                        .build()
        );
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.userName.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUserName()).isEqualTo("member5");
        assertThat(member6.getUserName()).isEqualTo("member6");
        assertThat(memberNull.getUserName()).isNull();
    }

    @Test
    public void paging1() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(0)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.userName.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void aggregation() throws Exception {
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
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.teamName, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.teamName)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.teamName)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.teamName)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    public void join() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.teamName.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("userName")
                .containsExactly("member1", "member2");
    }

    @Test
    public void theta_join() throws Exception {
        em.persist(Member.builder().userName("teamA").build());
        em.persist(Member.builder().userName("teamB").build());
        em.persist(Member.builder().userName("teamC").build());

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.userName.eq(team.teamName))
                .fetch();

        assertThat(result)
                .extracting("userName")
                .containsExactly("teamA", "teamB");
    }
}
