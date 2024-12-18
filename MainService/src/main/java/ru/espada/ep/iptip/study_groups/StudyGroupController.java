package ru.espada.ep.iptip.study_groups;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.espada.ep.iptip.study_groups.models.requests.*;

import java.security.Principal;

@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/studyGroup")
public class StudyGroupController {

    private StudyGroupRepository studyGroupRepository;

    // TODO: add methods: create, add users, remove users, get users, set semester
    @PostMapping("/studyGroup")
    public ResponseEntity<?> createStudyGroup(Principal principal, @Valid @RequestBody CreateStudyGroupRequest createStudyGroupRequest) {
        // TODO: implementation
        return null;
    }

    @PostMapping("/studyGroupWithUsers")
    public ResponseEntity<?> createStudyGroupWithUsers(Principal principal, @Valid @RequestBody CreateStudyGroupWithUsersRequest createStudyGroupWithUsersRequest) {
        // TODO: implementation
        return null;
    }

    @PatchMapping("/studyGroup")
    public ResponseEntity<?> modifyStudyGroup(Principal principal, @Valid @RequestBody ModifyStudyGroupRequest modifyStudyGroupRequest) {
        // TODO: implementation
        return null;
    }

    @DeleteMapping("/studyGroup")
    public ResponseEntity<?> deleteStudyGroup(Principal principal, Long studyGroupId) {
        // TODO: implementation
        return null;
    }

    @GetMapping("/studyGroup")
    public ResponseEntity<?> getStudyGroup(Principal principal, Long studyGroupId) {
        // TODO: implementation
        return null;
    }

    @PostMapping("/attachUser")
    public ResponseEntity<?> attachUserToStudyGroup(Principal principal, @Valid @RequestBody AttachUserToStudyGroupRequest attachUserToStudyGroupRequest) {
        // TODO: implementation
        return null;
    }

    @DeleteMapping("/detachUser")
    public ResponseEntity<?> detachUserFromStudyGroup(Principal principal, @Valid @RequestBody DetachUserFromStudyGroupRequest detachUserFromStudyGroupRequest) {
        // TODO: implementation
        return null;
    }

    @PutMapping("/setStudyGroupSemester")
    public ResponseEntity<?> setStudyGroupSemester(Principal principal, @Valid @RequestBody SetStudyGroupMembersSemesterRequest setStudyGroupMembersSemesterRequest) {
        // TODO: implementation
        return null;
    }

    @GetMapping("/getStudyGroupMembers")
    public ResponseEntity<?> getStudyGroupMembers(Principal principal, Long studyGroupId) {
        // TODO: implementation
        return null;
    }

    @Autowired
    public void setStudyGroupRepository(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }
}

