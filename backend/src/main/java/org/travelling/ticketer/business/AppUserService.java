package org.travelling.ticketer.business;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.travelling.ticketer.constants.NotificationType;
import org.travelling.ticketer.constants.RoleType;
import org.travelling.ticketer.dao.AppUserDAO;
import org.travelling.ticketer.dao.NotificationDAO;
import org.travelling.ticketer.dao.RoleDAO;
import org.travelling.ticketer.dto.*;
import org.travelling.ticketer.entity.AppUser;
import org.travelling.ticketer.entity.Notification;
import org.travelling.ticketer.entity.Role;
import org.travelling.ticketer.mapper.AppUserMapper;
import org.travelling.ticketer.utility.ExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    private final AppUserDAO appUserDAO;

    private final AppUserMapper appUserMapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleDAO roleDAO;

    private final NotificationTypesService notificationTypesService;

    @Autowired
    public AppUserService(RoleDAO roleDAO, AppUserDAO appUserDAO, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder, NotificationTypesService notificationTypesService) {
        this.appUserDAO = appUserDAO;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleDAO = roleDAO;
        this.notificationTypesService = notificationTypesService;
    }

    public UserPrivilegesDTO getUserPrivileges(UserCredentialsDTO userFromFrontend){
        AppUser userFromDB = appUserDAO.findByUsername(userFromFrontend.getUserName()).orElseThrow(ExceptionBuilder.createIllegalArgumentException("User not found"));
        boolean isPasswordMatch = passwordEncoder.matches(userFromFrontend.getPassword(), userFromDB.getPassword());
        if (isPasswordMatch){
            return appUserMapper.mapPrivileges(userFromDB);
        }
        return null;
    }

    public boolean userExists (String username){
        return appUserDAO.existsByUsername(username);
    }

    public void addUser(AppUserDTO appUserDTO){
        Set<RoleType> roles = appUserDTO.getRoles().stream().map(role -> Enum.valueOf(RoleType.class, role)).collect(Collectors.toSet());
        Set<Role> roleEntities = roleDAO.findByRoleTypeIn(roles);
        Set<Notification> acceptedNotifications = notificationTypesService.getNotificationTypesFromDB(appUserDTO.getPersonalData());
        AppUser appUser = appUserMapper.mapUser(appUserDTO, roleEntities, acceptedNotifications);
        appUserDAO.save(appUser);
    }

    public void editUser (long userId, UserPersonalDTO userPersonalDTO){
        AppUser appUser = appUserDAO.findById(userId).orElseThrow(()->new IllegalArgumentException("user not found"));
        Set<Notification> acceptedNotifications = notificationTypesService.getNotificationTypesFromDB(userPersonalDTO);
        appUserMapper.mapUserPersonalData(appUser, userPersonalDTO, acceptedNotifications);
        appUserDAO.save(appUser);
    }

    public UserPersonalDTO getUserPersonalData(long userId){
        AppUser appUser = appUserDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return appUserMapper.mapUserPersonalData(appUser);
    }

    public UserPrivilegesDTO getUserPermissions(long userId){
        AppUser appUser = appUserDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return appUserMapper.mapPrivileges(appUser);
    }

    public AppUser getUserById (long userId){
       return  appUserDAO.findById(userId).orElseThrow(ExceptionBuilder.createIllegalArgumentException("User does not exist"));
    }

    public Set<UserRolesDTO> getUsersWithRoles (){
        List<AppUser> users = appUserDAO.findAll(Sort.by("username"));
        return appUserMapper.mapRoles(users);
    }


    public void editUserRoles(String username, Set<String> roles) {
        AppUser user = appUserDAO.findByUsername(username).orElseThrow(ExceptionBuilder.createIllegalArgumentException("User not found"));
        Set<RoleType> roleTypes = roles.stream().map(RoleType::valueOf).collect(Collectors.toSet());
        Set<Role> rolesEntities = roleDAO.findByRoleTypeIn(roleTypes);
        user.setRoles(rolesEntities);
        appUserDAO.save(user);
    }
}
