package pt.ulisboa.tecnico.bling_bank.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.bling_bank.account.domain.AccountHolder;
import pt.ulisboa.tecnico.bling_bank.account.domain.Role;
import pt.ulisboa.tecnico.bling_bank.account.repository.AccountHolderRepository;
import pt.ulisboa.tecnico.bling_bank.config.JwtService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AccountHolderRepository accountHolderRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var holder = AccountHolder.builder()
            .holderName(request.getHolderName())
            .holderPassword(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        accountHolderRepository.save(holder);
        var jwtToken = jwtService.generateToken(holder);
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getHolderName(), request.getPassword()));
        var holder = accountHolderRepository.findById(request.getHolderName()).orElseThrow();
        var jwtToken = jwtService.generateToken(holder);
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }
}
