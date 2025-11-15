package com.teamMate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.Iterator;

// This class orchestrates building the teams.
public class TeamBuilder {

    // These roles are required for a balanced team
    private static final String REQUIRED_ROLE_1 = "Defender";
    private static final String REQUIRED_ROLE_2 = "Strategist";

    public List<Team> buildTeams(List<Participant> participants, int teamSize) {

        // 1. Create a thread-safe list for participants
        // This is crucial so multiple threads can safely remove items.
        List<Participant> availableParticipants = Collections.synchronizedList(new ArrayList<>(participants));

        // 2. Determine how many teams to build and set up the thread pool
        int numTeams = participants.size() / teamSize;
        // Using a fixed thread pool to run tasks in parallel
        ExecutorService executor = Executors.newFixedThreadPool(numTeams);

        // 3. Create a list to hold the "future" results of each task
        List<Future<Team>> futures = new ArrayList<>();

        // 4. Create and submit one task for each team we need to build
        for (int i = 0; i < numTeams; i++) {
            // A "Callable" is a task that returns a value (our Team)
            Callable<Team> task = () -> {
                Team team = new Team();

                // --- This is the Matching Algorithm ---

                // Try to fill the team to the desired size
                for (int j = 0; j < teamSize; j++) {
                    Participant p = findBestParticipantForTeam(team, availableParticipants);
                    if (p != null) {
                        team.addMember(p);
                    } else {
                        // Not enough participants left to fill the team
                        break;
                    }
                }
                return team;
            };
            // Submit the task to the pool
            futures.add(executor.submit(task));
        }

        // 5. Collect the results
        List<Team> formedTeams = new ArrayList<>();
        try {
            for (Future<Team> future : futures) {
                // future.get() waits for the task to complete and gets its result
                formedTeams.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error during team building: " + e.getMessage());
            e.printStackTrace();
        }

        // 6. Shut down the thread pool
        executor.shutdown();

        // 7. Return the completed teams
        return formedTeams;
    }

    /**
     * The core matching algorithm. Finds the "best" participant for a team.
     * This method MUST be thread-safe.
     * @param team The team we are currently building.
     * @param availableParticipants The master list of available participants.
     * @return The best participant found, or null if none.
     */
    private Participant findBestParticipantForTeam(Team team, List<Participant> availableParticipants) {
        Participant selected = null;

        // --- Synchronization is CRITICAL ---
        // We lock the list so only one thread can search and remove at a time.
        // This prevents two threads from grabbing the same participant.
        synchronized (availableParticipants) {
            // Use an Iterator to safely remove items while looping
            Iterator<Participant> iterator = availableParticipants.iterator();

            // --- Strategy 1: Find required roles first ---
            if (!team.hasRole(REQUIRED_ROLE_1)) {
                selected = findAndRemove(iterator, REQUIRED_ROLE_1);
            }
            if (selected == null && !team.hasRole(REQUIRED_ROLE_2)) {
                selected = findAndRemove(iterator, REQUIRED_ROLE_2);
            }

            // --- Strategy 2: If roles are filled, just grab the next available person ---
            if (selected == null && iterator.hasNext()) {
                selected = iterator.next();
                iterator.remove();
            }

            // Note: A more complex algorithm could also check for
            // "diverse interests" or "mixed personalities" here.
            // This is a simple, effective starting point.
        }
        return selected;
    }

    /**
     * Helper method to find a participant with a specific role
     * and remove them from the list.
     */
    private Participant findAndRemove(Iterator<Participant> iterator, String role) {
        while (iterator.hasNext()) {
            Participant p = iterator.next();
            if (p.getPreferredRole().equalsIgnoreCase(role)) {
                iterator.remove(); // Safely remove from the list
                return p;
            }
        }
        return null; // No participant with that role was found
    }
}