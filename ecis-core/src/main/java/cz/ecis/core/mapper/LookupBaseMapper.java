package cz.ecis.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cz.ecis.core.ent.lookup.EcisLookupEntity;
import cz.ecis.core.ent.lookup.EcisLookupTranslation;
import cz.ecis.core.model.LovDto;
import cz.ecis.localization.EcisLocaleContext;

@Mapper(componentModel = "spring")
public abstract class LookupBaseMapper {

    @Mapping(target = "label", ignore = true)
    abstract LovDto toLovDto(EcisLookupEntity<Long> ent);

    @AfterMapping
    void afterLookupBaseMapping(@MappingTarget LovDto dto, EcisLookupEntity<Long> ent) {
        dto.setLabel(getLovMappingLabel(ent));
    }

    public static String getLovMappingLabel(EcisLookupEntity<Long> ent) {
        EcisLookupTranslation trans = ent.getTranslations().get(EcisLocaleContext.getLocale());
        if (trans != null) {
            return trans.getName();
        }

        return "";
    }

}
