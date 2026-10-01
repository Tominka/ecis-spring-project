package cz.ecis.core.ent.lookup;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcisLookupTranslation implements Serializable {

    private static final long serialVersionUID = -1L;

    private String name;
    
    private String description;
}