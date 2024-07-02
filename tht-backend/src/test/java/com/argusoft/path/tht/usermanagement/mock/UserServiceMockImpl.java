package com.argusoft.path.tht.usermanagement.mock;

import com.argusoft.path.tht.notification.mock.NotificationServiceMockImpl;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapper;
import com.argusoft.path.tht.usermanagement.repository.TokenVerificationRepository;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceMockImpl {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenVerificationRepository tokenVerificationRepository;

    @Autowired
    NotificationServiceMockImpl notificationServiceMock;

    public void init() {
        createUser("user.01","Dummy User1", "dummyuser1@testmail.com", "$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe",UserServiceConstants.ROLE_ID_ASSESSEE);
        createUser("user.02","Dummy User2", "dummyuser2@testmail.com", "$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe",UserServiceConstants.ROLE_ID_ASSESSEE);
        createUser("user.03","Dummy User3", "dummyuser3@testmail.com", "$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe",UserServiceConstants.ROLE_ID_ASSESSEE);
        createUser("user.04","Dummy User4", "dummyuser4@testmail.com", "$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe",UserServiceConstants.ROLE_ID_ASSESSEE);
        createUser("user.05","Dummy User5", "dummyuser5@testmail.com", "$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe",UserServiceConstants.ROLE_ID_ASSESSEE);
        createUser("user.06","Dummy User6", "dummyuser6@testmail.com", "$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe",UserServiceConstants.ROLE_ID_TESTER);
        createUser("user.07","Dummy User7", "dummyuser7@testmail.com", "password", UserServiceConstants.ROLE_ID_ADMIN);
        createUser("user.08","Dummy User8", "dummyuser8@testmail.com", "password", UserServiceConstants.ROLE_ID_ASSESSEE);

    }

    public UserInfo createUser(String id, String name, String email, String password, String role){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setName(name);
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        userEntity.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        if(id.equals("user.03")){
            userEntity.setState(UserServiceConstants.USER_STATUS_INACTIVE);
        }
        userEntity.setCompanyName("Argusoft");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(role);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);

        userRepository.save(userEntity);
        return userMapper.modelToDto(userEntity);
    }

    public void clear() {
        tokenVerificationRepository.deleteAll();
        tokenVerificationRepository.flush();
        List<UserEntity> findAll = userRepository.findAll();
        for (UserEntity user : findAll) {
            if(!user.getEmail().equals("noreplytestharnesstool@gmail.com")){
                userRepository.deleteById(user.getId());
            }
        }
    }

}
