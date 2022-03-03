package app.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;
import java.util.Set;

import org.springframework.http.HttpMethod;

import javax.mail.AuthenticationFailedException;


@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JWTokenUtil tokenizer;

    private static final Set<String> NON_AUTH_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/login/refresh"
    );

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Always continue with OPTIONS request.
            System.out.println("Headers");
            for (Iterator<String> it = request.getHeaderNames().asIterator(); it.hasNext(); ) {
                String header = it.next();
                System.out.printf("%s: %s %n", header, request.getHeader(header));
            }
            if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
                filterChain.doFilter(request, response);
                return;
            }

            String servlet = request.getServletPath();

            // Allow for h2, as used on staging and development.
            if (servlet.startsWith("/h2-console")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Pass socket.
            if (servlet.startsWith("/socket/greenhouse")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (servlet.startsWith("/invite/user")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Check if the user wants to reach the auth paths.
            if (NON_AUTH_PATHS.contains(servlet)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (token == null) throw new AuthenticationException("invalid token");

            // Get token information.
            token = token.replace("Bearer ", "");
            JWTokenInfo jwTokenInfo = this.tokenizer.getInfo(token);

            if (jwTokenInfo.getExpiration().before(new Date(System.currentTimeMillis())))
                throw new AuthenticationException("token is expired");

            if (jwTokenInfo.getTeam() == 0 && !servlet.startsWith("/api/auth/team/"))
                throw new AuthenticationFailedException();

            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {

            // Handle 401 error.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, String.format("Cannot be authenticated because: %s",
                    e.getMessage()));
        } catch (AuthenticationFailedException e) {

            // Handle 403 error.
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This path is forbidden");
        } catch (JWTokenUtil.expired e) {//Handle expired token error
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token has expired");
        }
    }
}
