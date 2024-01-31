package com.capstone.D424.service.impl;

import com.capstone.D424.entities.User;
import com.capstone.D424.repository.UserRepository;
import com.capstone.D424.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public Optional<User> getUser(String name){
        Optional<User> user = userRepository.findByEmail(name);
        return user;
    }
    public ResponseEntity<User> getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<User> saveUser(User user) {
        try{
            if(userRepository.findByEmail(user.getEmail()).isPresent()) return new ResponseEntity<>(HttpStatus.CONFLICT);
            else userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch(DataIntegrityViolationException ee) {
            //DataIntegrityViolationExceptions happen when a SQL error occurs
            log.warn(ee.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            log.warn(e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    }
