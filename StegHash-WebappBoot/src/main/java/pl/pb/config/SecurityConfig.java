//package pl.pb.config;
//
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.csrf.CsrfFilter;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
//
//import pl.pb.filters.CsrfHeaderFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	@Override
//    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
//        builder.inMemoryAuthentication().withUser("test").password("test").roles("USER").and().withUser("admin")
//                .password("admin").roles("ADMIN");
//    }
//
//	@Override
//	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests().anyRequest().permitAll();
////				.antMatchers("/","/css/**","/js/**","/login-post","/user","/login.html","/welcome.html")
////				.permitAll().anyRequest().authenticated()
////				.and().formLogin().loginPage("/login.html").permitAll()
////				//.defaultSuccessUrl("/index.html").permitAll()
////				.and()
////				.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
////				.csrf().csrfTokenRepository(csrfTokenRepository())
////				.and().logout().logoutSuccessUrl("/login.html").deleteCookies("XSRF-TOKEN");
//
//	}
//
//	/*
//	 * tell Spring Security to expect Angular CSRF token format - XSRF-TOKEN
//	 */
//	private CsrfTokenRepository csrfTokenRepository() {
//		  HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//		  repository.setHeaderName("X-XSRF-TOKEN");
//		  return repository;
//		}
//
//	@Override
//    public void configure(WebSecurity web) throws Exception {
//      web
//        .ignoring()
//           .antMatchers("/resources/**"); // #3
//    }
//
//}
