package com.gym_membership.gym_membership_backend.analytics.projection;

public interface MembersPerTrainerView {
    Long getTrainerId();
    String getTrainerName();
    Long getMembersCount();
}
