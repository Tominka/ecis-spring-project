package cz.ecis.db.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import cz.ecis.core.ent.VersionAwareEntity;

@Component
public class EntityChangeListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private static final Logger LOGGER = LogManager.getLogger(EntityChangeListener.class);

    private final Map<Class<?>, Integer> versionIndexCache = new ConcurrentHashMap<>();

    @Override
    public void onPostInsert(PostInsertEvent event) {
        LOGGER.info("Entity inserted: {} id={}", event.getEntity().getClass().getSimpleName(), event.getId());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        Object entity = event.getEntity();

        LOGGER.info("Entity updated: {} id={}", entity.getClass().getSimpleName(), event.getId());

        if (entity instanceof VersionAwareEntity) {
            int versionIndex = getVersionIndex(event);

            if (versionIndex >= 0) {
                Object[] state = event.getState();
                Object[] oldState = event.getOldState();

                Object newVersion = state != null ? state[versionIndex] : null;
                Object oldVersion = oldState != null ? oldState[versionIndex] : null;

                LOGGER.info(
                    "Entity {} changed: id={} oldVersion={} newVersion={}",
                    entity.getClass().getSimpleName(),
                    event.getId(),
                    oldVersion,
                    newVersion
                );
            }
        }
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        LOGGER.info("Entity deleted: {} id={}",
                event.getEntity().getClass().getSimpleName(),
                event.getId());
    }

    @Override
    public boolean requiresPostCommitHandling(
            EntityPersister persister) {
        return false;
    }

    private int getVersionIndex(PostUpdateEvent event) {
        return versionIndexCache.computeIfAbsent(
            event.getEntity().getClass(),
            clazz -> event.getPersister().getVersionPropertyIndex()
        );
    }
}