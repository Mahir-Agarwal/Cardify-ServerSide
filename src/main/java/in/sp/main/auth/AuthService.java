package in.sp.main.auth;

import in.sp.main.core.constants.ErrorCode;
import in.sp.main.core.constants.Role;
import in.sp.main.core.exception.CustomException;
import in.sp.main.core.security.JwtUtils;
import in.sp.main.core.security.UserDetailsImpl;
import in.sp.main.user.User;
import in.sp.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "Error: Email is already in use!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = signUpRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.BUYER); 
        }
        user.setRoles(roles);
        
        userRepository.save(user);

        return authenticateUser(new AuthRequest(signUpRequest.getEmail(), signUpRequest.getPassword()));
    }

    public AuthResponse authenticateUser(AuthRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId()).orElseThrow();
        return new AuthResponse(jwt, userDetails.getId(), user.getName(), user.getEmail());
    }
}
