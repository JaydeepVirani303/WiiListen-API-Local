package com.wiilisten.utils;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.wiilisten.entity.Administration;
import com.wiilisten.entity.User;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.UserDetail;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	private final static Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private ServiceRegistry serviceRegistry;

	@Autowired
	private CommonServices commonServices;

	@Autowired
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver resolver;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

		try {

			if (resolver == null) {
				final ServletContext servletContext = request.getServletContext();
				final WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getWebApplicationContext(servletContext);
				resolver = webApplicationContext.getBean("handlerExceptionResolver", HandlerExceptionResolver.class);
			}

			if (tokenUtil == null) {
				final ServletContext servletContext = request.getServletContext();
				final WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getWebApplicationContext(servletContext);
				tokenUtil = webApplicationContext.getBean(TokenUtil.class);
			}

			if (serviceRegistry == null) {
				final ServletContext servletContext = request.getServletContext();
				final WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getWebApplicationContext(servletContext);
				serviceRegistry = webApplicationContext.getBean(ServiceRegistry.class);
			}

			if (commonServices == null) {
				final ServletContext servletContext = request.getServletContext();
				final WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getWebApplicationContext(servletContext);
				commonServices = webApplicationContext.getBean(CommonServices.class);
			}

			String authTokenFotDatabase = request.getHeader(ApplicationConstants.MOBILE_AUTH_HEADER);

			String path = request.getServletPath();
			if (!ApplicationUtils.isEmpty(authTokenFotDatabase)
					&& authTokenFotDatabase.startsWith(ApplicationConstants.BEARER)) {
				authTokenFotDatabase = authTokenFotDatabase.substring(7);

				final String userName = tokenUtil.getUsernameFromToken(authTokenFotDatabase);
				if (userName != null) {
					final User user = serviceRegistry.getUserService().findByEmailAndActiveTrue(userName);
					final Administration administration = serviceRegistry.getAdministrationService()
							.findByEmailAndActiveTrue(userName);
					if (user != null) {
						try {
							if(!isTokenValid(userName, authTokenFotDatabase)) {
								LOGGER.info("isTokenValid FALSE");
								throw new AccessDeniedException(
										commonServices.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
							}
							} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (path.contains(ApplicationURIConstants.ADMIN)
								&& !serviceRegistry.getAdministrationService().existsByEmailAndActiveTrue(userName)) {
							LOGGER.info("Invalid user role " + user.getRole() + " to access admin path 1" + path);
							throw new AccessDeniedException(
									commonServices.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
						}
						if (path.startsWith(ApplicationURIConstants.API + ApplicationURIConstants.V1
								+ ApplicationURIConstants.LISTENER)) {
							if (!user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
								LOGGER.info("Invalid user role " + user.getRole() + " to access " + path);
								throw new AccessDeniedException(commonServices
										.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
							}
						} else if (path.startsWith(ApplicationURIConstants.API + ApplicationURIConstants.V1
								+ ApplicationURIConstants.CALLER)) {
							if (!user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
								LOGGER.info("Invalid user role " + user.getRole() + " to access 2" + path);
								throw new AccessDeniedException(commonServices
										.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
							}
						} else if (path.startsWith(ApplicationURIConstants.API + ApplicationURIConstants.V1
								+ ApplicationURIConstants.COMMON)) {
							if (!user.getRole().equals(UserRoleEnum.CALLER.getRole())
									&& !user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
								LOGGER.info("Invalid user role " + user.getRole() + " to access " + path);
								throw new AccessDeniedException(commonServices
										.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
							}
						}

						final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
								new UserDetail(user.getEmail(), user.getPassword(), user.getRole()), user.getPassword(),
								new ArrayList<>());
						final Authentication authentication = token;
						SecurityContextHolder.getContext().setAuthentication(authentication);

						filterChain.doFilter(request, response);
					}

					else if (administration != null)

					{

						if (path.startsWith(ApplicationURIConstants.API + ApplicationURIConstants.V1
								+ ApplicationURIConstants.ADMIN)) {
							if (!administration.getRole().equals(UserRoleEnum.ADMIN.getRole())
									&& !administration.getRole().equals(UserRoleEnum.SUBADMIN.getRole())) {

								throw new AccessDeniedException(commonServices
										.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
							}
						} else if (path
								.contains(ApplicationURIConstants.API + ApplicationURIConstants.V1
										+ ApplicationURIConstants.AUTH + ApplicationURIConstants.LOGOUT)
								|| path.contains(ApplicationURIConstants.API + ApplicationURIConstants.V1
										+ ApplicationURIConstants.AUTH + ApplicationURIConstants.CHANGE_PASSWORD)
								|| path.contains(ApplicationURIConstants.API + ApplicationURIConstants.V1
										+ ApplicationURIConstants.TUTORIALS)
								|| path.contains(ApplicationURIConstants.API + ApplicationURIConstants.V1
										+ ApplicationURIConstants.LISTENER + ApplicationURIConstants.EARNING_HISTORY
										+ ApplicationURIConstants.LIST)) {
							if (!administration.getRole().equals(UserRoleEnum.ADMIN.getRole())
									&& !administration.getRole().equals(UserRoleEnum.SUBADMIN.getRole())) {

								throw new AccessDeniedException(commonServices
										.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
							}
						} else {

							throw new AccessDeniedException(
									commonServices.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
						}
						final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
								new UserDetail(administration.getEmail(), administration.getPassword(),
										administration.getRole()),
								administration.getPassword(), new ArrayList<>());
						final Authentication authentication = token;
						SecurityContextHolder.getContext().setAuthentication(authentication);

						filterChain.doFilter(request, response);
					}

					else {

						throw new BadCredentialsException(
								commonServices.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
					}
				} else {

					throw new BadCredentialsException(
							commonServices.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
				}
			} else {

				throw new BadCredentialsException(
						commonServices.getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()));
			}

		}

		catch (BadCredentialsException | LockedException | AccessDeniedException e) {
			resolver.resolveException(request, response, null, e);
		}

	}
	 public boolean isTokenValid(String email, String token) throws SQLException {
	        // Retrieve the stored token for the user
	       
	    	User user = serviceRegistry.getUserService().findByEmailAndActiveTrue(email);
	    	 String storedToken = user.getJwtToken();
	        // Validate the provided token matches the stored token
	        return token.equals(storedToken) && tokenUtil.getClaimsFromToken(token) != null && !user.getIsSuspended();
	    }
//  if (request.getHeader(ApplicationConstants.USER_AGENT)
//  .indexOf(ApplicationConstants.MOBILE) != -1
//  || request.getHeader(ApplicationConstants.USER_AGENT)
//  .indexOf(ApplicationConstants.POSTMAN) != -1
//  || (request.getHeader(ApplicationConstants.X_APIKEY)!=null && request.getHeader(ApplicationConstants.X_APIKEY)
//  .indexOf(ApplicationConstants.PORTAL) != -1)) {
//
//
//final String authToken = request
//      .getHeader(ApplicationConstants.MOBILE_AUTH_HEADER);
//processMobileLogin(authToken);
//
//}
//
//filterChain.doFilter(request, response);

	/**
	 * This is used to process mobile login requests.
	 *
	 * @param authToken
	 */
//    private void processMobileLogin(String authToken) {
//    	//System.err.println("================");
//        if (!ApplicationUtils.isEmpty(authToken)
//                && authToken.startsWith(ApplicationConstants.BEARER)) {
//            authToken = authToken.replace(ApplicationConstants.BEARER, "");
//        }
//
//        // contains user's mobile number and country code
//        final String mobile = tokenUtil.getUsernameFromToken(authToken);
//        System.err.println(mobile);
//        if (!ApplicationUtils.isEmpty(mobile)) {
//
//            final User employee = serviceRegistry.getUserService().findOne(Long.valueOf(mobile));
//          
//            if (tokenUtil.validateToken(authToken, employee).booleanValue()) {
//                final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                        new UserDetail(employee), employee.getPassword(),
//                        new ArrayList<>());
//                final Authentication authentication = token;
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//    }

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		final String path = request.getServletPath();

		return path.contentEquals("/v3/api-docs") || path.contentEquals("/swagger-ui.html")
				|| path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")

				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.LOGIN)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.OPEN_ENDED_QUESTIONS )
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.FAQ )
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.CHECK_USERNAME)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.REGISTER)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.FORGOT_PASSWORD)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.SEND_OTP)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.VERIFY_OTP)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.AUTH + ApplicationURIConstants.NEW_PASSWORD)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.NEWS_LETTER_SUBSCRIBE)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.PAGE_CONTENT + ApplicationURIConstants.LIST)
				|| path.contentEquals(ApplicationURIConstants.API + ApplicationURIConstants.V1
						+ ApplicationURIConstants.CONTACT_US + ApplicationURIConstants.ADD)
				|| path.startsWith(ApplicationURIConstants.PAGE_CONTENT)

				|| path.equals("/redoc/**") || path.equals("/v2/api-docs") || path.equals("/api/v2/api-docs");
	}

}
