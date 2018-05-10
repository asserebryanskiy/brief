package com.name.brief.config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.name.brief.repository")
public class DataConfig {
    private final Environment env;

    @Autowired
    public DataConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        factory.setDataSource(dataSource());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(env.getProperty("brief.entity.package"));

        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.implicit_naming_strategy",env.getProperty("hibernate.implicit_naming_strategy"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        factory.setJpaProperties(properties);

        return factory;
    }

    @Bean
    @Profile({"dev", "localPostgre", "none", "prod", "vm", "ubuntu"})
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("brief.db.driver"));
        ds.setUrl(env.getProperty("brief.db.url"));
        ds.setUsername(env.getProperty("brief.db.username"));
        ds.setPassword(env.getProperty("brief.db.password"));
        return ds;
    }

    @Bean(name = "dataSource")
    @Profile("heroku")
    public DataSource herokuDataSource() throws URISyntaxException {
//        URI dbUri = new URI("postgres://fibufkhugjdcwh:96f0e8f210edb6c3dc3e0898da86bae663d22032970fc5aa2851117967ec032e@ec2-50-17-206-214.compute-1.amazonaws.com:5432/d3q7op2e2vk40r");
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}
