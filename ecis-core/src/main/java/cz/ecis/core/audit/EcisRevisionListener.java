package cz.ecis.core.audit;

import org.hibernate.envers.RevisionListener;

import cz.ecis.db.ent.RevisionEnt;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EcisRevisionListener implements RevisionListener {
	
	private final IEcisRevisionResolver resolver;

    @Override
    public void newRevision(Object revisionEntity) {

        RevisionEnt revision = (RevisionEnt) revisionEntity;

        revision.setUsername(this.resolver.resolveUsername());

        revision.setSource(this.resolver.resolveApplication());
    }
}