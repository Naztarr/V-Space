package com.naz.vSpace.config;

import com.naz.vSpace.exception.VSpaceException;
import com.naz.vSpace.service.serviceImplementation.JwtImplementation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;


/**
 * Configuration class to handle logout functionality.
 */
@Configuration
@RequiredArgsConstructor
public class LogoutConfiguration implements LogoutHandler {
    private final JwtImplementation jwtImplementation;

    /**
     * Logout handler method.
     *
     * @param request        The HTTP servlet request.
     * @param response       The HTTP servlet response.
     * @param authentication The authentication object.
     */
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response, Authentication authentication) {
        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer")){
            return;
        }

        String jwtToken = header.substring(7);

        if(!jwtImplementation.isExpired(jwtToken)) {
            // Invalidate the current session
            request.getSession().invalidate();
            // Clear the authentication token
            response.setHeader("Authorization", "");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            throw new VSpaceException("Your session has expired. Please login");
        }
    }
}
