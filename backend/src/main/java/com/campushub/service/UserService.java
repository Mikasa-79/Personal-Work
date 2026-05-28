package com.campushub.service;

import com.campushub.dto.UserDtos;
import com.campushub.entity.User;
import com.campushub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDtos.ProfileResponse currentUserProfile(User user) {
        return new UserDtos.ProfileResponse(
                user.getId(),
                user.getStudentNo(),
                user.getNickname(),
                user.getCollege(),
                user.getContact(),
                user.getRole(),
                user.getAdmin(),
                user.getCreditScore(),
                user.getStatus()
        );
    }

    @Transactional
    public UserDtos.ProfileResponse updateProfile(User user, UserDtos.UpdateRequest request) {
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getCollege() != null) {
            user.setCollege(request.getCollege());
        }
        if (request.getContact() != null) {
            user.setContact(request.getContact());
        }
        User saved = userRepository.save(user);
        return currentUserProfile(saved);
    }
}
