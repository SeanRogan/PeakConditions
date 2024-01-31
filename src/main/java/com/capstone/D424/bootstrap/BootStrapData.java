package com.capstone.D424.bootstrap;

import com.capstone.D424.entities.MountainPeak;
import com.capstone.D424.entities.User;
import com.capstone.D424.entities.UserProfile;
import com.capstone.D424.model.Role;
import com.capstone.D424.repository.MountainPeakRepository;
import com.capstone.D424.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BootStrapData is a utility class used for creating sample users upon server startup, for testing and demo purposes.
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class BootStrapData implements CommandLineRunner {
    private final UserRepository userService;
    private final BootStrapDB bootStrapDB;
    private final MountainPeakRepository peakRepo;

    /**
     * run method is overridden from CommandLineRunner and will run upon server startup.
     **/
    @Override
    public void run(String... args) throws Exception {
        try {
            List<MountainPeak> peaks = peakRepo.findAll();
            if (peaks.isEmpty()) bootStrapDB.init();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        User user1 = User.builder().username("username").email("email@email.com").password("pass").profile(new UserProfile()).role(Role.ROLE_USER_PAID).build();
        //userService.save(user1);

    }
}
