package uk.ac.mib.antismashoops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class AntiSmashOopsApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AntiSmashOopsApplication.class, args);
    }


    @Bean
    public WebMvcConfigurerAdapter adapter()
    {
        return new WebMvcConfigurerAdapter()
        {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry)
            {
                registry.addResourceHandler("/local/**").addResourceLocations("file:appData/");
                registry.addResourceHandler("/lifeTree/**").addResourceLocations("file:lifeTreeOutput/");

                super.addResourceHandlers(registry);
            }
        };
    }
}
