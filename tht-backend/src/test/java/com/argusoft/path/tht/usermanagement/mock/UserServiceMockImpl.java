package com.argusoft.path.tht.usermanagement.mock;

import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapper;
import com.argusoft.path.tht.usermanagement.repository.RoleRepository;
import com.argusoft.path.tht.usermanagement.repository.TokenVerificationRepository;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class UserServiceMockImpl {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

//    @Autowired
//    RoleRepository roleRepository;

    @Autowired
    TokenVerificationRepository tokenVerificationRepository;

    public void init() {
        createUser("user.01", "Dummy User1", "dummyuser1@testmail.com", "password");
        createUser("user.02","Dummy User2", "dummyuser2@testmail.com", "password");
        createUser("user.03","Dummy User3", "dummyuser3@testmail.com", "password");
        createUser("user.04","Dummy User4", "dummyuser4@testmail.com", "password");
        createUser("user.05","Dummy User5", "dummyuser5@testmail.com", "password");
    }

    public UserInfo createUser(String id, String name, String email, String password){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setName(name);
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        userEntity.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(UserServiceConstants.ROLE_ID_ASSESSEE);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);

        userRepository.save(userEntity);
        return userMapper.modelToDto(userEntity);
    }

    public void clear() {
        tokenVerificationRepository.deleteAll();
        tokenVerificationRepository.flush();
//        Optional<UserEntity> superUser = userRepository.findUserByEmail("noreplytestharnesstool@gmail.com");
//        List<UserEntity> findAll = userRepository.findAll();
//        findAll.remove(superUser);
//        userRepository.deleteAll(findAll);
//        userRepository.deleteAll();
//        userRepository.flush();

        List userIds = List.of("user.01", "user.02", "user.03", "user.04", "user.05");
        for (Object userId : userIds) {
            userRepository.deleteById(userId.toString());
        }
    }

}
