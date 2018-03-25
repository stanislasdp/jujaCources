import model.utils.ResourcesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

import java.io.FileInputStream;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String USERS_RESOURCE_NAME = "users.properties";

    @Override
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        ResourcesUtils.getResourceAsInputStream(USERS_RESOURCE_NAME);
        Properties prop = new Properties();
        prop.load(ResourcesUtils.getResourceAsInputStream(USERS_RESOURCE_NAME));
        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();

        auth.inMemoryAuthentication().withUser(userBuilder
                .username(prop.getProperty("name"))
                .password(prop.getProperty("password"))
                .roles("USER"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/loginPage")
                .loginProcessingUrl("/authenticateUser")
                .permitAll();
    }
}
