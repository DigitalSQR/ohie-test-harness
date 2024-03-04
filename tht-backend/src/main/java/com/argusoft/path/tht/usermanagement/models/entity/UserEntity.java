package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This model is mapped to user table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "tht_user")
public class UserEntity extends IdStateMetaEntity {

    @Column(name = "email", updatable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "company_name")
    private String companyName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {})
    @JoinTable(
            name = "role_tht_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
    public UserEntity(){

    }

    public UserEntity(String id) {
        this.setId(id);
    }

    public UserEntity(UserEntity userEntity) {
        super(userEntity);
        this.setEmail(userEntity.getEmail());
        this.setName(userEntity.getName());
        this.setPassword(userEntity.getPassword());
        this.setCompanyName(userEntity.getCompanyName());
        if(userEntity.getRoles()!=null){
            this.setRoles(userEntity.getRoles().stream().map(RoleEntity::new).collect(Collectors.toSet()));
        }

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Set<RoleEntity> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }
}