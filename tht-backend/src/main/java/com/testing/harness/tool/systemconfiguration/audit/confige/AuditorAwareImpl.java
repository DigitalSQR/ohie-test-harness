package com.testing.harness.tool.systemconfiguration.audit.confige;

import com.testing.harness.tool.systemconfiguration.constant.Constant;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()) {
            return Optional.of(Constant.DEFAULT_AUDIT_NAME);
        }
        if (Constant.ANONYMOUS_USER.equals(authentication.getPrincipal())) {
            return Optional.of(Constant.ANONYMOUS_USER_NAME);
        }
        return Optional.of(((User) authentication.getPrincipal()).getUsername());
    }

}
