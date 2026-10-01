package cz.ecis.config.db;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return properties -> {
            properties.put("org.hibernate.envers.default_schema", "audit"); // Nastavení envers
            properties.put("org.hibernate.envers.audit_table_suffix","_history"); // Nastavení envers
            properties.put("org.hibernate.envers.revision_field_name", "id_revision"); // Nastavení envers
            properties.put("org.hibernate.envers.revision_type_field_name", "revision_type"); // Nastavení envers
        };
    }
}