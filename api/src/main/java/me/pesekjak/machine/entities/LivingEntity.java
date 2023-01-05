package me.pesekjak.machine.entities;

/**
 * Represents a living entity, such as a monster or player.
 */
public interface LivingEntity extends Entity {

    /**
     * @return health of the entity
     */
    float getHealth();

    /**
     * @param health new health
     */
    void setHealth(float health);

    /**
     * @return hurt time of the entity
     */
    short getHurtTime();

    /**
     * @param hurtTime new hurt time
     */
    void setHurtTime(short hurtTime);

    /**
     * @return hurt time by timestamp
     */
    int getHurtByTimestamp();

    /**
     * @param hurtByTimestamp new hurt time by timestamp
     */
    void setHurtByTimestamp(int hurtByTimestamp);

    /**
     * @return death time of the entity
     */
    short getDeathTime();

    /**
     * @param deathTime new death time
     */
    void setDeathTime(short deathTime);

    /**
     * @return absorption amount of the entity
     */
    float getAbsorptionAmount();

    /**
     * @param absorptionAmount new absorption amount
     */
    void setAbsorptionAmount(float absorptionAmount);

    /**
     * @return if the entity is fall flying
     */
    boolean isFallFlying();

    /**
     * @param fallFlying new fall flying
     */
    void setFallFlying(boolean fallFlying);

}
