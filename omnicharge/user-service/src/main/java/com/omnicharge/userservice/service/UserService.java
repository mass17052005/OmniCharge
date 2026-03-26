package com.omnicharge.userservice.service;

import com.omnicharge.userservice.dto.*;
import com.omnicharge.userservice.entity.User;
import com.omnicharge.userservice.exception.ResourceNotFoundException;
import com.omnicharge.userservice.feign.PaymentServiceClient;
import com.omnicharge.userservice.feign.RechargeServiceClient;
import com.omnicharge.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RechargeServiceClient rechargeServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public UserProfileResponse getProfile(String email) {
        User user = findByEmail(email);
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        return response;
    }

    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        return response;
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        userRepository.save(user);
        return getProfile(email);
    }

    public void deleteUser(String email) {
        User user = findByEmail(email);
        userRepository.delete(user);
    }

    public List<RechargeResponse> getRechargeHistory(String email) {
        User user = findByEmail(email);
        return rechargeServiceClient.getRechargesByUserId(user.getId());
    }

    public List<PaymentResponse> getTransactionStatus(String email) {
        User user = findByEmail(email);
        return paymentServiceClient.getTransactionsByUserId(user.getId());
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
