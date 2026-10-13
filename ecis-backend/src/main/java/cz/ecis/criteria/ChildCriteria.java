package cz.ecis.criteria;

import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import cz.ecis.db.ent.QChildEnt;
import cz.ecis.model.dto.ChildFilterDto;

public class ChildCriteria {

    private ChildCriteria() {
        /* This utility class should not be instantiated */
    }

    public static Predicate byFilter(ChildFilterDto filter, Long campId) {
        QChildEnt ch = QChildEnt.childEnt;
        BooleanBuilder builder = new BooleanBuilder().and(ch.applications.any().camp.id.eq(campId));

        if (filter == null) {
            return builder;
        }

        if (StringUtils.isNotEmpty(filter.getName())) {
            builder.and(ch.name.containsIgnoreCase(filter.getName()));
        }

        if (StringUtils.isNotEmpty(filter.getSurname())) {
            builder.and(ch.surname.eq(filter.getSurname()));
        }

        if (StringUtils.isNotEmpty(filter.getAddress())) {
            builder.and(ch.address.eq(filter.getAddress()));
        }

        if (filter.getBirthdate() != null) {
            builder.and(ch.birthdate.eq(filter.getBirthdate()));
        }

        return builder;
    }
}
