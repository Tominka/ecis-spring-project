package cz.ecis.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.UserSettingsEnt;

public interface UserSettingsRepository extends JpaRepository<UserSettingsEnt, Long> {

}
