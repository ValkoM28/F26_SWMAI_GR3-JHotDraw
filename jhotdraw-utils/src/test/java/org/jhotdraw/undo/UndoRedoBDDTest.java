package org.jhotdraw.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class UndoRedoBDDTest extends ScenarioTest<UndoRedoBDDTest.GivenStage, UndoRedoBDDTest.WhenStage, UndoRedoBDDTest.ThenStage> {

    @Test
    public void user_can_undo_an_action_to_correct_a_mistake() {
        given().a_drawing_with_undo_manager()
            .and().an_edit_action_has_been_performed();
        when().the_user_performs_undo();
        then().the_action_should_be_reversed()
            .and().undo_should_not_be_available()
            .and().redo_should_be_available();
    }

    @Test
    public void user_can_redo_an_undone_action_to_restore_progress() {
        given().a_drawing_with_undo_manager()
            .and().an_edit_action_has_been_performed()
            .and().the_action_has_been_undone();
        when().the_user_performs_redo();
        then().the_action_should_be_restored()
            .and().undo_should_be_available()
            .and().redo_should_not_be_available();
    }

    @Test
    public void user_can_undo_multiple_actions_sequentially() {
        given().a_drawing_with_undo_manager()
            .and().multiple_edit_actions_have_been_performed();
        when().the_user_performs_undo_multiple_times();
        then().all_actions_should_be_reversed_in_reverse_order()
            .and().redo_should_be_available();
    }

    @Test
    public void performing_new_action_after_undo_clears_redo_history() {
        given().a_drawing_with_undo_manager()
            .and().an_edit_action_has_been_performed()
            .and().the_action_has_been_undone();
        when().a_new_edit_action_is_performed();
        then().redo_should_not_be_available()
            .and().the_new_action_should_be_undoable();
    }

    public static class GivenStage extends Stage<GivenStage> {
        @ProvidedScenarioState
        UndoRedoManager undoManager;

        @ProvidedScenarioState
        List<TestableEdit> edits;

        public GivenStage a_drawing_with_undo_manager() {
            undoManager = new UndoRedoManager();
            edits = new ArrayList<>();
            return self();
        }

        public GivenStage an_edit_action_has_been_performed() {
            TestableEdit edit = new TestableEdit("First Action");
            edits.add(edit);
            undoManager.addEdit(edit);
            return self();
        }

        public GivenStage multiple_edit_actions_have_been_performed() {
            TestableEdit first = new TestableEdit("First Action");
            TestableEdit second = new TestableEdit("Second Action");
            TestableEdit third = new TestableEdit("Third Action");
            edits.add(first);
            edits.add(second);
            edits.add(third);
            undoManager.addEdit(first);
            undoManager.addEdit(second);
            undoManager.addEdit(third);
            return self();
        }

        public GivenStage the_action_has_been_undone() {
            undoManager.undo();
            return self();
        }
    }

    public static class WhenStage extends Stage<WhenStage> {
        @ExpectedScenarioState
        UndoRedoManager undoManager;

        @ExpectedScenarioState
        List<TestableEdit> edits;

        public WhenStage the_user_performs_undo() {
            undoManager.undo();
            return self();
        }

        public WhenStage the_user_performs_redo() {
            undoManager.redo();
            return self();
        }

        public WhenStage the_user_performs_undo_multiple_times() {
            undoManager.undo();
            undoManager.undo();
            undoManager.undo();
            return self();
        }

        public WhenStage a_new_edit_action_is_performed() {
            TestableEdit newEdit = new TestableEdit("New Action");
            edits.add(newEdit);
            undoManager.addEdit(newEdit);
            return self();
        }
    }

    public static class ThenStage extends Stage<ThenStage> {
        @ExpectedScenarioState
        UndoRedoManager undoManager;

        @ExpectedScenarioState
        List<TestableEdit> edits;

        public ThenStage the_action_should_be_reversed() {
            assertThat(edits.get(0).isApplied())
                .as("First edit should be reversed after undo")
                .isFalse();
            return self();
        }

        public ThenStage the_action_should_be_restored() {
            assertThat(edits.get(0).isApplied())
                .as("First edit should be restored after redo")
                .isTrue();
            return self();
        }

        public ThenStage all_actions_should_be_reversed_in_reverse_order() {
            assertThat(edits.get(0).isApplied())
                .as("First edit should be reversed")
                .isFalse();
            assertThat(edits.get(1).isApplied())
                .as("Second edit should be reversed")
                .isFalse();
            assertThat(edits.get(2).isApplied())
                .as("Third edit should be reversed")
                .isFalse();
            return self();
        }

        public ThenStage undo_should_be_available() {
            assertThat(undoManager.canUndo())
                .as("Undo should be available")
                .isTrue();
            assertThat(undoManager.getUndoAction().isEnabled())
                .as("Undo action should be enabled")
                .isTrue();
            return self();
        }

        public ThenStage undo_should_not_be_available() {
            assertThat(undoManager.canUndo())
                .as("Undo should not be available")
                .isFalse();
            assertThat(undoManager.getUndoAction().isEnabled())
                .as("Undo action should be disabled")
                .isFalse();
            return self();
        }

        public ThenStage redo_should_be_available() {
            assertThat(undoManager.canRedo())
                .as("Redo should be available")
                .isTrue();
            assertThat(undoManager.getRedoAction().isEnabled())
                .as("Redo action should be enabled")
                .isTrue();
            return self();
        }

        public ThenStage redo_should_not_be_available() {
            assertThat(undoManager.canRedo())
                .as("Redo should not be available")
                .isFalse();
            assertThat(undoManager.getRedoAction().isEnabled())
                .as("Redo action should be disabled")
                .isFalse();
            return self();
        }

        public ThenStage the_new_action_should_be_undoable() {
            assertThat(edits.get(edits.size() - 1).isApplied())
                .as("New edit should be applied")
                .isTrue();
            assertThat(undoManager.canUndo())
                .as("New action should be undoable")
                .isTrue();
            return self();
        }
    }

    private static class TestableEdit extends AbstractUndoableEdit {
        private final String name;
        private boolean applied = true;

        public TestableEdit(String name) {
            this.name = name;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            applied = false;
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            applied = true;
        }

        public boolean isApplied() {
            return applied;
        }

        @Override
        public String getPresentationName() {
            return name;
        }
    }
}
