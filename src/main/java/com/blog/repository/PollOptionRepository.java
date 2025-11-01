package com.blog.repository;

import com.blog.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    // Find options for a specific poll
    List<PollOption> findByPollId(Long pollId);

    // Find options ordered by vote count
    List<PollOption> findByPollIdOrderByVoteCountDesc(Long pollId);

    // Check if user has already voted for this poll
    @Query("SELECT COUNT(v) > 0 FROM PollVote v WHERE v.poll.id = :pollId AND v.voterIdentifier = :voterId")
    boolean hasUserVotedForPoll(@Param("pollId") Long pollId, @Param("voterId") String voterId);
}
