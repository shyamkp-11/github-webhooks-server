package com.shyampatel.webapp.githubplayroom.githubuser;

import com.shyampatel.webapp.githubplayroom.githubuserfcm.GithubUserFcm;
import com.shyampatel.webapp.githubplayroom.githubuserfcm.GithubUserFcmRepository;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class GithubUserRepositoryTest {

    @Autowired
    private GithubUserRepository githubUserRepository;
    @Autowired
    GithubUserFcmRepository githubUserFcmRepository;

    @AfterEach
    void tearDown() {
    }

    @Transactional
    @Test
    void injectedComponentsAreNotNull(){
        assertNotNull(githubUserRepository);
    }

    @Test
    void testJpaAuditingFields() {
        var user = GithubUser.builder()
                .globalId("globalId")
                .username("username")
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .githubUserFcmS(null)
                .build();
        user.setCreatedBy(1);
        githubUserRepository.save(user);
        var savedEntity = githubUserRepository.getGithubUserByGlobalId("globalId");
        System.out.println(savedEntity.getCreatedAt());
        assert savedEntity.getCreatedAt() != null;
    }

    @Test
    void testGetGithubUserFcmDefaultFailureCount() {
        var user = GithubUser.builder()
                .globalId("globalId")
                .username("username")
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .githubUserFcmS(null)
                .build();
        user.setCreatedBy(1);
        githubUserRepository.save(user);
        var fcm = githubUserFcmRepository.save(
                GithubUserFcm.builder()
                        .createdBy(1)
                        .deviceId("deviceId")
                        .githubUser(GithubUser.builder().globalId("globalId").build())
                        .fcmToken("fcmToken")
                        .enabled(false)
                        .signedOut(false)
                        .build()
        );
        assert fcm.getMessageDeliverFailureCount() == 0;
    }
}