package com.example.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
@EnableScheduling
public class FileConfig {

    private static final Logger logger = LoggerFactory.getLogger(FileConfig.class);

    @Bean
    public ViewResolver getResolver() {
        logger.info("Initializing ViewResolver for JSP views");
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setPrefix("/view/");
        vr.setSuffix(".jsp");
        return vr;
    }

    @Bean
    public DataSource dataSource() {
        logger.info("Setting up DataSource for MySQL database");
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/HyperGridK");
        ds.setUsername("root");
        ds.setPassword("12345678");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        logger.info("Configuring EntityManagerFactory with Hibernate");
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.example.entity"); // scan entities
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
        emf.setJpaProperties(props);

        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        logger.info("Initializing TransactionManager");
        JpaTransactionManager tx = new JpaTransactionManager();
        tx.setEntityManagerFactory(emf);
        return tx;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        logger.info("Configuring MultipartResolver for file uploads");
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(10_000_000L); // 10 MB
        return resolver;
    }
}
