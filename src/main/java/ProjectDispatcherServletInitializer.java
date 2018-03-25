import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ProjectDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        //no root config
        return new Class[]{WebConfig.class, AppSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
