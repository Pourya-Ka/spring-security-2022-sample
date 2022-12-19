package com.example.demo.config;

import com.example.demo.dao.UserDao;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAthFilter extends OncePerRequestFilter {
    private final UserDao userDao;
    private final JwtUtils jwtUtils;

    //    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//        final String autHeader = request.getHeader(AUTHORIZATION);
//        final String userEmail;
//        final String jwtToken;
//        if (autHeader == null || !autHeader.startsWith("Bearer")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        jwtToken = autHeader.substring(7);
//        userEmail = jwtUtils.extractUsername(jwtToken);
//        if (userEmail != null &&
//                SecurityContextHolder.getContext().getAuthentication() == null
//        ) {
//            UserDetails userDetails = userDao.userFindByEmail(userEmail);
//
//            if (jwtUtils.validateToken(jwtToken, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//
//    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws IOException, ServletException {
        final String autHeader = request.getHeader(AUTHORIZATION);
        final String userEmail;
        final String jwtToken;
        if (autHeader == null || !autHeader.startsWith("Bearer")) {

            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = autHeader.substring(7);
        userEmail = jwtUtils.extractUsername(jwtToken);
        if (userEmail != null &&
                SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            UserDetails userDetails = userDao.userFindByEmail(userEmail);

            if (jwtUtils.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);

    }
}
