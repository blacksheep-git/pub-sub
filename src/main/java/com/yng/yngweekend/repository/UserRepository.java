package com.yng.yngweekend.repository;

import com.yng.yngweekend.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {

}
