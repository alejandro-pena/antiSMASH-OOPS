package uk.ac.mib.antismashoops;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class MvcConfiguration extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter
{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("/local/**")
				.addResourceLocations("file:appData/NC_003888.3/2acd7e9e-4872-48d4-bae9-cac30ec52622/");

		super.addResourceHandlers(registry);
	}
}