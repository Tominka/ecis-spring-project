package cz.ecis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import cz.ecis.core.ent.lookup.EcisLookupEntity;
import cz.ecis.core.mapper.LookupBaseMapper;
import cz.ecis.model.dto.LookupBaseDto;

@Mapper(componentModel = ComponentModel.SPRING)
public interface LookupMapper extends LookupBaseMapper {

    LookupBaseDto toDto(EcisLookupEntity<Long> ent);

}
