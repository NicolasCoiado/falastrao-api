package br.com.falastrao.falastrao.security.userdetails;

import br.com.falastrao.falastrao.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
}