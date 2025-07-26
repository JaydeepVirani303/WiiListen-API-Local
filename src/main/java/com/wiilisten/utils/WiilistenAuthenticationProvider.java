package com.wiilisten.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.wiilisten.enums.ErrorDataEnum;

@Component
public class WiilistenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private CommonServices commonServices;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        throw new UsernameNotFoundException(
                commonServices.getMessageByCode(ErrorDataEnum.BAD_CREDENTIALS_MSG.getCode()));

    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public boolean supports(final Class<?> authentication) {

        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }


}
