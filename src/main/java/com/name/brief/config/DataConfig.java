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
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.name.brief.repository")
@PropertySource("classpath:db.properties")
public class DataConfig {
    private final Environment env;

    @Autowired
    public DataConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = setCommonEntityManagerProperties();
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.implicit_naming_strategy",env.getProperty("hibernate.implicit_naming_strategy"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        factory.setJpaProperties(properties);

        return factory;
    }

    @Bean("entityManagerFactory")
    @Profile("heroku")
    public LocalContainerEntityManagerFactoryBean herokuEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = setCommonEntityManagerProperties();
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.heroku.dialect"));
        properties.put("hibernate.implicit_naming_strategy",env.getProperty("hibernate.heroku.implicit_naming_strategy"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.heroku.format_sql"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.heroku.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.heroku.hbm2ddl.auto"));
        factory.setJpaProperties(properties);

        return factory;
    }

    private LocalContainerEntityManagerFactoryBean setCommonEntityManagerProperties() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        factory.setDataSource(dataSource());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(env.getProperty("brief.entity.package"));
        return factory;
    }

    @Bean
    @Profile("dev")
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
    public DataSource herokuDataSource() {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("brief.db.heroku.driver"));
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
