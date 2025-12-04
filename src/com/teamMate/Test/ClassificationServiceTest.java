package com.teamMate.Test;

import com.teamMate.Classes.ClassificationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassificationServiceTest {

    private final ClassificationService service = new ClassificationService();

    @Test
    void testClassifyPersonalityLeaderRange() {
        assertEquals("Leader", service.classifyPersonality(90));
        assertEquals("Leader", service.classifyPersonality(100));
    }

    @Test
    void testClassifyPersonalityBalancedRange() {
        assertEquals("Balanced", service.classifyPersonality(70));
        assertEquals("Balanced", service.classifyPersonality(89));
    }

    @Test
    void testClassifyPersonalityThinkerRange() {
        assertEquals("Thinker", service.classifyPersonality(50));
        assertEquals("Thinker", service.classifyPersonality(69));
    }

    @Test
    void testClassifyPersonalityInvalidScoreThrows() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.classifyPersonality(40));
        assertTrue(ex.getMessage().contains("Score must be between 50-100"));
    }

    @Test
    void testCalculatePersonalityScoreScaling() {
        int score = service.calculatePersonalityScore(5, 5, 5, 5, 5);
        assertEquals(100, score); // 25 * 4
    }

    @Test
    void testCalculatePersonalityScoreInvalidQuestionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculatePersonalityScore(0, 5, 5, 5, 5));
    }

    @Test
    void testValidRoleAndGame() {
        assertTrue(service.isValidRole("Strategist"));
        assertFalse(service.isValidRole("InvalidRole"));
        assertTrue(service.isValidGame("FIFA"));
        assertFalse(service.isValidGame("   "));
    }
    @Test
    void testClassificationBoundaries() {
        assertEquals("Leader", service.classifyPersonality(90));
        assertEquals("Balanced", service.classifyPersonality(89));
        assertEquals("Balanced", service.classifyPersonality(70));
        assertEquals("Thinker", service.classifyPersonality(69));
        assertEquals("Thinker", service.classifyPersonality(50));
    }

}
