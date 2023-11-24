package fr.univnantes.trainreservation;

/**
 * Represents a train available in the rail system.
 */
public interface Train {

    /**
     * Retrieves the name of the train.
     * @return The name of the train.
     */
    String getName();

    /**
     * Retrieves the maximum amount of passengers that the train can contain.
     * @return the maximum amount of passengers that the train can contain.
     */
    int getMaxPassengers();
}
