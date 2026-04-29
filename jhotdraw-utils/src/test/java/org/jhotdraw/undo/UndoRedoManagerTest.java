package org.jhotdraw.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UndoRedoManagerTest {

    @Test
    public void undoAndRedoRestoreEditState() {
        UndoRedoManager manager = new UndoRedoManager();
        RecordingEdit edit = new RecordingEdit();

        manager.addEdit(edit);
        assertTrue(manager.canUndo());
        assertFalse(manager.canRedo());
        assertTrue(manager.getUndoAction().isEnabled());
        assertFalse(manager.getRedoAction().isEnabled());

        manager.undo();
        assertFalse(edit.isApplied());
        assertFalse(manager.canUndo());
        assertTrue(manager.canRedo());
        assertTrue(manager.getRedoAction().isEnabled());
        assert edit.isApplied() == manager.canUndo();

        manager.redo();
        assertTrue(edit.isApplied());
        assertTrue(manager.canUndo());
        assertFalse(manager.canRedo());
        assertTrue(manager.getUndoAction().isEnabled());
        assertFalse(manager.getRedoAction().isEnabled());
    }

    @Test(expected = CannotUndoException.class)
    public void undoWithEmptyHistoryThrows() {
        UndoRedoManager manager = new UndoRedoManager();

        assertFalse(manager.canUndo());
        manager.undo();
    }

    @Test(expected = CannotRedoException.class)
    public void redoWithEmptyHistoryThrows() {
        UndoRedoManager manager = new UndoRedoManager();

        assertFalse(manager.canRedo());
        manager.redo();
    }

    @Test
    public void addingEditAfterUndoDiscardsRedoBranch() {
        UndoRedoManager manager = new UndoRedoManager();
        RecordingEdit firstEdit = new RecordingEdit();
        RecordingEdit secondEdit = new RecordingEdit();

        manager.addEdit(firstEdit);
        manager.undo();
        manager.addEdit(secondEdit);

        assertTrue(secondEdit.isApplied());
        assertTrue(manager.canUndo());
        assertFalse(manager.canRedo());
        assertFalse(firstEdit.isApplied());
    }

    @Test
    public void discardAllEditsResetsHistoryAndSignificance() {
        UndoRedoManager manager = new UndoRedoManager();
        manager.addEdit(new RecordingEdit());

        assertTrue(manager.hasSignificantEdits());
        manager.discardAllEdits();

        assertFalse(manager.hasSignificantEdits());
        assertFalse(manager.canUndo());
        assertFalse(manager.canRedo());
        assertFalse(manager.getUndoAction().isEnabled());
        assertFalse(manager.getRedoAction().isEnabled());
    }

    private static class RecordingEdit extends AbstractUndoableEdit {

        private boolean applied = true;

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
    }
}