package ru.octol1ttle.knockdowns.common.api;

import java.util.UUID;

public interface IKnockableDown {
    boolean knockdowns$isKnockedDown();

    void knockdowns$setKnockedDown(boolean knockedDown);

    boolean knockdowns$isBeingRevived();

    void knockdowns$setBeingRevived(boolean beingRevived);

    UUID knockdowns$getUuid();
}
