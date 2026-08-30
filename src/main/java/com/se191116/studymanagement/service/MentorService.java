package com.se191116.studymanagement.service;

import com.se191116.studymanagement.model.dto.request.MentorCreateRequest;
import com.se191116.studymanagement.model.dto.request.MentorUpdateRequest;
import com.se191116.studymanagement.model.dto.response.MentorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentorService {
    MentorResponse createMentor(MentorCreateRequest request);
    MentorResponse updateMentor(Integer mentorId, MentorUpdateRequest request);
    MentorResponse getMentorById(Integer mentorId);
    Page<MentorResponse> getMentors(Pageable pageable);
}
