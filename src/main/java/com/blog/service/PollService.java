package com.blog.service;

import com.blog.dto.*;
import com.blog.model.BlogPoll;
import com.blog.model.PollOption;
import com.blog.model.PollVote;
import com.blog.repository.BlogPollRepository;
import com.blog.repository.PollOptionRepository;
import com.blog.repository.PollVoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PollService {

    private final BlogPollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final PollVoteRepository voteRepository;

    /**
     * Create a new poll
     */
    public BlogPoll createPoll(PollCreateRequest request) {
        log.info("Creating new poll: {}", request.getQuestion());

        // Parse expiration date if provided
        LocalDateTime expiresAt = null;
        if (request.getExpiresAt() != null && !request.getExpiresAt().isEmpty()) {
            expiresAt = LocalDateTime.parse(request.getExpiresAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        // Create poll
        BlogPoll poll = BlogPoll.builder()
                .question(request.getQuestion())
                .description(request.getDescription())
                .active(true)
                .allowMultipleVotes(request.getAllowMultipleVotes() != null ? request.getAllowMultipleVotes() : false)
                .expiresAt(expiresAt)
                .totalVotes(0)
                .build();

        BlogPoll savedPoll = pollRepository.save(poll);

        // Create options
        for (String optionText : request.getOptions()) {
            PollOption option = PollOption.builder()
                    .optionText(optionText.trim())
                    .voteCount(0)
                    .percentage(0.0)
                    .poll(savedPoll)
                    .build();

            optionRepository.save(option);
        }

        log.info("Poll created with ID: {}", savedPoll.getId());
        return savedPoll;
    }

    /**
     * Get all active polls
     */
    @Transactional(readOnly = true)
    public List<PollResponse> getActivePolls() {
        List<BlogPoll> polls = pollRepository.findActiveAvailablePolls();
        return polls.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific poll by ID
     */
    @Transactional(readOnly = true)
    public PollResponse getPollById(Long pollId, String voterIdentifier) {
        BlogPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found with id: " + pollId));

        PollResponse response = convertToResponse(poll);

        // Check if user has voted
        if (voterIdentifier != null) {
            boolean hasVoted = optionRepository.hasUserVotedForPoll(pollId, voterIdentifier);
            response.setUserHasVoted(hasVoted);

            if (hasVoted) {
                PollVote userVote = voteRepository.findByPollIdAndVoterIdentifier(pollId, voterIdentifier);
                if (userVote != null) {
                    response.setUserSelectedOptionId(userVote.getSelectedOption().getId().intValue());
                }
            }
        }

        return response;
    }

    /**
     * Vote on a poll
     */
    public PollResponse voteOnPoll(Long optionId, String voterIdentifier, String ipAddress, String userAgent) {
        PollOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Poll option not found with id: " + optionId));

        BlogPoll poll = option.getPoll();

        // Check if poll is available
        if (!poll.isAvailable()) {
            throw new RuntimeException("Poll is not available for voting");
        }

        // Check if user has already voted (if multiple votes not allowed)
        if (!poll.getAllowMultipleVotes() && optionRepository.hasUserVotedForPoll(poll.getId(), voterIdentifier)) {
            throw new RuntimeException("You have already voted on this poll");
        }

        // If user previously voted, remove their old vote
        if (poll.getAllowMultipleVotes()) {
            PollVote existingVote = voteRepository.findByPollIdAndVoterIdentifier(poll.getId(), voterIdentifier);
            if (existingVote != null) {
                // Decrease vote count for previous option
                PollOption oldOption = existingVote.getSelectedOption();
                oldOption.setVoteCount(oldOption.getVoteCount() - 1);
                optionRepository.save(oldOption);

                // Remove old vote
                voteRepository.delete(existingVote);
            }
        }

        // Create new vote
        PollVote vote = PollVote.builder()
                .voterIdentifier(voterIdentifier)
                .selectedOption(option)
                .poll(poll)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        voteRepository.save(vote);

        // Update option vote count
        option.setVoteCount(option.getVoteCount() + 1);
        optionRepository.save(option);

        // Update poll total votes
        poll.setTotalVotes(poll.getTotalVotes() + 1);

        // Calculate percentages for all options
        List<PollOption> options = optionRepository.findByPollIdOrderByVoteCountDesc(poll.getId());
        int totalVotes = poll.getTotalVotes();
        for (PollOption opt : options) {
            opt.calculatePercentage(totalVotes);
            optionRepository.save(opt);
        }

        pollRepository.save(poll);

        log.info("Vote recorded for poll {} by user {}", poll.getId(), voterIdentifier);

        return getPollById(poll.getId(), voterIdentifier);
    }

    /**
     * Delete a poll
     */
    public void deletePoll(Long pollId) {
        BlogPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found with id: " + pollId));

        pollRepository.delete(poll);
        log.info("Poll deleted: {}", pollId);
    }

    /**
     * Toggle poll active status
     */
    public BlogPoll togglePollStatus(Long pollId) {
        BlogPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found with id: " + pollId));

        poll.setActive(!poll.getActive());
        return pollRepository.save(poll);
    }

    /**
     * Convert BlogPoll to PollResponse
     */
    private PollResponse convertToResponse(BlogPoll poll) {
        List<PollOptionResponse> optionResponses = poll.getOptions().stream()
                .map(option -> new PollOptionResponse(
                        option.getId(),
                        option.getOptionText(),
                        option.getDescription(),
                        option.getVoteCount(),
                        option.getPercentage()
                ))
                .collect(Collectors.toList());

        return new PollResponse(
                poll.getId(),
                poll.getQuestion(),
                poll.getDescription(),
                poll.getActive(),
                poll.getAllowMultipleVotes(),
                poll.getTotalVotes(),
                poll.getCreatedAt(),
                poll.getExpiresAt(),
                poll.isExpired(),
                poll.isAvailable(),
                optionResponses,
                false, // Will be set by getPollById if needed
                null   // Will be set by getPollById if needed
        );
    }
}
