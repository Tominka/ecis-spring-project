package cz.ecis.core.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cz.ecis.db.ent.CampEnt;
import cz.ecis.db.ent.CampRoleEnt;
import cz.ecis.db.ent.RoleEnt;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.repo.CampRepository;
import cz.ecis.db.repo.CampRoleRepository;
import cz.ecis.db.repo.RoleRepository;
import cz.ecis.db.repo.UserRepository;
import cz.ecis.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EcisUserDetailsService implements UserDetailsService {

    private final RoleRepository roleRepository;
    private final CampRoleRepository campRoleRepository;
    private final CampRepository campRepository;
    private final UserRepository userRepository;

    @Override
    public EcisUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.loadUserByUsername(username, true);
    }

    public EcisUserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        UserEnt user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<String> roles = List.of();
        List<CampEnt> camps = List.of();

        if (loadRoles) {
            if (user.isSystemAdmin()) { // Load all enabled roles
                roles = this.roleRepository.findAllByEnabledTrue().stream()
                    .map(RoleEnt::getCode)
                .toList();
                camps = this.campRepository.findAll();
            } else { // Load only enabled global roles. Camp roles will load as needed in interceptor
                roles = this.campRoleRepository.findAllByRoleGlobalTrueAndRoleEnabledTrueAndUserId(user.getId()).stream()
                    .map(r -> r.getRole().getCode())
                .toList();
                camps = this.campRepository.findAllUserCamps(user.getId());
            }
        }

        return new EcisUserDetails(user, roles, camps);
    }

    @Transactional
    public CampRoleEnt assignUserToCamp(CampEnt camp) {
        RoleEnt role = this.roleRepository.findByCodeAndEnabledTrue(EcisRoleEnum.USER.getCode());
        CampRoleEnt ent = new CampRoleEnt();
        ent.setUser(SecurityUtils.getUserEnt());
        ent.setCamp(camp);
        ent.setRole(role);

        return this.campRoleRepository.save(ent);
    }
}
