package ru.espada.ep.iptip.user;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.espada.ep.iptip.s3.S3Service;
import ru.espada.ep.iptip.user.permission.UserPermissionEntity;
import ru.espada.ep.iptip.user.profile.ProfileEntity;
import ru.espada.ep.iptip.user.profile.ProfileRepository;
import ru.espada.ep.iptip.user.models.request.AddRoleRequest;
import ru.espada.ep.iptip.user.models.request.CreateProfileRequest;
import ru.espada.ep.iptip.user.permission.UserPermissionService;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder bCryptPasswordEncoder;
    private UserPermissionService userPermissionService;
    @Value("${users.page-size}")
    private int pageSize;
    private ProfileRepository profileRepository;
    private S3Service s3Service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    @Cacheable(value = "users", key = "'users'", unless = "#result == null")
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#page - 'users'", unless = "#result == null")
    public List<UserEntity> allUsers(int page) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return userRepository.findAll(pageable).toList();
    }

    @CacheEvict(value = "users", key = "'users'")
    public void saveUser(UserEntity user) {
        UserEntity userFromDB = userRepository.findByUsername(user.getUsername()).orElse(null);

        if (userFromDB != null) {
            throw new IllegalArgumentException("exception.user.user_exists");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @CacheEvict(value = "users", key = "'users'")
    public void deleteUser(String deleteUserId) {
        UserEntity user = userRepository.findByUsername(deleteUserId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);
    }

    public UserEntity getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserEntity getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void createProfile(Principal principal, CreateProfileRequest createProfileRequest) {
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!Objects.equals(user.getId(), createProfileRequest.getUserId())) {
            throw new IllegalArgumentException("exception.user.only_owner");
        }
        if (user.getProfile() != null) {
            throw new IllegalArgumentException("exception.user.customer_exists");
        }

        ProfileEntity profile = new ProfileEntity();
        profile.setName(createProfileRequest.getName());
        profile.setSurname(createProfileRequest.getSurname());
        profile.setPhone(createProfileRequest.getPhone());
        profile.setEmail(createProfileRequest.getEmail());
        if (createProfileRequest.getPatronymic() != null) {
            profile.setPatronymic(createProfileRequest.getPatronymic());
        }
        if (createProfileRequest.getBirthDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                profile.setBirthDate(formatter.parse(createProfileRequest.getBirthDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        profileRepository.save(profile);
        user.setProfile(profile);
        userRepository.save(user);
    }

    public CompletableFuture<String> uploadAvatar(String name, byte[] avatar) {
        UserEntity user = userRepository.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return s3Service.uploadPng(avatar, "user-" + user.getId(), "user-avatar")
                .thenCompose(fileName -> {
                    if (fileName == null) {
                        throw new IllegalArgumentException("exception.user.avatar_upload_failed");
                    } else {
                        return s3Service.getFileUrl("user-avatar", fileName);
                    }
                }).thenApply(url -> url);
    }

    public String getAvatarUrl(String name) {
        UserEntity user = userRepository.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return s3Service.getFileUrl("user-avatar", "user-" + user.getId()).join();
    }

    public void addPermission(String username, AddRoleRequest addRoleRequest) {
        String permission = addRoleRequest.getPermission();
        for (String key : addRoleRequest.getCredentials().keySet()) {
            permission = permission.replace("{" + key + "}", addRoleRequest.getCredentials().get(key));
        }

        if (!userPermissionService.hasParentPermission(username, permission)) {
            throw new RuntimeException("exception.user.permission_not_found");
        }

        userPermissionService.addPermission(addRoleRequest.getUsername(), permission, addRoleRequest.getStartTime(), addRoleRequest.getEndTime());
    }


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    @Lazy
    public void setBCryptPasswordEncoder(PasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setCustomerRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Autowired
    public void setS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @Autowired
    public void setUserPermissionService(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }
}
