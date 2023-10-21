package com.peters.userservice.security;


import com.peters.userservice.entity.User;
import com.peters.userservice.exception.ApplicationAuthenticationException;
import com.peters.userservice.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username).orElseThrow(()-> new ApplicationAuthenticationException("Invalid username and password combination"));
        if(user==null){
            throw new ApplicationAuthenticationException("Access denied, no account found for this user");
        }

        if(user.isAccountDeleted()){
            throw new ApplicationAuthenticationException("Access denied, this account has been deleted");
        }

        return new CustomUserDetails(user);
    }
}
