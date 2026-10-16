package cz.ecis.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;

import cz.ecis.db.ent.ApplicationOverviewVieEnt;

public interface ApplicationOverviewVieRepository extends JpaRepository<ApplicationOverviewVieEnt, Long>, QuerydslPredicateExecutor<ApplicationOverviewVieEnt> {

    List<ApplicationOverviewVieEnt> findAll(Predicate predicate);

}