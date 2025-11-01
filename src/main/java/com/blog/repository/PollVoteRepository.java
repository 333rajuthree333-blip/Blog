package com.blog.repository;

import com.blog.model.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    // Find votes for a specific poll
    List<PollVote> findByPollId(Long pollId);

    // Check if user has voted for a specific option
    boolean existsByPollIdAndVoterIdentifier(Long pollId, String voterIdentifier);

    // Find user's vote for a poll
    PollVote findByPollIdAndVoterIdentifier(Long pollId, String voterIdentifier);

    // Count votes for a poll
    long countByPollId(Long pollId);
}
