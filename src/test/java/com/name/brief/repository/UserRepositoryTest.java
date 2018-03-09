package com.name.brief.repository;

import com.name.brief.model.Role;
import com.name.brief.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test
    public void findByUsername_returnsUserIfFound() {
        User user = new User("user", "password", Role.PLAYER.name());
        entityManager.persist(user);

        User found = repository.findByUsername(user.getUsername());

        assertThat(found.getUsername(), is(user.getUsername()));
        assertThat(found.getPassword(), is(user.getPassword()));
        assertThat(found.getRole(), is(user.getRole()));
    }

    @Test
    public void findByUsername_returnsNullIfNotFound() {
        assertThat(repository.findByUsername("non-existing-user"), nullValue());
    }

    @Test
    public void findByUsername_returnsNullOnEmptyInput() {
        assertThat(repository.findByUsername(""), nullValue());
    }
}