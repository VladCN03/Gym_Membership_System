package com.gym_membership.gym_membership_backend.member.repo;

import com.gym_membership.gym_membership_backend.analytics.projection.*;
import com.gym_membership.gym_membership_backend.member.domain.Member;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("""
    select t.id as trainerId, t.name as trainerName, count(m.id) as membersCount
    from Member m join m.trainer t
    group by t.id, t.name
    order by membersCount desc
  """)
    List<MembersPerTrainerView> getMembersPerTrainer();

    @Query("""
    select mt.id as membershipTypeId, mt.type as type, mt.price as price, count(m.id) as membersCount
    from com.gym_membership.gym_membership_backend.membership.domain.MembershipType mt
    left join Member m on m.membershipType = mt
    group by mt.id, mt.type, mt.price
    order by membersCount desc, price desc
  """)
    List<MembershipStatsView> getMembershipStatsByType();

    @Query("select m from Member m where m.trainer.id = :trainerId")
    List<Member> findByTrainerId(@Param("trainerId") Long trainerId);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByName(String name);
}
