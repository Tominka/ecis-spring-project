package cz.ecis.criteria;

import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import cz.ecis.db.ent.QApplicationOverviewVieEnt;
import cz.ecis.model.dto.ChildFilterDto;

public class ApplicationOverviewVieCriteria {

    private static final QApplicationOverviewVieEnt A = QApplicationOverviewVieEnt.applicationOverviewVieEnt;

    private ApplicationOverviewVieCriteria() {
        /* This utility class should not be instantiated */
    }

    public static Predicate byChildFilter(ChildFilterDto filter, Long campId) {
        BooleanExpression exp = A.camp.id.eq(campId);

        if (filter == null) {
            return exp;
        }

        if (StringUtils.isNotEmpty(filter.getName())) {
            exp = exp.and(A.child.name.containsIgnoreCase(filter.getName()));
        }

        if (StringUtils.isNotEmpty(filter.getSurname())) {
            exp = exp.and(A.child.surname.eq(filter.getSurname()));
        }

        if (StringUtils.isNotEmpty(filter.getAddress())) {
            exp = exp.and(A.child.address.eq(filter.getAddress()));
        }

        if (filter.getBirthdate() != null) {
            exp = exp.and(A.child.birthdate.eq(filter.getBirthdate()));
        }

        return exp;
    }
}
