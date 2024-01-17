package ru.octol1ttle.knockdowns.common.api;

public interface IKnockableDown {
    boolean is_KnockedDown();

    void set_KnockedDown(boolean knockedDown);

    int get_ReviverCount();

    void set_ReviverCount(int reviverCount);

    boolean is_Reviving();

    void set_Reviving(boolean reviving);

    int get_ReviveTimer();

    void set_ReviveTimer(int reviveTimer);
}
