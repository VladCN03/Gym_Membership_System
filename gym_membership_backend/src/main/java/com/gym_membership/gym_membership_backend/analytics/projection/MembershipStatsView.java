package com.gym_membership.gym_membership_backend.analytics.projection;

import java.math.BigDecimal;

public interface MembershipStatsView {
    Long getMembershipTypeId();
    String getType();
    BigDecimal getPrice();
    Long getMembersCount();
}
