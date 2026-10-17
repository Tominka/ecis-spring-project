package cz.ecis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import cz.ecis.config.mapper.DefaultMapperConfig;
import cz.ecis.db.ent.ApplicationDocumentEnt;
import cz.ecis.db.ent.ApplicationEnt;
import cz.ecis.db.ent.ApplicationOverviewVieEnt;
import cz.ecis.db.ent.ApplicationParentEnt;
import cz.ecis.model.dto.ApplicationCreateDto;
import cz.ecis.model.dto.ApplicationDocumentDto;
import cz.ecis.model.dto.ApplicationDto;
import cz.ecis.model.dto.ApplicationOverviewDto;
import cz.ecis.model.dto.ParentDto;

@Mapper(componentModel = ComponentModel.SPRING, config = DefaultMapperConfig.class, uses = {ParentMapper.class})
public interface ApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping (target = "camp", ignore = true)
    @Mapping (target = "confirmedAt", ignore = true)
    @Mapping (target = "canceledAt", ignore = true)
    ApplicationEnt create(ApplicationCreateDto dto);

    ApplicationDto toDto(ApplicationEnt ent);

    ApplicationDocumentDto toDocumentDto(ApplicationDocumentEnt ent);

    ApplicationOverviewDto toOverviewDto(ApplicationOverviewVieEnt ent);

    @Mapping(target = "documentCount", expression = "java(ent.getDocuments() == null ? 0L : (long) ent.getDocuments().size())")
    ApplicationOverviewDto toOverviewDto(ApplicationEnt ent);

    @Mapping(source = "parent", target = ".")
    ParentDto toParentDto(ApplicationParentEnt ent);
}
