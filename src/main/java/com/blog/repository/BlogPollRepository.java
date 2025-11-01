package com.blog.repository;

import com.blog.model.BlogPoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPollRepository extends JpaRepository<BlogPoll, Long> {

    // Find active polls
    List<BlogPoll> findByActiveTrue();

    // Find active and not expired polls
    @Query("SELECT p FROM BlogPoll p WHERE p.active = true AND (p.expiresAt IS NULL OR p.expiresAt > CURRENT_TIMESTAMP)")
    List<BlogPoll> findActiveAvailablePolls();

    // Find polls by status
    List<BlogPoll> findByActiveTrueOrderByCreatedAtDesc();

    // Count total votes for a poll
    @Query("SELECT SUM(o.voteCount) FROM BlogPoll p JOIN p.options o WHERE p.id = :pollId")
    Long getTotalVotesForPoll(@Param("pollId") Long pollId);
}
